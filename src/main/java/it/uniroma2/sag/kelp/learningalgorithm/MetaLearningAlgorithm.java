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

/**
 * It is a meta algorithm that operates modifying exploiting another learning algorithm
 * 
 * @author      Simone Filice
 */
public interface MetaLearningAlgorithm extends LearningAlgorithm{

	

	/**
	 * @param baseAlgorithm the baseAlgorithm to set
	 */
	public void setBaseAlgorithm(LearningAlgorithm baseAlgorithm);
	

	/**
	 * Returns the base algorithm this meta algorithm is based on
	 * 
	 * @return the base algorithm
	 */
	public LearningAlgorithm getBaseAlgorithm();
	

}

