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
 * It is a linear algorithm operating directly on an explicit vector space
 * 
 * 
 * @author      Simone Filice
 */
public interface LinearMethod{

	/**
	 * Returns the representation this learning algorithm exploits
	 * 
	 * @return the representation
	 */
	public String getRepresentation();
	
	/**
	 * Sets the representation this learning algorithm will exploit
	 * 
	 * @param representation the representation to set
	 */
	public void setRepresentation(String representation);
}
