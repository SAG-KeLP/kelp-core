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
 * Normalization of a generic kernel K</br>
 * 
 * Normalization formula: \(K(x,y) = \frac{K(x,y)}{\sqrt{(K(x,x) \cdot K(y,y))}}\)
 * 
 * @author Simone Filice
 */
@JsonTypeName("norm")
public class NormalizationKernel extends KernelComposition{
	
	public NormalizationKernel(Kernel kernelToNormalize){
		this.baseKernel = kernelToNormalize;
	}
	
	public NormalizationKernel(){
		
	}
	
	@Override
	protected float kernelComputation(Example exA, Example exB) {
		Float qNormA = this.baseKernel.squaredNorm(exA);
		if(qNormA==0){
			return 0;
		}
		Float qNormB = this.baseKernel.squaredNorm(exB);
		if(qNormB==0){
			return 0;
		}		
		float kernelVal=this.baseKernel.innerProduct(exA, exB);
		
		return (float)(kernelVal/(Math.sqrt(qNormA*qNormB)));
	}

	@Override
	public float squaredNorm(Example example){
		return 1;
	}
}
