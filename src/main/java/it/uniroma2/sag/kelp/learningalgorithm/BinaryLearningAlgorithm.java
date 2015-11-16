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

import it.uniroma2.sag.kelp.data.label.Label;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * It is a learning algorithm that has to learn a concept associated to a single label.
 * Examples of binaryLearningAlgorithm are:
 * <ul>
 * <li>
 * one-classSVM
 * </li>
 * <li>
 * a binary classification learning algorithm (all the examples not associated to the label
 *  to be learned are considered negative examples)
 * </li>
 * <li>
 * a uni-variate regression algorithm, where the property to be learn is a single label
 * </li>
 * </ul>
 * 
 * @author Simone Filice
 *
 */
public interface BinaryLearningAlgorithm extends LearningAlgorithm{

	/**
	 * @return the label associated to the positive class
	 */
	public Label getLabel();

	/**
	 * @param positiveClass the label associated to the positive class
	 */
	public void setLabel(Label label);


	/**
	 * @param positiveClass the label associated to the positive class, i.e. the list must contain a single entry
	 */
	@Override
	public void setLabels(List<Label> labels);

	/**
	 * @return the label associated to the positive class, i.e. the output list contains a single entry
	 */
	@Override
	@JsonIgnore
	public List<Label> getLabels();
	
}
