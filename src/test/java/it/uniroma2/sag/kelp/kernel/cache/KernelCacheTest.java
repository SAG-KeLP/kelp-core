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

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;

import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class KernelCacheTest {

	private static SimpleDataset dataset;
	private static Kernel kernel;

	@BeforeClass
	public static void loadDatasetAndPrepareKernel() {
		dataset = new SimpleDataset();
		
		try {
			dataset.populate("src/test/resources/svmTest/binary/binary_test.klp");
			dataset.shuffleExamples(new Random());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		kernel = new LinearKernel("0");

	}

	@Test
	public void checkStripeKernelCache() {
		StripeKernelCache cache = new StripeKernelCache(dataset);
		checkNCorrectlyStored(cache, dataset, dataset.getNumberOfExamples()
				* dataset.getNumberOfExamples() - dataset.getNumberOfExamples());
		int nRows = 20;
		StripeKernelCache smallCache = new StripeKernelCache(dataset, nRows);
		checkFistRowsCorrectlyStored(smallCache, dataset, nRows,
				dataset.getNumberOfExamples() * nRows - nRows);
		
		checkNCorrectlyStored(smallCache, dataset, nRows);	
	}

	public void checkFistRowsCorrectlyStored(KernelCache cache,
			SimpleDataset dataset, int maxRows, int expectedHits) {

		kernel.setKernelCache(cache);
		int counter = 0;
		for (Example ex1 : dataset.getExamples()) {
			for (Example ex2 : dataset.getExamples()) {
				kernel.innerProduct(ex1, ex2);
			}
			counter++;
			if (counter >= maxRows)
				break;
		}
		kernel.disableCache();

		int hits = 0;
		counter = 0;
		for (Example ex1 : dataset.getExamples()) {
			for (Example ex2 : dataset.getExamples()) {
				float val = kernel.innerProduct(ex1, ex2);
				Float cachedVal = cache.getKernelValue(ex1, ex2);
				if (cachedVal != null) {
					Assert.assertEquals(val, cachedVal.floatValue(), 0.00001f);
					hits++;
				}
			}
			counter++;
			if (counter >= maxRows)
				break;
		}

		Assert.assertTrue(hits >= expectedHits);
		cache.flushCache();
	}
	
	@Test
	public void checkFixIndexKernelCache() {
		FixIndexKernelCache cache = new FixIndexKernelCache(dataset.getNumberOfExamples());
		checkNCorrectlyStored(cache, dataset, dataset.getNumberOfExamples()*dataset.getNumberOfExamples());
		FixIndexKernelCache smallCache = new FixIndexKernelCache(dataset.getNumberOfExamples()/2);
		checkNCorrectlyStored(smallCache, dataset, 0);
	}

	@Test
	public void checkDynamicIndexKernelCache() {
		DynamicIndexKernelCache cache = new DynamicIndexKernelCache(dataset.getNumberOfExamples());
		checkNCorrectlyStored(cache, dataset, dataset.getNumberOfExamples()*dataset.getNumberOfExamples());
		DynamicIndexKernelCache smallCache = new DynamicIndexKernelCache(dataset.getNumberOfExamples()/2);
		checkNCorrectlyStored(smallCache, dataset, 0);
		SimpleDataset [] smallDatasets = dataset.nFolding(2);
		smallCache.setExamplesToStore(smallDatasets[0].getNumberOfExamples());
		checkNCorrectlyStored(smallCache, smallDatasets[0], smallDatasets[0].getNumberOfExamples()*smallDatasets[0].getNumberOfExamples());
		smallCache.setExamplesToStore(smallDatasets[1].getNumberOfExamples());
		checkNCorrectlyStored(smallCache, smallDatasets[1], smallDatasets[1].getNumberOfExamples()*smallDatasets[1].getNumberOfExamples());

	}

	public void checkNCorrectlyStored(KernelCache cache, SimpleDataset dataset, int n){

		kernel.setKernelCache(cache);
		for(Example ex1 : dataset.getExamples()){
			for(Example ex2 : dataset.getExamples()){
				kernel.innerProduct(ex1, ex2);
			}

		}
		kernel.disableCache();

		int hits = 0;
		for(Example ex1 : dataset.getExamples()){
			for(Example ex2 : dataset.getExamples()){
				float val = kernel.innerProduct(ex1, ex2);
				Float cachedVal = cache.getKernelValue(ex1, ex2);
				if(cachedVal!=null){
					Assert.assertEquals(val, cachedVal.floatValue(), 0.00001f);
					hits++;
				}

			}

		}

		Assert.assertTrue(hits>=n);
		cache.flushCache();
	}

	@Test
	public void checkFixIndexSquaredNormCache() {
		FixIndexSquaredNormCache cache = new FixIndexSquaredNormCache(dataset.getNumberOfExamples());
		checkNCorrectlyStored(cache, dataset, dataset.getNumberOfExamples());
		FixIndexSquaredNormCache smallCache = new FixIndexSquaredNormCache(dataset.getNumberOfExamples()/2);
		checkNCorrectlyStored(smallCache, dataset, 0);
	}
	
	@Test
	public void checkDynamicIndexSquaredNormCache() {
		DynamicIndexSquaredNormCache cache = new DynamicIndexSquaredNormCache(dataset.getNumberOfExamples());
		checkNCorrectlyStored(cache, dataset, dataset.getNumberOfExamples());
		DynamicIndexSquaredNormCache smallCache = new DynamicIndexSquaredNormCache(dataset.getNumberOfExamples()/2);
		checkNCorrectlyStored(smallCache, dataset, 0);
		SimpleDataset [] smallDatasets = dataset.nFolding(2);
		smallCache.setExamplesToStore(smallDatasets[0].getNumberOfExamples());
		checkNCorrectlyStored(smallCache, smallDatasets[0], smallDatasets[0].getNumberOfExamples());
		smallCache.setExamplesToStore(smallDatasets[1].getNumberOfExamples());
		checkNCorrectlyStored(smallCache, smallDatasets[1], smallDatasets[1].getNumberOfExamples());

	}
	
	public void checkNCorrectlyStored(SquaredNormCache cache, SimpleDataset dataset, int n){

		kernel.setSquaredNormCache(cache);
		for(Example ex1 : dataset.getExamples()){
			kernel.squaredNorm(ex1);
		}
		kernel.disableCache();

		int hits = 0;
		for(Example ex1 : dataset.getExamples()){

			float val = kernel.squaredNorm(ex1);
			Float cachedVal = cache.getSquaredNorm(ex1);
			if(cachedVal!=null){
				Assert.assertEquals(val, cachedVal.floatValue(), 0.00001f);
				hits++;
			}
		}

		Assert.assertTrue(hits>=n);
		cache.flush();
	}

	


}
