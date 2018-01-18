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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;

/**
 * This is an instance of an Evaluator. It allows to compute the some common
 * measure for classification tasks. It computes precision, recall, f1s for each
 * class, a global accuracy, micro measures and macro measures.
 * 
 * In case of multilabel classification, a prediction is considered correct (for computing the accuracy) if and only if
 * all the predicted labels for a given instance are correct and no additional labels are predicted
 * 
 * @author Simone Filice, Giuseppe Castellucci
 */
public class MulticlassClassificationEvaluator extends Evaluator {
	
	protected class ClassStats{
		protected int tp;
		protected int fp;
		protected int tn;
		protected int fn;
		protected float precision, recall, f1;
	}
	
	protected List<Label> labels;	
	protected HashMap<Label, ClassStats> classStats = new HashMap<Label, ClassStats>();

	protected int total, correct, totalTp, totalTn, totalFp, totalFn;
	private float accuracy, overallPrecision, overallRecall, overallF1;
	private float microPrecision, microRecall, microF1;
	private float macroPrecision, macroRecall, macroF1;

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
			
			ClassStats stats = new ClassStats();
			this.classStats.put(l, stats);

		}
		accuracy = 0.0f;
		this.computed=false;
	}

	public void addCount(Example test, Prediction prediction) {
		ClassificationOutput tmp = (ClassificationOutput) prediction;
		
		boolean correct = true;
		for(Label l: this.labels){
			ClassStats stats = this.classStats.get(l);
			if(test.isExampleOf(l)){
				if(tmp.isClassPredicted(l)){
					stats.tp++;
					totalTp++;
				}else{
					stats.fn++;
					totalFn++;
					correct = false;
				}
			}else{
				if(tmp.isClassPredicted(l)){
					stats.fp++;
					totalFp++;
					correct = false;
				}else{
					totalTn++;
					stats.tn++;
				}
			}
		}
		
		this.total++;
		if(correct){
			this.correct++;
		}
		
//		for (Label l : test.getClassificationLabels()) {
//			toBePredictedCounter.put(l, toBePredictedCounter.get(l) + 1);
//			total++;
//		}
//		List<Label> predictions = tmp.getPredictedClasses();
//		if (predictions.size() > 0) {
//			for (Label p : predictions) {
//				predictedCounter.put(p, predictedCounter.get(p) + 1);
//				if (test.isExampleOf(p)) {
//					correctCounter.put(p, correctCounter.get(p) + 1);
//					correct++;
//				}
//			}
//		}
		this.computed=false;
	}

	protected void compute() {
		
		//Compute accuracy
		if(this.total > 0){
			this.accuracy = (float)this.correct/(this.total);
		}		
		
		//Compute class precision, recall, f1 and macro precision, macro recall
		this.macroPrecision = 0;
		for(Label l : labels){
			ClassStats classStats = this.classStats.get(l);
			//Computing precision
			if(classStats.tp+classStats.fp == 0){
				classStats.precision = 1;
			}else{
				classStats.precision = (float)classStats.tp/(classStats.tp+classStats.fp);
			}
			macroPrecision+=classStats.precision;
			
			//Computing recall
			if(classStats.tp+classStats.fn == 0){
				classStats.recall = 0;
			}else{
				classStats.recall = (float)classStats.tp/(classStats.tp+classStats.fn);
			}
			macroRecall+=classStats.recall;
			
			//Computing f1
			if(classStats.precision == 0 || classStats.recall == 0){
				classStats.f1 = 0;
			}else{
				classStats.f1 = (2 * classStats.precision * classStats.recall) / (classStats.precision + classStats.recall);
			}
		}
		
		macroPrecision/=this.labels.size();
		macroRecall/=this.labels.size();
		if(macroPrecision == 0 || macroRecall == 0){
			macroF1 = 0;
		}else{
			macroF1 = 2 * macroPrecision *macroRecall /(macroPrecision+macroRecall);
		}
		
		//Computing micro measures
		if(totalTp+totalFp>0){
			microPrecision = (float)totalTp/(totalTp+totalFp);
		}else{
			microPrecision = 0;
		}
		
		if(totalTp+totalFn>0){
			microRecall = (float)totalTp/(totalTp+totalFn);
		}else{
			microRecall = 0;
		}
		
		if(microPrecision == 0 || microRecall == 0){
			microF1 = 0;
		}else{
			microF1 = 2 * microPrecision *microRecall /(microPrecision+microRecall);
		}

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
		
		ClassStats stats = this.classStats.get(l);
		if(stats != null){
			return stats.precision;
		}
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
		
		ClassStats stats = this.classStats.get(l);
		if(stats != null){
			return stats.recall;
		}
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
		
		ClassStats stats = this.classStats.get(l);
		if(stats != null){
			return stats.f1;
		}
		return -1.0f;
	}
	
	/**
	 * Return the true positives for the specified label
	 * 
	 * @return tp of Label l
	 */
	public float getTpFor(Label l) {
		ClassStats stats = this.classStats.get(l);
		if (stats!=null)
			return stats.tp;
		return -1.0f;
	}
	
	/**
	 * Return the true negatives for the specified label
	 * 
	 * @return tn of Label l
	 */
	public float getTnFor(Label l) {
		ClassStats stats = this.classStats.get(l);
		if (stats!=null)
			return stats.tn;
		return -1.0f;
	}

	/**
	 * Return the false positives for the specified label
	 * 
	 * @return fp of Label l
	 */
	public float getFpFor(Label l) {
		ClassStats stats = this.classStats.get(l);
		if (stats!=null)
			return stats.fp;
		return -1.0f;
	}

	/**
	 * Return the false negatives for the specified label
	 * 
	 * @return fn of Label l
	 */
	public float getFnFor(Label l) {
		ClassStats stats = this.classStats.get(l);
		if (stats!=null)
			return stats.fn;
		return -1.0f;
	}

	

	/**
	 * Returns the accuracy
	 * 
	 * @return accuracy
	 */
	public float getAccuracy() {
		if (!this.computed)
			compute();
		
		return accuracy;
	}

	/**
	 * Returns the micro-precision
	 * 
	 * @return micro-precision
	 */
	public float getMicroPrecision() {
		if (!this.computed)
			compute();
		
		return microPrecision;
	}
	
	/**
	 * Returns the micro-recall
	 * 
	 * @return micro-recall
	 */
	public float getMicroRecall() {
		if (!this.computed)
			compute();
		
		return microRecall;
	}
	
	/**
	 * Returns the micro-f1
	 * 
	 * @return micro-f1
	 */
	public float getMicroF1() {
		if (!this.computed)
			compute();
		
		return microF1;
	}
	
	/**
	 * Return the precision considering all classes together
	 * 
	 * @return precision
	 * 
	 * This method is deprecated, use getMicroPrecision instead
	 */
	@Deprecated
	public float getOverallPrecision() {
		if (!this.computed)
			compute();
		
		return overallPrecision;
	}

	/**
	 * Return the recall considering all classes together
	 * 
	 * @return recall
	 * 
	 * This method is deprecated, use getMicroRecall instead
	 */
	@Deprecated
	public float getOverallRecall() {
		if (!this.computed)
			compute();
		
		return overallRecall;
	}

	/**
	 * Return the f1 considering all classes together
	 * 
	 * @return f1
	 * 
	 * This method is deprecated, use getMicroF1 instead
	 */
	@Deprecated
	public float getOverallF1() {
		if (!this.computed)
			compute();
		
		return overallF1;
	}

	/**
	 * Return the mean of the F1 scores considering all the labels involved
	 * 
	 * @return mean F1 of all Label{s}
	 * 
	 * This method is deprecated, use getMacroF1 instead
	 */
	@Deprecated
	public float getMeanF1() {
		if (!this.computed)
			compute();
		
		return this.macroF1;
	}
	
	/**
	 * Return the macro-precision 
	 * 
	 * @return macro-precision
	 */
	public float getMacroPrecision() {
		if (!this.computed)
			compute();
		
		return this.macroPrecision;
	}
	
	/**
	 * Return the macro-recall 
	 * 
	 * @return macro-recall
	 */
	public float getMacroRecall() {
		if (!this.computed)
			compute();
		
		return this.macroRecall;
	}
	
	/**
	 * Return the macro-F1 
	 * 
	 * @return macro-F1
	 */
	public float getMacroF1() {
		if (!this.computed)
			compute();
		
		return this.macroF1;
	}

	/**
	 * Return the mean of the F1 scores considering the specified labels only
	 * 
	 * @return mean F1 of specified Label{s} ls
	 */
	public float getMeanF1For(ArrayList<Label> ls) {
		if (!this.computed)
			compute();
		
		float sum = 0.0f;
		for (Label l : ls) {
			ClassStats stats = this.classStats.get(l);
			if(stats != null){
				sum += stats.f1;
			}
		}
		float mean = sum / (float) ls.size();
		return mean;
	}

	/**
	 * Clear all the counters for a new processing.
	 */
	@Override
	public void clear() {
		for(ClassStats stats : this.classStats.values()){
			stats.tp = 0;
			stats.tn = 0;
			stats.fp = 0;
			stats.fn = 0;
		}
		total = 0;
		correct = 0;
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
		ClassStats stats = this.classStats.get(l);
		if(stats != null){
			System.out.println("class " + l.toString() + ": tp=" + stats.tp + " tn=" + stats.tn
					+ " fp=" + stats.fp + " fn=" + stats.fn);
		}else{
			System.out.println("There are no counters for the label " + l.toString());
		}

	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Label l : labels) {
			b.append(l + "\t" + this.getPrecisionFor(l) + "\t" + this.getRecallFor(l)
					+ "\t" + this.getF1For(l) + "\n");
		}
		return b.toString().trim();
	}

	@Override
	public MulticlassClassificationEvaluator duplicate() {
		return new MulticlassClassificationEvaluator(labels);
	}
}
