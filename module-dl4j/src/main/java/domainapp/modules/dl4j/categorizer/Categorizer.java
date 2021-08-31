/**
 * 
 */
package domainapp.modules.dl4j.categorizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jayeshecs
 *
 * @param <R> Record or Data
 * @param <L> Label
 */
@Slf4j
public class Categorizer<R, L> {
	
	private ICategorizerDataHandler<R, L> handler;
	private TokenPreProcess tokenPreProcessor;
	private DefaultTokenizerFactory tokenizerFactory;

	
	/**
	 * @param labelProvider
	 * @param tokenPreProcessor
	 */
	public Categorizer(ICategorizerDataHandler<R, L> labelProvider, TokenPreProcess tokenPreProcessor) {
		this.handler = labelProvider;
		this.tokenPreProcessor = tokenPreProcessor;
		
		// initialize tokenizer factory
		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(this.tokenPreProcessor);
	}
	
	public Word2Vec train(Collection<R> listAllCategorized) {

		Word2Vec word2Vec = new Word2Vec.Builder()
			.allowParallelTokenization(true)
			.batchSize(100)
			.elementsLearningAlgorithm(new SkipGram<VocabWord>())
			.epochs(10)
			.iterate(new CategoryAwareParagraphIterator<R, L>(this.handler, Collections.synchronizedCollection(listAllCategorized)))
			.layerSize(100)
			.learningRate(0.001)
			.minWordFrequency(1)
			.seed(123)
			.tokenizerFactory(tokenizerFactory)
			.build();
		
		// start model training
		word2Vec.fit();

		// print vocab
		printVocab(word2Vec);
		
		return word2Vec;
	}
	
	private void printVocab(SequenceVectors<VocabWord> vectors) {
		VocabCache<VocabWord> vocab = vectors.getVocab();
		log.info(String.format("Vocab Detail:\n"
				+ "Total words: %d\n"
				+ "Total Docs: %d\n"
				+ "Total Word Occurances: %d\n"
				+ "Total Tokens: %d\n"
				+ "Words: %s\n"
				, vocab.numWords()
				, vocab.totalNumberOfDocs()
				, vocab.totalWordOccurrences()
				, vocab.tokens().size()
				, vocab.words()));
		for (VocabWord vocabWord : vocab.vocabWords()) {
			log.info(String.format("Is Label: %s, Word: %s (%s)"
					, vocabWord.isLabel()
					, vocabWord.getLabel()
					, vocabWord));
		}
	}
	
	/**
	 * @param vectors
	 * @param listUncategorized
	 */
	public Map<R, L> categorize(Word2Vec vectors, Collection<R> listUncategorized) {

		MeansBuilder meansBuilder = new MeansBuilder(
				(InMemoryLookupTable<VocabWord>) vectors.getLookupTable(),
				tokenizerFactory);
		
		for (R record : listUncategorized) {
			handler.getLabel(record);
		}
		Collection<String> labels = vectors.getVocab().words(); //vectors.getLabelsSource().getLabels();
		
		LabelSeeker seeker = new LabelSeeker(new ArrayList<String>(labels),
				(InMemoryLookupTable<VocabWord>) vectors.getLookupTable());
		
		long count = 0;
		Map<R, L> result = new HashMap<>();
		for (R data : listUncategorized) {
			String paragraph = handler.getParagraph(data);
			INDArray vector = meansBuilder.documentAsVector(paragraph);
			Pair<String, Double> scores = seeker.getScores(vector);
			if (scores == null) {
				continue;
			}
			if (scores.getValue().isNaN()) {
				continue;
			}
			count++;
			result.put(data, handler.toLabel(scores.getKey()));
			log.info(String.format("%s = %s (%f)", paragraph, scores.getKey(), scores.getValue()));
		}
		log.info(String.format("Total %d/%d record categorized", count, listUncategorized.size()));
		return result;
	}

	public void save(ParagraphVectors model, File file) {
		try {
			WordVectorSerializer.writeParagraphVectors(model, file);
		} catch (Exception e) {
			log.error("Exception occurred while writing model", e);
		}
	}

	public ParagraphVectors loadModel(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		try {
			ParagraphVectors result = WordVectorSerializer.readParagraphVectors(file);

			// print vocab
			printVocab(result);

			return result;
		} catch (IOException e) {
			log.error("I/O error occurred while reading paragraph vectors", e);
		}
		return null;
	}
	
}
