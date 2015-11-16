/*
 * Copyright 2015 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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

import org.junit.Assert;
import org.junit.Test;

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;

public class ExampleParsingTest {
	public String example1 = "one |BV:A| a:0.1 b:0.2 |EV| SSSS |BV:B| a:0.2 c:0.3 |EV|";
	public String example2 = "one |BV:A| a:0.1 b:0.2 |EV| |BV:B| a:0.2 c:0.3 |EV| |BV:A| a:0.1 |EV|";
	
	public String datasetPath = "src/test/resources/parsingDataset.klp";
	
	@Test
	public void testExceptionWhenCharsBetweenRepresentations() {
		try {
			ExampleFactory.parseExample(example1);
			Assert.fail();
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testExceptionWhenRepresentationsAreOverwritten() {
		try {
			ExampleFactory.parseExample(example2);
			Assert.fail();
		} catch (InstantiationException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		} catch (ParsingExampleException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testReadDataset() {
		SimpleDataset dat = new SimpleDataset();
		try {
			dat.populate(datasetPath);
			Assert.fail();
		} catch (ParsingExampleException e) {
			e.printStackTrace();
//			System.out.println(e.getExampleString());
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
	}
}
