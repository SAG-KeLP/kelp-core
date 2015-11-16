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

package it.uniroma2.sag.kelp.kernel.pairs;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.pairs.KernelOnPairs;

import com.fasterxml.jackson.annotation.JsonTypeName;
/**
 * It is a kernel operating on ExamplePairs applying the following formula:<br>
 * 
 * \(K( < x_1, x_2 >, < y_1,y_2 > ) = BK(x_1, y_1) + BK(x_2, y_2) - BK(x_1, y_2) - BK(x_2, y_1)\) <br>
 * 
 * where BK is another kernel the preference kernel relies on. <br> 
 *
 * The preference kernel was firstly introduced in: 
 * [Shen and Joshi, 2003] L. Shen and A. K. Joshi. An SVM based voting algorithm
 * with application to parse reranking. In Proc. of CoNLL. 2003.
 * 
 * @author Simone Filice
 */
@JsonTypeName("preference")
public class PreferenceKernel extends KernelOnPairs{
			
	public PreferenceKernel(Kernel baseKernel){
		this.baseKernel=baseKernel;		
	}
	
	public PreferenceKernel(){
		
	}

	@Override
	public float kernelComputationOverPairs(Example exA1, Example exA2, Example exB1,
			Example exB2) {
		return this.baseKernel.innerProduct(exA1, exB1) + this.baseKernel.innerProduct(exA2, exB2) 
				- this.baseKernel.innerProduct(exA1, exB2) - this.baseKernel.innerProduct(exA2, exB1);
	}
}
