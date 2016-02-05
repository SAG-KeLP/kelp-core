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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import it.uniroma2.sag.kelp.data.example.Example;

@JsonTypeName("clusterexample")
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(ClusterExampleTypeResolver.class)
public abstract class ClusterExample implements Comparable<ClusterExample>, Serializable {
	private static final long serialVersionUID = 7307191813347830208L;

	protected Example example;
	protected Float dist;

	public ClusterExample(Example e, float dist2) {
		this.example = e;
		this.dist = dist2;
	}

	public ClusterExample() {
	}

	public void setExample(Example example) {
		this.example = example;
	}

	public abstract Example getExample();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dist == null) ? 0 : dist.hashCode());
		result = prime * result + ((example == null) ? 0 : example.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterExample other = (ClusterExample) obj;
		if (dist == null) {
			if (other.dist != null)
				return false;
		} else if (!dist.equals(other.dist))
			return false;
		if (example == null) {
			if (other.example != null)
				return false;
		} else if (!example.toString().equals(other.example.toString()))
			return false;
		return true;
	}

	@Override
	public int compareTo(ClusterExample arg0) {
		int res = dist.compareTo(arg0.dist);
		if (res == 0)
			return example.toString().compareTo(arg0.example.toString());
		return res;
	}

	public Float getDist() {
		return dist;
	}

	public void setDist(Float dist) {
		this.dist = dist;
	}

	@Override
	public String toString() {
		return dist + "\t" + example;
	}
}
