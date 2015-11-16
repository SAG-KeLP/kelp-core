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

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryModel;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;
import it.uniroma2.sag.kelp.predictionfunction.model.MulticlassModel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is a multiclass classifier operating in a one-vs-all schema. It is suitable for multiclass 
 * classification tasks where every example must be associated to a single class. Basically, given a multiclass 
 * classification task with C classes, starting from the output provided by C different binary classifiers (on which
 * this classifier relies on), the final predicted class is the one associated to the highest score (i.e. argmax policy)
 * 
 * @author Simone Filice
 *
 */
@JsonTypeName("oneVsAllClassifier")
public class OneVsAllClassifier implements Classifier{

	private Classifier [] binaryClassifiers;
	private List<Label> labels;
	private MulticlassModel model;
	
	
	public OneVsAllClassifier(){
		this.model = new MulticlassModel();
	}
	
	/**
	 * @return the binaryClassifiers
	 */
	public Classifier[] getBinaryClassifiers() {
		return binaryClassifiers;
	}



	/**
	 * @param binaryClassifiers the binaryClassifiers to set
	 */
	public void setBinaryClassifiers(Classifier[] binaryClassifiers) {
		this.binaryClassifiers = binaryClassifiers;
		
		List<BinaryModel> models = new ArrayList<BinaryModel>();
		for(Classifier classifier : binaryClassifiers){
			models.add((BinaryModel) classifier.getModel());
		}
		this.model.setModels(models);
	}



	@Override
	public OneVsAllClassificationOutput predict(Example example) {
		OneVsAllClassificationOutput finalOutput = new OneVsAllClassificationOutput();
		for(Classifier classifier : binaryClassifiers){
			ClassificationOutput binaryPrediction = classifier.predict(example);
			Label label = binaryPrediction.getAllClasses().get(0);
			Float prediction = binaryPrediction.getScore(label);
			finalOutput.addBinaryPrediction(label, prediction);
		}
		return finalOutput;
	}



	@Override
	public void reset() {
		for(Classifier classifier : binaryClassifiers){
			classifier.reset();
		}
		
	}



	@Override
	public void setLabels(List<Label> labels){
		this.labels=labels;		
	}



	@Override
	public List<Label> getLabels() {
		return labels;
	}



	@Override
	public MulticlassModel getModel() {
		return model;
	}



	@Override
	public void setModel(Model model) {
		this.model = (MulticlassModel)model;
		
	}

}
