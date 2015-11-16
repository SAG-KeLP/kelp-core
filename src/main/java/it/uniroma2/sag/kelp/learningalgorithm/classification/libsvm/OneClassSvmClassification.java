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

package it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.SvmSolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

//TODO: currently this class, extending BinaryCSvmClassification, has the getter and setter methods for Cp and Cn, which are meaningless in this  learning algorithm

/**
 * It implements the One class SVM learning algorithm discussed in [CC Chang &
 * CJ Lin, 2011]. It is a learning algorithm for estimating the Support of a High-Dimensional Distribution
 * and it relies on kernel functions. The model is acquired only by considering
 * positive examples. It is useful in anomaly detection (a.k.a. novelty detection) 
 * <p>
 * It is a Java porting of the library LIBSVM v3.17, written in C++.
 * <p>
 * Further details can be found in:
 * <p>
 * [CC Chang & CJ Lin, 2011] Chih-Chung Chang and Chih-Jen Lin. LIBSVM: A
 * library for support vector machines. ACM Transactions on Intelligent Systems
 * and Technology, 2:27:1-27:27, 2011.
 * <p>
 * and
 * <p>
 * <code>http://www.csie.ntu.edu.tw/~cjlin/libsvm/</code>
 * 
 * @author Danilo Croce
 */
@JsonTypeName("oneClassSvmClassification")
public class OneClassSvmClassification extends BinaryCSvmClassification {

	private Logger logger = LoggerFactory
			.getLogger(OneClassSvmClassification.class);
	/**
	 * The \(\nu\) parameter
	 */
	private float nu;

	public OneClassSvmClassification() {
		super();
	}

	/**
	 * @param kernel
	 *            The kernel function
	 * @param label
	 *            The label to be learned
	 * @param nu
	 *            The \(\nu\) parameter
	 */
	public OneClassSvmClassification(Kernel kernel, Label label, float nu) {
		super(kernel, label, 1.0f, 1.0f);
		this.nu = nu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.
	 * BinaryCSvmClassification#duplicate()
	 */
	@Override
	public OneClassSvmClassification duplicate() {
		return new OneClassSvmClassification(kernel, label, nu);
	}

	/**
	 * @return the \(\nu\) parameter
	 */
	public float getNu() {
		return nu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.
	 * BinaryCSvmClassification#learn(it.uniroma2.sag.kelp.data.dataset.Dataset)
	 */
	@Override
	public void learn(Dataset trainingSet) {
		Dataset onlyPositiveDataset = new SimpleDataset();
		for (Example example : trainingSet.getExamples()) {
			if (example.isExampleOf(super.getLabel()))
				onlyPositiveDataset.addExample(example);
		}

		int l = onlyPositiveDataset.getNumberOfExamples();
		float[] zeros = new float[l];

		int[] ones = new int[l];
		for (int i = 0; i < l; i++) {
			ones[i] = 1;
		}

		int n = (int) (nu * l); // # of alpha's at upper bound

		float[] initialAlphas = new float[l];
		for (int i = 0; i < n; i++)
			initialAlphas[i] = 1;
		if (n < l)
			initialAlphas[n] = nu * l - n;
		for (int i = n + 1; i < l; i++)
			initialAlphas[i] = 0;

		for (int i = 0; i < l; i++) {
			zeros[i] = 0;
		}

		SvmSolution solution = solve(onlyPositiveDataset.getNumberOfExamples(),
				onlyPositiveDataset, zeros, ones, initialAlphas);

		this.classifier.getModel().setBias(-solution.getRho());

		float[] alphas = solution.getAlphas();
		for (int i = 0; i < trainingSet.getNumberOfExamples(); i++) {

			if (alphas[i] != 0) {
				// System.out.println(alphas[i]);
				this.classifier.getModel().addExample(alphas[i],
						trainingSet.getExamples().get(i));
			}
		}

		classifier.getModel().setKernel(kernel);
		logger.info("RHO\t" + solution.getRho());
	}

	/**
	 * @param nu
	 *            the \(\nu\) parameter
	 */
	public void setNu(float nu) {
		this.nu = nu;
	}

}
