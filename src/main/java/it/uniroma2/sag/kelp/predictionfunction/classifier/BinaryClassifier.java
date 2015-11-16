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

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryModel;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * It is a generic binary classification function that can be learned with a machine learning algorithm
 * It learns a binary concept. It considers positive examples the ones associated to 
 * a specific <code>Label</code> and treats like negative examples all the others 
 *  
 * @author      Simone Filice
 */
public abstract class BinaryClassifier implements Classifier{

	protected Label positiveClass;
	
	/**
	 * @param positiveClass the label associated to the positive class, i.e. the list must contain a sigle entry
	 */
	@Override
	public void setLabels(List<Label> labels) {
		if(labels.size()!=1){
			throw new IllegalArgumentException("A BinaryClassifier can predict a single Label");
		}
		else{
			this.positiveClass=labels.get(0);
			
		}
	}
	
	/**
	 * @return the label associated to the positive class, i.e. the output list contains a single entry
	 */
	@Override
	@JsonIgnore
	public List<Label> getLabels() {
		
		return Arrays.asList(positiveClass);
	}
	
	/**
	 * @return the label associated to the positive class
	 */
	public Label getLabel(){
		return positiveClass;
	}
	
	/**
	 * @param positiveClass the label associated to the positive class
	 */
	public void setLabel(Label label){
		this.positiveClass = label;
	}
		
	@Override
	public abstract BinaryMarginClassifierOutput predict(Example example);
	
	/**
	 * @return the model
	 */
	@Override
	public abstract BinaryModel getModel();
	
}
