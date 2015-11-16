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

package it.uniroma2.sag.kelp.learningalgorithm.regression.libsvm;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.learningalgorithm.KernelMethod;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.LibCSvmSolver;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.SvmSolution;
import it.uniroma2.sag.kelp.learningalgorithm.regression.RegressionLearningAlgorithm;
import it.uniroma2.sag.kelp.predictionfunction.regressionfunction.RegressionFunction;
import it.uniroma2.sag.kelp.predictionfunction.regressionfunction.UnivariateKernelMachineRegressionFunction;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

//TODO: currently this class, extending LibCSvmSolver, has the getter and setter methods for Cp and Cn, which are meaningless in this learning algorithm

/**
 * It implements the \(\epsilon\)-SVR learning algorithm discussed in [CC Chang
 * & CJ Lin, 2011]. It is a learning algorithm for linear regression based on
 * Support Vector Machines [Vapnik, 1998]. It relies on kernel functions.
 * <p>
 * It is a Java porting of the library LIBSVM v3.17, written in C++.
 * <p>
 * Further details can be found in:
 * <p>
 * [CC Chang & CJ Lin, 2011] Chih-Chung Chang and Chih-Jen Lin. LIBSVM: A
 * library for support vector machines. ACM Transactions on Intelligent Systems
 * and Technology, 2:27:1-27:27, 2011.
 * <p>
 * [Vapnik, 1998] V. Vapnik. Statistical Learning Theory. Wiley, New York, NY,
 * 1998.
 * <p>
 * and
 * <p>
 * <code>http://www.csie.ntu.edu.tw/~cjlin/libsvm/</code>
 * 
 * @author Danilo Croce
 */
@JsonTypeName("epsilonSvmRegression")
public class EpsilonSvmRegression extends LibCSvmSolver implements
		RegressionLearningAlgorithm, KernelMethod {
	private Logger logger = LoggerFactory.getLogger(EpsilonSvmRegression.class);

	/**
	 * The epsilon in loss function
	 */
	private float pReg = 0.1f;

	@JsonIgnore
	private float[] sign;

	@JsonIgnore
	private int[] index;

	/**
	 * The regression function to be returned
	 */
	@JsonIgnore
	protected UnivariateKernelMachineRegressionFunction regressor;

	public EpsilonSvmRegression(Kernel kernel, Label label, float c, float pReg) {
		super(kernel, c, c);
		this.regressor = new UnivariateKernelMachineRegressionFunction();
		this.pReg = pReg;
		this.setLabel(label);
	}
	
	public EpsilonSvmRegression(Kernel kernel, Label label, float cp, float cn, float pReg) {
		super(kernel, cp, cn);
		this.regressor = new UnivariateKernelMachineRegressionFunction();
		this.pReg = pReg;
		this.setLabel(label);
	}

	public EpsilonSvmRegression() {
		super();
		this.regressor = new UnivariateKernelMachineRegressionFunction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#duplicate()
	 */
	@Override
	public EpsilonSvmRegression duplicate() {
		return new EpsilonSvmRegression(getKernel(), getLabel(), getCp(), getCn(),
				getpReg());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#get_QD()
	 */
	protected float[] get_QD() {

		int l = super.examples.length;

		logger.info(new Integer(l).toString());

		float[] res = new float[2 * l];

		for (int j = 0; j < l; j++) {
			Example ex = super.examples[j];

			sign[j] = 1;
			sign[j + l] = -1;
			index[j] = j;
			index[j + l] = j;
			res[j] = kernel(ex, ex);
			res[j + l] = res[j];
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#get_Qij(int, int)
	 */
	protected float get_Qij(int i, int j) {
		return sign[i] * sign[j]
				* kernel(examples[index[i]], examples[index[j]]);
	}

	public float getpReg() {
		return pReg;
	}

	public void setpReg(float pReg) {
		this.pReg = pReg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#
	 * getPredictionFunction()
	 */
	@Override
	public RegressionFunction getPredictionFunction() {
		return regressor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#learn(it.uniroma2
	 * .sag.kelp.data.dataset.Dataset)
	 */
	@Override
	public void learn(Dataset dataset) {
		int l = dataset.getNumberOfExamples();
		float[] alpha2 = new float[2 * l];

		float[] linear_term = new float[2 * l];
		int[] y = new int[2 * l];
		float sum_alpha = 0;

		this.sign = new float[2 * l];
		this.index = new int[2 * l];

		List<Example> examples = dataset.getExamples();

		for (int i = 0; i < l; i++) {
			alpha2[i] = 0;
			linear_term[i] = pReg - examples.get(i).getRegressionValue(label);
			y[i] = 1;

			alpha2[i + l] = 0;
			linear_term[i + l] = pReg
					+ examples.get(i).getRegressionValue(label);
			y[i + l] = -1;
		}

		SvmSolution solution = solve(2 * l, dataset, linear_term, y, alpha2);

		this.regressor.getModel().setBias(-solution.getRho());

		float[] alphas = solution.getAlphas();

		int svCount = 0;
		for (int i = 0; i < l; i++) {

			float newAlpha = alphas[i] - alphas[i + l];
			if (newAlpha != 0) {
				this.regressor.getModel().addExample(newAlpha, examples.get(i));
				svCount++;
			}

			sum_alpha += Math.abs(newAlpha);
		}
		logger.info("nu = " + sum_alpha / (super.cp * (float) l));
		logger.info("obj = " + solution.getObj());
		
		regressor.getModel().setKernel(kernel);

		logger.info("RHO\t" + solution.getRho());
		logger.info("svCount\t" + svCount);

		this.sign = null;
		this.index = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm#reset()
	 */
	public void reset() {
		this.regressor.reset();
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

	public void setLabels(Label... labels) {
		this.setLabel(labels[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#swap_index(int, int)
	 */
	protected void swap_index(int i, int j) {
		super.swap(index, i, j);
		super.swap(sign, i, j);

		super.swap(y, i, j);
		super.swap(G, i, j);
		super.swap(alpha_status, i, j);
		super.swap(alpha, i, j);
		super.swap(p, i, j);
		super.swap(active_set, i, j);
		super.swap(G_bar, i, j);

		super.swap(QD, i, j);
	}
}
