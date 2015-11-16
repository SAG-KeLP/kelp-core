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
import it.uniroma2.sag.kelp.predictionfunction.regressionfunction.UnivariateRegressionOutput;

/**
 * This is an instance of an Evaluator. It allows to compute the some common
 * measure for regression tasks.
 * 
 * @author Giuseppe Castellucci
 */
public class RegressorEvaluator extends Evaluator {
	private HashMap<Label, Float> values = new HashMap<Label, Float>();
	private HashMap<Label, Float> errors = new HashMap<Label, Float>();
	int n = 0;

	public RegressorEvaluator(List<Label> labels) {
		for (Label numericLabel : labels) {
			values.put(numericLabel, 0.0f);
			errors.put(numericLabel, 0.0f);
		}
	}

	public void addCount(Example test, Prediction prediction) {
		for (Label numericLabel : values.keySet()) {
			Float gold = test.getRegressionValue(numericLabel);
			UnivariateRegressionOutput out = (UnivariateRegressionOutput) prediction;
			Float score = out.getScore(numericLabel);
			float v = (score - gold) * (score - gold);
			values.put(numericLabel, values.get(numericLabel) + v);
		}
		n++;
		this.computed = false;
	}

	@Override
	protected void compute() {
		for (Label numericLabel : values.keySet()) {
			float v = values.get(numericLabel);
			errors.put(numericLabel, v / n);
		}
		this.computed = true;
	}

	/**
	 * Returns the mean square error of the Label label.
	 * 
	 * @param label
	 * @return a float or -1 if label is not a valid label for this Evaluator
	 */
	public float getMeanSquaredError(Label label) {
		if (!this.computed)
			compute();
		if (errors.containsKey(label)) {
			return errors.get(label);
		}
		return -1f;
	}

	/**
	 * Returns the mean error between the different Label{s}
	 * 
	 * @return a float
	 */
	public float getMeanSquaredErrors() {
		if (!this.computed)
			compute();
		
		float allSum = 0.0f;
		for (Label numericLabel : errors.keySet()) {
			allSum += errors.get(numericLabel);
		}
		return allSum / errors.keySet().size();
	}

	@Override
	public void clear() {
		values.clear();
		errors.clear();
		n = 0;
		
		this.computed=false;
	}

	@Override
	public RegressorEvaluator duplicate() {
		return new RegressorEvaluator(new ArrayList<Label>(values.keySet()));
	}
}
