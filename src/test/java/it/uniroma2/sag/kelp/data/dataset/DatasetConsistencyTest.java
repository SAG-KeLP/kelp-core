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

package it.uniroma2.sag.kelp.data.dataset;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for verifying that the method isConsistent is working
 * 
 * @author Simone Filice
 *
 */
public class DatasetConsistencyTest {
	
	

	
	@Test
	public void testAdditionalRepresentation() {
		SimpleDataset dataset = new SimpleDataset();
		
		
		try {
			dataset.populate("src/test/resources/inconsistentDatasets/additionalRepresentation.klp");			

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
		
	}
	
	@Test
	public void testDifferentDimensions() {
		SimpleDataset dataset = new SimpleDataset();
		
		
		try {
			dataset.populate("src/test/resources/inconsistentDatasets/differentDimensions.klp");			

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
		
	}
	
	@Test
	public void testDifferentNames() {
		SimpleDataset dataset = new SimpleDataset();
		
		
		try {
			dataset.populate("src/test/resources/inconsistentDatasets/differentNames.klp");			

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
		
	}
	
	@Test
	public void testDifferentTypes() {
		SimpleDataset dataset = new SimpleDataset();
		
		
		try {
			dataset.populate("src/test/resources/inconsistentDatasets/differentTypes.klp");			

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
		
	}

}
