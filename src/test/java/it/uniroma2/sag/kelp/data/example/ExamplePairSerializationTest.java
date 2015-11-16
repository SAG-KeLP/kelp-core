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

package it.uniroma2.sag.kelp.data.example;

import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.pairs.PreferenceKernel;
import it.uniroma2.sag.kelp.kernel.standard.LinearKernelCombination;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for serializing and deserializing ExamplePairs via JSON.
 * Moreover the serialization from the dataset format is tested
 * 
 * @author Simone Filice
 *
 */
public class ExamplePairSerializationTest {
	
	private Example pairA;
	private Example pairB;
	private static final String DENSE_NAME = "Dense";
	private static final String SPARSE_NAME = "Sparse";
	
	private LinearKernelCombination kernel;
	

	
	/**
	 * This method will be executed before each test method.
	 */
	@Before
	public void initializeExamples() {
		String textualA = "class1 class2 |<| lab1 |BDV:" + DENSE_NAME + "| 0.5 1 |EDV| |,| |BDV:" + DENSE_NAME + "| -3 1 |EDV| |>| |BV:" + SPARSE_NAME + "| ptkSim:0.8 |EV|";
		String textualB = "class1 class2 |<| lab1 |BDV:" + DENSE_NAME + "| -0.5 1.3 |EDV| |,| |BDV:" + DENSE_NAME + "| 0 1 |EDV| |>| |BV:" + SPARSE_NAME + "| ptkSim:0.3 |EV|";
		

		try {
			pairA = ExampleFactory.parseExample(textualA);
			pairB = ExampleFactory.parseExample(textualB);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.fail();
		}
	

		Kernel kernelOnDense = new LinearKernel(DENSE_NAME);
		PreferenceKernel preference = new PreferenceKernel(kernelOnDense);
		Kernel kernelOnSparse = new LinearKernel(SPARSE_NAME);
		kernel = new LinearKernelCombination();
		kernel.addKernel(1, preference);
		kernel.addKernel(2, kernelOnSparse);
	}
	
	@Test
	public void testExampleFactoryCorrectness(){
		float aVsA = kernel.innerProduct(pairA, pairA);
		Assert.assertEquals(13.53, aVsA, 0.00001);
		float aVsB = kernel.innerProduct(pairA, pairB);
		Assert.assertEquals(-1.27, aVsB, 0.00001);
		float bVsA = kernel.innerProduct(pairB, pairA);
		Assert.assertEquals(-1.27, bVsA, 0.00001);
		float bVsB = kernel.innerProduct(pairB, pairB);
		Assert.assertEquals(0.52, bVsB, 0.00001);
	}
	
	@Test
	public void testJsonSerialization(){
		Example aCopy = SimpleExampleSerializationTest.copyViaJSON(pairA);
		Example bCopy = SimpleExampleSerializationTest.copyViaJSON(pairB);
		
		assertSameLabels(aCopy, pairA);
		assertSameLabels(bCopy, pairB);
		
		Assert.assertEquals(kernel.innerProduct(pairA, pairA), kernel.innerProduct(aCopy, aCopy), 0.0001);
		Assert.assertEquals(kernel.innerProduct(pairA, pairB), kernel.innerProduct(aCopy, bCopy), 0.0001);
		Assert.assertEquals(kernel.innerProduct(pairB, pairA), kernel.innerProduct(bCopy, aCopy), 0.0001);
		Assert.assertEquals(kernel.innerProduct(pairB, pairB), kernel.innerProduct(bCopy, bCopy), 0.0001);

	}

	public static void assertSameLabels(Example exA, Example exB){
		SimpleExampleSerializationTest.assertSameLabels(exA, exB);
		if(exA instanceof ExamplePair){
			ExamplePair pairA = (ExamplePair) exA;
			ExamplePair pairB = (ExamplePair) exB;
			assertSameLabels(pairA.getLeftExample(), pairB.getLeftExample());
			assertSameLabels(pairA.getRightExample(), pairB.getRightExample());
		}
	}
}
