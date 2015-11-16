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

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.KernelCombination;

/**
 * Multiplication of Kernels
 * <br>Given the kernels \(K_1 \ldots K_n\), the combination formula is:
 * <br> \(\prod_{i}K_i\)

 * @author      Simone Filice
 */
@JsonTypeName("multiplication")
public class KernelMultiplication extends KernelCombination{

	public KernelMultiplication(){
		super();
	}
	
	@Override
	protected float kernelComputation(Example exA, Example exB) {
		float result = 1;
		for(Kernel kernel : this.toCombine){
			result*=kernel.innerProduct(exA, exB);
		}
		return result;
	}

}
