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

package it.uniroma2.sag.kelp.data.representation;

/**
 * It is a representation that has a norm
 *  
 * @author Simone Filice
 *
 */
public interface Normalizable extends Representation{
	
	/**
	 * Returns the squared norm of this vector
	 * 
	 * @return the squared norm
	 */
	public float getSquaredNorm();
	
	/**
	 * Scales the representation in order to have a unit norm in the explicit feature
	 * space
	 * 
	 */
	public void normalize();
	
	
	/**
	 * Multiplies each element of this representation by <code>coeff</code>
	 * 
	 * @param factor
	 *            the scaling factor
	 */
	public void scale(float factor);

}
