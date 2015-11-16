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

package it.uniroma2.sag.kelp.data.representation.vector.test;

import gnu.trove.map.TIntFloatMap;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.representation.vector.DenseVector;
import it.uniroma2.sag.kelp.data.representation.vector.SparseVector;

import java.util.Map;

import org.ejml.data.DenseMatrix64F;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VectorsTest {
	private Example a;
	private Example b;
	
	private String denseName = "Dense";
	private String sparseName = "Sparse";
	
	@Before
	/**
	 * This method will be executed before each test method.
	 */
	public void initializeExamples() {
		String reprA = "fakeclass |BDV:"+denseName+"| 1.0,0.0,1.0 |EV| |BV:"+sparseName+"| one:1.0 three:1.0 |EV|";
		String reprB = "fakeclass |BDV:"+denseName+"| 0.0,1.0,1.0 |EV| |BV:"+sparseName+"| two:1.0 three:1.0 |EV|";
		
		try {
			a = ExampleFactory.parseExample(reprA);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.fail();
		}
		try {
			b = ExampleFactory.parseExample(reprB);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testDenseSum() {
		DenseVector aVector = (DenseVector) a.getRepresentation(denseName);
		DenseVector bVector = (DenseVector) b.getRepresentation(denseName);
		
		aVector.add(bVector);
		
		Assert.assertEquals(1.0f, aVector.getFeatureValue(0), 0.0001f);
		Assert.assertEquals(1.0f, aVector.getFeatureValue(1), 0.0001f);
		Assert.assertEquals(2.0f, aVector.getFeatureValue(2), 0.0001f);	
	}
	
	@Test
	public void testSparseSum() {
		SparseVector aVector = (SparseVector) a.getRepresentation(sparseName);
		SparseVector bVector = (SparseVector) b.getRepresentation(sparseName);
		
		aVector.add(bVector);
		
		Assert.assertEquals(1.0f, aVector.getActiveFeatures().get("one").floatValue(), 0.0001f);
		Assert.assertEquals(1.0f, aVector.getActiveFeatures().get("two").floatValue(), 0.0001f);
		Assert.assertEquals(2.0f, aVector.getActiveFeatures().get("three").floatValue(), 0.0001f);	
	}
	
	@Test
	public void testDenseSumWithCoefficient() {
		DenseVector aVector = (DenseVector) a.getRepresentation(denseName);
		DenseVector bVector = (DenseVector) b.getRepresentation(denseName);
		
		aVector.add(2.0f, bVector);
		
		Assert.assertEquals(1.0f, aVector.getFeatureValue(0), 0.0001f);
		Assert.assertEquals(2.0f, aVector.getFeatureValue(1), 0.0001f);
		Assert.assertEquals(3.0f, aVector.getFeatureValue(2), 0.0001f);	
	}
	
	@Test
	public void testSparseSumWithCoefficient() {
		SparseVector aVector = (SparseVector) a.getRepresentation(sparseName);
		SparseVector bVector = (SparseVector) b.getRepresentation(sparseName);
		
		aVector.add(2.0f, bVector);
		
		Assert.assertEquals(1.0f, aVector.getActiveFeatures().get("one").floatValue(), 0.0001f);
		Assert.assertEquals(2.0f, aVector.getActiveFeatures().get("two").floatValue(), 0.0001f);
		Assert.assertEquals(3.0f, aVector.getActiveFeatures().get("three").floatValue(), 0.0001f);	
	}
	
	@Test
	public void testDensePointWiseProduct() {
		DenseVector aVector = (DenseVector) a.getRepresentation(denseName);
		DenseVector bVector = (DenseVector) b.getRepresentation(denseName);
		
		aVector.pointWiseProduct(bVector);
		
		Assert.assertEquals(0.0f, aVector.getFeatureValue(0), 0.0001f);
		Assert.assertEquals(0.0f, aVector.getFeatureValue(1), 0.0001f);
		Assert.assertEquals(1.0f, aVector.getFeatureValue(2), 0.0001f);
	}
	
	@Test
	public void testSparsePointWiseProduct() {
		SparseVector aVector = (SparseVector) a.getRepresentation(sparseName);
		SparseVector bVector = (SparseVector) b.getRepresentation(sparseName);
		
		aVector.pointWiseProduct(bVector);
		
		Assert.assertEquals(1, aVector.getActiveFeatures().size());
		
		Assert.assertNull(aVector.getActiveFeatures().get("one"));
		Assert.assertNull(aVector.getActiveFeatures().get("two"));
		Assert.assertEquals(1.0f, aVector.getActiveFeatures().get("three").floatValue(), 0.0001f);	
	}
	
	@Test
	public void testDenseInnerProduct() {
		DenseVector aVector = (DenseVector) a.getRepresentation(denseName);
		DenseVector bVector = (DenseVector) b.getRepresentation(denseName);
		
		float innerProduct = aVector.innerProduct(bVector);
		
		Assert.assertEquals(1.0f, innerProduct, 0.000001f);
	}
	
	@Test
	public void testSparseInnerProduct() {
		SparseVector aVector = (SparseVector) a.getRepresentation(sparseName);
		SparseVector bVector = (SparseVector) b.getRepresentation(sparseName);
		
		float innerProduct = aVector.innerProduct(bVector);
		
		Assert.assertEquals(1.0f, innerProduct, 0.000001f);
	}
	
	@Test
	public void testCopySparse() {
		SparseVector aVector = (SparseVector) a.getRepresentation(sparseName);
		SparseVector copyVector = aVector.copyVector();
		SparseVector copy = (SparseVector) copyVector;
		
		Assert.assertNotSame(copy, aVector);
		
		Assert.assertEquals(aVector.getTextFromData(), copy.getTextFromData());
		
		TIntFloatMap origMap = aVector.getVector();
		TIntFloatMap copyMap = copy.getVector();
		
		Assert.assertEquals(origMap.size(), copyMap.size());
		
		for (int i : origMap.keys()) {
			Assert.assertEquals(origMap.get(i), copyMap.get(i), 0.000001f);
		}

		Map<Object, Number> activeFeaturesOrig = aVector.getActiveFeatures();
		Map<Object, Number> activeFeaturesCopy = copy.getActiveFeatures();
		
		Assert.assertEquals(activeFeaturesOrig.size(), activeFeaturesCopy.size());
		
		for (Object a : activeFeaturesOrig.keySet()) {
			Assert.assertEquals(activeFeaturesOrig.get(a).doubleValue(), activeFeaturesCopy.get(a).doubleValue(), 0.000001f);
		}
	}
	
	@Test
	public void testCopyDense() {
		DenseVector aVector = (DenseVector) a.getRepresentation(denseName);
		DenseVector copyVector = aVector.copyVector();
		DenseVector copy = (DenseVector) copyVector;
		
		Assert.assertEquals(aVector.getTextFromData(), copy.getTextFromData());
		Assert.assertNotSame(copy, aVector);
		
		DenseMatrix64F featureValuesOIrig = aVector.getFeatureValues();
		DenseMatrix64F featureValuesCopy = copy.getFeatureValues();
		
		Assert.assertEquals(featureValuesOIrig.numCols, featureValuesCopy.numCols);
		Assert.assertEquals(featureValuesOIrig.numRows, featureValuesCopy.numRows);
		
		for (int i=0; i<featureValuesCopy.data.length; ++i) {
			Assert.assertEquals(featureValuesOIrig.data[i], featureValuesCopy.data[i], 0.000000001f);
		}
	}
}
