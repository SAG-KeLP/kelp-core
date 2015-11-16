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

package it.uniroma2.sag.kelp.kernel.cache;

/**
 * Cache that stores quadratic norms. It has a fix dimension.
 * Every example can be assigned to a fix position in the cache that depends on its ID.
 * It is an optimal solution for storing norms when the cache size is large enough to contain all the examples 
 * in the Dataset. When the Dataset is larger than the cache size, some collisions can occur.
 * 
 * @author      Simone Filice
 */

import it.uniroma2.sag.kelp.data.example.Example;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("fixIndex")
public class FixIndexSquaredNormCache implements SquaredNormCache{
	
	private int size;
	private float [] normValues;
	private long [] storedExample;
	private static final int INVALID_EXAMPLE_VALUE = -1;
	
	/**
	 * Initializes a cache with a defined dimension for squared norms 
	 * 
	 * @param cacheSize the number of squared norms the cache can simultaneously store
	 */
	public FixIndexSquaredNormCache(int cacheSize){
		this.setSize(cacheSize);
	}
	
	public FixIndexSquaredNormCache(){
		
	}
		
	/**
	 * Returns the size of the cache, i.e. the number of norms that can be simultaneously stored
	 * 
	 * @return the size of the cache
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of the cache, i.e. the number of norms that can be simultaneously stored
	 * 
	 * @param size the size to set
	 * 
	 * <p>
	 * NOTE: when this methods is invoked, the cache is initialized from scratch, so all the currently stored norms 
	 * will be lost 
	 */
	public void setSize(int cacheSize) {
		this.size=cacheSize;
		this.normValues = new float[cacheSize];
		this.storedExample = new long[cacheSize];
		Arrays.fill(storedExample, INVALID_EXAMPLE_VALUE);
	}
	
	@Override
	public Float getSquaredNorm(Example example){
		int exampleIndex = this.getExampleIndex(example);
		if(this.storedExample[exampleIndex]==example.getId()){
			return new Float(this.normValues[exampleIndex]);
		}
		
		return null;
	}
	
	private int getExampleIndex(Example example){
		return (int) (example.getId()%this.size);
	}
	
	@Override
	public void setSquaredNormValue(Example example, float squaredNorm){
		int exampleIndex = this.getExampleIndex(example);
		this.storedExample[exampleIndex] = example.getId();
		this.normValues[exampleIndex] = squaredNorm;
	}

	@Override
	public void flush() {
		Arrays.fill(storedExample, INVALID_EXAMPLE_VALUE);
	}

}
