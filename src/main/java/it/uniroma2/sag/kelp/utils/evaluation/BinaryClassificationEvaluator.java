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

package it.uniroma2.sag.kelp.utils.evaluation;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;

/**
 * This is an instance of an Evaluator. It allows to compute the some common
 * measure for binary classification tasks.
 * 
 * @author Giuseppe Castellucci
 */
public class BinaryClassificationEvaluator extends Evaluator {
	private Label positiveLabel;

	private int total, correct, truePositivePredicted, predictedPositive, realPositive;
	private float accuracy, precision, recall, f1;

	public BinaryClassificationEvaluator(Label positiveClass) {
		this.positiveLabel = positiveClass;
		initializeCounters();
	}

	private void initializeCounters() {
		total = 0;
		correct = 0;
		accuracy = 0.0f;
		realPositive = 0;
		truePositivePredicted = 0;
		predictedPositive = 0;
		precision = 0.0f;
		recall = 0.0f;
		f1 = 0.0f;
		this.computed=false;
	}

	public void addCount(Example test, Prediction prediction) {
		total++;
		if (test.isExampleOf(positiveLabel))
			realPositive++;
		if (prediction.getScore(positiveLabel) >= 0)
			predictedPositive++;
		if (prediction.getScore(positiveLabel) >= 0 && test.isExampleOf(positiveLabel)) {
			correct++;
			truePositivePredicted++;
		} else if (prediction.getScore(positiveLabel) < 0 && !test.isExampleOf(positiveLabel))
			correct++;
		this.computed = false;
	}

	protected void compute() {
		precision = (float) truePositivePredicted / (float) predictedPositive;
		recall = (float) truePositivePredicted / (float) realPositive;
		f1 = (2 * precision * recall) / (precision + recall);

		accuracy = (float) correct / (float) total;
		this.computed=true;
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
	 * Return the precision considering all classes together
	 * 
	 * @return precision
	 */
	public float getPrecision() {
		if (!this.computed)
			compute();
		return precision;
	}

	/**
	 * Return the recall considering all classes together
	 * 
	 * @return recall
	 */
	public float getRecall() {
		if (!this.computed)
			compute();
		return recall;
	}

	/**
	 * Return the f1 considering all classes together
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
		total = 0;
		correct = 0;
		accuracy = 0.0f;
		truePositivePredicted = 0;
		predictedPositive = 0;
		precision = 0.0f;
		recall = 0.0f;
		f1 = 0.0f;
		this.computed=false;
	}

	/**
	 * Print the counters in a human-readable format
	 */
	@SuppressWarnings("unused")
	private void printCounters() {
		System.out.println("Accuracy measures");
		System.out.println("\tCorrect: " + correct);
		System.out.println("\tTotal: " + total);
		System.out.println("F1 measures");
		System.out.println("\tCorrect: " + truePositivePredicted);
		System.out.println("\tPredicted: " + predictedPositive);
		System.out.println("\tToBePredicted: " + total);
	}

	@Override
	public BinaryClassificationEvaluator duplicate() {
		return new BinaryClassificationEvaluator(positiveLabel);
	}
}
