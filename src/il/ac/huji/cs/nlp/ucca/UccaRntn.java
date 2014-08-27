package il.ac.huji.cs.nlp.ucca;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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
	private static String sentence = "<LABEL> This is one sentence. </LABEL>";

	private TreeVectorizer vectorizer;
	private Word2Vec vec;
	private RNTN rntn;
	private SentenceIterator sentenceIter;

	public UccaRntn() throws Exception {
		vectorizer = new TreeVectorizer();
		sentenceIter = new CollectionSentenceIterator(Arrays.asList(sentence));
		File vecModel = new File("wordvectors.ser");
		if (vecModel.exists()) {
			vec = (Word2Vec) SerializationUtils.readObject(vecModel);
		} else {
			vec = new Word2Vec(sentenceIter);
			vec.train();
			log.info("Saving word2vec model...");
			SerializationUtils.saveObject(vec, vecModel);
		}
		rntn = new RNTN.Builder().setActivationFunction(Activations.tanh())
				.setAdagradResetFrequency(1).setCombineClassification(true).setFeatureVectors(vec)
				.setRandomFeatureVectors(false).setRng(new MersenneTwister(123))
				.setUseTensors(true).setNumHidden(25).build();
	}

	private void train() throws Exception {
        List<Tree> trees = vectorizer.getTreesWithLabels(sentence,Arrays.asList("LABEL","NONE"));
        rntn.train(trees);
	}

	private List<Integer> predict() throws Exception {
        List<Tree> trees = vectorizer.getTreesWithLabels(sentence,Arrays.asList("LABEL","NONE"));
		return rntn.predict(trees);
	}

	public static void main(String[] args) throws Exception {
		UccaRntn rntn = new UccaRntn();
		rntn.train();
		System.out.println(rntn.predict());
	}

}
