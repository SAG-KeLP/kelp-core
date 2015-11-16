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
import it.uniroma2.sag.kelp.data.example.ExamplePair;
import it.uniroma2.sag.kelp.data.representation.vector.DenseVector;
import it.uniroma2.sag.kelp.kernel.Kernel;

/**
 * This manipulator manipulates <code>ExamplePair</code> object extracting some similarity scores between the
 * left and the right examples of the pair. The extracted similarity scores are stored in a <code>DenseVector</code>
 * that is added to the representations set of the <code>ExamplePair</code> to be manipulated.
 * 
 *  @author Simone Filice
 */
public class PairSimilarityExtractor implements Manipulator{


	private Kernel[] similarityMetrics;
	private String similarityVectorName;

	/**
	 * @param similarityVectorName the name of the similarity vector that will be added to the examples to be manipulated
	 * @param kernel the kernels that will extract the similarity scores
	 */
	public PairSimilarityExtractor(String similarityVectorName, Kernel ... kernel){
		this.similarityVectorName = similarityVectorName;
		this.similarityMetrics = kernel;
	}

	@Override
	public void manipulate(Example example) {
		if(example instanceof ExamplePair){
			ExamplePair pair = (ExamplePair) example;
			Example left = pair.getLeftExample();
			Example right = pair.getRightExample();

			DenseVector similarityVector;

			similarityVector = new DenseVector();
			double [] featureVector = new double[similarityMetrics.length];
			for(int i=0; i<similarityMetrics.length; i++){
				try{
					featureVector[i] = similarityMetrics[i].innerProduct(left, right);
				}catch(IllegalArgumentException e){
					return;//it can happen if there is a wrong usage of this manipulator (for instance if a DirectKernel has an error in its representationName field)
					//Or it can be due to an ExamplePair whose left and right examples are ExamplePairs to and we are manipulating the wrong level of the example structure
				}
				
			}
			similarityVector.setFeatureValues(featureVector);
			pair.addRepresentation(similarityVectorName, similarityVector);
			
		}

	}

}
