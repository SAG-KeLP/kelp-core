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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * It is a Vectorial representation whose dimensions are identified by Objects
 * 
 * @author Simone Filice
 *
 */
public interface Vector extends Normalizable {

	/**
	 * Returns the dot product between this vector and <code>vector</code>
	 * 
	 * @param vector
	 * @return the dot product
	 */
	public float innerProduct(Vector vector);
	
	/**
	 * Returns the euclidean distance between this vector and <code>vector</code>
	 * 
	 * @param vector
	 * @return the euclidead distance
	 */
	public float euclideanDistance(Vector vector);

	/**
	 * Compute the point-wise product of this vector with the one in
	 * <code>vector</code>.
	 * 
	 * @param vector the vector used for the point-wise product
	 */
	public void pointWiseProduct(Vector vector);

	/**
	 * Add a <code>vector</code> to this vector
	 * 
	 * @param vector
	 *            the vector to be added
	 */
	public void add(Vector vector);

	/**
	 * Add a <code>vector</code> multiplied by <code>coeff</code> to this vector
	 * 
	 * @param coeff
	 *            the <code>vector</code> coefficient
	 * @param vector
	 *            the vector to be added
	 */
	public void add(float coeff, Vector vector);

	/**
	 * Add a <code>vector</code> multiplied by <code>vectorCoeff</code> to this
	 * vector multiplied by
	 * 
	 * @param coeff
	 *            the coefficient of this vector
	 * @param vectorCoeff
	 *            the <code>vector</code> coefficient
	 * @param vector
	 *            the vector to be added
	 */
	public void add(float coeff, float vectorCoeff, Vector vector);

	/**
	 * Returns a vector whose values are all 0. The returned vector has the same
	 * dimensions of this
	 * 
	 * @return a zero vector
	 */
	public Vector getZeroVector();
	
	/**
	 * Returns a copy of this vector. 
	 * 
	 * @return Vector
	 */
	public Vector copyVector();

	/**
	 * Returns a map containing all the non-zero features
	 * 
	 * @return the non zero features
	 */
	@JsonIgnore
	public Map<Object, Number> getActiveFeatures();
	
	/**
	 * Assigns <code>value</value> to the feature identified by <code>featureIdentifier</code> 
	 * 
	 * <p>
	 * NOTE: this method could be not the most efficient to set a feature value. Actual implementations
	 * of the Vector class may provide faster methods using their specific type to identify features (instead of
	 * a generic Object) 
	 * 
	 * 
	 * @param featureIdentifier the identifier of the feature
	 * @param value the value of the feature
	 */
	public void setFeatureValue(Object featureIdentifier, float value);
	
	/**
	 * Returns the value of the feature identified with <code>featureIdentifier</code>
	 * <p>
	 * NOTE: this method could be not the most efficient to get a feature value. Actual implementations
	 * of the Vector class may provide faster methods using their specific type to identify features (instead of
	 * a generic Object) 
	 * 
	 * @return the value of the feature
	 */
	public float getFeatureValue(Object featureIdentifier);

}
