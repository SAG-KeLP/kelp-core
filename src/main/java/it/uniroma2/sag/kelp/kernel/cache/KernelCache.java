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

import it.uniroma2.sag.kelp.data.example.Example;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;


/**
 * Generic Cache for kernel computations 
 * 
 * @author      Simone Filice
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "cacheType")
@JsonTypeIdResolver(KernelCacheTypeResolver.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "cacheID")
public abstract class KernelCache {

	private long cacheHit=0;
	private long cacheMiss=0;
	
	/**
	 * Retrieves in the cache the kernel operation between two examples
	 * 
	 * @param exA the first example
	 * @param exB the second example
	 * @return the kernel similarity, <code>null<\code> if a cache miss occurs
	 */
	public Float getKernelValue(Example exA, Example exB){
		Float value=this.getStoredKernelValue(exA, exB);
		if(value==null){
			cacheMiss++;
		}else{
			cacheHit++;
		}
		return value;
	}
	
	/**
	 * Retrieves in the cache the kernel operation between two examples
	 * 
	 * @param exA the first example
	 * @param exB the second example
	 * @return the kernel similarity, <code>null<\code> if a cache miss occurs
	 */
	protected abstract Float getStoredKernelValue(Example exA, Example exB);


	/**
	 * @return the number of cache hits
	 */
	@JsonIgnore
	public long getCacheHits(){
		return this.cacheHit;
	}
	
	/**
	 * @return the number of cache misses
	 */
	@JsonIgnore
	public long getCacheMisses(){
		return this.cacheMiss;
	}
	
	/**
	 * Sets cache hits and misses to 0
	 */
	@JsonIgnore
	public void resetCacheStats(){
		this.cacheHit=0;
		this.cacheMiss=0;
	}
	
	/**
	 * Stores a kernel computation in cache
	 * 
	 * @param exA the first example
	 * @param exB the second example
	 * @param value the kernel value to be stored in cache
	 */
	public abstract void setKernelValue(Example exA, Example exB, float value);
	
	
	/**
	 * Empties the cache
	 */
	public abstract void flushCache();
}
