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
 * Cache for kernel computations. It is the optimal solution when all the pairwise kernel computations
 * between all the examples in the Dataset can be simultaneously stored in cache. 
 * Given a number of examples to store m, the memory occupation is about m*(m+1)/2 floats = m*(m+1)*2B
 * Once the cache is initialized, its dimension is immutable. 
 * Every example has some reserved cache space that depends on its ID. It means that if two examples have
 * have IDs pointing at the same memory space, their kernel computations cannot be simultaneously stored
 * in cache.   
 * 
 * @author      Simone Filice
 */


import it.uniroma2.sag.kelp.data.example.Example;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("fixIndex")
public class FixIndexKernelCache extends KernelCache implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private static final float INVALID_KERNEL_VALUE = Float.NaN; // se si cambia, bisogna cambiare anche il getKernelValue
	private static final long INVALID_EXAMPLE_VALUE = -1;
	
	private int cacheSize;//the number of kernel computations that can be stored
	private int examplesToStore;

	private long [] cachedExample;
	private float [] kernelValue;
	
	/**
	 * Initializes a FixIndexKernelCache that can contain all the possible pairwise kernel computations
	 * between up to <code>examplesToStore</code> examples
	 * 
	 * @param examplesToStore the maximum number of examples whose pairwise kernel computations
	 * can be simultaneously stored
	 */
	public FixIndexKernelCache(int examplesToStore){
		super();
		setExamplesToStore(examplesToStore);
	}
	
	public FixIndexKernelCache(){
		super();
	}
	
	/**
	 * Returns the maximum number of examples whose pairwise kernel computations
	 * can be simultaneously stored
	 * 
	 * @return the examplesToStore
	 */
	public int getExamplesToStore() {
		return examplesToStore;
	}

	/**
	 * Sets the maximum number of examples whose pairwise kernel computations
	 * can be simultaneously stored
	 * 
	 * @param examplesToStore the examplesToStore to set
	 * <p>
	 * NOTE: all the already stored kernel computations will be lost
	 */
	public void setExamplesToStore(int examplesToStore) {
		this.examplesToStore = examplesToStore;
		this.cacheSize = examplesToStore*(examplesToStore+1)/2;
		this.cachedExample = new long[examplesToStore];
		this.kernelValue = new float [this.cacheSize];
		Arrays.fill(this.cachedExample, INVALID_EXAMPLE_VALUE);
		Arrays.fill(this.kernelValue, INVALID_KERNEL_VALUE);
	}
	
	@Override
	protected Float getStoredKernelValue(Example exA, Example exB){
		
		int indexA = this.getExampleIndex(exA);
		int indexB = this.getExampleIndex(exB);
		
		if(this.cachedExample[indexA]!=exA.getId() || this.cachedExample[indexB]!=exB.getId()){
			return null;
		}
				
		int kernelValueIndex = this.getKernelValueIndex(indexA, indexB);
		
		if(Float.isNaN(this.kernelValue[kernelValueIndex])){			
			return null;
		}
		
		return new Float(this.kernelValue[kernelValueIndex]);
	}
	
	private int getExampleIndex(Example exA){
		return (int) (exA.getId()%this.examplesToStore);
	}
	
	private int getKernelValueIndex(int indexA, int indexB){
		
		int minimum=indexA;
		int maximum=indexB;
		if(indexA>indexB){
			minimum=indexB;
			maximum=indexA;
		}
		if(minimum==0){
			return maximum;
		}
		
		int index=minimum*(this.examplesToStore-1);
		index-=((minimum-1)*minimum)>>1;
				
		return index+maximum;
		
	}
	
	@Override
	public void setKernelValue(Example exA, Example exB, float value){
		
		int indexA = this.getExampleIndex(exA);
		int indexB = this.getExampleIndex(exB);
		
		if(indexA==indexB && exA.getId()!=exB.getId()){
			//collision among examples, it is not possible to store this kernel 
			return;
		}
		
		if(this.cachedExample[indexA]!=exA.getId()){
			if(this.cachedExample[indexA]!=INVALID_EXAMPLE_VALUE){
				this.invalidateKernelValues(indexA);
			}
			this.cachedExample[indexA]=exA.getId();
		}
		
		if(this.cachedExample[indexB]!=exB.getId()){
			if(this.cachedExample[indexB]!=INVALID_EXAMPLE_VALUE){
				this.invalidateKernelValues(indexB);
			}
			this.cachedExample[indexB]=exB.getId();
		}
		
		int kernelIndex=this.getKernelValueIndex(indexA, indexB);
		this.kernelValue[kernelIndex]=value;
	}
	
	private void invalidateKernelValues(int exampleIndex){
		int counter=0;
		int baseIndex=0;
		int indexToInvalidate=0;
		while(counter<exampleIndex){
			indexToInvalidate=baseIndex+exampleIndex;
			
			this.kernelValue[indexToInvalidate] = INVALID_KERNEL_VALUE; 
			counter++;
			baseIndex+=(this.examplesToStore-counter);
		}
		
		int startingIndex = this.getKernelValueIndex(exampleIndex, exampleIndex);
		for(int i = 0; i<this.examplesToStore-counter; i++){
			this.kernelValue[startingIndex+i] = INVALID_KERNEL_VALUE;
		}
	}
	
	@Override
	public void flushCache() {
		for(int i=0; i<this.examplesToStore; i++){
			this.cachedExample[i] = INVALID_EXAMPLE_VALUE;
		}
		for(int i=0; i<this.cacheSize; i++){
			this.kernelValue[i] = INVALID_KERNEL_VALUE;
		}
	
	}

	
}
