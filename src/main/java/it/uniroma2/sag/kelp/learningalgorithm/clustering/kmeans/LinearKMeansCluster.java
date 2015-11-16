/*
 * Copyright 2015 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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

package it.uniroma2.sag.kelp.learningalgorithm.clustering.kmeans;

import it.uniroma2.sag.kelp.data.clustering.Cluster;
import it.uniroma2.sag.kelp.data.clustering.ClusterExample;
import it.uniroma2.sag.kelp.data.representation.Vector;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is the instance of a Cluster for Linear Algorithms, intended as a set of
 * objects, instantiated as Examples, grouped together according to a measure of
 * similarity. <br>
 * This class extends the
 * <code>it.uniroma2.sag.kelp.data.clustering.Cluster</code> with the notion of
 * Centroid.
 * 
 * @author Danilo Croce
 */
@JsonTypeName("linearkmeanscluster")
public class LinearKMeansCluster extends Cluster {

	private static final long serialVersionUID = 5929926539580341620L;

	/**
	 * The cluster centroid.
	 */
	private Vector centroid;

	public LinearKMeansCluster() {
		super();
	}

	/**
	 * @param label
	 *            The cluster label
	 */
	public LinearKMeansCluster(String label) {
		super(label);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.data.clustering.Cluster#clear()
	 */
	public void clear() {
		super.clear();
		centroid = null;
	}

	/**
	 * @return The cluster centroid
	 */
	public Vector getCentroid() {
		return centroid;
	}

	public void setCentroid(Vector centroid) {
		this.centroid = centroid;
	}

	/**
	 * The centroid is calculated as the mean of all <code>Vector</code>
	 * representations stored in the clusters.
	 * 
	 * @param representationName
	 *            The name of the rapresentation considered in the Mean
	 */
	public void updateCentroid(String representationName) {

		if (this.examples.isEmpty()) {
			centroid = null;
		}

		for (ClusterExample clusterExample : this.examples) {

			Vector vector = (Vector) (clusterExample.getExample()
					.getRepresentation(representationName));

			if (centroid == null) {
				centroid = vector.getZeroVector();
			}

			centroid.add(vector);

		}

		if (centroid != null) {
			centroid.scale(1 / (float) this.examples.size());
		}
	}
}
