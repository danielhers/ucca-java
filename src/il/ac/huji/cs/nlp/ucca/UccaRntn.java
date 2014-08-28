package il.ac.huji.cs.nlp.ucca;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.MersenneTwister;
import org.deeplearning4j.nn.activation.Activations;
import org.deeplearning4j.rntn.RNTN;
import org.deeplearning4j.rntn.Tree;
import org.deeplearning4j.text.treeparser.TreeVectorizer;
import org.deeplearning4j.util.SerializationUtils;
import org.deeplearning4j.word2vec.Word2Vec;
import org.deeplearning4j.word2vec.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.word2vec.sentenceiterator.SentenceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UccaRntn {

    private static Logger log = LoggerFactory.getLogger(UccaRntn.class);

	private TreeVectorizer vectorizer;
	private Word2Vec vec;
	private RNTN rntn;
	private SentenceIterator sentenceIter;

	public UccaRntn(List<Passage> passages) throws Exception {
		vectorizer = new TreeVectorizer();
		sentenceIter = new CollectionSentenceIterator(getPassageTexts(passages));
		vec = getWord2VecModel(sentenceIter);
		rntn = new RNTN.Builder().setActivationFunction(Activations.tanh())
				.setAdagradResetFrequency(1).setCombineClassification(true).setFeatureVectors(vec)
				.setRandomFeatureVectors(false).setRng(new MersenneTwister(123))
				.setUseTensors(true).setNumHidden(25).build(); // TODO change setUseTensors to true
	}

	private static Word2Vec getWord2VecModel(SentenceIterator sentenceIter) {
		File modelDir = new File("models");
		modelDir.mkdir();
		File vecModel = new File(modelDir, "wordvectors.ser");
		if (vecModel.exists()) {
			return (Word2Vec) SerializationUtils.readObject(vecModel);
		}
		Word2Vec vec = new Word2Vec(sentenceIter); // TODO LoadGoogleVectors
		vec.train();
		log.info("Saving word2vec model...");
		SerializationUtils.saveObject(vec, vecModel);
		return vec;
	}

	private static List<String> getPassageTexts(List<Passage> passages) {
		List<String> passageTexts = new ArrayList<String>();
		for (Passage passage : passages) {
			passageTexts.add(passage.getText());
		}
		return passageTexts;
	}

	private void train(List<Passage> passages) throws Exception {
        List<Tree> trees = vectorizer.getTreesWithLabels(
        		StringUtils.join(passages, "\\n"), getAllLabels(passages));
        rntn.train(trees);
	}

	private ArrayList<String> getAllLabels(List<Passage> passages) {
		Set<String> labels = new TreeSet<String>();
		for (Passage passage : passages) {
			labels.addAll(passage.getAllEdgeTypes());
		}
		return new ArrayList<String>(labels);
	}

	private List<Tree> predict(List<Passage> passages) throws Exception {
        List<Tree> trees = vectorizer.getTrees(StringUtils.join(passages, "\\n"));
        for (Tree tree : trees) {
    		rntn.forwardPropagateTree(tree);
		}
		return trees;
	}

	public static void main(String[] args) throws Exception {
		List<Passage> passages = readPassages("../ucca/corpus");
		UccaRntn rntn = new UccaRntn(passages);
		rntn.train(passages);
		System.out.println(rntn.predict(passages));
	}

	private static List<Passage> readPassages(String path) throws JAXBException {
		List<Passage> passages = new ArrayList<Passage>();
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

}
