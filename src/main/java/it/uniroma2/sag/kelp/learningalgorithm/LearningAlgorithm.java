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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.PredictionFunction;

/**
 * It is a generic Machine Learning algorithm
 * 
 * @author      Simone Filice
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "algorithm")
@JsonTypeIdResolver(LearningAlgorithmTypeResolver.class)
public interface LearningAlgorithm{

//	private static final ObjectSerializer serializer = new JacksonSerializerWrapper();
	
	/**
	 * It starts the training process exploiting the provided <code>dataset</code>
	 * 
	 * @param dataset the training data
	 */
	public void learn(Dataset dataset);
	
	/**
	 * Sets the labels representing the concept to be learned. 
	 * 
	 * @param labels the labels representing the concept to be learned
	 */
	public void setLabels(List<Label> labels);	
	
	/**
	 * Returns the labels representing the concept to be learned.
	 * 
	 * @return the labels representing the concept to be learned
	 */
	public List<Label> getLabels();	

	/**
	 * Creates a new instance of the LearningAlgorithm initialized with the same parameters
	 * of the learningAlgorithm to be duplicated. A shallow copy of the learning parameters
	 * is performed, while the state parameters are initialized to their default value.
	 * The labels are not initialized neither copied 
	 * 
	 */
	public LearningAlgorithm duplicate();
	
	/**
	 * Resets all the learning process, returning to the default state. All the
	 * learned process is forgotten 
	 */
	public void reset();
	
	/**
	 * Returns the predictionFunction learned during the training process
	 * 
	 * @return the predictionFunction learned during the training process
	 */
	@JsonIgnore
	public PredictionFunction getPredictionFunction();
}
