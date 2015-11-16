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

import it.uniroma2.sag.kelp.data.label.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * It is the output provided by binary margin classifiers like the ones trained with SVM or perceptron based 
 * learning algorithms. The output is basically a real number whose sign identifies the membership to the positive class
 * and whose absolute value expresses the distance from the classification hyperplane 
 * 
 * @author Simone Filice
 *
 */
public class BinaryMarginClassifierOutput implements ClassificationOutput{

	private Label positiveClass;
	private float prediction;
	
	public BinaryMarginClassifierOutput(Label positiveClass, float prediction){
		this.positiveClass=positiveClass;
		this.prediction=prediction;
	}

	
	@Override
	public Float getScore(Label label) {
		if(positiveClass.equals(label)){
			return prediction;
		}
		//TODO: forse sarebbe il caso di restituire -prediction?
		return null;
	}

	@Override
	public boolean isClassPredicted(Label label) {
		if(positiveClass.equals(label)){
			if(prediction > 0){
				return true;
			}
			return false;
		}
		//TODO: che dovrebbe ritornare?
		return false;
	}

	@Override
	public List<Label> getPredictedClasses() {
		List<Label> predictedClasses = new ArrayList<Label>();
		if(prediction>0){
			predictedClasses.add(positiveClass);
		}else{
			//TODO: ?
		}
		return predictedClasses;
	}

	@Override
	public List<Label> getAllClasses() {
		List<Label> labels = new ArrayList<Label>();
		labels.add(positiveClass);
		
		return labels;
	}

}
