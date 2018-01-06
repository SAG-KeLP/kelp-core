/*
 * Copyright 2017 Simone Filice and Giuseppe Castellucci and Danilo Croce
 * and Giovanni Da San Martino and Alessandro Moschitti and Roberto Basili
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

package it.uniroma2.sag.kelp.algorithms.json;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.cache.FixSizeKernelCache;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.ClassificationLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryCSvmClassification;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;
import it.uniroma2.sag.kelp.utils.ObjectSerializer;

public class JsonSerializationTest {
	private static Classifier f = null;
	private static SimpleDataset trainingSet;
	private static SimpleDataset testSet;
	private static ObjectSerializer serializer = new JacksonSerializerWrapper();
	private static BinaryCSvmClassification learner;

	private static Label positiveClass = new StringLabel("+1");

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

		// define the kernel
		Kernel kernel = new LinearKernel("0");

		// add a cache
		kernel.setKernelCache(new FixSizeKernelCache(trainingSet
				.getNumberOfExamples()));

		// define the learning algorithm
		learner = new BinaryCSvmClassification(kernel,
				positiveClass, 1, 1);

		// learn and get the prediction function
		learner.learn(trainingSet);
		f = learner.getPredictionFunction();
	}
	
	@Test
	public void learnFromJsonAlgo() throws IOException{
		String jsonSerialization = serializer.writeValueAsString(learner);
		System.out.println(jsonSerialization);
		ClassificationLearningAlgorithm jsonAlgo = serializer.readValue(jsonSerialization, ClassificationLearningAlgorithm.class);
		jsonAlgo.learn(trainingSet);
		Classifier jsonClassifier = jsonAlgo.getPredictionFunction();
		
		for(Example ex : testSet.getExamples()){
			ClassificationOutput p = f.predict(ex);
			Float score = p.getScore(positiveClass);
			ClassificationOutput pJson = jsonClassifier.predict(ex);
			Float scoreJson = pJson.getScore(positiveClass);
			Assert.assertEquals(scoreJson.floatValue(), score.floatValue(),
					0.001f);
		}
	}
	
	@Test
	public void predictFromJsonAlgo() throws IOException{
		String jsonSerialization = serializer.writeValueAsString(f);
		System.out.println(jsonSerialization);
		Classifier jsonClassifier = serializer.readValue(jsonSerialization, Classifier.class);
		
		for(Example ex : testSet.getExamples()){
			ClassificationOutput p = f.predict(ex);
			Float score = p.getScore(positiveClass);
			ClassificationOutput pJson = jsonClassifier.predict(ex);
			Float scoreJson = pJson.getScore(positiveClass);
			Assert.assertEquals(scoreJson.floatValue(), score.floatValue(),
					0.001f);
		}
	}
}
