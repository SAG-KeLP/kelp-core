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

package it.uniroma2.sag.kelp.predictionfunction.model;

import it.uniroma2.sag.kelp.data.example.Example;

/**
 * It is a support vector for kernel methods consisting of an example and the associated weight
 *  
 * @author      Simone Filice
 */
public class SupportVector {

	private float weight;
	
	private Example instance;
	
	
	public SupportVector(float weight, Example instance){
		this.weight=weight;
		this.instance = instance;
	}
	
	public SupportVector(){
		
	}
	
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(Example instance) {
		this.instance = instance;
	}

	/**
	 * @return the weight
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * @return the instance
	 */
	public Example getInstance() {
		return instance;
	}
	
	
	/**
	 * Increments the weight of this support vector
	 * 
	 * @param weightIncrement the value to be added to the weight of this support vector
	 */
	public void incrementWeight(float weightIncrement){
		this.weight+=weightIncrement;
	}
}
