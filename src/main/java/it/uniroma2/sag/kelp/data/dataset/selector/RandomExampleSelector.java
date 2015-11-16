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
import it.uniroma2.sag.kelp.data.example.Example;

import java.util.List;

/**
 * This class allows selecting a subset of <code>m</code> examples from a
 * <code>Dataset</code> according to a random selection policy.
 * 
 * 
 * @author Danilo Croce
 */
public class RandomExampleSelector implements ExampleSelector {

	/**
	 * The number of <code>Example</code>s to be selected.
	 */
	private int m;

	/**
	 * The seed of the Random selection policy.
	 */
	private int randomSeed;

	/**
	 * @param m
	 *            The number of <code>Example</code>s to be selected.
	 */
	public RandomExampleSelector(int m) {
		this(m, 0);

	}

	/**
	 * @param m
	 *            The number of <code>Example</code>s to be selected.
	 * @param randomSeed
	 *            The seed of the Random selection.
	 */
	public RandomExampleSelector(int m, int randomSeed) {
		super();
		this.m = m;
		this.randomSeed = randomSeed;
	}

	/**
	 * @return The number of <code>Example</code>s to be selected.
	 */
	public int getM() {
		return m;
	}

	/**
	 * @return The seed of the Random selection policy.
	 */
	public int getRandomSeed() {
		return randomSeed;
	}

	@Override
	public List<Example> select(Dataset dataset) {
		dataset.setSeed(randomSeed);
		return dataset.getShuffledDataset().getNextExamples(m);
	}

}
