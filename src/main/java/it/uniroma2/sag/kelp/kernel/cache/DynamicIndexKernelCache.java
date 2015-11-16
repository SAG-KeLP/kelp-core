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

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import it.uniroma2.sag.kelp.data.example.Example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * Cache for kernel computations. It can stores all the pairwise kernel computations
 * among any set of examples with cardinality not exceeding the <code>exampleToStore</code>
 * parameter. It is the optimal solution when all the pairwise kernel computations
 * between all the examples in the datasets can be simultaneously stored in cache, but examples IDs
 * are not consecutive making FixIndexKernelCache not working. 
 * Given a number of examples to store m, the memory occupation is about m*(m+1)/2 floats = m*(m+1)*2B
 * it uses a Least Recently Used policy when free space is required
 * 
 * @author      Simone Filice
 */
@JsonTypeName("dynamicIndex")
public class DynamicIndexKernelCache extends KernelCache implements Serializable{

	private static final long serialVersionUID = -4866777451585203988L;
	private static final float INVALID_KERNEL_VALUE = Float.NaN; // do not change this
	private static final float FLUSHING_PERCENTAGE = 0.1f;
	private static final long NULL_EXAMPLE_ID = -1;
	private static final int NULL_POSITION = -1;

	private int cacheSize;//the number of kernel computations that can be stored
	private int examplesToStore;
	private int examplesToDiscard;
	
	private TLongIntHashMap fromExampleIdToCachePosition;
	private TIntLongHashMap fromCachePositionToExampleId;
	private float [] kernelValue;
	private long accessesCounter;
	private long [] exampleLastAccess;
	private ArrayList<Integer> freePositions;
	
	


	/**
	 * Initializes a DynamicIndexKernelCache that can contain all the possible pairwise kernel computations
	 * between up to <code>examplesToStore</code> examples
	 * 
	 * @param examplesToStore the maximum number of examples whose pairwise kernel computations
	 * can be simultaneously stored
	 */
	public DynamicIndexKernelCache(int examplesToStore){
		this();
		setExamplesToStore(examplesToStore);
	}

	public DynamicIndexKernelCache(){
		super();
		this.freePositions = new ArrayList<Integer>();
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

		this.fromExampleIdToCachePosition = new TLongIntHashMap(examplesToStore, 0.75f, NULL_EXAMPLE_ID, NULL_POSITION);
		this.fromCachePositionToExampleId = new TIntLongHashMap(examplesToStore, 0.75f, NULL_POSITION, NULL_EXAMPLE_ID);

		this.kernelValue = new float [this.cacheSize];
		//Arrays.fill(this.cachedExample, INVALID_EXAMPLE_VALUE);
		Arrays.fill(this.kernelValue, INVALID_KERNEL_VALUE);
		
		for(int i=0; i<examplesToStore; i++){
			this.freePositions.add(i);
		}
		this.exampleLastAccess = new long[examplesToStore];
		Arrays.fill(this.exampleLastAccess, 0);
		this.accessesCounter=0;
		this.examplesToDiscard = (int) (FLUSHING_PERCENTAGE*this.examplesToStore)+1;
	}

	@Override
	protected Float getStoredKernelValue(Example exA, Example exB) {
		long idA = exA.getId();
		long idB = exB.getId();

		int positionA = this.fromExampleIdToCachePosition.get(idA);
		int positionB = this.fromExampleIdToCachePosition.get(idB);

		if(positionA==NULL_POSITION || positionB==NULL_POSITION){
			return null;
		}
		
		int kernelIndex=this.getKernelValueIndex(positionA, positionB);

		float value =this.kernelValue[kernelIndex];
		if(Float.isNaN(value)){
			return null;
		}
		this.exampleLastAccess[positionA] = this.accessesCounter;
		this.accessesCounter++;
		this.exampleLastAccess[positionB] = this.accessesCounter;
		this.accessesCounter++;
		//System.out.println("found: " + value);
		return new Float(value);
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
	public void setKernelValue(Example exA, Example exB, float value) {
		long idA = exA.getId();
		long idB = exB.getId();
		
		int positionA = this.fromExampleIdToCachePosition.get(idA);
		int positionB = this.fromExampleIdToCachePosition.get(idB);

		if(positionA!=NULL_POSITION){
			this.exampleLastAccess[positionA] = this.accessesCounter;
		}
		if(positionB!=NULL_POSITION){
			this.exampleLastAccess[positionB] = this.accessesCounter;
		}
		
		if(positionA==NULL_POSITION){
			//System.out.println("not found example " + exA.getId());
			positionA=this.insertNewExample(idA);
			
		}
		
		if(positionB==NULL_POSITION){
			if(idA==idB){
				positionB=positionA;
			}else{
				positionB=this.insertNewExample(idB);
			}
			//System.out.println("not found example " + exA.getId());
			
			
		}

		int kernelIndex=this.getKernelValueIndex(positionA, positionB);
		this.kernelValue[kernelIndex] = value;
		this.accessesCounter++;


	}
	
	private int insertNewExample(long exampleID){
		
		if(this.freePositions.size()==0){
			this.removeOldValues();
		}
		
		int position = this.freePositions.remove(this.freePositions.size()-1);
		this.fromExampleIdToCachePosition.put(exampleID, position);
		this.fromCachePositionToExampleId.put(position, exampleID);
		this.exampleLastAccess[position] = this.accessesCounter;
		return position;
	}
	
	private void removeOldValues(){
		LinkedList<Integer> indicesToClean = findIndicesSmallerNValues(this.exampleLastAccess, this.examplesToDiscard);
		long accessShift = this.exampleLastAccess[indicesToClean.getFirst()];
		for(int i=0; i<this.exampleLastAccess.length; i++){
			this.exampleLastAccess[i]=this.exampleLastAccess[i]-accessShift;
		}
		this.accessesCounter -= accessShift;
		
		for(int index : indicesToClean){
			this.freePositions.add(index);
			this.invalidKernelValues(index);
			this.exampleLastAccess[index]=0;
			
			this.fromExampleIdToCachePosition.remove(this.fromCachePositionToExampleId.get(index));
			this.fromCachePositionToExampleId.remove(index);
		}

	}

	private void invalidKernelValues(int exampleIndex){
		
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
		this.accessesCounter=0;
		this.fromExampleIdToCachePosition.clear();
		this.fromCachePositionToExampleId.clear();
		Arrays.fill(this.exampleLastAccess, 0);
		Arrays.fill(this.kernelValue, INVALID_KERNEL_VALUE);
		this.freePositions.clear();
		for(int i=0; i<examplesToStore; i++){
			this.freePositions.add(i);
		}
		this.accessesCounter=0;
		
	}
	
	private static LinkedList<Integer> findIndicesSmallerNValues(long [] values, int n){
		LinkedList<Integer> indices = new LinkedList<Integer>();
		for(int i=0; i<n; i++){
			insertIntoOrderedList(indices, values, i, values[i]);
		}
		
		for(int i=n; i<values.length; i++){
			if(values[i]<values[indices.get(0)]){
				indices.remove();
				insertIntoOrderedList(indices, values, i, values[i]);
			}
		}
		return indices;
	}
	
	private static void insertIntoOrderedList(List<Integer> indices, long[] values, int elementIndex, long elementValue){
		int position=0;
		for(int index : indices){
			if(values[index]<elementValue){
				break;
			}
			position++;
		}
		indices.add(position, elementIndex);
	}

}
