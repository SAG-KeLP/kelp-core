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

package it.uniroma2.sag.kelp.data.example;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.NumericLabel;
import it.uniroma2.sag.kelp.data.manipulator.Manipulator;
import it.uniroma2.sag.kelp.data.representation.Representation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * It is the instance of an example in the Machine Learning context. An Example
 * consists of one or more representation of the same object and zero
 * (unsupervised learning) or more classificationLabels (supervised learning.
 * Single or multitask learning)
 * 
 * @author Simone Filice
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(ExampleTypeResolver.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "ID")
public abstract class Example implements Serializable {
	private static final long serialVersionUID = 755613612497626480L;
	private static long created = 0;

	private HashSet<Label> classificationLabels;
	private ArrayList<NumericLabel> regressionValues;

	@JsonIgnore
	private HashMap<Label, Integer> propertyToIndexMapping;
	private long exampleId;

	// private HashMap<Integer, Integer> cacheIndex;

	/**
	 * Initializes an empty example (0 classificationLabels and 0 regression
	 * values)
	 */
	public Example() {
		this.classificationLabels = new HashSet<Label>();
		this.exampleId = this.generateUniqueIdentifier();
		this.regressionValues = new ArrayList<NumericLabel>();
		this.propertyToIndexMapping = new HashMap<Label, Integer>();
		// this.cacheIndex = new HashMap<Integer, Integer>();
	}

	/**
	 * Generates a unique identifier to be assigned to a new example
	 * 
	 * @return the ID
	 */
	private synchronized long generateUniqueIdentifier() {
		long id = created;
		created++;
		return id;
	}

	/**
	 * Returns a unique identifier of the example. The Unique identifier is a
	 * number univocally and statically assigned to the example during its
	 * initialization.
	 * <p>
	 * Note: initializing two identical examples will make them having two
	 * different IDs
	 * 
	 * @return the example ID
	 */
	@JsonIgnore
	public long getId() {
		return this.exampleId;
	}

	/**
	 * Sets the example classificationLabels
	 * 
	 * @param classificationLabels
	 *            of which this instance is a positive example
	 */
	@JsonIgnore
	public void setLabels(Label[] labels) {
		this.classificationLabels.clear();
		this.regressionValues.clear();
		for (Label label : labels) {
			this.addLabel(label);
		}

	}

	/**
	 * Returns the classification classificationLabels of this example
	 * 
	 * @return the classification classificationLabels of this example
	 */
	@JsonIgnore
	public Label[] getLabels() {
		return classificationLabels.toArray(new Label[classificationLabels
				.size()]);
	}

	/**
	 * Returns the classificationLabels of this example
	 * 
	 * @return the classificationLabels of this example
	 */
	@JsonIgnore
	public NumericLabel[] getRegressionLabels() {
		return regressionValues.toArray(new NumericLabel[regressionValues
				.size()]);
	}

	/**
	 * Adds a label to the example
	 * 
	 * @param label
	 */
	public void addLabel(Label label) {
		if (label instanceof NumericLabel) {
			NumericLabel regressionLabel = (NumericLabel) label;
			Integer index = this.propertyToIndexMapping.get(regressionLabel
					.getProperty());
			if (index == null) {
				int nextIndex = this.regressionValues.size();
				this.regressionValues.add(regressionLabel);
				this.propertyToIndexMapping.put(regressionLabel.getProperty(),
						nextIndex);
			} else {
				this.regressionValues.set(index, regressionLabel);
			}
		} else {
			classificationLabels.add(label);
		}
	}

	/**
	 * Returns the number of classification classificationLabels whose this
	 * instance is a positive example
	 * 
	 * @return the number of classificationLabels whose this instance is a
	 *         positive example
	 */
	@JsonIgnore
	public int getNumberOfClassificationLabels() {
		return classificationLabels.size();
	}

	/**
	 * Returns the number of regression classificationLabels
	 * 
	 * @return the number of regression classificationLabels
	 */
	@JsonIgnore
	public int getNumberOfRegressionLabels() {
		return regressionValues.size();
	}

	/**
	 * Asserts whether this is a positive example for the input label or not
	 * 
	 * @param label
	 * @return <code>true</code> if this is a positive example of the class
	 *         <code>label</code>, false otherwise
	 */
	public boolean isExampleOf(Label label) {
		return classificationLabels.contains(label);
	}

	/**
	 * Returns the numeric value associated to a label
	 * 
	 * @param propertyName
	 *            the regression label identifier
	 * @return the numeric value associated to the label identified by
	 *         <code>propertyName</code> or null if that label is not a
	 *         NumericLabel or this example does not contain that label
	 */
	@JsonIgnore
	public Float getRegressionValue(Label property) {
		Integer index = this.propertyToIndexMapping.get(property);
		if (index != null) {
			return this.regressionValues.get(index).getValue();
		}
		return null;
	}

	/**
	 * @return the classificationLabels
	 */
	public HashSet<Label> getClassificationLabels() {
		return classificationLabels;
	}

	/**
	 * @param classificationLabels
	 *            the classificationLabels to set
	 */
	public void setClassificationLabels(HashSet<Label> labels) {
		this.classificationLabels = labels;
	}

	/**
	 * @return the regressionValues
	 */
	public ArrayList<NumericLabel> getRegressionValues() {
		return regressionValues;
	}

	/**
	 * @param regressionValues
	 *            the regressionValues to set
	 */
	public void setRegressionValues(ArrayList<NumericLabel> regressionValues) {
		this.regressionValues = regressionValues;
		this.propertyToIndexMapping.clear();
		for (int i = 0; i < regressionValues.size(); i++) {
			this.propertyToIndexMapping.put(regressionValues.get(i)
					.getProperty(), i);
		}
	}

	@Override
	public boolean equals(Object example) {
		if (example == null) {
			return false;
		}
		if (this == example) {
			return true;
		}
		if (example instanceof Example) {
			Example that = (Example) example;
			return (this.getLabels().equals(that.getLabels()))
					&& (this.getRegressionLabels().equals(that
							.getRegressionLabels()));
		}
		return true;
	}

	/**
	 * @return A copu of the current object with a different identifier, i.e.
	 *         the <code>exampleId</code>
	 */
	public Example duplicate() {
		Example res = SerializationUtils.clone(this);
		res.exampleId = this.generateUniqueIdentifier();
		return res;
	}

	protected String getTextualLabelPart() {
		String ret = "";
		for (Label label : this.getLabels()) {
			ret += label.toString() + ExampleFactory.LABEL_SEPARATOR;
		}
		for (NumericLabel label : this.getRegressionLabels()) {
			ret += label.toString() + ExampleFactory.LABEL_SEPARATOR;
		}
		return ret;
	}

	/**
	 * Sets the example representations
	 * 
	 * @param representations
	 *            to associate to this example
	 */
	public abstract void setRepresentations(
			HashMap<String, Representation> representations);

	/**
	 * Returns the example representations
	 * 
	 * @return the representations of this example
	 */
	public abstract Map<String, Representation> getRepresentations();

	/**
	 * Adds a representation to this example
	 * 
	 * @param representationName
	 *            the identifier of the representation to be added
	 * @param representation
	 *            the representation to be added
	 */
	public abstract void addRepresentation(String representationName,
			Representation representation);

	/**
	 * Returns the number of representations in which this example is modeled
	 * 
	 * @return the number of representations
	 */
	@JsonIgnore
	public abstract int getNumberOfRepresentations();

	/**
	 * Returns the representation corresponding to
	 * <code>representationName</code>
	 * 
	 * @param representationName
	 *            it is a representation identifier
	 * @return the representation corresponding to
	 *         <code>representationName</code>
	 */
	public abstract Representation getRepresentation(String representationName);

	// public abstract Vector getZeroVector(String representationIdentifier);

	/**
	 * Manipulate this example accordingly to the provided
	 * <code>manipulator</code>
	 * 
	 * @param manipulator
	 *            the manipulator
	 */
	public abstract void manipulate(Manipulator manipulator);
	
	/**
	 * Evaluates whether an example is compatible with this one, i.e., they have the
	 * same structure in terms of Example type and representations 
	 * 
	 * @param example the example to be compared with this one
	 * @return whether this example is compatible with the input one
	 */
	public abstract boolean isCompatible(Example example);

}
