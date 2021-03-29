/**
 * 
 */
package domainapp.modules.dl4j.categorizer;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.primitives.Pair;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jayeshecs
 *
 */
@Slf4j
public class LabelSeeker {
	private List<String> labelsUsed;
	private InMemoryLookupTable<VocabWord> lookupTable;

	/**
	 * @param labelsUsed
	 * @param lookupTable
	 */
	public LabelSeeker(List<String> labelsUsed, InMemoryLookupTable<VocabWord> lookupTable) {
		if (labelsUsed.isEmpty()) {
			throw new IllegalStateException("You can't have 0 labels used for ParagraphVectors");
		}
		this.lookupTable = lookupTable;
		this.labelsUsed = labelsUsed;
	}

	/**
	 * This method accepts vector, that represents any document, and returns
	 * distances between this document, and previously trained categories
	 * 
	 * @return
	 */
	public Pair<String, Double> getScores(INDArray vector) {
		Set<Pair<String, Double>> result = new TreeSet<>(new Comparator<Pair<String, Double>>() {

			@Override
			public int compare(Pair<String, Double> pair1, Pair<String, Double> pair2) {
				return pair2.getValue().compareTo(pair1.getValue());
			}
		});
		for (String label : labelsUsed) {
			INDArray vecLabel = lookupTable.vector(label);
			if (vecLabel == null) {
				throw new IllegalStateException("Label '" + label + "' has no known vector!");
			}
			double sim = Transforms.cosineSim(vector, vecLabel);
			// if not a number then ignore it
			if (Double.isNaN(sim)) {
				continue;
			}
//			log.info(String.format("%s = %f", label, sim));
			result.add(new Pair<String, Double>(label, sim));
		}
		if (result.isEmpty()) {
			return null;
		}
		Pair<String, Double> score = result.iterator().next();
//		return score;
		return score.getValue().compareTo(new Double(0.3)) > 0 ? null : score; 
	}

}
