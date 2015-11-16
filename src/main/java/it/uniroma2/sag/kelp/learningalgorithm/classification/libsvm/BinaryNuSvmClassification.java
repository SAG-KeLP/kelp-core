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
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.learningalgorithm.KernelMethod;
import it.uniroma2.sag.kelp.learningalgorithm.classification.ClassificationLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.LibNuSvmSolver;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.SvmSolution;
import it.uniroma2.sag.kelp.predictionfunction.classifier.BinaryKernelMachineClassifier;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryKernelMachineModel;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It implements the \(\nu\)-SVM learning algorithm discussed in [CC Chang & CJ
 * Lin, 2011]. It is a learning algorithm for binary linear classification and
 * it relies on kernel functions.
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
@JsonTypeName("binaryNuSvmClassification")
public class BinaryNuSvmClassification extends LibNuSvmSolver implements
		ClassificationLearningAlgorithm, KernelMethod {

	/**
	 * The \(\nu\) parameter
	 */
	private float nu = 0.5f;

	/**
	 * The label to be learned
	 */
	private Label label;

	/**
	 * The classifier to be returned
	 */
	private BinaryKernelMachineClassifier classifier;

	public BinaryNuSvmClassification() {
		super();
		initializeClassifier();
	}

	/**
	 * @param kernel
	 *            The kernel function
	 * @param label
	 *            The label to be learned
	 * @param nu
	 *            The \(\nu\) parameter
	 */
	public BinaryNuSvmClassification(Kernel kernel, Label label, float nu) {
		super(kernel, 1, 1);
		this.label = label;
		this.nu = checkNu(nu);
		initializeClassifier();
		this.setLabel(label);
	}

	/**
	 * Check that 0<=\(\nu\)<=1
	 * 
	 * @param nu
	 * @return True if \(\nu\) is valid. False otherwise
	 */
	private float checkNu(float nu) {
		if (nu <= 0 || nu >= 1) {
			System.err
					.println("Nu must be in the (0,1) interval. Nu is set to 0.5");
			return 0.5f;
		}
		return nu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#duplicate()
	 */
	@Override
	public BinaryNuSvmClassification duplicate() {
		return new BinaryNuSvmClassification(kernel, label, nu);
	}

	/**
	 * @return The \(\nu\) parameter
	 */
	public float getNu() {
		return nu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#
	 * getPredictionFunction()
	 */
	@Override
	public BinaryKernelMachineClassifier getPredictionFunction() {
		return this.classifier;
	}

	/**
	 * Initialize the classifier
	 */
	private void initializeClassifier() {
		BinaryKernelMachineModel model = new BinaryKernelMachineModel();
		this.classifier = new BinaryKernelMachineClassifier();
		this.classifier.setModel(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#learn(it.uniroma2
	 * .sag.kelp.data.dataset.Dataset)
	 */
	public void learn(Dataset trainingSet) {
		int l = trainingSet.getNumberOfExamples();
		/*
		 * CHECK
		 */
		int[] y = new int[l];
		for (int i = 0; i < y.length; i++) {
			if (trainingSet.getExamples().get(i).isExampleOf(this.label))
				y[i] = +1;
			else
				y[i] = -1;
		}

		float sum_pos = nu * (float) l / 2f;
		float sum_neg = nu * (float) l / 2f;

		float[] initialAlpha = new float[l];
		for (int i = 0; i < l; i++)
			if (y[i] == +1) {
				initialAlpha[i] = Math.min(1.0f, sum_pos);
				sum_pos -= initialAlpha[i];
			} else {
				initialAlpha[i] = Math.min(1.0f, sum_neg);
				sum_neg -= initialAlpha[i];
			}

		classifier.getModel().setKernel(kernel);

		learn(trainingSet, initialAlpha);
	}

	/**
	 * It starts the training process exploiting the provided
	 * <code>dataset</code> and initial values of the Support Vector weights
	 * 
	 * @param trainingSet
	 *            the initial dataset
	 * @param initialAlpha
	 *            initial values of the Support Vector weights
	 * @return the classifier
	 */
	private Classifier learn(Dataset trainingSet, float[] initialAlpha) {

		l = trainingSet.getNumberOfExamples();

		float[] zeros = new float[l];

		for (int i = 0; i < l; i++)
			zeros[i] = 0;

		int[] y = new int[trainingSet.getNumberOfExamples()];
		for (int i = 0; i < y.length; i++) {
			if (trainingSet.getExamples().get(i).isExampleOf(this.label))
				y[i] = +1;
			else
				y[i] = -1;
		}

		SvmSolution solution = solve(trainingSet.getNumberOfExamples(),
				trainingSet, zeros, y, initialAlpha);

		float r = calculate_r();

		float[] alphas = solution.getAlphas();
		for (int i = 0; i < trainingSet.getNumberOfExamples(); i++) {

			if (alphas[i] != 0) {
				this.classifier.getModel().addExample(y[i] * alphas[i] / r,
						trainingSet.getExamples().get(i));
			}
		}

		this.classifier.getModel().setBias(-solution.getRho() / r);

		info("C = " + 1 / r);
		info("obj = " + r * r);
		info("rho = " + -solution.getRho() / r);

		return this.classifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#reset()
	 */
	@Override
	public void reset() {
		this.classifier.reset();
	}

	/**
	 * @param nu
	 *            The \(\nu\) parameter
	 */
	public void setNu(float nu) {
		this.nu = checkNu(nu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.KernelMethod#setKernel(it.uniroma2
	 * .sag.kelp.kernel.Kernel)
	 */
	@Override
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}
}
