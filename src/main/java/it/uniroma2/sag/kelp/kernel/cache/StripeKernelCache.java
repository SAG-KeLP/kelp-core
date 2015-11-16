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

import gnu.trove.map.hash.TLongIntHashMap;
import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.example.Example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Given a dataset, this cache stores kernel computations in "Stripes", i.e.
 * whole rows of the complete Gram Matrix. In other words given a subset S of
 * the examples in the dataset D, the cache is able to store all the kernel
 * computations between any example in S and any example in D. To reduce the
 * requirement of memory space, the cache stores only <code>n</code> stripes.
 * When the number of stripes is exceeded, they are removed according to a FIFO
 * policy.
 * 
 * 
 * @author Danilo Croce
 * 
 */
@JsonTypeName("stripe")
public class StripeKernelCache extends KernelCache implements Serializable {

	private static final long serialVersionUID = -4040974882736610829L;

	/**
	 * A float to set a cache element as invalid. <br>
	 * NOTE: DO NOT CHAGE this constant. If you have to change this constant,
	 * you should change the isNan() function in getStoredKernelValue()
	 */
	private static final float INVALID_KERNEL_VALUE = Float.NaN;

	/**
	 * The maximum number of stripes that can be added to the cache
	 */
	private int maxNumberOfRows;

	/**
	 * The maximum number of different examples that can be added to the cache
	 * (i.e. the matrix columns in the matrix buffer). This size cannot be
	 * exceed.
	 */
	private int numberOfColumns;

	/**
	 * An array of vector containing the kernel values. Each row i in the matrix
	 * reflects the i-th example and the j-th column reflects the i-th example
	 */
	private float[][] buffer;

	/**
	 * The list of rows in <code>buffer</code> that are empty
	 */
	private ArrayList<Integer> freeRowsIds;

	/**
	 * The FIFO list of added examples. It helps to remove the "oldest" example
	 * when the cache is full
	 */
	private Queue<Long> examplesIdQueue;

	/**
	 * The map between Example Id and the row of the matrix to which the Example
	 * is assigned to
	 */
	private TLongIntHashMap rowDict;

	/**
	 * The map between Example Id and columns of the matrix to which the Example
	 * is assigned to
	 */
	private TLongIntHashMap columnDict;

	/**
	 * This counter is used to determine the assignment of Examples to the
	 * correct cache columns
	 */
	private int matrixColumnIndex;

	/**
	 * @param dataset
	 *            The cache is initialized with a number of rows (and columns)
	 *            that is equal to the size of the <code>dataset</code>
	 */
	public StripeKernelCache(Dataset dataset) {
		this(dataset.getNumberOfExamples(), dataset.getNumberOfExamples());
	}

	/**
	 * @param dataset
	 *            The cache is initialized with a number of columns that is
	 *            equal to the size of the dataset
	 * @param maxNumberOfItems
	 *            The maximum number of stripes in the dataset
	 */
	public StripeKernelCache(Dataset dataset, int maxNumberOfRows) {
		this(maxNumberOfRows, dataset.getNumberOfExamples());
	}

	/**
	 * @param maxNumberOfRows
	 *            The maximum number of stripes in the matrix
	 * @param numberOfColumns
	 *            The maximum number of columns in the matrix
	 */
	public StripeKernelCache(int maxNumberOfRows, int numberOfColumns) {
		this();
		setNumberOfColumns(numberOfColumns);
		setMaxNumberOfRows(maxNumberOfRows);
	}

	public StripeKernelCache() {
		this.matrixColumnIndex = 0;

		this.freeRowsIds = new ArrayList<Integer>();
		this.examplesIdQueue = new LinkedList<Long>();

		this.rowDict = new TLongIntHashMap();
		this.columnDict = new TLongIntHashMap();
	}

	/**
	 * @return the maxNumberOfRows
	 */
	public int getMaxNumberOfRows() {
		return maxNumberOfRows;
	}

	/**
	 * @param maxNumberOfRows
	 *            the maxNumberOfRows to set
	 */
	public void setMaxNumberOfRows(int maxNumberOfRows) {
		this.maxNumberOfRows = maxNumberOfRows;
		this.buffer = new float[this.maxNumberOfRows][];

		for (int rowId = 0; rowId < buffer.length; rowId++) {
			freeRowsIds.add(rowId);
		}
	}

	/**
	 * @return the numberOfColumns
	 */
	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	/**
	 * @param numberOfColumns
	 *            the numberOfColumns to set
	 */
	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	/**
	 * This function retrieves the kernel values from the matrix by using the
	 * <code>Example</code>s IDs
	 * 
	 * @param indexA
	 *            The Id of the first <code>Example</code>
	 * @param indexB
	 *            The Id of the second <code>Example</code>
	 * @return The cached kernel valued if present. INVALID_KERNEL_VALUE is
	 *         returned otherwise
	 */
	private float search(long indexA, long indexB) {

		if (!rowDict.containsKey(indexA) || !columnDict.containsKey(indexB))
			return INVALID_KERNEL_VALUE;

		int rowId = rowDict.get(indexA);
		int colId = columnDict.get(indexB);

		return buffer[rowId][colId];
	}

	private long lastAddedIndexRow=-1;

	@Override
	public void setKernelValue(Example exA, Example exB, float value) {

		// Get the example identifier
		long indexA = exA.getId();
		long indexB = exB.getId();

		if (indexA != lastAddedIndexRow) {
			indexB = exA.getId();
			indexA = exB.getId();
		}
		// System.out.println(indexA + " " + rowDict.containsKey(indexA) +
		// "\t-\t"
		// + indexB + " " + rowDict.containsKey(indexB));

		int rowId;
		int colId;

		// Get the column id in the matrix assigned to exB
		if (columnDict.containsKey(indexB)) {
			colId = columnDict.get(indexB);
		} else {
			// Each element should be added as column in the kernel buffer, so
			// it is
			// expected to find a free column in the matrix.
			if (matrixColumnIndex == numberOfColumns) {
				info("The example " + indexB
						+ " cannot be stored because the matrix (of size "
						+ numberOfColumns + " is full");
				return;
			}
			// Add to the dict a mapping between the example and the row in the
			// matrix buffer
			colId = matrixColumnIndex;
			columnDict.put(indexB, matrixColumnIndex);
			matrixColumnIndex++;
		}

		/*
		 * Get the row id in the matrix assigned to exA. If present, the kernel
		 * value is added to the buffer. Otherwise, a new row is added. If the
		 * matrix does not have enough rows to store this new item, the row
		 * corresponding to the oldest element is removed.
		 */
		// If the matrix contains a row assigned to exA, just add the kernel
		// value
		if (rowDict.containsKey(indexA)) {
			rowId = this.rowDict.get(indexA);
			this.buffer[rowId][colId] = value;
		}// Otherwise, a new row must be added. Note: do not add row for K_ii
		else if (indexA != indexB) {
			// If there are no free rows, the oldest element (i.e. the first
			// element in addedFIFOItems) is removed and the corresponding row
			// is cleared
			if (freeRowsIds.isEmpty()) {
				long elementToRemove = this.examplesIdQueue.poll();
				int rowToClear = this.rowDict.get(elementToRemove);
				// All the element in the row are set as invalid
				Arrays.fill(this.buffer[rowToClear], INVALID_KERNEL_VALUE);
				// A new element is added in the list of empty rows
				this.freeRowsIds.add(rowToClear);
				// The deleted row is removed from the dictionary
				this.rowDict.remove(elementToRemove);
			}
			// The first free row is selected and cleared
			rowId = this.freeRowsIds.get(0);
			this.freeRowsIds.remove(0);
			if (this.buffer[rowId] == null)
				this.buffer[rowId] = new float[this.numberOfColumns];
			Arrays.fill(this.buffer[rowId], INVALID_KERNEL_VALUE);
			this.examplesIdQueue.add(indexA);

			// The row is assigned to exA
			this.rowDict.put(indexA, rowId);
			// The value is added
			this.buffer[rowId][colId] = value;
		}

		lastAddedIndexRow = indexA;

	}

	private void info(String string) {
		System.err.println(string);
	}

	@Override
	public void flushCache() {
		this.matrixColumnIndex = 0;

		this.freeRowsIds.clear();
		this.examplesIdQueue.clear();

		this.rowDict.clear();
		this.columnDict.clear();

		for (int rowId = 0; rowId < buffer.length; rowId++) {
			freeRowsIds.add(rowId);
			if (buffer[rowId] == null)
				continue;
			for (int columnId = 0; columnId < buffer[rowId].length; columnId++)
				buffer[rowId][columnId] = INVALID_KERNEL_VALUE;
		}
		
		this.lastAddedIndexRow=-1;
	}

	@Override
	protected Float getStoredKernelValue(Example exA, Example exB) {

		long indexA = exA.getId();
		long indexB = exB.getId();

		float res = search(indexA, indexB);
		if (Float.isNaN(res))
			res = search(indexB, indexA);

		if (Float.isNaN(res)) {
			return null;
		} else {
			return res;
		}
	}
}
