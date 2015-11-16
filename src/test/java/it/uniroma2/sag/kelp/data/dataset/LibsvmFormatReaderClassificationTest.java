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
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryCSvmClassification;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;
import it.uniroma2.sag.kelp.predictionfunction.classifier.BinaryClassifier;
import it.uniroma2.sag.kelp.utils.evaluation.BinaryClassificationEvaluator;

/**
 * Unit test for reading a file in libsvm/libliner format for 
 * classification task. The expected accuracy has been computed using 
 * liblinear v1.96 on the same datasets with the default parameterization (-c 1 -s 1) 
 * 
 * @author Simone Filice
 *
 */
public class LibsvmFormatReaderClassificationTest {

	private static SimpleDataset trainset;
	private static SimpleDataset testset;
	
	private static final String REPRESENTATION_NAME = "features";
	private static final Label positiveClass = new StringLabel("+1");
	
	@BeforeClass
	public static void loadDatasetAndPrepareKernel() {
		trainset = new SimpleDataset();
		testset = new SimpleDataset();
		
		try {
			LibsvmDatasetReader trainReader = new LibsvmDatasetReader("src/test/resources/svmTest/libsvmFormat/classification/a1a_train", REPRESENTATION_NAME);
			LibsvmDatasetReader testReader = new LibsvmDatasetReader("src/test/resources/svmTest/libsvmFormat/classification/a1a_test", REPRESENTATION_NAME);
			trainset.populate(trainReader);
			testset.populate(testReader);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		

	}
	
	@Test
	public void testAccuracy() {
		BinaryClassificationEvaluator ev = new BinaryClassificationEvaluator(
				positiveClass);
		Kernel kernel = new LinearKernel(REPRESENTATION_NAME);
		BinaryCSvmClassification solver = new BinaryCSvmClassification();
		solver.setLabel(positiveClass);
		solver.setCn(1);
		solver.setCp(1);
		solver.setKernel(kernel);
		solver.learn(trainset);
		
		BinaryClassifier classifier = solver.getPredictionFunction();
		for(Example ex : testset.getExamples()){
			Prediction pred = classifier.predict(ex);
			ev.addCount(ex, pred);
		}		
		Assert.assertTrue(ev.getAccuracy()>=0.83822197 - 0.0001f);
	}
	
}
