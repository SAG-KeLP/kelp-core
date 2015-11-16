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

package it.uniroma2.sag.kelp.kernel;

import java.util.ArrayList;
import java.util.List;

/**
 * It is a kernel that operates combining other kernels
 * 
 * @author Simone Filice
 */
public abstract class KernelCombination extends Kernel {
	protected List<Kernel> toCombine;

	public KernelCombination() {
		this.toCombine = new ArrayList<Kernel>();
	}

	/**
	 * Returns a list of the kernels this kernel is combining
	 * 
	 * @return the kernels to be combined
	 */
	public List<Kernel> getToCombine() {
		return this.toCombine;
	}

	/**
	 * @param toCombine
	 *            the toCombine to set
	 */
	public void setToCombine(List<Kernel> toCombine) {
		this.toCombine = toCombine;
	}
}
