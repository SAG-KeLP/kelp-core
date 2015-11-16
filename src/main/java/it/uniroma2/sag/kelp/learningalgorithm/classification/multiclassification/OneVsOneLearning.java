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
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.MetaLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.classification.ClassificationLearningAlgorithm;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsOneClassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is a meta algorithm that operates by applying a One-Vs-One strategy over the base 
 * learning algorithm which is intended to be a binary learner.
 * This meta-algorithms will learn N*(N-1)/2 classifiers, by comparing each class with all the others
 * separately. The resulting classifier applies a a voting stratefy to perform the final decision.
 * (N is the number of classes in the dataset)
 * 
 * <p>
 * NOTE: the base learning algorithm must provide a duplicate method which properly works
 * 
 * @author      Giuseppe Castellucci, Simone Filice
 */

@JsonTypeName("oneVsOne")
public class OneVsOneLearning implements ClassificationLearningAlgorithm, MetaLearningAlgorithm {
	private Logger logger = LoggerFactory.getLogger(OneVsOneLearning.class);
	
	private LearningAlgorithm baseAlgorithm;
	
	@JsonIgnore
	private LearningAlgorithm[] algorithms;
	private List<Label> labels;

	@JsonIgnore
	private Label[] negatives;

	@JsonIgnore
	private OneVsOneClassifier classifier;

	public OneVsOneLearning(){
		this.classifier = new OneVsOneClassifier();
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
	 * Returns the labels to be learned applying a one-vs-one strategy
	 * 
	 * @return the labels to be learned
	 */
	public List<Label> getLabels(){
		return labels;
	}
	
	private void initialize(){
		int learningAlgSize = this.labels.size() * (this.labels.size()-1) / 2;
		algorithms = new LearningAlgorithm[learningAlgSize];
		negatives = new Label[learningAlgSize];
		Classifier[] binaryClassifiers = new Classifier[learningAlgSize];
		int counter = 0;
		for (int i = 0; i < labels.size() - 1; ++i) {
			Label l1 = labels.get(i);
			for (int j = (i + 1); j < labels.size(); ++j) {
				Label l2 = labels.get(j);
				try {					
					algorithms[counter] = this.baseAlgorithm.duplicate();
					algorithms[counter].setLabels(Arrays.asList(l1));
					binaryClassifiers[counter] = (Classifier) algorithms[counter].getPredictionFunction();
					negatives[counter] = l2;					
					++counter;
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		
		classifier.setNegativeLabelsForClassifier(negatives);
		classifier.setBinaryClassifiers(binaryClassifiers);
		
	}

	/**
	 * This method will cause the meta-learning algorithm to learn 
	 * N*(N-1)/2 classifiers, where N is the number of classes in the dataset.
	 */
	@Override
	public void learn(Dataset dataset) {
				
		if(algorithms==null){
			this.initialize();
		}
		int counter = 0;
		for (int i = 0; i < labels.size() - 1; ++i) {

			for (int j = (i + 1); j < labels.size(); ++j) {

				try {
					ArrayList<Label> labelPair = new ArrayList<Label>();
					labelPair.add(labels.get(i));
					labelPair.add(labels.get(j));
					Dataset pairDataset = SimpleDataset.extractExamplesOfClasses(dataset, labelPair);
					algorithms[counter].learn(pairDataset);
					++counter;
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	public OneVsOneClassifier getPredictionFunction() {
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
		return this.baseAlgorithm;
	}

	/**
	 * This method will duplicate the current Learning algorithm
	 */
	@Override
	public OneVsOneLearning duplicate(){
		OneVsOneLearning copy = new OneVsOneLearning();
		copy.setBaseAlgorithm(this.baseAlgorithm);		
		return copy;
	}


}
