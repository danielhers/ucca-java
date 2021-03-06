package il.ac.huji.cs.nlp.ucca;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.models.rntn.RNTN;
import org.apache.commons.math3.random.MersenneTwister;
import org.deeplearning4j.models.rntn.RNTNEval;
import org.deeplearning4j.models.rntn.Tree;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.util.SerializationUtils;
import org.nd4j.linalg.api.activation.Activations;
import org.deeplearning4j.text.tokenization.tokenizerfactory.UimaTokenizerFactory;
import org.deeplearning4j.text.corpora.treeparser.TreeVectorizer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UccaRntn {

    private static final Logger log = LoggerFactory.getLogger(UccaRntn.class);

	private TreeVectorizer vectorizer;
	private RNTN rntn;

	public UccaRntn(List<Passage> passages) throws Exception {
		vectorizer = new TreeVectorizer();
		SentenceIterator sentenceIter = new CollectionSentenceIterator(getPassageTexts(passages));
		Word2Vec vec = getWord2VecModel(sentenceIter);
		rntn = new RNTN.Builder().setActivationFunction(Activations.tanh())
				.setAdagradResetFrequency(1).setCombineClassification(true).setFeatureVectors(vec)
				.setRandomFeatureVectors(false).setRng(new MersenneTwister(123))
				.setUseTensors(false).build(); // TODO change setUseTensors to true
	}

	public void train(List<Tree> trees) {
		rntn.fit(trees);
	}

	public List<Tree> predict(List<Passage> passages) throws Exception {
        List<Tree> trees = vectorizer.getTrees(passagesToString(passages));
        for (Tree tree : trees) {
    		rntn.forwardPropagateTree(tree);
		}
		return trees;
	}

	public void eval(List<Passage> passages) throws Exception {
		List<Tree> trees = passagesToTrees(passages);
		train(trees);
//		log.info(passagesToString(treesToPassages(r.predict(passages))));
		RNTNEval eval = new RNTNEval();
		log.info("Value: " + rntn.getValue());
		eval.eval(rntn, trees);
		log.info("Stats: " + eval.stats());
	}

	private static Word2Vec getWord2VecModel(SentenceIterator sentenceIter) throws ResourceInitializationException {
		Word2Vec vec;
		File modelDir = new File("models");
		//noinspection ResultOfMethodCallIgnored
		modelDir.mkdir();
		File vecModel = new File(modelDir, "wordvectors.ser");
		if (vecModel.exists()) {
			vec = SerializationUtils.readObject(vecModel);
			vec.setCache(new InMemoryLookupCache(vec.getLayerSize()));
		} else {
			TokenizerFactory t = new UimaTokenizerFactory();
			// TODO LoadGoogleVectors
			vec = new Word2Vec.Builder()
					.iterate(sentenceIter).tokenizerFactory(t).build();
			vec.setCache(new InMemoryLookupCache(vec.getLayerSize()));
			vec.fit();
			log.info("Saving word2vec model...");
			SerializationUtils.saveObject(vec, vecModel);
		}
		return vec;
	}

	private static List<String> getPassageTexts(List<Passage> passages) {
		List<String> passageTexts = new ArrayList<>();
		for (Passage passage : passages) {
			passageTexts.add(passage.getText());
		}
		return passageTexts;
	}

	private static List<Passage> readPassages(String path) throws JAXBException {
		List<Passage> passages = new ArrayList<>();
		File corpusDir = new File(path);
		FilenameFilter xmlFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		};
		for (File file : corpusDir.listFiles(xmlFilter)) {
			passages.add(Passage.read(file));
		}
		return passages;
	}

	private static List<Passage> treesToPassages(List<Tree> trees) throws Exception {
		List<Passage> passages = new ArrayList<>();
		for (Tree tree : trees) {
			passages.add(new Passage(tree));
		}
		return passages;
	}

	private List<Tree> passagesToTrees(List<Passage> passages) throws Exception {
		String sentences = passagesToString(passages);
		log.info(sentences);
		return vectorizer.getTreesWithLabels(sentences, getAllLabels(passages));
	}

	private static String passagesToString(List<Passage> passages) {
		return StringUtils.join(passages, "\\n");//.replaceFirst("<A>", "{A}").replaceFirst("</A>", "{/A}").replaceAll("</?[^>]*>\\s*", "").replace('{', '<').replace('}','>');
	}

	private static ArrayList<String> getAllLabels(List<Passage> passages) {
		Set<String> labels = new TreeSet<>();
		for (Passage passage : passages) {
			labels.addAll(passage.getAllEdgeTypes());
		}
		return new ArrayList<>(labels);
	}

	public static void main(String[] args) throws Exception {
//		List<Passage> passages = readPassages("../ucca/corpus");
		List<Passage> passages = readPassages("examples");
		new UccaRntn(passages).eval(passages);
	}

}
