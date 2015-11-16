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

import java.io.IOException;

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.manipulator.StandardizationManipulator;
import it.uniroma2.sag.kelp.data.representation.vector.DenseVector;
import it.uniroma2.sag.kelp.data.representation.vector.SparseVector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test for the StandardizationManipulator
 * 
 * @author Simone Filice
 *
 */
public class VectorStandardizationTest {
	
	
	private static SimpleDataset dataset;
	
	private String denseName = "Dense";
	private String sparseName = "Sparse";
	
	@Before
	/**
	 * This method will be executed before each test method.
	 */
	public void initializeExamples() {
		String reprA = "fakeclass |BDV:"+denseName+"| 1.0,0.0,1.0 |EV| |BV:"+sparseName+"| one:1.0 three:1.0 |EV|";
		String reprB = "fakeclass |BDV:"+denseName+"| 0.0,1.0,1.0 |EV| |BV:"+sparseName+"| two:1.0 three:1.0 |EV|";
		String reprC = "fakeclass |BDV:"+denseName+"| 0.5,1.0,1.0 |EV| |BV:"+sparseName+"| one:1.0 three:1.0 ten:1.0 |EV|";
		
		try {
			Example a = ExampleFactory.parseExample(reprA);
			Example b = ExampleFactory.parseExample(reprB);
			Example c = ExampleFactory.parseExample(reprC);
			dataset = new SimpleDataset();
			dataset.addExample(a);
			dataset.addExample(b);
			dataset.addExample(c);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	@Test
	public void testSparseStandardization() {
		StandardizationManipulator standardizer = new StandardizationManipulator(sparseName, dataset.getExamples());
		dataset.manipulate(standardizer);
		SparseVector vector = new SparseVector();
		try {
			vector.setDataFromText("one:0.5773502 two:-0.5773503 ten:-0.5773503 ");
			SparseVector vecA = (SparseVector) dataset.getExample(0).getRepresentation(sparseName);
			vector.add(-1, vecA);
			Assert.assertTrue(vector.getSquaredNorm() < 0.0001f);
			vector.setDataFromText("one:-1.1547005 two:1.1547005 ten:-0.5773503");
			SparseVector vecB = (SparseVector) dataset.getExample(1).getRepresentation(sparseName);
			vector.add(-1, vecB);
			Assert.assertTrue(vector.getSquaredNorm() < 0.0001f);
			vector.setDataFromText("one:0.5773502 two:-0.5773503 ten:1.1547005 ");
			SparseVector vecC = (SparseVector) dataset.getExample(2).getRepresentation(sparseName);
			vector.add(-1, vecC);
			Assert.assertTrue(vector.getSquaredNorm() < 0.0001f);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);
	}
	
	@Test
	public void testDenseStandardization() {
		StandardizationManipulator standardizer = new StandardizationManipulator(denseName, dataset.getExamples());
		dataset.manipulate(standardizer);
		DenseVector vector = new DenseVector();
		try {
			vector.setDataFromText("0.9999999701976776,-1.154700552067176,0");
			DenseVector vecA = (DenseVector) dataset.getExample(0).getRepresentation(denseName);
			vector.add(-1, vecA);
			Assert.assertTrue(vector.getSquaredNorm() < 0.0001f);
			vector.setDataFromText("-1.0000000298023224,0.5773502244144524,0");
			DenseVector vecB = (DenseVector) dataset.getExample(1).getRepresentation(denseName);
			vector.add(-1, vecB);
			Assert.assertTrue(vector.getSquaredNorm() < 0.0001f);
			vector.setDataFromText("-2.9802322387695312E-8,0.5773502244144524,0");
			DenseVector vecC = (DenseVector) dataset.getExample(2).getRepresentation(denseName);
			vector.add(-1, vecC);
			Assert.assertTrue(vector.getSquaredNorm() < 0.0001f);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertTrue(true);
	}

}
