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

package it.uniroma2.sag.kelp.predictionfunction.classifier;

import java.util.List;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;

/**
 * It is a generic output provided by a classifier
 * 
 * @author Simone Filice
 *
 */
public interface ClassificationOutput extends Prediction{

	/**
	 * Returns a boolean identifying the predicted membership to a specified class
	 * 
	 * @param label the class whose membership is required
	 * @return a boolean identifying the predicted membership to a specified class
	 */
	public boolean isClassPredicted(Label label);
	
	/**
	 * Returns all the classes that the classifier has predicted
	 * 
	 * @return the predicted classes 
	 */
	public List<Label> getPredictedClasses();
	
	/**
	 * Returns all the classes involved in the classification process (both predicted and not)
	 * 
	 * @return all the classes involved in the classification process (both predicted and not)
	 */
	public List<Label> getAllClasses();
	
}
