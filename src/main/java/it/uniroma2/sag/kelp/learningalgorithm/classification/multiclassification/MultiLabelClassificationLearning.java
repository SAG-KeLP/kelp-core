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

package it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.MetaLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.classification.ClassificationLearningAlgorithm;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.MultiLabelClassifier;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is a meta algorithm that operates applying a multi label learning strategy over the base 
 * learning algorithm which is intended to be a binary learner. A multi label classification task
 * is a multiclass problem in which each instance can belongs to zero, one or multiple classes.
 * The multi label strategy will learn N different classifiers, where N is the number
 * of classes involved in the dataset.
 * In this strategy each classifier is learned by considering in turn the examples of a single 
 * class as positives, while all the other examples are considered as negative.
 * <p>
 * 
 * NOTE: the base learning algorithm must provide a duplicate method which properly works
 * 
 * @author      Simone Filice
 */
@JsonTypeName("multiLabel")
public class MultiLabelClassificationLearning implements ClassificationLearningAlgorithm, MetaLearningAlgorithm{
	private Logger logger = LoggerFactory.getLogger(MultiLabelClassificationLearning.class);

	private LearningAlgorithm baseAlgorithm;
	
	@JsonIgnore
	private LearningAlgorithm[] algorithms;
	
	@JsonIgnore
	private MultiLabelClassifier classifier;
	
	private List<Label> labels;
	
	public MultiLabelClassificationLearning(){
		this.classifier = new MultiLabelClassifier();
	}
	
	/**
	 * Set the labels associated to this multi-classifier.
	 */
	@Override
	public void setLabels(List<Label> labels){
		this.labels=labels;
		this.classifier.setLabels(labels);
	}
		
	/**
	 * Returns the labels to be learned
	 * 
	 * @return the labels to be learned
	 */
	public List<Label> getLabels(){
		return labels;
	}
		
	private void initialize() {
		algorithms = new LearningAlgorithm[labels.size()];		
		Classifier[] binaryClassifier = new Classifier[labels.size()];
		for(int i=0; i<labels.size(); i++){
			try {
				algorithms[i] = this.baseAlgorithm.duplicate();
				algorithms[i].setLabels(Arrays.asList(labels.get(i)));
				binaryClassifier[i] = (Classifier)algorithms[i].getPredictionFunction();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}
		classifier.setBinaryClassifiers(binaryClassifier);	
		
	}
	
	/**
	 * This method will cause the meta-learning algorithm to learn 
	 * N classifiers, where N is the number of classes in the dataset.
	 */
	@Override
	public void learn(Dataset dataset) {
		
		if(this.algorithms==null){
			this.initialize();
		}		
		for(int i=0; i<labels.size(); i++){
			try {				
				algorithms[i].learn(dataset);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * This method will cause the reset of all the base algorithms
	 */
	@Override
	public void reset() {
		if(this.algorithms!=null){
			for(LearningAlgorithm algo : this.algorithms){
				algo.reset();
			}
		}	
		
	}

	/**
	 * This method returns the learned PredictionFunction. It is of type 
	 * MultiLabelClassifier.
	 */
	@Override
	public MultiLabelClassifier getPredictionFunction() {
		return this.classifier;
	}

	/**
	 * This method will set the type of the base algorithms to be learned.
	 */
	@Override
	public void setBaseAlgorithm(LearningAlgorithm baseAlgorithm) {
		this.baseAlgorithm=baseAlgorithm;		
	}

	/**
	 * This method will return the base algorithm.
	 */
	@Override
	public LearningAlgorithm getBaseAlgorithm() {
		return baseAlgorithm;
	}

	/**
	 * This method will duplicate the current Learning algorithm
	 */
	@Override
	public MultiLabelClassificationLearning duplicate(){
		MultiLabelClassificationLearning copy = new MultiLabelClassificationLearning();
		copy.setBaseAlgorithm(this.baseAlgorithm);		
		return copy;
	}
	
}
