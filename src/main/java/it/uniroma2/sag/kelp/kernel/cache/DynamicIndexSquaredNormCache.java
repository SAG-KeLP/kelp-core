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
 * When the cache is full a Last recently used strategy is applied for eliminating some entries
 * 
 * @author      Simone Filice
 */

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import it.uniroma2.sag.kelp.data.example.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("dynamicIndex")
public class DynamicIndexSquaredNormCache implements SquaredNormCache{
	
	private static final float INVALID_NORM_VALUE = Float.NaN; // do not change this
	private static final float FLUSHING_PERCENTAGE = 0.1f;
	private static final long NULL_EXAMPLE_ID = -1;
	private static final int NULL_POSITION = -1;

	private int examplesToStore;
	private int examplesToDiscard;
	
	private TLongIntHashMap fromExampleIdToCachePosition;
	private TIntLongHashMap fromCachePositionToExampleId;
	private float [] normValues;
	private long accessesCounter;
	private long [] exampleLastAccess;
	private ArrayList<Integer> freePositions;
	
	


	/**
	 * Initializes a DynamicIndexSquaredNormCache that can contain all up to <code>examplesToStore</code> norms
	 * 
	 * @param examplesToStore the maximum number of norms that
	 * can be simultaneously stored
	 */
	public DynamicIndexSquaredNormCache(int examplesToStore){
		this();
		setExamplesToStore(examplesToStore);
	}

	public DynamicIndexSquaredNormCache(){
		this.freePositions = new ArrayList<Integer>();
	}

	/**
	 * Returns the maximum number of norms that
	 * can be simultaneously stored
	 * 
	 * @return the examplesToStore
	 */
	public int getExamplesToStore() {
		return examplesToStore;
	}

	/**
	 * Sets the maximum number of norms that
	 * can be simultaneously stored
	 * 
	 * @param examplesToStore the examplesToStore to set
	 * <p>
	 * NOTE: all the already stored norms will be lost
	 */
	public void setExamplesToStore(int examplesToStore) {
		this.examplesToStore = examplesToStore;

		this.fromExampleIdToCachePosition = new TLongIntHashMap(examplesToStore, 0.75f, NULL_EXAMPLE_ID, NULL_POSITION);
		this.fromCachePositionToExampleId = new TIntLongHashMap(examplesToStore, 0.75f, NULL_POSITION, NULL_EXAMPLE_ID);

		this.normValues = new float [examplesToStore];
		//Arrays.fill(this.cachedExample, INVALID_EXAMPLE_VALUE);
		Arrays.fill(this.normValues, INVALID_NORM_VALUE);
		
		for(int i=0; i<examplesToStore; i++){
			this.freePositions.add(i);
		}
		this.exampleLastAccess = new long[examplesToStore];
		Arrays.fill(this.exampleLastAccess, 0);
		this.accessesCounter=0;
		this.examplesToDiscard = (int) (FLUSHING_PERCENTAGE*this.examplesToStore)+1;
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
			this.normValues[index]=INVALID_NORM_VALUE;
			this.exampleLastAccess[index]=0;
			
			this.fromExampleIdToCachePosition.remove(this.fromCachePositionToExampleId.get(index));
			this.fromCachePositionToExampleId.remove(index);
		}

	}
	
//	@Override
//	public void flushCache() {
//		this.accessesCounter=0;
//		this.fromExampleIdToCachePosition.clear();
//		this.fromCachePositionToExampleId.clear();
//		Arrays.fill(this.exampleLastAccess, 0);
//		Arrays.fill(this.kernelValue, INVALID_KERNEL_VALUE);
//		this.freePositions.clear();
//		for(int i=0; i<examplesToStore; i++){
//			this.freePositions.add(i);
//		}
//		this.accessesCounter=0;
//		
//	}
	
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

	@Override
	public Float getSquaredNorm(Example example) {
		long id = example.getId();
	
		int position = this.fromExampleIdToCachePosition.get(id);

		if(position==NULL_POSITION){
			return null;
		}

		float value =this.normValues[position];
		if(Float.isNaN(value)){
			return null;
		}
		this.exampleLastAccess[position] = this.accessesCounter;
		this.accessesCounter++;
		//System.out.println("found: " + value);
		return new Float(value);
	}

	@Override
	public void setSquaredNormValue(Example example, float squaredNorm) {
		long id = example.getId();
		
		int position = this.fromExampleIdToCachePosition.get(id);

		if(position!=NULL_POSITION){
			this.exampleLastAccess[position] = this.accessesCounter;
		}else{
			position=this.insertNewExample(id);
			
		}
		
		this.normValues[position] = squaredNorm;
		this.accessesCounter++;
		
	}

	@Override
	public void flush() {
		this.accessesCounter=0;
		this.fromExampleIdToCachePosition.clear();
		this.fromCachePositionToExampleId.clear();
		Arrays.fill(this.exampleLastAccess, 0);
		Arrays.fill(this.normValues, INVALID_NORM_VALUE);
		this.freePositions.clear();
		for(int i=0; i<examplesToStore; i++){
			this.freePositions.add(i);
		}
		this.accessesCounter=0;
	}

}
