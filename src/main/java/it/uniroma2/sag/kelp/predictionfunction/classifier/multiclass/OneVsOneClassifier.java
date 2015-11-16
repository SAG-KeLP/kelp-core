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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryModel;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;
import it.uniroma2.sag.kelp.predictionfunction.model.MulticlassModel;


/**
 * 
 * 
 * @author Giuseppe Castellucci
 *
 */
@JsonTypeName("oneVsOneClassifier")
public class OneVsOneClassifier implements Classifier{
	private Logger logger = LoggerFactory.getLogger(OneVsOneClassifier.class);
	
	private Classifier [] binaryClassifiers;
	private Label[] negativeLabelsForClassifier;
	private List<Label> labels;
	private MulticlassModel model;
	
	public OneVsOneClassifier(){
		this.model = new MulticlassModel();
	}
	
	/**
	 * Return the negative labels associated to each classifier
	 * 
	 * @return an array in which entry i is the negative label of classifier i
	 */
	public Label[] getNegativeLabelsForClassifier() {
		return negativeLabelsForClassifier;
	}

	/**
	 * Set the negative label classifier array
	 * 
	 * @param negativeLabelsForClassifier
	 */
	public void setNegativeLabelsForClassifier(Label[] negativeLabelsForClassifier) {
		this.negativeLabelsForClassifier = negativeLabelsForClassifier;
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
	public OneVsOneClassificationOutput predict(Example example) {
		OneVsOneClassificationOutput finalOutput = new OneVsOneClassificationOutput();
		logger.debug("----------------");
		for (int i=0; i<binaryClassifiers.length;++i) {
			Classifier classifier = binaryClassifiers[i];
			ClassificationOutput binaryPrediction = classifier.predict(example);
			Label label = binaryPrediction.getAllClasses().get(0);
			Float prediction = binaryPrediction.getScore(label);
			logger.debug(label.toString() + " vs " + negativeLabelsForClassifier[i].toString() + ": " + prediction);
			Label votedLabel = null;
			if (prediction >= 0.0f) {
				votedLabel = label;
			} else {
				votedLabel = negativeLabelsForClassifier[i];
			}
			
			finalOutput.addVotedPrediction(votedLabel, prediction);
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
		this.model = (MulticlassModel) model;
		
	}
}
