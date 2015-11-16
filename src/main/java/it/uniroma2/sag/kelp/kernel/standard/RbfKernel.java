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

package it.uniroma2.sag.kelp.kernel.standard;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.KernelComposition;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Radial Basis Function Kernel. <br>
 * Given a kernel K, the RBF formula Kr is: <br>
 * \(Kr(x,y) = e^{-\gamma * \left \lVert x-y \right \rVert ^2}\) where: <br>
 * \(\left\lVert a \right\rVert\) is the norm of a in the kernel space K, thus \(\left\lVert x-y \right\rVert ^2\) can be computed
 * as: <br>
 * \(\left\lVert x-y \right\rVert ^2 = \left\lVert x \right\rVert ^2 + \left\lVert y \right\rVert ^2 - 2K(x,y) = K(x,x) + K(y,y) - 2K(x,y)\)
 * 
 * @author Simone Filice
 */
@JsonTypeName("rbf")
public class RbfKernel extends KernelComposition {

	private float gamma;

	public RbfKernel(float gamma, Kernel inputSpace) {
		this.baseKernel = inputSpace;
		this.gamma = gamma;
	}

	public RbfKernel() {

	}

	/**
	 * @return the gamma
	 */
	public float getGamma() {
		return gamma;
	}

	/**
	 * @param gamma
	 *            the gamma to set
	 */
	public void setGamma(float gamma) {
		this.gamma = gamma;
	}

	@Override
	protected float kernelComputation(Example exA, Example exB) {
		float innerProductOfTheDiff = this.baseKernel
				.squaredNormOfTheDifference(exA, exB);
		return (float) Math.exp(-gamma * innerProductOfTheDiff);
	}
}
