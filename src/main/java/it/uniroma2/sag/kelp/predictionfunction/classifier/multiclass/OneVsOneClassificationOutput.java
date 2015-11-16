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

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 
 * @author Giuseppe Castellucci
 *
 */
public class OneVsOneClassificationOutput implements ClassificationOutput {
	private HashMap<Label, Integer> counter = new HashMap<Label, Integer>();
	private HashMap<Label, Float> maxMarginForLabel = new HashMap<Label, Float>();
	private HashMap<Label, ArrayList<Float>> scoresForLabel = new HashMap<Label, ArrayList<Float>>();
	
	private Label mostVotedLabel;
	
	public void addVotedPrediction(Label l, float score){
		if (counter.containsKey(l)) {
			counter.put(l, counter.get(l)+1);
			float curMargin = maxMarginForLabel.get(l);
			if (score > curMargin)
				maxMarginForLabel.put(l, score);
			scoresForLabel.get(l).add(score);
		} else {
			counter.put(l, 1);
			maxMarginForLabel.put(l, score);
			ArrayList<Float> scoresF = new ArrayList<Float>();
			scoresF.add(score);
			scoresForLabel.put(l, scoresF);
		}
	}
	
	public HashMap<Label, Integer> getCounter() {
		return counter;
	}

	public HashMap<Label, Float> getMaxMarginForLabel() {
		return maxMarginForLabel;
	}
	
	@Override
	public Float getScore(Label label) {
		return maxMarginForLabel.get(label);
	}

	@Override
	public boolean isClassPredicted(Label label) {
		if (mostVotedLabel == null) 
			mostVotedLabel = getMostVotedLabelWithMaxMargin();
		return label.equals(mostVotedLabel);
	}
	
	private Label getMostVotedLabelWithMaxMargin() {
		int max = Collections.max(counter.values());
		ArrayList<Label> labelsWithMaxCounter = new ArrayList<Label>();
		for (Label l : counter.keySet()) {
			if (counter.get(l) == max)
				labelsWithMaxCounter.add(l);
		}
		
		if (labelsWithMaxCounter.size() == 1)
			return labelsWithMaxCounter.get(0);
		
		Label toReturn = labelsWithMaxCounter.get(0);
		float maxScore = maxMarginForLabel.get(toReturn);
		for (int i=1; i<labelsWithMaxCounter.size(); ++i) {
			Label l = labelsWithMaxCounter.get(i);
			if (maxMarginForLabel.get(l) > maxScore) {
				toReturn = l;
				maxScore = maxMarginForLabel.get(l);
			}
		}
		return toReturn;
	}

	@Override
	public List<Label> getPredictedClasses() {
		if (mostVotedLabel == null)
			mostVotedLabel = getMostVotedLabelWithMaxMargin();
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.add(mostVotedLabel);
		return labels;
	}

	@Override
	public List<Label> getAllClasses() {
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.addAll(counter.keySet());
		return labels;
	}

}
