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

/**
 * Unit test for reading a file in libsvm/libliner format for 
 * regression task. The expected accuracy has been computed using 
 * liblinear v1.96 on the same datasets with the following parameterization: 
 * -c 1 -s 11 -p 0.1 
 * 
 * @author Simone Filice
 *
 */
package it.uniroma2.sag.kelp.data.dataset;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.regression.libsvm.EpsilonSvmRegression;
import it.uniroma2.sag.kelp.predictionfunction.regressionfunction.UnivariateRegressionFunction;
import it.uniroma2.sag.kelp.predictionfunction.regressionfunction.UnivariateRegressionOutput;

public class LibsvmFormatReaderRegressionTest {

	private static SimpleDataset trainset;
	private static SimpleDataset testset;

	private static final String REPRESENTATION_NAME = "features";
	private static final StringLabel property = new StringLabel("value");

	@BeforeClass
	public static void loadDatasetAndPrepareKernel() {
		trainset = new SimpleDataset();
		testset = new SimpleDataset();

		try {
			LibsvmDatasetReader trainReader = new LibsvmDatasetReader(
					"src/test/resources/svmTest/libsvmFormat/regression/housing_train", REPRESENTATION_NAME, property);
			LibsvmDatasetReader testReader = new LibsvmDatasetReader(
					"src/test/resources/svmTest/libsvmFormat/regression/housing_test", REPRESENTATION_NAME, property);
			trainset.populate(trainReader);
			testset.populate(testReader);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

	}

	@Test
	public void testAccuracy() {
		Kernel kernel = new LinearKernel(REPRESENTATION_NAME);
		EpsilonSvmRegression regressorLearn = new EpsilonSvmRegression(kernel, property, 1, 0.1f);
		regressorLearn.learn(trainset);
		UnivariateRegressionFunction regressor = (UnivariateRegressionFunction) regressorLearn.getPredictionFunction();
		float meanSquaredError = 0;
		for (Example ex : testset.getExamples()) {
			UnivariateRegressionOutput pred = regressor.predict(ex);
			float realValue = ex.getRegressionValue(property);

			float predictedValue = pred.getScore(property);
			meanSquaredError += (realValue - predictedValue) * (realValue - predictedValue);
		}
		meanSquaredError /= testset.getNumberOfExamples();

		Assert.assertTrue(meanSquaredError <= 35.4973 + 0.0001f);
	}
}
