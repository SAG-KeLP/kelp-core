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

package it.uniroma2.sag.kelp.data.dataset;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.manipulator.Manipulator;
import it.uniroma2.sag.kelp.data.representation.Vector;

import java.util.List;


/**
 * Dataset is a set of <code>Example</code>s
 * 
 * @author Simone Filice
 *
 */
public interface Dataset {
	
	public void addExample(Example e);
	
	/**
	 * Returns the next <code>n Example</code>s stored in the Dataset or a fewer number 
	 * if <code>n</code> examples are not available.
	 * 
	 * @return the next <code>n Example</code>s
	 */
	public Example getNextExample();
	
	/**
	 * Returns the next <code>Example</code> stored in the Dataset
	 * 
	 * @param n the number of examples to be returned
	 * 
	 * @return the next <code>Example</code> 
	 */
	public List<Example> getNextExamples(int n);

	/**
	 * Returns a boolean declaring whether there are other Examples in the dataset 
	 * 
	 * @return  <code>true</code> if and only if there is at least another Example in the dataset 
	 */
	public boolean hasNextExample();
	
	/**
	 * Reset the reading pointer 
	 */
	public void reset();
	
	/**
	 * Returns the number of positive <code>Example</code>s of a given class
	 * 
	 * @param positiveClass the class whose number of positive <code>Example</code>s are required
	 * @return the number of positive <code>Example</code>s of positiveClass
	 */
	public int getNumberOfPositiveExamples(Label positiveClass);
	
	/**
	 * Returns the number of negative <code>Example</code>s of a given class
	 * 
	 * @param positiveClass the class whose number of negative <code>Example</code>s are required
	 * @return the number of negative <code>Example</code>s of positiveClass
	 */
	public int getNumberOfNegativeExamples(Label positiveClass);
	
	/**
	 * Returns the number of <code>Example</code>s in the dataset
	 * 
	 * @return the number of <code>Example</code>s in the dataset
	 */
	public int getNumberOfExamples();
	
	/**
	 * Returns all the classification labels in the dataset.
	 * 
	 * @return the classification labels in the dataset
	 */
	public List<Label> getClassificationLabels();
	
	/**
	 * Returns all the regression properties in the dataset.
	 * 
	 * @return the regression properties in the dataset
	 */
	public List<Label> getRegressionProperties();
	
		
	/**
	 * Returns an array containing all the stored examples
	 * 
	 * @return the stored examples
	 */
	public List<Example> getExamples();
	
	/**
	 * Returns a zero vector compliant with the representation identifier by <code>representationIdentifier</code> containing all zeros
	 * <p>NOTE: it assumes that there is at least an example in the dataset and that the representation is directly available on the example
	 * using the getRepresentation method (i.e., the example is not an ExamplePair storing the representation in its left or right element)
	 * 
	 * 
	 * @param representationIdentifier the identifier of the representation
	 * @return a zero vector compliant with the representation identifier by <code>representationIdentifier</code> containing all zeros
	 */
	public Vector getZeroVector(String representationIdentifier);
	
	/**
	 * @return a random example
	 */
	public Example getRandExample();
	
	/**
	 * @param k the number of examples to be returned
	 * @return a list containing <code>k</code> random examples
	 */
	public List<Example> getRandExamples(int k);
	
	/**
	 * @return a Dataset containing all the examples in this Dataset in a shuffled order
	 */
	public Dataset getShuffledDataset();
	
	/**
	 * Sets the seed of the random generator used to shuffling examples and getting random examples
	 * 
	 * @param seed the seed of the random generator
	 */
	public void setSeed(long seed);
	
	/**
	 * Manipulates all the examples in the dataset accordingly to the strategies defined by the given <code>manipulators</code>. 
	 * <br><br>NOTE: If more than one manipulator is adopted, they will be applied in the same order of the <code>manipulator</code> in the array  
	 * 
	 * 
	 * @param manipulators the manipulators that must be applied to all the examples in the dataset
	 */
	public void manipulate(Manipulator... manipulators);
		
}
