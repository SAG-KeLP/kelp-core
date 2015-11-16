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

package it.uniroma2.sag.kelp.learningalgorithm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * It is an ensemble method that operates combining various learning algorithms
 * 
 * @author      Simone Filice
 */
@JsonPropertyOrder({ "toCombine"})
public interface EnsembleLearningAlgorithm extends LearningAlgorithm{

	
	/**
	 * @param toCombine the toCombine to set
	 */
	public void setToCombine(List<LearningAlgorithm> toCombine);

	/**
	 * Returns a list of the learning algorithm this ensemble method is combining
	 * 
	 * @return the learning algorithms to be combined
	 */
	public List<LearningAlgorithm> getToCombine();
		
//	@Override
//	public void setLabels(Label... labels) throws Exception {
//		if(toCombine==null){
//			throw new Exception("In EnsembleLearningAlgorithm, toCombine field must be specified before labels");
//		}
//		for (LearningAlgorithm l : toCombine) {
//			if (l == null)
//				throw new Exception("In EnsembleLearningAlgorithm toCombine field must be specified before labels.");
//			l.setLabels(labels);
//		}
//	}	
	
}
