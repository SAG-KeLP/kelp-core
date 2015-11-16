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
import it.uniroma2.sag.kelp.kernel.Kernel;

/**
 * This class implements the solver of the C-SVM quadratic problem described in
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
public abstract class LibCSvmSolver extends LibSvmSolver {	
	/**
	 * @param kernel
	 *            The kernel function
	 * @param cp
	 *            The regularization parameter for positive examples
	 * @param cn
	 *            The regularization parameter for negative examples
	 */
	public LibCSvmSolver(Kernel kernel, float cp, float cn) {
		super(kernel, cp, cn);
	}

	public LibCSvmSolver() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver
	 * #select_working_set(it.uniroma2.sag.kelp.learningalgorithm.classification
	 * .libsvm.solver.LibSvmSolver.Pair)
	 */
	protected int select_working_set(Pair pair) {
		float Gmax = -Float.MAX_VALUE;
		float Gmax2 = -Float.MAX_VALUE;
		int Gmax_idx = -1;
		int Gmin_idx = -1;
		float obj_diff_min = Float.MAX_VALUE;

		for (int t = 0; t < active_size; t++)
			if (y[t] == +1) {
				if (!is_upper_bound(t))
					if (-G[t] >= Gmax) {
						Gmax = -G[t];
						Gmax_idx = t;
					}
			} else {
				if (!is_lower_bound(t))
					if (G[t] >= Gmax) {
						Gmax = G[t];
						Gmax_idx = t;
					}
			}

		int i = Gmax_idx;
		// float[] Q_i = null;
		// if (i != -1) // NULL Q_i not accessed: Gmax=-INF if i=-1
		// Q_i = get_Q(i, active_size);

		for (int j = 0; j < active_size; j++) {
			if (y[j] == +1) {
				if (!is_lower_bound(j)) {
					float grad_diff = Gmax + G[j];
					if (G[j] >= Gmax2)
						Gmax2 = G[j];
					if (grad_diff > 0) {
						float obj_diff;
						float quad_coef = QD[i] + QD[j] - 2.0f * y[i]
								* get_Qij(i, j);
						if (quad_coef > 0)
							obj_diff = -(grad_diff * grad_diff) / quad_coef;
						else
							obj_diff = -(grad_diff * grad_diff) / TAU;

						if (obj_diff <= obj_diff_min) {
							Gmin_idx = j;
							obj_diff_min = obj_diff;
						}
					}
				}
			} else {
				if (!is_upper_bound(j)) {
					float grad_diff = Gmax - G[j];
					if (-G[j] >= Gmax2)
						Gmax2 = -G[j];
					if (grad_diff > 0) {
						float obj_diff;
						float quad_coef = QD[i] + QD[j] + 2.0f * y[i]
								* get_Qij(i, j);
						if (quad_coef > 0)
							obj_diff = -(grad_diff * grad_diff) / quad_coef;
						else
							obj_diff = -(grad_diff * grad_diff) / TAU;

						if (obj_diff <= obj_diff_min) {
							Gmin_idx = j;
							obj_diff_min = obj_diff;
						}
					}
				}
			}
		}

		// System.out.println(Gmax+" "+Gmax2);

		if (Gmax + Gmax2 < eps) {
			return 1;
		}

		pair.i = Gmax_idx;
		pair.j = Gmin_idx;

		return 0;
	}

	protected boolean be_shrunk(int i, float Gmax1, float Gmax2) {
		if (is_upper_bound(i)) {
			if (y[i] == +1)
				return (-G[i] > Gmax1);
			else
				return (-G[i] > Gmax2);
		} else if (is_lower_bound(i)) {
			if (y[i] == +1)
				return (G[i] > Gmax2);
			else
				return (G[i] > Gmax1);
		} else
			return (false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#calculate_rho()
	 */
	protected float calculate_rho() {
		float r;
		int nr_free = 0;
		float ub = Float.MAX_VALUE, lb = -Float.MAX_VALUE, sum_free = 0;
		for (int i = 0; i < active_size; i++) {
			float yG = y[i] * G[i];

			if (is_upper_bound(i)) {
				if (y[i] == -1)
					ub = Math.min(ub, yG);
				else
					lb = Math.max(lb, yG);
			} else if (is_lower_bound(i)) {
				if (y[i] == +1)
					ub = Math.min(ub, yG);
				else
					lb = Math.max(lb, yG);
			} else {
				++nr_free;
				sum_free += yG;
			}
		}

		if (nr_free > 0)
			r = sum_free / nr_free;
		else
			r = (ub + lb) / 2;

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#do_shrinking()
	 */
	protected void do_shrinking() {
		int i;
		// max { -y_i * grad(f)_i | i in I_up(\alpha) }
		float Gmax1 = -Float.MAX_VALUE;

		// max { y_i * grad(f)_i | i in I_low(\alpha) }
		float Gmax2 = -Float.MAX_VALUE;

		// find maximal violating pair first
		for (i = 0; i < active_size; i++) {
			if (y[i] == +1) {
				if (!is_upper_bound(i)) {
					if (-G[i] >= Gmax1)
						Gmax1 = -G[i];
				}
				if (!is_lower_bound(i)) {
					if (G[i] >= Gmax2)
						Gmax2 = G[i];
				}
			} else {
				if (!is_upper_bound(i)) {
					if (-G[i] >= Gmax2)
						Gmax2 = -G[i];
				}
				if (!is_lower_bound(i)) {
					if (G[i] >= Gmax1)
						Gmax1 = G[i];
				}
			}
		}

		if (unshrink == false && Gmax1 + Gmax2 <= eps * 10) {
			unshrink = true;
			reconstruct_gradient();
			active_size = l;
			info("*");
		}

		for (i = 0; i < active_size; i++)
			if (be_shrunk(i, Gmax1, Gmax2)) {
				active_size--;
				while (active_size > i) {
					if (!be_shrunk(active_size, Gmax1, Gmax2)) {
						swap_index(i, active_size);
						break;
					}
					active_size--;
				}
			}
	}

	/**
	 * Get the initial weight for the future Support Vectors
	 * 
	 * @param trainingSet
	 *            the training set
	 * @return
	 */
	protected float[] getCSvmAlpha(Dataset trainingSet) {
		float[] res = new float[trainingSet.getNumberOfExamples()];
		for (int i = 0; i < res.length; i++) {
			res[i] = 0f;
		}
		return res;
	}

}
