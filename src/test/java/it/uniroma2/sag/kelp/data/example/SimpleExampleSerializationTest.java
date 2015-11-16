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
import it.uniroma2.sag.kelp.kernel.standard.LinearKernelCombination;
import it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel;
import it.uniroma2.sag.kelp.kernel.standard.PolynomialKernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;
import it.uniroma2.sag.kelp.utils.ObjectSerializer;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Unit test for serializing and deserializing SimpleExample via JSON.
 * Moreover the serialization from the dataset format is tested
 * 
 * @author Simone Filice
 *
 */
public class SimpleExampleSerializationTest {
	private Example a;
	private Example b;

	private LinearKernelCombination kernel;

	private static final String DENSE_NAME = "Dense";
	private static final String SPARSE_NAME = "Sparse";
	private static final ObjectSerializer serializer = new JacksonSerializerWrapper();
	
	
	/**
	 * This method will be executed before each test method.
	 */
	@Before
	public void initializeExamples() {
		String reprA = "fakeclass |BDV:"+DENSE_NAME+"| 1.0,0.0,1.0 |EV| |BV:"+SPARSE_NAME+"| one:1.0 three:1.0 |EV|";
		String reprB = "fakeclass |BDV:"+DENSE_NAME+"| 0.0,1.0,1.0 |EV| |BV:"+SPARSE_NAME+"| two:1.0 three:1.0 |EV|";

		try {
			a = ExampleFactory.parseExample(reprA);
			b = ExampleFactory.parseExample(reprB);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		Kernel kernelOnDense = new PolynomialKernel(2, new LinearKernel(DENSE_NAME));
		Kernel kernelOnSparse = new NormalizationKernel(new LinearKernel(SPARSE_NAME));
		kernel = new LinearKernelCombination();
		kernel.addKernel(1, kernelOnDense);
		kernel.addKernel(2, kernelOnSparse);
	}

	@Test
	public void testExampleFactoryCorrectness(){
		float aVsA = kernel.innerProduct(a, a);
		Assert.assertEquals(6, aVsA, 0.00001);
		float aVsB = kernel.innerProduct(a, b);
		Assert.assertEquals(2, aVsB, 0.00001);
		float bVsA = kernel.innerProduct(b, a);
		Assert.assertEquals(2, bVsA, 0.00001);
		float bVsB = kernel.innerProduct(b, b);
		Assert.assertEquals(6, bVsB, 0.00001);
	}

	@Test
	public void testJsonSerialization(){
		Example aCopy = copyViaJSON(a);
		Example bCopy = copyViaJSON(b);
		
		assertSameLabels(aCopy, a);
		assertSameLabels(bCopy, b);
		
		Assert.assertEquals(kernel.innerProduct(a, a), kernel.innerProduct(aCopy, aCopy), 0.0001);
		Assert.assertEquals(kernel.innerProduct(a, b), kernel.innerProduct(aCopy, bCopy), 0.0001);
		Assert.assertEquals(kernel.innerProduct(b, a), kernel.innerProduct(bCopy, aCopy), 0.0001);
		Assert.assertEquals(kernel.innerProduct(b, b), kernel.innerProduct(bCopy, bCopy), 0.0001);

	}
	
	public static void assertSameLabels(Example exA, Example exB){
		Assert.assertArrayEquals(exA.getLabels(), exB.getLabels());
		Assert.assertArrayEquals(exA.getRegressionLabels(), exB.getRegressionLabels());
	}

	public static Example copyViaJSON(Example example){

		Example copy = null;
		try {
			String serialized = serializer.writeValueAsString(example);
			copy = serializer.readValue(serialized, Example.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (JsonMappingException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		return copy;
	}


}
