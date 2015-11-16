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

package it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver;

import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.learningalgorithm.BinaryLearningAlgorithm;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class implements the solver of the SVM quadratic problem described in
 * [CC Chang & CJ Lin, 2011]. It is a Java porting of the library LIBSVM v3.17,
 * written in C++.
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
public abstract class LibSvmSolver implements BinaryLearningAlgorithm {
	private Logger logger = LoggerFactory.getLogger(LibSvmSolver.class);

	/**
	 * Info about the Shrinking status for each example
	 * 
	 * @author Danilo Croce
	 * 
	 */
	private enum AlphaStatus {
		/**
		 * alpha[i] <= 0
		 */
		LOWER_BOUND,

		/**
		 * alpha[i] >= get_C(i)
		 */
		UPPER_BOUND,

		/**
		 * 0 < alpha[i] < get_C(i)
		 */
		FREE
	}

	/**
	 * The pair of indices i and j that are selected as working set
	 * 
	 * @author Danilo Croce
	 * 
	 */
	protected class Pair {
		int i;
		int j;
	}

	/*
	 * =================================================================
	 * Parameters to be passed
	 * =================================================================
	 */
	/**
	 * The regularization parameter of positive examples
	 */
	protected float cp = 1;

	/**
	 * The regularization parameter of negative examples
	 */
	protected float cn = 1;

	/**
	 * The Kernel function between examples, i.e. \(K_ij\)
	 */
	protected Kernel kernel;

	/**
	 * The label to be learned by the classifier
	 */
	protected Label label;

	/*
	 * =================================================================
	 * Variables used in the optimization problem that are not passed as
	 * parameters
	 * =================================================================
	 */
	@JsonIgnore
	protected boolean unshrink;

	/**
	 * Total number of Support Vectors
	 */
	@JsonIgnore
	protected int l;

	/**
	 * The integer label \(\pm 1\) of the training example
	 */
	@JsonIgnore
	protected int[] y;

	/**
	 * The input examples
	 */
	@JsonIgnore
	protected Example[] examples;

	/**
	 * The weight \(\alpha\) of the Support Vectors
	 */
	@JsonIgnore
	protected float[] alpha;

	@JsonIgnore
	protected float[] p;

	/**
	 * Q-MATRIX is derived from kernel matrix: Q_{ij}=y_{i}*y_{j}*K_{ij}
	 */
	@JsonIgnore
	protected float[] QD;

	/**
	 * The status of each example
	 */
	@JsonIgnore
	protected AlphaStatus[] alpha_status;

	/**
	 * The set of active examples with 0 < alpha[i] < get_C(i)
	 */
	@JsonIgnore
	protected int[] active_set;

	/**
	 * The number of active examples with 0 < alpha[i] < get_C(i)
	 */
	@JsonIgnore
	protected int active_size;

	/**
	 * Gradient
	 */
	@JsonIgnore
	protected float[] G;

	/**
	 * Gradient bar
	 */
	@JsonIgnore
	protected float[] G_bar;

	/*
	 * =================================================================
	 * Constants
	 * =================================================================
	 */
	/**
	 * A small positive constant to
	 */
	@JsonIgnore
	protected static final float TAU = 0.0000000001f;

	/**
	 * Number of iterations to be accomplished before shrinking
	 */
	@JsonIgnore
	protected static final int shrinkingIteration = 1000;

	/**
	 * The number of iteration to be accomplished to print info in the standard
	 * output
	 */
	@JsonIgnore
	protected static final int logIteration = 100;

	/**
	 * Tolerance of termination criterion
	 */
	@JsonIgnore
	protected static final float eps = 0.001f;

	/**
	 * A boolean value to apply shrinking
	 */
	@JsonIgnore
	protected static final boolean doShrinking = true;

	/**
	 * Upperbound to the number of iterations
	 */
	@JsonIgnore
	private static final float MAX_ITERATION = 10000000;

	public LibSvmSolver() {

	}

	/**
	 * @param kernel
	 *            The kernel function
	 * @param Cp
	 *            The regularization parameter for positive examples
	 * @param Cn
	 *            The regularization parameter for negative examples
	 */
	public LibSvmSolver(Kernel kernel, float Cp, float Cn) {
		this();
		this.kernel = kernel;
		this.cp = Cp;
		this.cn = Cn;
	}

	protected abstract float calculate_rho();

	/**
	 * Apply the shrinking step
	 */
	protected abstract void do_shrinking();

	private float get_C(int i) {
		return (y[i] > 0) ? cp : cn;
	}

	/**
	 * For each example i, it return the K_ii score
	 * 
	 * @return the array of K_ii score
	 */
	protected float[] get_QD() {
		float[] res = new float[examples.length];
		for (int j = 0; j < examples.length; j++) {
			Example ex = examples[j];
			res[j] = kernel(ex, ex);
		}
		return res;
	}

	protected float get_Qij(int i, int j) {
		Example exA = examples[i];
		Example exB = examples[j];
		return y[i] * y[j] * kernel(exA, exB);
	}

	/**
	 * @return the \(C_n\) for the negative examples
	 */
	public float getCn() {
		return cn;
	}

	/**
	 * @return the \(C_n\) for the positive examples
	 */
	public float getCp() {
		return cp;
	}

	/**
	 * @return the kernel function
	 */
	public Kernel getKernel() {
		return this.kernel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.BinaryLearningAlgorithm#getLabel()
	 */
	@Override
	public Label getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.BinaryLearningAlgorithm#getLabels
	 * ()
	 */
	@Override
	public List<Label> getLabels() {
		return Arrays.asList(label);
	}

	protected void info(String msg) {
		logger.info(msg);
		// System.out.print(string);
	}

	/**
	 * Check if 0 < alpha[i] < get_C(i)
	 * 
	 * @param i
	 *            the index of the example
	 * @return
	 */
	protected boolean is_free(int i) {
		return alpha_status[i] == AlphaStatus.FREE;
	}

	/**
	 * Check if alpha[i] <= 0
	 * 
	 * @param i
	 *            the index of the example
	 * @return
	 */
	protected boolean is_lower_bound(int i) {
		return alpha_status[i] == AlphaStatus.LOWER_BOUND;
	}

	/**
	 * Check if alpha[i] >= get_C(i)
	 * 
	 * @param i
	 *            the index of the example
	 * @return
	 */
	protected boolean is_upper_bound(int i) {
		return alpha_status[i] == AlphaStatus.UPPER_BOUND;
	}

	/**
	 * This function embeds the call to the kernel function
	 * 
	 * @param exA
	 * @param exB
	 * @return the kernel function result
	 */
	protected float kernel(Example exA, Example exB) {
		return kernel.innerProduct(exA, exB);
	}

	/**
	 * Reconstruct inactive elements of G from G_bar and free variables
	 */
	protected void reconstruct_gradient() {
		info("r");

		if (active_size == l)
			return;

		int i, j;
		int nr_free = 0;

		for (j = active_size; j < l; j++)
			G[j] = G_bar[j] + p[j];

		for (j = 0; j < active_size; j++)
			if (is_free(j))
				nr_free++;

		// if (2 * nr_free < active_size)
		// info("\nWARNING: using -h 0 may be faster\n");

		if (nr_free * l > 2 * active_size * (l - active_size)) {
			for (i = active_size; i < l; i++) {
				// float[] Q_i = get_Q(i, active_size);
				for (j = 0; j < active_size; j++)
					if (is_free(j))
						G[i] += alpha[j] * get_Qij(i, j);
			}
		} else {
			for (i = 0; i < active_size; i++)
				if (is_free(i)) {
					// float[] Q_i = get_Q(i, active_size, l);
					double alpha_i = alpha[i];
					for (j = active_size; j < l; j++)
						G[j] += alpha_i * get_Qij(i, j);
				}
		}
	}

	/**
	 * Select the working set in each iteration. This function returns the pair
	 * i,j such that<br>
	 * i: maximizes -y_i * grad(f)_i, i in I_up(\alpha) <br>
	 * j: minimizes the decrease of obj value <br>
	 * (if quadratic coefficeint <= 0, replace it with tau) <br>
	 * -y_j*grad(f)_j < -y_i*grad(f)_i, j in I_low(\alpha) <br>
	 * 
	 * 
	 * @param pair
	 *            The Q_ij to be evaluated
	 * @return 1 if already optimal, return 0 otherwise
	 */
	protected abstract int select_working_set(Pair pair);

	/**
	 * @param cn
	 *            The regularization parameter for positive examples
	 */
	public void setCn(float cn) {
		this.cn = cn;
	}

	/**
	 * @param cp
	 *            The regularization parameter for negative examples
	 */
	public void setCp(float cp) {
		this.cp = cp;
	}
	
	/**
	 * @param c
	 *            The regularization parameter for both positive and negative examples
	 */
	public void setC(float c){
		this.setCp(c);
		this.setCn(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.BinaryLearningAlgorithm#setLabel
	 * (it.uniroma2.sag.kelp.data.label.Label)
	 */
	@Override
	public void setLabel(Label label) {
		this.setLabels(Arrays.asList(label));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.uniroma2.sag.kelp.learningalgorithm.BinaryLearningAlgorithm#setLabels
	 * (java.util.List)
	 */
	@Override
	public void setLabels(List<Label> labels) {
		if (labels.size() != 1) {
			throw new IllegalArgumentException(
					"LibSVMSolver is a binary method which can learn a single Label");
		} else {
			this.label = labels.get(0);
			this.getPredictionFunction().setLabels(labels);
		}
	}

	/**
	 * It solves the SMO algorithm in [CC Chang & CJ Lin, 2011]
	 * 
	 * min 0.5(\alpha^T Q \alpha) + p^T \alpha
	 * 
	 * y^T \alpha = \delta <br>
	 * y_i = +1 or -1 <br>
	 * 0 <= alpha_i <= Cp for y_i = 1 <br>
	 * 0 <= alpha_i <= Cn for y_i = -1 <br>
	 * 
	 * Given:
	 * 
	 * Q, p, y, Cp, Cn, and an initial feasible point \alpha l is the size of
	 * vectors and matrices eps is the stopping tolerance
	 * 
	 * solution will be put in \alpha, objective value will be put in obj
	 * 
	 * 
	 * @param l_
	 *            the size of input examples
	 * @param dataset
	 *            the dataset
	 * @param p_
	 * @param y_
	 *            the labels of the examples
	 * @param initial_alpha
	 *            the initial \(\alpha\) values
	 * @return the solution of the SMO problem
	 */
	public SvmSolution solve(int l_, Dataset dataset, float[] p_, int y_[],
			float[] initial_alpha) {

		SvmSolution solution = new SvmSolution();

		/**
		 * INITIALIZE VARIABLES
		 */

		this.l = l_;

		this.examples = new Example[dataset.getNumberOfExamples()];
		for (int i = 0; i < dataset.getNumberOfExamples(); i++) {
			examples[i] = dataset.getExamples().get(i);
		}

		// CHECK
		this.QD = get_QD();
		this.p = p_.clone();

		this.y = y_.clone();

		this.alpha = initial_alpha.clone();

		// initialize alpha_status
		{
			alpha_status = new AlphaStatus[l];
			for (int i = 0; i < l; i++)
				update_alpha_status(i);
		}

		// initialize active set (for shrinking)
		{
			active_set = new int[l];
			for (int i = 0; i < l; i++)
				active_set[i] = i;
			active_size = l;
		}

		// initialize gradient
		{
			G = new float[l];
			G_bar = new float[l];
			int i;
			for (i = 0; i < l; i++) {
				G[i] = p[i];
				G_bar[i] = 0;
			}
			for (i = 0; i < l; i++)
				if (!is_lower_bound(i)) {
					// float[] Q_i = get_Q(i, l);
					double alpha_i = alpha[i];
					int j;
					for (j = 0; j < l; j++)
						G[j] += alpha_i * get_Qij(i, j);
					if (is_upper_bound(i))
						for (j = 0; j < l; j++)
							G_bar[j] += get_C(i) * get_Qij(i, j);
				}
		}

		this.unshrink = false;

		/**
		 * optimization step
		 */

		int iter = 0;
		int max_iter = (int) Math
				.max(MAX_ITERATION,
						l > (float) Float.MAX_VALUE / 100f ? Float.MAX_VALUE
								: 100f * l);
		int counter = Math.min(l, shrinkingIteration) + 1;

		/*
		 * USED TO GET i and j from the select_working_set()
		 */
		Pair pair = new Pair();

		while (iter < max_iter) {

			// show progress and do shrinking

			if (iter % logIteration == 0)
				info(".");

			if (--counter == 0) {
				counter = Math.min(l, shrinkingIteration);
				if (doShrinking)
					do_shrinking();
				info("s");
			}

			if (select_working_set(pair) != 0) {
				// reconstruct the whole gradient
				reconstruct_gradient();
				// reset active set size and check
				active_size = l;
				info("d");
				if (select_working_set(pair) != 0) {
					/*
					 * DONE
					 */
					break;
				} else
					counter = 1; // do shrinking next iteration
			}

			int i = pair.i;
			int j = pair.j;

			// System.out.println(i+" "+j);

			++iter;

			// update alpha[i] and alpha[j], handle bounds carefully

			// float[] Q_i = get_Q(i, active_size);
			// float[] Q_j = get_Q(j, active_size);

			float C_i = get_C(i);
			float C_j = get_C(j);

			float old_alpha_i = alpha[i];
			float old_alpha_j = alpha[j];

			if (y[i] != y[j]) {

				float quad_coef = QD[i] + QD[j] + 2 * get_Qij(i, j);

				// System.out.println(i +" "+ j +" "+ QD[i]+ " "+QD[j]+ " "+
				// get_Qij(i, j));
				if (quad_coef <= 0)
					quad_coef = TAU;
				float delta = (-G[i] - G[j]) / quad_coef;
				float diff = alpha[i] - alpha[j];
				alpha[i] += delta;
				alpha[j] += delta;

				if (diff > 0) {
					if (alpha[j] < 0) {
						alpha[j] = 0;
						alpha[i] = diff;
					}
				} else {
					if (alpha[i] < 0) {
						alpha[i] = 0;
						alpha[j] = -diff;
					}
				}
				if (diff > C_i - C_j) {
					if (alpha[i] > C_i) {
						alpha[i] = C_i;
						alpha[j] = C_i - diff;
					}
				} else {
					if (alpha[j] > C_j) {
						alpha[j] = C_j;
						alpha[i] = C_j + diff;
					}
				}
			} else {
				float quad_coef = QD[i] + QD[j] - 2 * get_Qij(i, j);
				if (quad_coef <= 0)
					quad_coef = TAU;
				float delta = (G[i] - G[j]) / quad_coef;
				float sum = alpha[i] + alpha[j];
				alpha[i] -= delta;
				alpha[j] += delta;

				if (sum > C_i) {
					if (alpha[i] > C_i) {
						alpha[i] = C_i;
						alpha[j] = sum - C_i;
					}
				} else {
					if (alpha[j] < 0) {
						alpha[j] = 0;
						alpha[i] = sum;
					}
				}
				if (sum > C_j) {
					if (alpha[j] > C_j) {
						alpha[j] = C_j;
						alpha[i] = sum - C_j;
					}
				} else {
					if (alpha[i] < 0) {
						alpha[i] = 0;
						alpha[j] = sum;
					}
				}
			}

			// update G
			double delta_alpha_i = alpha[i] - old_alpha_i;
			double delta_alpha_j = alpha[j] - old_alpha_j;

			for (int k = 0; k < active_size; k++) {
				G[k] += get_Qij(i, k) * delta_alpha_i + get_Qij(j, k)
						* delta_alpha_j;
			}

			// update alpha_status and G_bar
			{
				boolean ui = is_upper_bound(i);
				boolean uj = is_upper_bound(j);
				update_alpha_status(i);
				update_alpha_status(j);
				int k;
				if (ui != is_upper_bound(i)) {

					if (ui)
						for (k = 0; k < l; k++)
							G_bar[k] -= C_i * get_Qij(i, k);
					else
						for (k = 0; k < l; k++)
							G_bar[k] += C_i * get_Qij(i, k);
				}

				if (uj != is_upper_bound(j)) {
					// Q_j = get_Q(j, l);
					if (uj)
						for (k = 0; k < l; k++)
							G_bar[k] -= C_j * get_Qij(j, k);
					else
						for (k = 0; k < l; k++)
							G_bar[k] += C_j * get_Qij(j, k);
				}
			}
		}

		if (iter > max_iter) {
			if (active_size < l) {
				// reconstruct the whole gradient to calculate objective value
				reconstruct_gradient();
				active_size = l;
				info("*");
			}
			info("\nWARNING: reaching max number of iterations\n");
		}

		// calculate rho
		solution.setRho(calculate_rho());

		// calculate objective value
		{
			float v = 0;
			int i;
			for (i = 0; i < l; i++)
				v += alpha[i] * (G[i] + p[i]);

			solution.setObj(v / 2);
		}

		// juggle everything back
		/*
		 * { for(int i=0;i<l;i++) while(active_set[i] != i)
		 * swap_index(i,active_set[i]); // or Q.swap_index(i,active_set[i]); }
		 */

		solution.setUpper_bound_p(cp);
		solution.setUpper_bound_n(cn);

		float[] newAlphas = new float[alpha.length];

		for (int i = 0; i < l; i++)
			newAlphas[active_set[i]] = alpha[i];

		solution.setAlphas(newAlphas);

		info("\nOptimization finished after #iter = " + iter + "\n");

		return solution;
	}

	protected void swap(AlphaStatus[] array, int i, int j) {
		AlphaStatus tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	protected void swap(Example[] array, int i, int j) {
		Example tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	protected void swap(float[] array, int i, int j) {
		float tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	protected void swap(int[] array, int i, int j) {
		int tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	/**
	 * Swap the info of two examples
	 * 
	 * @param i
	 *            the first example index
	 * @param j
	 *            the second example index
	 */
	protected void swap_index(int i, int j) {
		swap(examples, i, j);
		swap(y, i, j);
		swap(G, i, j);
		swap(alpha_status, i, j);
		swap(alpha, i, j);
		swap(p, i, j);
		swap(active_set, i, j);
		swap(G_bar, i, j);

		swap(QD, i, j);
	}

	/**
	 * @param i
	 *            the index of the example whose status needs to be updated
	 */
	private void update_alpha_status(int i) {
		if (alpha[i] >= get_C(i))
			alpha_status[i] = AlphaStatus.UPPER_BOUND;
		else if (alpha[i] <= 0)
			alpha_status[i] = AlphaStatus.LOWER_BOUND;
		else
			alpha_status[i] = AlphaStatus.FREE;
	}
}
