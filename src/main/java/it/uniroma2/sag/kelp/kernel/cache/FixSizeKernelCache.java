/*
 * Copyright 2017 Simone Filice and Giuseppe Castellucci and Danilo 
 * Croce and Giovanni Da San Martino and Roberto Basili and Alessandro Moschitti
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.cache.KernelCache;


/**
 * Cache for kernel computations. It can stores all the pairwise kernel computations
 * among the k instances, where k is the <code>examplesToStore</code> parameter.
 * Once the cache is initialized, its dimension is immutable. 
 * The memory occupation is about k*(k+1)/2 floats.
 * It uses a Least Recently Used policy when free space is required.
 * 
 * @author      Simone Filice
 */
@JsonTypeName("fixSize")
public class FixSizeKernelCache extends KernelCache implements Serializable{

	private static final long serialVersionUID = -4866777451585203988L;
	private static final float INVALID_KERNEL_VALUE = Float.NaN; // do not change this
	private static final float FLUSHING_PERCENTAGE = 0.1f;
	private static final long NULL_EXAMPLE_ID = -1;
	private static final int NULL_POSITION = -1;

	private int examplesToStore;
	private int examplesToDiscard;
	
	private TLongIntHashMap fromExampleIdToCacheRow;
	private TIntLongHashMap fromCacheRowToExampleId;
	private float [][] kernelValues;
	private long accessesCounter;
	private long [] exampleLastAccess;
	private ArrayList<Integer> freeRows;
	
	


	/**
	 * Initializes a DynamicIndexKernelCache that can contain all the possible pairwise kernel computations
	 * between up to <code>examplesToStore</code> examples
	 * 
	 * @param examplesToStore the maximum number of examples whose pairwise kernel computations
	 * can be simultaneously stored
	 */
	public FixSizeKernelCache(int examplesToStore){
		this();
		setExamplesToStore(examplesToStore);
	}

	public FixSizeKernelCache(){
		super();
		this.freeRows = new ArrayList<Integer>();
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

		this.fromExampleIdToCacheRow = new TLongIntHashMap(examplesToStore, 0.75f, NULL_EXAMPLE_ID, NULL_POSITION);
		this.fromCacheRowToExampleId = new TIntLongHashMap(examplesToStore, 0.75f, NULL_POSITION, NULL_EXAMPLE_ID);

		this.kernelValues = new float [this.examplesToStore][];
		for(int i=0; i<examplesToStore; i++){
			this.kernelValues[i] = new float[examplesToStore-i];
			Arrays.fill(this.kernelValues[i], INVALID_KERNEL_VALUE);
		}
		//Arrays.fill(this.cachedExample, INVALID_EXAMPLE_VALUE);
		
		
		for(int i=0; i<examplesToStore; i++){
			this.freeRows.add(i);
		}
		this.exampleLastAccess = new long[examplesToStore];
		//Arrays.fill(this.exampleLastAccess, 0); NOT NEEDED
		this.accessesCounter=0;
		this.examplesToDiscard = (int) (FLUSHING_PERCENTAGE*this.examplesToStore)+1;
	}

	@Override
	protected Float getStoredKernelValue(Example exA, Example exB) {
		long idA = exA.getId();
		long idB = exB.getId();

		int rowA = this.fromExampleIdToCacheRow.get(idA);
		int rowB = this.fromExampleIdToCacheRow.get(idB);

		if(rowA==NULL_POSITION || rowB==NULL_POSITION){
			return null;
		}

		float value;
		if(rowA<rowB){
			value = this.kernelValues[rowA][rowB-rowA];
		}else{
			value = this.kernelValues[rowB][rowA-rowB];
		}

		if(Float.isNaN(value)){
			return null;
		}
		this.exampleLastAccess[rowA] = this.accessesCounter;
		this.accessesCounter++;
		this.exampleLastAccess[rowB] = this.accessesCounter;
		this.accessesCounter++;
		//System.out.println("found: " + value);
		return new Float(value);
	}

	@Override
	public void setKernelValue(Example exA, Example exB, float value) {
		long idA = exA.getId();
		long idB = exB.getId();
		
		int rowA = this.fromExampleIdToCacheRow.get(idA);
		int rowB = this.fromExampleIdToCacheRow.get(idB);

		if(rowA!=NULL_POSITION){
			this.exampleLastAccess[rowA] = this.accessesCounter;
		}
		if(rowB!=NULL_POSITION){
			this.exampleLastAccess[rowB] = this.accessesCounter;
		}
		
		if(rowA==NULL_POSITION){
			//System.out.println("not found example " + exA.getId());
			rowA=this.insertNewExample(idA);
			
		}
		
		if(rowB==NULL_POSITION){
			if(idA==idB){
				rowB=rowA;
			}else{
				rowB=this.insertNewExample(idB);
			}
			//System.out.println("not found example " + exA.getId());
			
			
		}

		if(rowA<rowB){
			this.kernelValues[rowA][rowB-rowA] = value;
		}else{
			this.kernelValues[rowB][rowA-rowB] = value;
		}
		this.accessesCounter++;
	}
	
	private int insertNewExample(long exampleID){
		
		if(this.freeRows.size()==0){
			this.removeOldValues();
		}
		
		int row = this.freeRows.remove(this.freeRows.size()-1);
		this.fromExampleIdToCacheRow.put(exampleID, row);
		this.fromCacheRowToExampleId.put(row, exampleID);
		this.exampleLastAccess[row] = this.accessesCounter;
		return row;
	}
	
	private void removeOldValues(){
		LinkedList<Integer> rowsToClean = findIndicesSmallerNValues(this.exampleLastAccess, this.examplesToDiscard);
		long accessShift = this.exampleLastAccess[rowsToClean.getFirst()];
		for(int i=0; i<this.exampleLastAccess.length; i++){
			this.exampleLastAccess[i]=this.exampleLastAccess[i]-accessShift;
		}
		this.accessesCounter -= accessShift;
		
		for(int row : rowsToClean){
			this.freeRows.add(row);
			this.invalidKernelValues(row);
			this.exampleLastAccess[row]=0;
			
			this.fromExampleIdToCacheRow.remove(this.fromCacheRowToExampleId.get(row));
			this.fromCacheRowToExampleId.remove(row);
		}

	}

	/*
	 * INVALIDATES ALL THE KERNEL COMPUTATIONS K(A,B) WHERE A is the example 
	 * associated to the row
	 */
	private void invalidKernelValues(int row){
		
		for(int i=0; i<row; i++){
			this.kernelValues[i][row-i] = INVALID_KERNEL_VALUE; 
		}
		Arrays.fill(this.kernelValues[row], INVALID_KERNEL_VALUE);
		
	}
	
	@Override
	public void flushCache() {
		this.accessesCounter=0;
		this.fromExampleIdToCacheRow.clear();
		this.fromCacheRowToExampleId.clear();
		Arrays.fill(this.exampleLastAccess, 0);
		for(float [] rowValues : this.kernelValues){
			Arrays.fill(rowValues, INVALID_KERNEL_VALUE);
		}
		
		this.freeRows.clear();
		for(int i=0; i<examplesToStore; i++){
			this.freeRows.add(i);
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
