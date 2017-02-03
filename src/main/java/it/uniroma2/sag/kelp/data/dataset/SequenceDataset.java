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

package it.uniroma2.sag.kelp.data.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.example.SequenceExample;
import it.uniroma2.sag.kelp.data.label.Label;

/**
 * A dataset made of <code>SequenceExample</code>s
 * 
 * @author Danilo Croce
 *
 */
public class SequenceDataset extends SimpleDataset {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.data.dataset.SimpleDataset#getClassificationLabels()
	 */
	public List<Label> getClassificationLabels() {
		HashSet<Label> resHashSet = new HashSet<Label>();
		for (SequenceExample sequenceExample : getSequenceExamples()) {
			resHashSet.addAll(sequenceExample.getClassificationLabels());
		}
		List<Label> res = new ArrayList<Label>();
		for (Label label : resHashSet)
			res.add(label);
		return res;
	}

	/**
	 * @return The list of sequence of examples in the dataset
	 */
	public List<SequenceExample> getSequenceExamples() {
		List<SequenceExample> res = new ArrayList<SequenceExample>();
		for (Example e : getExamples()) {
			res.add((SequenceExample) e);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.data.dataset.SimpleDataset#populate(java.lang.
	 * String)
	 */
	public void populate(String inputFilePath) throws IOException, InstantiationException, ParsingExampleException {
		SequenceDatasetReader sequenceDatasetReader = new SequenceDatasetReader(inputFilePath);
		SequenceExample sequenceExample;
		while ((sequenceExample = sequenceDatasetReader.readNextExample()) != null) {
			addExample(sequenceExample);
		}
		sequenceDatasetReader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.data.dataset.SimpleDataset#split(float)
	 */
	@Override
	public SequenceDataset[] split(float percentage) {
		SequenceDataset[] datasets = new SequenceDataset[2];
		datasets[0] = new SequenceDataset();
		datasets[1] = new SequenceDataset();
		for (int i = 0; i < this.getNumberOfExamples(); i++) {
			Example currentExample = this.getExample(i);
			if (i < this.getNumberOfExamples() * percentage) {
				datasets[0].addExample(currentExample);
			} else {
				datasets[1].addExample(currentExample);
			}
		}
		return datasets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.data.dataset.SimpleDataset#
	 * splitClassDistributionInvariant(float)
	 */
	@Override
	public SequenceDataset[] splitClassDistributionInvariant(float percentage) {
		return this.split(percentage);
	}

}
