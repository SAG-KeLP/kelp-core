/*
 * Copyright 2016 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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

package it.uniroma2.sag.kelp.data.example;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.SequenceEmission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the output of a sequence labeling process.
 * 
 * @author Danilo Croce
 */
public class SequencePath implements Comparable<SequencePath>, Serializable {

	private static final long serialVersionUID = -6161123714742699932L;

	/**
	 * This delimiter is used in the construction of artificial features
	 * representing transitions for the
	 * <code>SequenceClassificationLearningAlgorithm<code>
	 */
	public final static String SEQDELIM = SequenceExample.SEQDELIM;

	/**
	 * The collection of <code>SequenceLabel</code>s produced during the
	 * sequence labeling process
	 */
	private List<SequenceEmission> assignedSequenceLabels;

	/**
	 * This score can be used to select one specific sequence labeling among
	 * several provided solutions, e.g., when a Beam Searcher is activated.
	 */
	private Double score;

	public SequencePath() {
		this.assignedSequenceLabels = new ArrayList<SequenceEmission>();
		this.score = 0.0;
	}

	/**
	 * @param sequenceLabel
	 *            A sequence label to be added at the end of this list
	 */
	public void add(SequenceEmission sequenceLabel) {
		this.assignedSequenceLabels.add(sequenceLabel);
	}

	@Override
	public int compareTo(SequencePath o) {
		int res = score.compareTo(o.score);
		if (res == 0) {
			return assignedSequenceLabels.toString().compareTo(o.assignedSequenceLabels.toString());
		}
		return res;
	}

	/**
	 * @param i
	 *            the index of the targeted element
	 * @return the label assigned to the i-th element
	 */
	public Label getAssignedLabel(int i) {
		if (i >= 0 && i < assignedSequenceLabels.size())
			return assignedSequenceLabels.get(i).getLabel();
		return null;
	}

	/**
	 * @return The collection of <code>SequenceLabel</code>s produced during the
	 *         sequence labeling process
	 */
	public List<SequenceEmission> getAssignedSequnceLabels() {
		return assignedSequenceLabels;
	}

	/**
	 * This method generate a string representing an artificial feature used in
	 * the labeling process. In particular it reflects the $h$ labels preceding
	 * a targeted element.
	 * 
	 * @param targetId
	 *            the index of the element whose previous labels are required
	 * @param historySize
	 *            the number of labels to return
	 * @return a string that is the concatenation of labels preceding a targeted
	 *         element in the sequence
	 */
	public String getHistoryBefore(int targetId, int historySize) {
		String res = new String();
		for (int j = targetId - historySize; j < targetId; j++) {
			Label prevLabel = getAssignedLabel(j);
			if (prevLabel == null) {
				res += SEQDELIM + j + "init";
			} else {
				res += SEQDELIM + prevLabel;
			}
		}
		return res;
	}

	/**
	 * @return the score assigned to this labeling
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * @param assignedLabels
	 *            The collection of <code>SequenceLabel</code>s produced during
	 *            the sequence labeling process
	 */
	public void setAssignedSequenceLabels(List<SequenceEmission> assignedLabels) {
		this.assignedSequenceLabels = assignedLabels;
	}

	/**
	 * @param score
	 *            the score assigned to this labeling
	 */
	public void setScore(Double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "SequencePath [score=" + score + ", assignedLabels=" + assignedSequenceLabels + "]";
	}

}
