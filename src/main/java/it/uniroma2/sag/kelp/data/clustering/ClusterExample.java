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
	public int compareTo(ClusterExample arg0) {
		int res = dist.compareTo(arg0.dist);
		if (res == 0)
			return example.toString().compareTo(arg0.toString());
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
