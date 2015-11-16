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

import java.util.Arrays;
import java.util.List;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.MetaLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.classification.ClassificationLearningAlgorithm;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsAllClassifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * It is a meta algorithm that operates applying a One-Vs-All strategy over the base 
 * learning algorithm which is intended to be a binary learner. 
 * The One-Vs-All strategy will learn N different classifiers, where N is the number
 * of classes involved in the dataset.
 * In this strategy each classifier is learned by considering in turn the examples of a single 
 * class as positives, while all the other examples are considered as negative.
 * <p>
 * 
 * NOTE: the base learning algorithm must provide a duplicate method which properly works
 * 
 * @author      Giuseppe Castellucci, Simone Filice
 */
@JsonTypeName("oneVsAll")
public class OneVsAllLearning implements ClassificationLearningAlgorithm, MetaLearningAlgorithm{
	private Logger logger = LoggerFactory.getLogger(OneVsAllLearning.class);

	private LearningAlgorithm baseAlgorithm;
	
	@JsonIgnore
	private LearningAlgorithm[] algorithms;
	
	@JsonIgnore
	private OneVsAllClassifier classifier;
	
	private List<Label> labels;
	
	public OneVsAllLearning(){
		this.classifier = new OneVsAllClassifier();
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
	 * Returns the labels to be learned applying a one-vs-all strategy
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
	 * OneVsOneClassifier.
	 */
	@Override
	public OneVsAllClassifier getPredictionFunction() {
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
	public OneVsAllLearning duplicate(){
		OneVsAllLearning copy = new OneVsAllLearning();
		copy.setBaseAlgorithm(this.baseAlgorithm);		
		return copy;
	}
	
}
