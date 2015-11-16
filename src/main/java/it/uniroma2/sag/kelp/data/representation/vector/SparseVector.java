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

package it.uniroma2.sag.kelp.data.representation.vector;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Sparse Feature Vector. It is based on a Hashmap implementation.
 * 
 * @author Simone Filice
 */
@JsonTypeName("V")
public class SparseVector implements Vector {
	private Logger logger = LoggerFactory.getLogger(SparseVector.class);

	private static final long serialVersionUID = 1856046477863508095L;
	private static final int INITIAL_SIZE = 10000;
	private static final String FEATURE_SEPARATOR = " ";
	private static final String NAME_VALUE_SEPARATOR = ":";
	private static TIntObjectMap<String> fromIntToWord = new TIntObjectHashMap<String>(
			INITIAL_SIZE);
	private static TObjectIntMap<String> fromWordToInt = new TObjectIntHashMap<String>(
			INITIAL_SIZE);
	private static int wordCounter = Integer.MIN_VALUE;

	@JsonIgnore
	private TIntFloatMap vector;

	public SparseVector() {
		this.vector = new TIntFloatHashMap();
	}

	@Override
	public void normalize() {
		float norm = (float) Math.sqrt(this.getSquaredNorm());
		if (norm == 0) {
			return;// TODO: verificare cosa fare
		}
		for (TIntFloatIterator it = vector.iterator(); it.hasNext();) {
			it.advance();
			it.setValue(it.value() / norm);
		}
	}

	@Override
	public void setDataFromText(String representationDescription)
			throws IOException {

		String[] feats = representationDescription.trim().split(
				FEATURE_SEPARATOR);
		if (feats[0].equals("")) {
			return;
		}
		String dimTmp = null;
		String valueTmp = null;
		float value;
		for (String feature : feats) {
			int separatorIndex = feature.lastIndexOf(NAME_VALUE_SEPARATOR);
			if (separatorIndex <= 0) {
				throw new IOException(
						"Parsing error in SparseVector.init function: formatting error in the feat-value pair "
								+ feature);
			}
			dimTmp = feature.substring(0, separatorIndex);
			valueTmp = feature.substring(separatorIndex + 1);
			Float val = Float.parseFloat(valueTmp);
			if (val.isNaN()) {
				logger.warn("NaN value in representation: "
						+ representationDescription);
			}
			value = val.floatValue();

			this.setFeatureValue(dimTmp, value);
		}
	}

	@Override
	public String toString() {
		StringBuilder description = new StringBuilder();
		// accessing keys/values through an iterator:
		for (TIntFloatIterator it = this.vector.iterator(); it.hasNext();) {
			it.advance();
			String name = fromIntToWord.get(it.key());
			description.append(name + NAME_VALUE_SEPARATOR
					+ Float.toString(it.value()) + FEATURE_SEPARATOR);
		}
		return description.toString();
	}

	/**
	 * Returns the feature value of the <code>featureIndex</code>-th element
	 * 
	 * @param featureIndex
	 *            the index of the feature whose value must be returned
	 * @return the value of the feature
	 */
	private float getFeatureValue(int featureIndex) {
		return this.vector.get(featureIndex);
	}

	/**
	 * @return the vector
	 */
	@JsonIgnore
	public TIntFloatMap getVector() {
		return vector;
	}

	/**
	 * @param vector
	 *            the vector to set
	 */
	@JsonIgnore
	public void setVector(TIntFloatMap vector) {
		this.vector = vector;
	}

	@Override
	public float innerProduct(Vector vector) {
		if (vector instanceof SparseVector) {
			float sum = 0;
			SparseVector sparse = (SparseVector) vector;
			TIntFloatMap shortest;
			TIntFloatMap longest;
			if (this.vector.size() < sparse.vector.size()) {
				shortest = this.vector;
				longest = sparse.vector;
			} else {
				shortest = sparse.vector;
				longest = this.vector;
			}
			for (TIntFloatIterator it = shortest.iterator(); it.hasNext();) {
				it.advance();
				float shortestValue = it.value();
				float longestValue = longest.get(it.key());
				sum += shortestValue * longestValue;
			}
			return sum;
		}
		throw new IllegalArgumentException(
				"Expected a SparseVector to performe the innerProduct");
	}

	@Override
	public void scale(float coeff) {
		for (TIntFloatIterator it = this.vector.iterator(); it.hasNext();) {
			it.advance();
			it.setValue(it.value() * coeff);
		}
	}

	@Override
	public void add(Vector vector) {
		if (vector instanceof SparseVector) {
			SparseVector that = (SparseVector) vector;
			for (TIntFloatIterator it = that.vector.iterator(); it.hasNext();) {
				it.advance();
				float thisValue = this.getFeatureValue(it.key());
				this.vector.put(it.key(), thisValue + it.value());
			}

		} else {
			throw new IllegalArgumentException(
					"Expected a SparseVector to performe add operation");
		}
	}

	@Override
	public void add(float coeff, Vector vector) {
		if (vector instanceof SparseVector) {
			SparseVector that = (SparseVector) vector;
			for (TIntFloatIterator it = that.vector.iterator(); it.hasNext();) {
				it.advance();
				float thisValue = this.getFeatureValue(it.key());
				this.vector.put(it.key(), thisValue + coeff * it.value());
			}
		} else {
			throw new IllegalArgumentException(
					"Expected a SparseVector to performe add operation");
		}
	}

	@Override
	public void add(float coeff, float vectorCoeff, Vector vector) {
		this.scale(coeff);
		this.add(vectorCoeff, vector);
		// if (vector instanceof SparseVector) {
		// SparseVector that = (SparseVector) vector;
		// for (TIntFloatIterator it = that.vector.iterator(); it.hasNext();) {
		// it.advance();
		// float thisValue = this.getFeatureValue(it.key());
		// this.vector.put(it.key(),
		// thisValue * coeff + vectorCoeff * it.value());
		// }
		// } else {
		// throw new IllegalArgumentException(
		// "Expected a SparseVector to performe add operation");
		// }
	}

	@JsonIgnore
	@Override
	public SparseVector getZeroVector() {
		return new SparseVector();
	}

	@Override
	public String getTextFromData() {
		return this.toString();
	}

	@Override
	@JsonIgnore
	public float getSquaredNorm() {
		float norm = 0;
		float[] values = vector.values();
		for (float value : values) {
			norm += value * value;
		}
		return norm;
	}

	@Override
	public Map<Object, Number> getActiveFeatures() {
		HashMap<Object, Number> res = new HashMap<Object, Number>();

		for (TIntFloatIterator it = this.vector.iterator(); it.hasNext();) {
			it.advance();

			res.put(fromIntToWord.get(it.key()), it.value());
		}
		return res;
	}

	
	/**
	 * Merge this vector with <code>vector</code> (it is like a vector concatenation)
	 * If V1 is the space where this vector lies and V2 is the space where <code>vector</code> lies, 
	 * then the resulting vector lies in V1xV2
	 * 
	 * <p>
	 * NOTE: this is not a sum because even if some feature names are shared between the two vectors,
	 * those will be considered different dimensions (a prefix is added to all feature names of <code>vector</code>)
	 * 
	 * @param vector the input vector to be merged with this
	 * @param coefficient a scaling factor for <code>vector</code> 
	 * @param newDimensionPrefix the prefix to be added to all the feature names of <code>vector</code>
	 * during the merging process (<code>vector</code> is not modified)
	 */
	public void merge(Vector vector, float coefficient,
			String newDimensionPrefix) {
		Map<Object, Number> activeFeats = vector.getActiveFeatures();
		for (Entry<Object, Number> entry : activeFeats.entrySet()) {
			String dimension = newDimensionPrefix + "_" + entry.getKey().toString();
			
			float value = coefficient * entry.getValue().floatValue();

			this.setFeatureValue(dimension, value);
		}
	}

	@Override
	public void pointWiseProduct(Vector vector) {
		if (vector instanceof SparseVector) {
			SparseVector that = (SparseVector) vector;
			// A list of integer hash to be removed by the map after the pointwise product operation
			ArrayList<Integer> remove = new ArrayList<Integer>();
			for (TIntFloatIterator it = this.vector.iterator(); it.hasNext();) {
				it.advance();
				float myValue = it.value();
				float itsValue = that.getVector().get(it.key());
				float newValue = myValue*itsValue;
				// if the new value is zero and this vector contained that element
				// then add the current key to remove
				if (newValue == 0.0f && this.vector.containsKey(it.key())) {
					remove.add(it.key());
				} else {
					// if the new value is different from zero
					// then add it to the map
					if (newValue!=0.0f)
						this.vector.put(it.key(), newValue);
				}
			}
			// remove zero elements
			for (Integer i : remove)
				this.vector.remove(i);

		} else {
			throw new IllegalArgumentException(
					"Expected a SparseVector to performe add operation");
		}
	}

	@Override
	public SparseVector copyVector() {
		SparseVector copy = new SparseVector();
		
		try {
			copy.setDataFromText(this.getTextFromData());
		} catch (Exception e) {
			e.printStackTrace();
			return this.getZeroVector();
		}
		
		return copy;
	}

	/**
	 * Sets the value of a feature
	 * 
	 * @param featureName the name of the feature
	 * @param value the value of the feature
	 */
	public void setFeatureValue(String featureName, float value){
		int index = fromWordToInt.get(featureName);

		logger.debug(featureName);
		logger.debug(Integer.toString(index));

		if (index == 0) {
			fromWordToInt.put(featureName, wordCounter);
			fromIntToWord.put(wordCounter, featureName);
			this.vector.put(wordCounter, value);
			wordCounter++;
			if (wordCounter == 0) {
				wordCounter++;
			}
		} else {
			this.vector.put(index, value);
		}
	}
	
	/**
	 * Returns the value associated to a feature
	 * 
	 * @param featureName the identifier of the feature
	 * @return the feature value
	 */
	public float getFeatureValue(String featureName){
		int index = fromWordToInt.get(featureName);
		return this.getFeatureValue(index);
	}
	
	/**
	 * Increments the value associated to a feature
	 * 
	 * @param featureName the identifier of the feature
	 * @param valueIncrement the increment
	 */
	public void incrementFeature(String featureName, float valueIncrement){
		int index = fromWordToInt.get(featureName);
		if(index==0){
			fromWordToInt.put(featureName, wordCounter);
			fromIntToWord.put(wordCounter, featureName);
			this.vector.put(wordCounter, valueIncrement);
			wordCounter++;
			if (wordCounter == 0) {
				wordCounter++;
			}
		}else{
			float newValue = this.getFeatureValue(index) + valueIncrement;
			this.vector.put(index, newValue);
		}
	}

	@Override
	public void setFeatureValue(Object featureIdentifier, float value) {
		if(!(featureIdentifier instanceof String)){
			throw new IllegalArgumentException("The argument featureIdentifier must be a String");
		}
		this.setFeatureValue((String)featureIdentifier, value);
	}

	@Override
	public float getFeatureValue(Object featureIdentifier) {
		if(!(featureIdentifier instanceof String)){
			throw new IllegalArgumentException("The argument featureIdentifier must be a String");
		}
		return this.getFeatureValue((String)featureIdentifier);
	}

	@Override
	public float euclideanDistance(Vector vector) {
		float normA = vector.getSquaredNorm();
		float normB = this.getSquaredNorm();

		return (float) Math.sqrt(normA + normB - 2 * vector.innerProduct(this));
	}

	@Override
	public boolean isCompatible(Representation rep) {
		if (!( rep instanceof SparseVector)){
			logger.error("incompatible representations: " + this.getClass().getSimpleName() + " vs " + rep.getClass().getSimpleName());
			return false;
		}
		return true;
	}
}
