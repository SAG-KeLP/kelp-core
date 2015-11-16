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

package it.uniroma2.sag.kelp.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.cache.KernelCache;
import it.uniroma2.sag.kelp.kernel.cache.SquaredNormCache;


/**
 * Abstract class for a generic kernel function
 * 
 * @author Simone Filice
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "kernelType")
@JsonTypeIdResolver(KernelTypeResolver.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "kernelID")
public abstract class Kernel {

	@JsonIgnore
	private long numberOfKernelComputations=0;

	@JsonIgnore
	private long numberOfHits=0;

	@JsonIgnore
	private SquaredNormCache normCache=null;

	@JsonIgnore
	private KernelCache cache = null;

	/**
	 * Returns the kernel similarity between the given examples. This
	 * corresponds to the inner product in the implicit Reproducing Kernel
	 * Hilbert Space (RKHS)
	 * 
	 * @param exA
	 *            the first example
	 * @param exB
	 *            the second example
	 * @return the kernel operation between <code>exA</code> and
	 *         <code>exB</code>
	 */
	public final float innerProduct(Example exA, Example exB) {
		this.numberOfKernelComputations++;
		float kernelResult;
		Example first = exA;
		Example second = exB;
		if(exA.getId()> exB.getId()){
			first=exB;
			second = exA;
		}
		if (this.cache != null) {
			Float cacheValue = this.cache.getKernelValue(first, second);
			if(cacheValue != null){
				this.numberOfHits++;
				kernelResult = cacheValue.floatValue();
			}else{
				kernelResult = this.kernelComputation(first, second);
				this.cache.setKernelValue(first, second, kernelResult);
			}
		}else{
			kernelResult = this.kernelComputation(first, second);
		}
		return kernelResult;
	}

	/**
	 * Sets the cache in which storing the squared norms in the RKHS defined
	 * by this kernel
	 * 
	 * @param normCache
	 *            the cache for the squared norms
	 */
	public void setSquaredNormCache(SquaredNormCache normCache){
		this.normCache= normCache;
	}

	/**
	 * Returns the cache in which storing the squared norms in the RKHS defined
	 * by this kernel
	 * 
	 * @return the cache for the squared norms
	 */
	public SquaredNormCache getSquaredNormCache(){
		return this.normCache;
	}

	/**
	 * Sets the cache in which storing the kernel operations in the RKHS defined
	 * by this kernel
	 * 
	 * @param cache
	 *            the cache for the kernel operations
	 */
	public void setKernelCache(KernelCache cache) {
		this.cache = cache;
	}

	/**
	 * Returns the cache in which storing the kernel operations in the RKHS defined
	 * by this kernel
	 * 
	 * @param cache
	 *            the cache for the kernel operations
	 */
	public KernelCache getKernelCache() {
		return this.cache;
	}

	/**
	 * Returns the kernel similarity between the given examples. This
	 * corresponds to the inner product in the implicit Reproducing Kernel
	 * Hilbert Space (RKHS)
	 * 
	 * @param exA
	 *            the first example
	 * @param exB
	 *            the second example
	 * @return the kernel operation between <code>exA</code> and
	 *         <code>exB</code>
	 */
	protected abstract float kernelComputation(Example exA, Example exB);

	/**
	 * Returns the squared norm of the given example in the RKHS defined by this kernel
	 * 
	 * @param example the example whose squared norm must be computed
	 * @return the squared norm in the RKHS
	 */

	public float squaredNorm(Example example){

		this.numberOfKernelComputations++;
		if (this.normCache != null) {

			Float cacheValue = this.normCache.getSquaredNorm(example);
			if (cacheValue == null) {


				float squaredNorm=this.innerProduct(example, example);
				this.normCache.setSquaredNormValue(example, squaredNorm);
				return squaredNorm;
			}
			this.numberOfHits++;
			return cacheValue.floatValue();
		}
		return this.innerProduct(example, example);
	}

	/**
	 * Returns the squared norm of the difference between the given examples in the RKHS.
	 * It computes ||exA-exB||^2 = <exA,exA> + <exB,exB> - 2*<exA.exB> where <a,b> defines the inner product between
	 * two generic examples a and b in the RKHS defined by this kernel
	 * 
	 * @param exA
	 *            the first example
	 * @param exB
	 *            the second example
	 * @return the squared norm of the difference in the RKHS
	 */

	public float squaredNormOfTheDifference(Example exA, Example exB){
		float normA=this.squaredNorm(exA);
		float normB=this.squaredNorm(exB);
		float innerProduct=this.innerProduct(exA, exB);
		return normA+normB-2*innerProduct;
	}

	/**
	 * Disables the kernel cache
	 */
	public void disableCache() {
		this.cache = null;
		this.normCache = null;
	}	

	/**
	 * Returns the number of times the kernel function has been invoked
	 * 
	 * @return the number of performed kernel operations
	 */
	@JsonIgnore
	public long getKernelComputations(){
		return numberOfKernelComputations;
	}

	/**
	 * Returns the number of times a cache hit happened
	 * 
	 * @return the number of cache hits
	 */
	@JsonIgnore
	public long getNumberOfHits(){
		return numberOfHits;
	}

	/**
	 * Returns the number of times a cache miss happened
	 * 
	 * @return the number of cache misses
	 */
	@JsonIgnore
	public long getNumberOfMisses(){
		return numberOfKernelComputations-numberOfHits;
	}

	/**
	 * Resets the kernel statistics (number of kernel computations,
	 * cache hits and misses)
	 */
	public void reset(){
		this.numberOfHits=0;
		this.numberOfKernelComputations=0;
	}

	/**
	 * Save the input kernel in a file. If the .gz extension is specified, the
	 * file is compressed.
	 * 
	 * @param kernel
	 *            The input kernel
	 * @param outputFilePath
	 *            The output file path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void save(Kernel kernel, String outputFilePath)
			throws FileNotFoundException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		if (outputFilePath.endsWith(".gz")) {
			GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(
					new File(outputFilePath)));
			mapper.writeValue(zip, kernel);
		} else {
			mapper.writeValue(new File(outputFilePath), kernel);
		}

	}

	/**
	 * Load a kernel function from a file path. If the .gz extension is
	 * specified, the file considered compressed.
	 * 
	 * @param kernel
	 *            The input kernel
	 * @param outputFilePath
	 *            The output file path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Kernel load(String inputFilePath)
			throws FileNotFoundException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		if (inputFilePath.endsWith(".gz")) {
			GZIPInputStream zip = new GZIPInputStream(new FileInputStream(
					new File(inputFilePath)));
			return mapper.readValue(zip, Kernel.class);
		} else {
			return mapper.readValue(new File(inputFilePath), Kernel.class);
		}
	}


}
