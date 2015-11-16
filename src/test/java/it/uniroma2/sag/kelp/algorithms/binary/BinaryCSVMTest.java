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

package it.uniroma2.sag.kelp.algorithms.binary;

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.cache.FixIndexKernelCache;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryCSvmClassification;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.utils.evaluation.BinaryClassificationEvaluator;
import it.uniroma2.sag.kelp.utils.exception.NoSuchPerformanceMeasureException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinaryCSVMTest {
	// Accuracy: 0.9766667
	// F1 = 0.9769737
	private static Classifier f = null;
	private static SimpleDataset trainingSet;
	private static SimpleDataset testSet;
	private static ArrayList<Float> scores;

	private Label positiveClass = new StringLabel("+1");

	@BeforeClass
	public static void learnModel() {
		trainingSet = new SimpleDataset();
		testSet = new SimpleDataset();
		try {
			trainingSet.populate("src/test/resources/svmTest/binary/binary_train.klp");
			// Read a dataset into a test variable
			testSet.populate("src/test/resources/svmTest/binary/binary_test.klp");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

		// define the positive class
		StringLabel positiveClass = new StringLabel("+1");

		// define the kernel
		Kernel kernel = new LinearKernel("0");

		// add a cache
		kernel.setKernelCache(new FixIndexKernelCache(trainingSet
				.getNumberOfExamples()));

		// define the learning algorithm
		BinaryCSvmClassification learner = new BinaryCSvmClassification(kernel,
				positiveClass, 1, 1);

		// learn and get the prediction function
		learner.learn(trainingSet);
		f = learner.getPredictionFunction();
	}

	@BeforeClass
	public static void loadClassificationScores() {
		try {
			scores = new ArrayList<Float>();
			String filepath = "src/test/resources/svmTest/binary/binaryCSvm/outScores_libsvm_c-svm.txt";
			BufferedReader in = null;
			String encoding = "UTF-8";
			if (filepath.endsWith(".gz")) {
				in = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(filepath)),
						encoding));
			} else {
				in = new BufferedReader(new InputStreamReader(
						new FileInputStream(filepath), encoding));
			}

			String str = "";
			while ((str = in.readLine()) != null) {
				scores.add(Float.parseFloat(str));
			}

			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testAccuracy() {
		BinaryClassificationEvaluator ev = new BinaryClassificationEvaluator(
				positiveClass);
		for (Example e : testSet.getExamples()) {
			ClassificationOutput p = f.predict(testSet.getNextExample());
			ev.addCount(e, p);
		}

		try {
			float acc = ev.getPerformanceMeasure("accuracy");
			Assert.assertEquals(0.9766667f, acc, 0.000001);
		} catch (NoSuchPerformanceMeasureException e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void testF1() {
		BinaryClassificationEvaluator ev = new BinaryClassificationEvaluator(
				positiveClass);
		for (Example e : testSet.getExamples()) {
			ClassificationOutput p = f.predict(e);
			ev.addCount(e, p);
		}

		try {
			float acc = ev.getPerformanceMeasure("f1");
			Assert.assertEquals(0.9769737f, acc, 0.000001);
		} catch (NoSuchPerformanceMeasureException e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void checkScores() {
		for (int i = 0; i < testSet.getExamples().size(); ++i) {
			Example e = testSet.getExample(i);
			ClassificationOutput p = f.predict(e);
			Float score = p.getScore(positiveClass);
			Assert.assertEquals(scores.get(i).floatValue(), score.floatValue(),
					0.001f);
		}
	}
}
