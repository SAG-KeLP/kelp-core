/* Copyright 2015 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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
package it.uniroma2.sag.kelp.data.dataset.selector;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.dataset.DatasetReader;
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;

import java.io.IOException;
import java.util.List;

/**
 * This class allows to select the first <code>m</code> examples from a
 * <code>Dataset</code>.
 * 
 * @author Danilo Croce
 * 
 */
public class FirstExamplesSelector implements ExampleSelector {

	private int m;

	/**
	 * @param m
	 *            The number of <code>Example</code>s to be selected.
	 */
	public FirstExamplesSelector(int m) {
		super();
		this.m = m;
	}

	/**
	 * @param m
	 *            The number of <code>Example</code>s to be selected.
	 */
	public int getM() {
		return m;
	}

	@Override
	public List<Example> select(Dataset dataset) {
		return dataset.getNextExamples(m);
	}

	/**
	 * This function allows to select a subset of <code>Example</code>s from the
	 * input <code>DatasetReader</code>
	 * 
	 * @param dataset
	 *            A <code>DatasetReader</code> to select <code>Example</code>s
	 *            from.
	 * @return A list of <code>Example</code>s.
	 * @throws ParsingExampleException 
	 */
	public List<Example> select(DatasetReader datasetReader)
			throws IOException, InstantiationException, ParsingExampleException {

		int counter = 1;
		SimpleDataset dataset = new SimpleDataset();
		while (datasetReader.hasNext()) {
			Example e = datasetReader.readNextExample();
			dataset.addExample(e);

			if (counter % 100 == 0) {
				System.out.println("Loaded " + counter
						+ " landmark candidates.");
			}

		}

		return dataset.getShuffledDataset().getNextExamples(m);
	}

}
