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

package it.uniroma2.sag.kelp.predictionfunction;

import java.util.List;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;


/**
 * It is a generic prediction function that can be learned with a machine learning algorithm 
 *  
 * @author      Simone Filice
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(PredictionFunctionTypeResolver.class)
public interface PredictionFunction {

	public Prediction predict(Example example);
	
	/**
	 * Resets all the predictor parameters to the default state. All the
	 * learned process is forgotten 
	 */
	public void reset();
	
	/**
	 * Sets the labels representing the concept to be predicted. 
	 * 
	 * @param labels the labels representing the concept to be predicted
	 */
	public void setLabels(List<Label> labels);	
	
	/**
	 * Returns the labels representing the concept to be predicted.
	 * 
	 * @return the labels representing the concept to be predicted
	 */
	public List<Label> getLabels();	
	
	/**
	 * Returns the model
	 * 
	 * @return model the model
	 */
	public Model getModel();
	
	/**
	 * Sets the model
	 * 
	 * @param model the model to set
	 */
	public void setModel(Model model);
	
}
