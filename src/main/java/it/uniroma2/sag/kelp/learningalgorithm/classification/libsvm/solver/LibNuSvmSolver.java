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

import it.uniroma2.sag.kelp.kernel.Kernel;

/**
 * It is the instance of a solution provided the \(\nu\)-SVM solver of the
 * optimization problem. It is a Java porting of the library LIBSVM v3.17, written in
 * C++.
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
public abstract class LibNuSvmSolver extends LibSvmSolver {

	/**
	 * @param kernel
	 *            The kernel function
	 * @param cp
	 *            The regularization parameter for positive examples
	 * @param cn
	 *            The regularization parameter for negative examples
	 */
	public LibNuSvmSolver(Kernel kernel, int cp, int cn) {
		super(kernel, cp, cn);
	}

	public LibNuSvmSolver() {
		super();
	}

	protected boolean be_shrunk(int i, float Gmax1, float Gmax2, float Gmax3,
			float Gmax4) {
		if (is_upper_bound(i)) {
			if (y[i] == +1)
				return (-G[i] > Gmax1);
			else
				return (-G[i] > Gmax4);
		} else if (is_lower_bound(i)) {
			if (y[i] == +1)
				return (G[i] > Gmax2);
			else
				return (G[i] > Gmax3);
		} else
			return (false);
	}

	protected float calculate_r() {
		int nr_free1 = 0, nr_free2 = 0;
		float ub1 = Float.MAX_VALUE, ub2 = Float.MAX_VALUE;
		float lb1 = -Float.MAX_VALUE, lb2 = -Float.MAX_VALUE;
		float sum_free1 = 0, sum_free2 = 0;

		for (int i = 0; i < active_size; i++) {
			if (y[i] == +1) {
				if (is_upper_bound(i))
					lb1 = Math.max(lb1, G[i]);
				else if (is_lower_bound(i))
					ub1 = Math.min(ub1, G[i]);
				else {
					++nr_free1;
					sum_free1 += G[i];
				}
			} else {
				if (is_upper_bound(i))
					lb2 = Math.max(lb2, G[i]);
				else if (is_lower_bound(i))
					ub2 = Math.min(ub2, G[i]);
				else {
					++nr_free2;
					sum_free2 += G[i];
				}
			}
		}

		float r1, r2;
		if (nr_free1 > 0)
			r1 = sum_free1 / nr_free1;
		else
			r1 = (ub1 + lb1) / 2;

		if (nr_free2 > 0)
			r2 = sum_free2 / nr_free2;
		else
			r2 = (ub2 + lb2) / 2;

		return (r1 + r2) / 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#calculate_rho()
	 */
	protected float calculate_rho() {
		int nr_free1 = 0, nr_free2 = 0;
		float ub1 = Float.MAX_VALUE, ub2 = Float.MAX_VALUE;
		float lb1 = -Float.MAX_VALUE, lb2 = -Float.MAX_VALUE;
		float sum_free1 = 0, sum_free2 = 0;

		for (int i = 0; i < active_size; i++) {
			if (y[i] == +1) {
				if (is_upper_bound(i))
					lb1 = Math.max(lb1, G[i]);
				else if (is_lower_bound(i))
					ub1 = Math.min(ub1, G[i]);
				else {
					++nr_free1;
					sum_free1 += G[i];
				}
			} else {
				if (is_upper_bound(i))
					lb2 = Math.max(lb2, G[i]);
				else if (is_lower_bound(i))
					ub2 = Math.min(ub2, G[i]);
				else {
					++nr_free2;
					sum_free2 += G[i];
				}
			}
		}

		float r1, r2;
		if (nr_free1 > 0)
			r1 = sum_free1 / nr_free1;
		else
			r1 = (ub1 + lb1) / 2;

		if (nr_free2 > 0)
			r2 = sum_free2 / nr_free2;
		else
			r2 = (ub2 + lb2) / 2;

		return (r1 - r2) / 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver#do_shrinking()
	 */
	protected void do_shrinking() {
		// max { -y_i * grad(f)_i | y_i = +1, i in I_up(\alpha) }
		float Gmax1 = -Float.MAX_VALUE;
		// max { y_i * grad(f)_i | y_i = +1, i in I_low(\alpha) }
		float Gmax2 = -Float.MAX_VALUE;
		// max { -y_i * grad(f)_i | y_i = -1, i in I_up(\alpha) }
		float Gmax3 = -Float.MAX_VALUE;
		// max { y_i * grad(f)_i | y_i = -1, i in I_low(\alpha) }
		float Gmax4 = -Float.MAX_VALUE;

		// find maximal violating pair first
		int i;
		for (i = 0; i < active_size; i++) {
			if (!is_upper_bound(i)) {
				if (y[i] == +1) {
					if (-G[i] > Gmax1)
						Gmax1 = -G[i];
				} else if (-G[i] > Gmax4)
					Gmax4 = -G[i];
			}
			if (!is_lower_bound(i)) {
				if (y[i] == +1) {
					if (G[i] > Gmax2)
						Gmax2 = G[i];
				} else if (G[i] > Gmax3)
					Gmax3 = G[i];
			}
		}

		if (unshrink == false
				&& Math.max(Gmax1 + Gmax2, Gmax3 + Gmax4) <= eps * 10) {
			unshrink = true;
			reconstruct_gradient();
			active_size = l;
		}

		for (i = 0; i < active_size; i++)
			if (be_shrunk(i, Gmax1, Gmax2, Gmax3, Gmax4)) {
				active_size--;
				while (active_size > i) {
					if (!be_shrunk(active_size, Gmax1, Gmax2, Gmax3, Gmax4)) {
						swap_index(i, active_size);
						break;
					}
					active_size--;
				}
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.solver.
	 * LibSvmSolver
	 * #select_working_set(it.uniroma2.sag.kelp.learningalgorithm.classification
	 * .libsvm.solver.LibSvmSolver.Pair)
	 */
	@Override
	protected int select_working_set(Pair p) {

		float Gmaxp = -Float.MAX_VALUE;
		float Gmaxp2 = -Float.MAX_VALUE;
		int Gmaxp_idx = -1;

		float Gmaxn = -Float.MAX_VALUE;
		float Gmaxn2 = -Float.MAX_VALUE;
		int Gmaxn_idx = -1;

		int Gmin_idx = -1;
		float obj_diff_min = Float.MAX_VALUE;

		for (int t = 0; t < active_size; t++)
			if (y[t] == +1) {
				if (!is_upper_bound(t))
					if (-G[t] >= Gmaxp) {
						Gmaxp = -G[t];
						Gmaxp_idx = t;
					}
			} else {
				if (!is_lower_bound(t))
					if (G[t] >= Gmaxn) {
						Gmaxn = G[t];
						Gmaxn_idx = t;
					}
			}

		int ip = Gmaxp_idx;
		int in = Gmaxn_idx;
		// const Qfloat *Q_ip = NULL;
		// const Qfloat *Q_in = NULL;
		// if(ip != -1) // NULL Q_ip not accessed: Gmaxp=-Float.MAX_VALUE if
		// ip=-1
		// Q_ip = Q->get_Q(ip,active_size);
		// if(in != -1)
		// Q_in = Q->get_Q(in,active_size);

		for (int j = 0; j < active_size; j++) {
			if (y[j] == +1) {
				if (!is_lower_bound(j)) {
					float grad_diff = Gmaxp + G[j];
					if (G[j] >= Gmaxp2)
						Gmaxp2 = G[j];
					if (grad_diff > 0) {
						float obj_diff;
						float quad_coef = QD[ip] + QD[j] - 2 * get_Qij(ip, j);
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
					float grad_diff = Gmaxn - G[j];
					if (-G[j] >= Gmaxn2)
						Gmaxn2 = -G[j];
					if (grad_diff > 0) {
						float obj_diff;
						float quad_coef = QD[in] + QD[j] - 2 * get_Qij(in, j);
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

		if (Math.max(Gmaxp + Gmaxp2, Gmaxn + Gmaxn2) < eps)
			return 1;

		if (y[Gmin_idx] == +1)
			p.i = Gmaxp_idx;
		else
			p.i = Gmaxn_idx;
		p.j = Gmin_idx;

		return 0;
	}

}
