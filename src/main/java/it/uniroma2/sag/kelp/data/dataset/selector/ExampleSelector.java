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
 * This interface allows defining an <code>Example</code> selectors: given a
 * <code>Dataset</code> a select should select a subset of <code>Examples</code>
 * to be used, e.g. as seed in a K-mean clustering algorithm.
 * 
 * @author Danilo Croce
 * 
 */
public interface ExampleSelector {

	/**
	 * This function allows to select a subset of <code>Example</code>s from the
	 * input <code>Dataset</code>
	 * 
	 * @param dataset
	 *            A dataset to select <code>Example</code>s from.
	 * @return A list of <code>Example</code>s.
	 */
	public List<Example> select(Dataset dataset);
}
