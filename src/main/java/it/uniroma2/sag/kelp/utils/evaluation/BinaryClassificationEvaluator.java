/*
 * Copyright 2014-2017 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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

package it.uniroma2.sag.kelp.utils.evaluation;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;

/**
 * This is an instance of an Evaluator. It allows to compute the some common
 * measure for binary classification tasks.
 * 
 * @author Simone Filice, Giuseppe Castellucci
 */
public class BinaryClassificationEvaluator extends Evaluator {
	private Label positiveLabel;

	//private int total, correct, truePositivePredicted, predictedPositive, realPositive;
	private int tp, tn, fp, fn;
	private float accuracy, precision, recall, f1;

	public BinaryClassificationEvaluator(Label positiveClass) {
		this.positiveLabel = positiveClass;
		initializeCounters();
	}

	private void initializeCounters() {
		tp = 0;
		tn = 0;
		fp = 0;
		fn = 0;
		this.computed=false;
	}

	@Override
	public void addCount(Example test, Prediction prediction) {
//		total++;
//		if (test.isExampleOf(positiveLabel))
//			realPositive++;
//		if (prediction.getScore(positiveLabel) >= 0)
//			predictedPositive++;
//		if (prediction.getScore(positiveLabel) >= 0 && test.isExampleOf(positiveLabel)) {
//			correct++;
//			truePositivePredicted++;
//		} else if (prediction.getScore(positiveLabel) < 0 && !test.isExampleOf(positiveLabel))
//			correct++;
//		this.computed = false;
		if(test.isExampleOf(positiveLabel)){
			if (prediction.getScore(positiveLabel) >= 0){
				tp++;
			}else{
				fn++;
			}
		}else{
			if (prediction.getScore(positiveLabel) >= 0){
				fp++;
			}else{
				tn++;
			}
		}
		this.computed = false;
	}

	@Override
	protected void compute() {
//		if(predictedPositive==0){
//			precision = 0;
//		}else{
//			precision = (float) truePositivePredicted / (float) predictedPositive;
//		}
//		if(realPositive==0){
//			recall = 0;
//		}else{
//			recall = (float) truePositivePredicted / (float) realPositive;
//		}
//		
//		if(precision == 0 || recall == 0){
//			f1 = 0;
//		}else{
//			f1 = (2 * precision * recall) / (precision + recall);
//		}
//		
//		accuracy = (float) correct / (float) total;
		//Computing accuracy
		if(tp+tn+fp+fn == 0){
			accuracy = 0;
		}else{
			accuracy = (float)(tp+tn)/(tp+tn+fp+fn);
		}		
		
		//Computing precision
		if(tp+fp == 0){
			precision = 0;
		}else{
			precision = (float)tp/(tp+fp);
		}
		
		//Computing recall
		if(tp+fn == 0){
			recall = 0;
		}else{
			recall = (float)tp/(tp+fn);
		}
		
		//Computing f1
		if(precision == 0 || recall == 0){
			f1 = 0;
		}else{
			f1 = (2 * precision * recall) / (precision + recall);
		}

		this.computed=true;
	}
	
	/**
	 * Return the true positives
	 * 
	 * @return true positives
	 */
	public float getTp() {
		return tp;
	}
	
	/**
	 * Return the false positives
	 * 
	 * @return false positives
	 */
	public float getFp() {
		return fp;
	}
	
	/**
	 * Return the true negatives
	 * 
	 * @return true negatives
	 */
	public float getTn() {
		return tn;
	}
	
	/**
	 * Return the false negatives
	 * 
	 * @return false negatives
	 */
	public float getFn() {
		return fn;
	}

	/**
	 * Return the accuracy
	 * 
	 * @return accuracy
	 */
	public float getAccuracy() {
		if (!this.computed)
			compute();
		return accuracy;
	}

	/**
	 * Return the precision of the positive class
	 * 
	 * @return precision
	 */
	public float getPrecision() {
		if (!this.computed)
			compute();
		return precision;
	}

	/**
	 * Return the recall of the positive class
	 * 
	 * @return recall
	 */
	public float getRecall() {
		if (!this.computed)
			compute();
		return recall;
	}

	/**
	 * Return the f1 of the positive class
	 * 
	 * @return f1
	 */
	public float getF1() {
		if (!this.computed)
			compute();
		return f1;
	}

	/**
	 * Clear all the counters for a new processing.
	 */
	@Override
	public void clear() {
		initializeCounters();
	}

	/**
	 * Print the counters in a human-readable format
	 */
	@SuppressWarnings("unused")
	private void printCounters() {
//		System.out.println("Accuracy measures");
//		System.out.println("\tCorrect: " + correct);
//		System.out.println("\tTotal: " + total);
//		System.out.println("F1 measures");
//		System.out.println("\tCorrect: " + truePositivePredicted);
//		System.out.println("\tPredicted: " + predictedPositive);
//		System.out.println("\tToBePredicted: " + total);
		System.out.println("True positive:" + tp);
		System.out.println("True negative:" + tn);
		System.out.println("False positive:" + fp);
		System.out.println("False negative:" + fn);
	}

	@Override
	public BinaryClassificationEvaluator duplicate() {
		return new BinaryClassificationEvaluator(positiveLabel);
	}
}
