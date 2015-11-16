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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import it.uniroma2.sag.kelp.data.example.Example;

/**
 * Cache for store squared norms
 * 
 * @author      Simone Filice
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "cacheType")
@JsonTypeIdResolver(SquaredNormCacheTypeResolver.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "cacheID")
public interface SquaredNormCache {

	/**
	 * Returns a previously stored norm of a given example
	 * 
	 * @param example the instance whose squared norm is required
	 * @return the squared norm or <code>null</code> if a cache miss occurs
	 */
	public Float getSquaredNorm(Example example);
	
	/**
	 * Stores a squared norm in the cache
	 * 
	 * @param example the example whose quadratic norm must be stored
	 * @param squaredNorm the squared norm to be stored
	 */
	public void setSquaredNormValue(Example example, float squaredNorm);
	
	/**
	 * Empties the cache
	 */
	public void flush();
	
}
