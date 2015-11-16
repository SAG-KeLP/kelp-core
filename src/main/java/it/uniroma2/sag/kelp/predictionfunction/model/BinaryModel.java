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
 * It is the model for a binary method consisting on a hyperplane
 *  
 * @author      Simone Filice
 */
public abstract class BinaryModel implements Model{

	protected float bias;

	
	/**
	 * @return the bias (i.e. the constant term of the hyperplane)
	 */
	public float getBias() {
		return bias;
	}
	
	/**
	 * @param bias the bias (i.e. the constant term of the hyperplane) to set 
	 */
	public void setBias(float bias) {
		this.bias = bias;
	}
	
	/**
	 * Adds an example to the model with a given weight. The model will become a linear
	 * combination of the current model and the example
	 * 
	 * @param weight the weight of the new example to add
	 * @param example the example to add
	 */
	public abstract void addExample(float weight, Example example);
	
	/**
	 * Computes the squared norm of a given example according to the space in which the model
	 * is operating
	 * 
	 * @param example the example whose squared norm is required
	 * @return the squared norm of a given example
	 */
	public abstract float getSquaredNorm(Example example);
	
	/**
	 * Computes the squared norm of the hyperplane this model is based on
	 * 
	 * @return the squared norm of the hyperplane
	 */
	public abstract float getSquaredNorm();
}
