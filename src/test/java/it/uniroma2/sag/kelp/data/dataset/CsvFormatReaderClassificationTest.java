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

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.sag.kelp.data.dataset.CsvDatasetReader.LabelPosition;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryCSvmClassification;
import it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification.OneVsAllLearning;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsAllClassifier;
import it.uniroma2.sag.kelp.utils.evaluation.MulticlassClassificationEvaluator;

/**
 * Unit test for reading a file in csv format for 
 * classification task. 
 * 
 * @author Simone Filice
 *
 */
public class CsvFormatReaderClassificationTest {
	private static SimpleDataset dataset;
	
	private static final String REPRESENTATION_NAME = "features";
	
	@BeforeClass
	public static void loadDatasetAndPrepareKernel() {
		dataset = new SimpleDataset();
		
		
		try {
			CsvDatasetReader trainReader = new CsvDatasetReader("src/test/resources/svmTest/csvFormat/classification/iris.csv", REPRESENTATION_NAME, true, LabelPosition.LAST_COLUMN);
			dataset.populate(trainReader);
			

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		

	}
	
	@Test
	public void testAccuracy() {
		List<Label> classes = dataset.getClassificationLabels();
		MulticlassClassificationEvaluator ev = new MulticlassClassificationEvaluator(classes);
		
		Kernel kernel = new LinearKernel(REPRESENTATION_NAME);
		BinaryCSvmClassification solver = new BinaryCSvmClassification();
		solver.setCn(1);
		solver.setCp(1);
		solver.setKernel(kernel);
		
		OneVsAllLearning ova = new OneVsAllLearning();
		ova.setBaseAlgorithm(solver);
		ova.setLabels(classes);
		SimpleDataset [] folds = dataset.splitClassDistributionInvariant(0.7f);
		ova.learn(folds[0]);
		OneVsAllClassifier classifier = ova.getPredictionFunction();
		for(Example ex : folds[1].getExamples()){
			Prediction pred = classifier.predict(ex);
			ev.addCount(ex, pred);
		}
		System.out.println(ev.getAccuracy());
		Assert.assertTrue(ev.getAccuracy()>0);
	}

}
