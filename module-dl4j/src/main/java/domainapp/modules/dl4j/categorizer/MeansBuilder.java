/**
 * 
 */
package domainapp.modules.dl4j.categorizer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @author jayeshecs
 *
 */
public class MeansBuilder {
	private VocabCache<VocabWord> vocabCache;
	private InMemoryLookupTable<VocabWord> lookupTable;
	private TokenizerFactory tokenizerFactory;

	@SuppressWarnings("unchecked")
	public MeansBuilder(InMemoryLookupTable<VocabWord> lookupTable, TokenizerFactory tokenizerFactory) {
		this.lookupTable = lookupTable;
		this.vocabCache = lookupTable.getVocab();
		this.tokenizerFactory = tokenizerFactory;
	}

	/**
	 * This method returns centroid (mean vector) for document.
	 *
	 * @param document
	 * @return
	 */
	public INDArray documentAsVector(String content) {
		List<String> documentAsTokens = tokenizerFactory.create(content).getTokens();
		AtomicInteger cnt = new AtomicInteger(0);
		for (String word : documentAsTokens) {
			if (vocabCache.containsWord(word))
				cnt.incrementAndGet();
		}
		if (cnt.get() == 0) {
			// nothing found and hence returning empty array
			return Nd4j.create(1, lookupTable.layerSize());
		}
		INDArray allWords = Nd4j.create(cnt.get(), lookupTable.layerSize());

		cnt.set(0);
		for (String word : documentAsTokens) {
			if (vocabCache.containsWord(word))
				allWords.putRow(cnt.getAndIncrement(), lookupTable.vector(word));
		}

		INDArray mean = allWords.mean(0);

		return mean;
	}

}
