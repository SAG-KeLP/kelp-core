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

package it.uniroma2.sag.kelp.data.clustering;

import java.io.Serializable;
import java.util.Collections;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * It is the instance of a Cluster, intended as a set of objects, instantiated
 * as Examples, grouped together according to a measure of similarity.
 * 
 * @author Danilo Croce
 */
@JsonTypeName("cluster")
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(ClusterTypeResolver.class)
public class Cluster implements Serializable {

	private static final long serialVersionUID = 5118220715068288983L;

	/**
	 * A possible label to characterize the cluster
	 */
	private String label;

	/**
	 * The set of objects within the cluster
	 */
	protected Vector<ClusterExample> examples;

	/**
	 * The cluster is initialized without any label
	 */
	public Cluster() {
		this.examples = new Vector<ClusterExample>();
	}

	/**
	 * The cluster is initialized and labeled
	 * 
	 * @param label
	 */
	public Cluster(String label) {
		this();
		this.label = label;
	}

	public void add(ClusterExample clusterExample) {
		this.examples.add(clusterExample);
	}

	/**
	 * This function clear the set of object inside the cluster
	 */
	public void clear() {
		examples.clear();
	}

	/**
	 * This function returns the set of objects inside the cluster
	 * 
	 * @return
	 */
	public Vector<ClusterExample> getExamples() {
		return examples;
	}

	/**
	 * This function returns the label of the cluster
	 * 
	 * @return the label of the cluster
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * This function initialize the set of objects inside the cluster
	 * 
	 * @param examples
	 *            The set of objects
	 */
	public void setExamples(Vector<ClusterExample> examples) {
		this.examples = examples;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * This functions returns the number of objects inside the cluster
	 * 
	 * @return
	 */
	public int size() {
		return this.examples.size();
	}

	public void sortAscendingOrder() {
		Collections.sort(examples);
	}

	public void sortDescendingOrder() {
		Collections.sort(examples);
		Collections.reverse(examples);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ClusterExample clusterExample : examples) {
			sb.append(clusterExample.toString() + "\n");
		}
		return sb.toString();
	}

}
