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

package it.uniroma2.sag.kelp.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;
import it.uniroma2.sag.kelp.predictionfunction.PredictionFunction;
import it.uniroma2.sag.kelp.utils.evaluation.Evaluator;

/**
 * Class containing some useful methods for evaluating the performance of a learning algorithm 
 * 
 * @author Simone Filice
 *
 */
public class ExperimentUtils {
	private static Logger logger = LoggerFactory.getLogger(ExperimentUtils.class);
	
	/**
	 * Evaluates a prediction function over a testset
	 * 
	 * @param predictionFunction the prediction function to be evaluated
	 * @param evaluator the evaluator to be applied during the evaluation process
	 * @param testset the dataset of containing the test examples
	 * @return the predictions provided by the prediction function on the testset
	 */
	public static List<Prediction> test(PredictionFunction predictionFunction,  Evaluator evaluator, Dataset testset ){
	
		List<Prediction> predictions = new ArrayList<Prediction>();
		for(Example ex : testset.getExamples()){
			Prediction prediction = predictionFunction.predict(ex);
			evaluator.addCount(ex, prediction);
			predictions.add(prediction);
		}
		return predictions;
	}
	
	/**
	 * Performs a n-fold cross validation
	 * 
	 * @param nFold the number of folds
	 * @param algorithm the learning algorithm to be validated
	 * @param allData the dataset to be exploited
	 * @param evaluator the evaluator to be applied during the evaluation process
	 * @return a list containing <code>nFold</code> evaluators, each one storing the evaluations 
	 * computed on the corresponding fold 
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Evaluator> List<T> nFoldCrossValidation(int nFold, LearningAlgorithm algorithm, SimpleDataset allData, T evaluator) {
		Dataset[] folds = allData.nFoldingClassDistributionInvariant(nFold);
		List<T> evaluators = new ArrayList<T>();
		evaluators.add(evaluator);
		for(int i=1; i<nFold; i++){
			evaluators.add((T) evaluator.duplicate());
		}
		for (int i = 0; i < nFold; ++i) {
			SimpleDataset testSet = (SimpleDataset) folds[i];
			SimpleDataset trainingSet = getAllExcept(folds, i);
			
			logger.info("start testing on fold=" + i);
			algorithm.learn(trainingSet);
			PredictionFunction predictionFunction = algorithm.getPredictionFunction();
			test(predictionFunction, evaluators.get(i), testSet);
			algorithm.reset();
		}
		
		return evaluators;
	}
	
	
	private static SimpleDataset getAllExcept(Dataset[] folds, int i) {
		SimpleDataset ret = new SimpleDataset();
		for (int k = 0; k < folds.length; ++k) {
			if (i != k)
				ret.addExamples(folds[k]);
		}
		return ret;
	}

}
