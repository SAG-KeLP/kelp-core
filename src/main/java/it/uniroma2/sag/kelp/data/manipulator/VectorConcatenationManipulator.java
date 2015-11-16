/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.data.manipulator;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.vector.SparseVector;

import java.util.List;

/**
 * VectorConcatenationManipulator is an implementations of Manipulator that allows to concatenate vectors 
 * into a new SparseVector representation. It is useful when a linear approach must be applied to multiple vector
 * representations
 * 
 * @author Simone Filice
 *
 */
public class VectorConcatenationManipulator implements Manipulator{

	private String concatenationName;
	private List<String> representationsToBeConcatenated;
	private List<Float> weights;

	public VectorConcatenationManipulator(String concatenationName,
			List<String> representationsToBeConcatenated, List<Float> weights) {
		this.concatenationName = concatenationName;
		this.representationsToBeConcatenated = representationsToBeConcatenated;
		this.weights = weights;
	}

	@Override
	public void manipulate(Example example) {
		for(String repName : representationsToBeConcatenated){
			if(example.getRepresentation(repName)==null){
				return;//probably we are trying to manipulate the wrong level of the example structure
			}
		}
		concatenateVectors(example, representationsToBeConcatenated, weights, concatenationName);
	}

	/**
	 * Returns a SparseVector corresponding to the concatenation of the vectors in <code>example</code> identified with <code>representationsToBeMerged</code>
	 * Each vector is scaled with respect to the corresponding scaling factor in <code>weights</code> 
	 * 
	 * @param example the example whose vectors must be concatenated
	 * @param representationsToConcatenated the identifiers of the vectors to be concatenated
	 * @param weights the scaling factors of the vectors to be concatenated
	 * @return a SparseVector corresponding to the concatenation of the vectors
	 */
	public static SparseVector concatenateVectors(Example example, List<String> representationsToConcatenated, List<Float> weights){
		SparseVector vector = new SparseVector();
		for(int i=0; i<representationsToConcatenated.size(); i++){
			String representation = representationsToConcatenated.get(i);
			Vector vectorToBeAdded = (Vector) example.getRepresentation(representation);
			vector.merge(vectorToBeAdded, weights.get(i), representation);
		}

		return vector;
	}

	/**
	 * Add a new representation identified with <code>combinationName<code> corresponding to the concatenation of the vectors in <code>example</code> identified with <code>representationsToBeMerged</code>
	 * Each vector is scaled with respect to the corresponding scaling factor in <code>weights</code> 
	 * 
	 * @param example the example whose vectors must be concatenated
	 * @param representationsToBeConcatenated the identifiers of the vectors to be concatenated
	 * @param weights the scaling factors of the vectors to be concatenated
	 * @param combinationName the name of the new representation to be added
	 */
	public static void concatenateVectors(Example example, List<String> representationsToBeConcatenated, List<Float> weights, String combinationName){
		SparseVector combination = concatenateVectors(example, representationsToBeConcatenated, weights);
		example.addRepresentation(combinationName, combination);
	}

}
