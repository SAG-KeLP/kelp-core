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
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.LibCSvmSolver;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.SvmSolution;
import it.uniroma2.sag.kelp.predictionfunction.classifier.BinaryKernelMachineClassifier;
import it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryKernelMachineModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It implements the C-SVM learning algorithm discussed in [CC Chang & CJ Lin,
 * 2011]. It is a learning algorithm for binary linear classification and it
 * relies on kernel functions.
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
@JsonTypeName("binaryCSvmClassification")
public class BinaryCSvmClassification extends LibCSvmSolver implements
		ClassificationLearningAlgorithm, KernelMethod {

	private boolean fairness = false;

	/**
	 * The classifier to be returned
	 */
	@JsonIgnore
	protected BinaryKernelMachineClassifier classifier;

	public BinaryCSvmClassification() {
		super();
		initializeClassifier();
	}

	/**
	 * @param kernel
	 *            The kernel function
	 * @param label
	 *            The label to be learned
	 * @param cp
	 *            The regularization parameter for positive examples
	 * @param cn
	 *            The regularization parameter for negative examples
	 */
	public BinaryCSvmClassification(Kernel kernel, Label label, float cp,
			float cn) {
		super(kernel, cp, cn);
		initializeClassifier();
		this.setLabel(label);
	}

	/**
	 * @param kernel
	 *            The kernel function
	 * @param label
	 *            The label to be learned
	 * @param cp
	 *            The regularization parameter for positive examples
	 * @param cn
	 *            The regularization parameter for negative examples
	 * @param useFairness
	 *            A boolean parameter to force the fairness policy
	 */
	public BinaryCSvmClassification(Kernel kernel, Label label, float cp,
			float cn, boolean useFairness) {
		super(kernel, cp, cn);
		initializeClassifier();
		this.setLabel(label);
		this.fairness = useFairness;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#duplicate()
	 */
	public BinaryCSvmClassification duplicate() {
		return new BinaryCSvmClassification(kernel, label, cp, cn, fairness);
	}

	private float[] getCSvmP(Dataset trainingSet) {
		float[] res = new float[trainingSet.getNumberOfExamples()];
		for (int i = 0; i < res.length; i++) {
			res[i] = -1f;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#
	 * getPredictionFunction()
	 */
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

	public boolean isFairness() {
		return fairness;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#learn(it.uniroma2
	 * .sag.kelp.data.dataset.Dataset)
	 */
	public void learn(Dataset trainingSet) {
		if (isFairness()) {
			float positiveExample = trainingSet
					.getNumberOfPositiveExamples(label);
			float negativeExample = trainingSet
					.getNumberOfNegativeExamples(label);
			cp = cn * negativeExample / positiveExample;
			info("cn=" + cn + " cp=" + cp);
		}
		float[] initialAlpha = getCSvmAlpha(trainingSet);
		learn(trainingSet, initialAlpha);
	}

	/**
	 * It starts the training process exploiting the provided
	 * <code>dataset</code> and the initial Support Vectors weights
	 * 
	 * @param trainingSet
	 * @param initialAlpha
	 * @return
	 */
	private Classifier learn(Dataset trainingSet, float[] initialAlpha) {

		float[] p = getCSvmP(trainingSet);

		int[] y = new int[trainingSet.getNumberOfExamples()];
		for (int i = 0; i < y.length; i++) {
			if (trainingSet.getExamples().get(i).isExampleOf(this.label))
				y[i] = +1;
			else
				y[i] = -1;
		}

		SvmSolution solution = solve(trainingSet.getNumberOfExamples(),
				trainingSet, p, y, initialAlpha);

		this.classifier.getModel().setBias(-solution.getRho());

		float[] alphas = solution.getAlphas();
		for (int i = 0; i < trainingSet.getNumberOfExamples(); i++) {

			if (alphas[i] != 0) {
				this.classifier.getModel().addExample(y[i] * alphas[i],
						trainingSet.getExamples().get(i));
			}
		}

		classifier.getModel().setKernel(kernel);

		return this.classifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#reset()
	 */
	public void reset() {
		this.classifier.reset();
	}

	/**
	 * @param fairness
	 *            A boolean parameter to force the fairness policy
	 */
	public void setFairness(boolean fairness) {
		this.fairness = fairness;
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
