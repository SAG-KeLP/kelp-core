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

package it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * It is the output provided by a classifier operating in a one-vs-all schema. It is suitable for multiclass 
 * classification tasks where every example must be associated to a single class. Basically, given a multiclass 
 * classification task with C classes, starting from the output provided by C different binary classifiers, the final
 * predicted class is the one associated to the highest score (i.e. argmax policy)
 * 
 * @author Simone Filice
 *
 */
public class OneVsAllClassificationOutput implements ClassificationOutput{

	private HashMap<Label, Float> binaryOutputs;
	private Label argmax;
	
	public OneVsAllClassificationOutput(){
		this.binaryOutputs = new HashMap<Label, Float>();
	}
	
	/**
	 * Sets the score associated to a given class
	 * 
	 * @param label the class
	 * @param prediction the score
	 */
	public void addBinaryPrediction(Label label, float prediction){
		if(argmax!=null ){
			Float max = this.binaryOutputs.get(argmax);
			if(max<prediction){
				argmax=label;
			}
		}else{
			argmax=label;
		}
		binaryOutputs.put(label, prediction);
		
	}
	
	@Override
	public Float getScore(Label label) {
		return binaryOutputs.get(label);
	}

	@Override
	public boolean isClassPredicted(Label label) {
		return label.equals(argmax);
	}

	@Override
	public List<Label> getPredictedClasses() {
		ArrayList<Label> predictedLabels = new ArrayList<Label>();
		predictedLabels.add(argmax);
		return predictedLabels;
	}

	@Override
	public List<Label> getAllClasses() {
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.addAll(binaryOutputs.keySet());
		return labels;
	}

}
