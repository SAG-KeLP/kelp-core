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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;

/**
 * It is the output provided by a  multi label classifier. It is suitable for multi label 
 * classification tasks where every instance can be associated to zero, one or multiple class. Basically, given a multi label 
 * classification task with C classes, starting from the output provided by C different binary classifiers, the final
 * predicted classes are the ones associated to a positive score
 * 
 * @author Simone Filice
 *
 */
public class MultiLabelClassificationOutput implements ClassificationOutput{

	private HashMap<Label, Float> binaryOutputs;
	private List<Label> predictedClasses;

	public MultiLabelClassificationOutput(){
		this.binaryOutputs = new HashMap<Label, Float>();
	}

	/**
	 * Sets the score associated to a given class
	 * 
	 * @param label the class
	 * @param prediction the score
	 */
	public void addBinaryPrediction(Label label, float prediction){

		if(prediction>0){
			predictedClasses.add(label);
		}
		binaryOutputs.put(label, prediction);

	}

	@Override
	public Float getScore(Label label) {
		return binaryOutputs.get(label);
	}

	@Override
	public boolean isClassPredicted(Label label) {
		Float pred = this.binaryOutputs.get(label);
		if(pred!=null){
			return pred>0;
		}
		return false;
	}

	@Override
	public List<Label> getPredictedClasses() {
		return predictedClasses;
	}

	@Override
	public List<Label> getAllClasses() {
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.addAll(binaryOutputs.keySet());
		return labels;
	}
}
