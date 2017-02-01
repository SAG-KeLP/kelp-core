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

package it.uniroma2.sag.kelp.data.label;

import java.io.Serializable;

/**
 * It represents the pair (class_label,emission_score) assigned to each element
 * in a sequence. The emission_score is assigned from the classifier and used in
 * the Viterbi-like decoding to derive the best sequence labeling.
 * 
 * @author Danilo Croce
 *
 */
public class SequenceEmission implements Serializable, Comparable<SequenceEmission> {

	private static final long serialVersionUID = -4088372333901214120L;

	/**
	 * The class assigned to an element in the sequence
	 */
	private Label label;

	/**
	 * It is assigned from the classifier and used in the Viterbi-like decoding
	 * to derive the best sequence labeling.
	 */
	private Float emission;

	public SequenceEmission(Label label, float emission) {
		super();
		this.label = label;
		this.emission = emission;
	}

	@Override
	public int compareTo(SequenceEmission o) {
		int res = emission.compareTo(o.emission);
		if (res == 0)
			return label.toString().compareTo(o.toString());
		return res;
	}

	/**
	 * @return the emission score assigned from the classifier and used in the
	 *         Viterbi-like decoding to derive the best sequence labeling.
	 */
	public float getEmission() {
		return emission;
	}

	/**
	 * @return the class assigned to an element in the sequence
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @param emission
	 *            the emission score assigned from the classifier and used in
	 *            the Viterbi-like decoding to derive the best sequence
	 *            labeling.
	 */
	public void setEmission(float emission) {
		this.emission = emission;
	}

	/**
	 * @param label
	 *            the class assigned to an element in the sequence
	 */
	public void setLabel(Label label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "(" + label + "," + emission + ")";
	}

}
