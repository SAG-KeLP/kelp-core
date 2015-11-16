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

package it.uniroma2.sag.kelp.learningalgorithm.clustering;

import it.uniroma2.sag.kelp.data.clustering.Cluster;
import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.dataset.selector.ExampleSelector;

import java.util.List;

/**
 * 
 * It is a generic Clustering algorithm
 * 
 * @author Danilo Croce
 * 
 */
public interface ClusteringAlgorithm {

	/**
	 * It starts the clustering process exploiting the provided
	 * <code>dataset</code>
	 * 
	 * @param dataset
	 * @return the data instances grouped in clusters
	 */
	public List<Cluster> cluster(Dataset dataset);

	/**
	 * It starts the clustering process exploiting the provided
	 * <code>dataset</code>
	 * 
	 * @param dataset
	 * @param seedSelector
	 *            the seed selector
	 * @return the data instances grouped in clusters
	 */
	public List<Cluster> cluster(Dataset dataset, ExampleSelector seedSelector);

}
