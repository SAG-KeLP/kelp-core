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

import gnu.trove.map.hash.TObjectFloatHashMap;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an instance of an Evaluator. It allows to compute the some common
 * measure for classification tasks. It computes precision, recall, f1s for each
 * class, and a global accuracy.
 * 
 * @author Giuseppe Castellucci
 */
public class MulticlassClassificationEvaluator extends Evaluator {
	private List<Label> labels;
	private TObjectFloatHashMap<Label> correctCounter = new TObjectFloatHashMap<Label>();
	private TObjectFloatHashMap<Label> predictedCounter = new TObjectFloatHashMap<Label>();
	private TObjectFloatHashMap<Label> toBePredictedCounter = new TObjectFloatHashMap<Label>();

	private TObjectFloatHashMap<Label> precisions = new TObjectFloatHashMap<Label>();
	private TObjectFloatHashMap<Label> recalls = new TObjectFloatHashMap<Label>();
	private TObjectFloatHashMap<Label> f1s = new TObjectFloatHashMap<Label>();

	private int total, correct;
	private float accuracy, overallPrecision, overallRecall, overallF1;

	/**
	 * Initialize a new F1Evaluator that will work on the specified classes
	 * 
	 * @param labels
	 */
	public MulticlassClassificationEvaluator(List<Label> labels) {
		this.labels = labels;
		initializeCounters();
	}

	private void initializeCounters() {
		for (Label l : labels) {
			correctCounter.put(l, 0.0f);
			toBePredictedCounter.put(l, 0.0f);
			predictedCounter.put(l, 0.0f);
			precisions.put(l, -1.0f);
			recalls.put(l, -1.0f);
			f1s.put(l, -1.0f);
		}
		total = 0;
		correct = 0;
		accuracy = 0.0f;
		this.computed=false;
	}

	/**
	 * Return the precision map
	 * 
	 * @return
	 */
	public TObjectFloatHashMap<Label> getPrecisions() {
		if (!this.computed)
			compute();
		return precisions;
	}

	/**
	 * Return the recall map
	 * 
	 * @return
	 */
	public TObjectFloatHashMap<Label> getRecalls() {
		if (!this.computed)
			compute();
		return recalls;
	}

	/**
	 * Return the F1 map
	 * 
	 * @return
	 */
	public TObjectFloatHashMap<Label> getF1s() {
		if (!this.computed)
			compute();
		return f1s;
	}

	public void addCount(Example test, Prediction prediction) {
		ClassificationOutput tmp = (ClassificationOutput) prediction;
		for (Label l : test.getClassificationLabels()) {
			toBePredictedCounter.put(l, toBePredictedCounter.get(l) + 1);
			total++;
		}
		List<Label> predictions = tmp.getPredictedClasses();
		if (predictions.size() > 0) {
			for (Label p : predictions) {
				predictedCounter.put(p, predictedCounter.get(p) + 1);
				if (test.isExampleOf(p)) {
					correctCounter.put(p, correctCounter.get(p) + 1);
					correct++;
				}
			}
		}
		this.computed=false;
	}

	protected void compute() {
		int c = 0;
		int p = 0;
		int tobe=0;
		for (Label l : labels) {
			float precision = 0.0f;
			float recall = 0.0f;
			float f1 = 0.0f;
			if (correctCounter.get(l) != 0 && predictedCounter.get(l) != 0
					&& toBePredictedCounter.get(l) != 0) {
				precision = correctCounter.get(l) / predictedCounter.get(l);
				recall = correctCounter.get(l) / toBePredictedCounter.get(l);
				f1 = (2 * precision * recall) / (precision + recall);
			}

			c += correctCounter.get(l);
			p += predictedCounter.get(l);
			tobe+=toBePredictedCounter.get(l);

			precisions.put(l, precision);
			recalls.put(l, recall);
			f1s.put(l, f1);
		}
		
		overallPrecision = (float) c / (float) p;
		overallRecall = (float) c / (float) tobe;
		overallF1 = 2 * overallPrecision * overallRecall
				/ (overallPrecision + overallRecall);

		accuracy = (float) correct / (float) total;
		this.computed=true;
	}

	/**
	 * Return the precision for the specified label
	 * 
	 * @return precision of Label l
	 */
	public float getPrecisionFor(Label l) {
		if (!this.computed)
			compute();
		
		if (precisions.containsKey(l))
			return precisions.get(l);
		return -1.0f;
	}

	/**
	 * Return the recall for the specified label
	 * 
	 * @return recall of Label l
	 */
	public float getRecallFor(Label l) {
		if (!this.computed)
			compute();
		
		if (recalls.containsKey(l))
			return recalls.get(l);
		return -1.0f;
	}

	/**
	 * Return the f1 for the specified label
	 * 
	 * @return f1 of Label l
	 */
	public float getF1For(Label l) {
		if (!this.computed)
			compute();
		
		if (f1s.containsKey(l))
			return f1s.get(l);
		return -1.0f;
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
	public float getOverallPrecision() {
		if (!this.computed)
			compute();
		
		return overallPrecision;
	}

	/**
	 * Return the recall considering all classes together
	 * 
	 * @return recall
	 */
	public float getOverallRecall() {
		if (!this.computed)
			compute();
		
		return overallRecall;
	}

	/**
	 * Return the f1 considering all classes together
	 * 
	 * @return f1
	 */
	public float getOverallF1() {
		if (!this.computed)
			compute();
		
		return overallF1;
	}

	/**
	 * Return the mean of the F1 scores considering all the labels involved
	 * 
	 * @return mean F1 of all Label{s}
	 */
	public float getMeanF1() {
		if (!this.computed)
			compute();
		
		return getMeanF1For((ArrayList<Label>) labels);
	}

	/**
	 * Return the mean of the F1 scores considering the specified labels
	 * 
	 * @return mean F1 of specified Label{s} ls
	 */
	public float getMeanF1For(ArrayList<Label> ls) {
		if (!this.computed)
			compute();
		
		float sum = 0.0f;
		for (Label l : ls) {
			sum += f1s.get(l);
		}
		float mean = sum / (float) ls.size();
		return mean;
	}

	/**
	 * Clear all the counters for a new processing.
	 */
	@Override
	public void clear() {
		correctCounter.clear();
		predictedCounter.clear();
		toBePredictedCounter.clear();
		precisions.clear();
		recalls.clear();
		f1s.clear();
		accuracy = 0.0f;
		this.computed=false;
	}

	/**
	 * Print the counters in a human-readable format
	 */
	@SuppressWarnings("unused")
	private void printCounters() {
		for (Label l : labels) {
			System.out.println(l);
			System.out.print("\t");
			printCounters(l);
		}

	}

	/**
	 * Print the counters of the specified Label l.
	 * 
	 * @param l
	 */
	public void printCounters(Label l) {
		System.out.println(correctCounter.get(l) + " "
				+ predictedCounter.get(l) + " " + toBePredictedCounter.get(l));
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Label l : labels) {
			b.append(l + "\t" + precisions.get(l) + "\t" + recalls.get(l)
					+ "\t" + f1s.get(l) + "\n");
		}
		return b.toString().trim();
	}

	@Override
	public MulticlassClassificationEvaluator duplicate() {
		return new MulticlassClassificationEvaluator(labels);
	}
}
