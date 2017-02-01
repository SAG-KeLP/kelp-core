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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A <code>SequenceExample</code> represents a sequence of <code>Examples</code>
 * s, each containing a set of <code>Representation</code>s and a set of
 * <code>Label</code>s. This is the object used to train a
 * <code>SequenceClassificationLearningAlgorithm<code>
 * 
 * @author Danilo Croce
 */
public class SequenceExample extends SimpleExample {

	private static final long serialVersionUID = 6630622367839228834L;

	/**
	 * This delimiter is used in the construction of artificial features
	 * representing transitions for the
	 * <code>SequenceClassificationLearningAlgorithm<code>
	 */
	public static final String SEQDELIM = "_";

	/**
	 * The list of examples composing the sequence
	 */
	private List<Example> examples;

	public SequenceExample() {
		examples = new ArrayList<Example>();
	}

	public void add(Example example) {
		examples.add(example);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.data.example.Example#getClassificationLabels()
	 */
	public HashSet<Label> getClassificationLabels() {
		HashSet<Label> res = new HashSet<Label>();
		for (Example example : this.examples) {
			res.addAll(example.getClassificationLabels());
		}
		return res;
	}

	/**
	 * 
	 * @param i
	 *            i-th <code>Example</code> in the sequence
	 * @return
	 */
	public Example getExample(int i) {
		return examples.get(i);
	}

	/**
	 * @return the list of <code>Example</code>s in the sequence
	 */
	public List<Example> getExamples() {
		return examples;
	}

	/**
	 * @return the number of <code>Example</code>s in the sequence
	 */
	public int getLenght() {
		return examples.size();
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (Example example : examples) {
			res.append(example.toString() + "\n");
		}
		return res.toString().trim();
	}

}
