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


/**
 * It is the instance of a solution provided the LIBSVM solver of the SMO
 * optimization problem. It is a Java porting of the SVM implemented in LIBSVM v3.17
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
public class SvmSolution {

	/**
	 * The weight \(\alpha\) of the Support Vectors
	 */
	private float[] alphas;

	/**
	 * The bias term (-b)
	 */
	private float rho;

	/**
	 * The Objective Value
	 */
	private float obj;

	/**
	 * The \(C_n\) value
	 */
	private float upper_bound_n;

	/**
	 * The \(C_p\) value
	 */
	private float upper_bound_p;

	public float[] getAlphas() {
		return alphas;
	}

	/**
	 * @return The Objective Value
	 */
	public float getObj() {
		return obj;
	}

	/**
	 * @return The bias term (-b)
	 */
	public float getRho() {
		return rho;
	}

	/**
	 * @return the \(C_n\) value
	 */
	public float getUpper_bound_n() {
		return upper_bound_n;
	}

	/**
	 * @return the \(C_p\) value
	 */
	public float getUpper_bound_p() {
		return upper_bound_p;
	}

	/**
	 * @param alphas
	 *            the weight of the Support Vectors
	 */
	protected void setAlphas(float[] alphas) {
		this.alphas = alphas;
	}

	/**
	 * @param obj
	 *            The Objective Value
	 */
	public void setObj(float obj) {
		this.obj = obj;
	}

	/**
	 * @param rho
	 *            The bias term (-b)
	 */
	public void setRho(float rho) {
		this.rho = rho;
	}

	/**
	 * Set the \(C_n\) value
	 * 
	 * @param upper_bound_n
	 *            the \(C_n\) value
	 */
	public void setUpper_bound_n(float upper_bound_n) {
		this.upper_bound_n = upper_bound_n;
	}

	/**
	 * Set the \(C_p\) value
	 * 
	 * @param upper_bound_n
	 *            the \(C_p\) value
	 */
	public void setUpper_bound_p(float upper_bound_p) {
		this.upper_bound_p = upper_bound_p;
	}

}
