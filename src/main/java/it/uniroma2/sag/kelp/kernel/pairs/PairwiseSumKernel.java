/*
 * Copyright 2014-2015 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * and Giovanni Da San Martino and Alessandro Moschitti
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
 * \(K( < x_1, x_2 >, < y_1,y_2 > ) = BK(x_1, y_1) + BK(x_2, y_2) + BK(x_1, y_2) + BK(x_2, y_1)\) <br>
 * 
 * where BK is another kernel the kernel on pairs relies on. <br> 
 *
 * The pairwise sum kernel is described in: 
 * [Filice et al., 2015] Simone Filice, Giovanni Da San Martino and Alessandro Moschitti. 
 * Structural Representations for Learning Relations between Pairs of Texts. In Proc. of ACL 2015.
 * 
 * @author Simone Filice
 */
@JsonTypeName("pairwiseSum")
public class PairwiseSumKernel extends KernelOnPairs{

	private boolean addIntraPairSimProduct = false;

	/**
	 * Defines a Kernel operating on pairs that applies the following formula:<br>
	 * 
	 * \(K( < x_1, x_2 >, < y_1,y_2 > ) = BK(x_1, y_1) + BK(x_2, y_2) + BK(x_1, y_2) + BK(x_2, y_1)\) <br>
	 * 
	 * @param baseKernel the base kernel BK
	 * @param intraPairSimProduct whether adding or not the following term to K: \(BK(x_1,x_2) \cdot BK(y_1,y_2)\) 
	 * 
	 * <p>
	 * NOTE: the additional intra-pair similarity term corresponds to adding a feature \(BK(x_1,x_2)\) to the example \(< x_1, x_2 >\).
	 * Instead of enabling this additional term, it would be more efficient to explicitly adding a new vector representation to example
	 * that includes that feature. This can be easily done using the manipulate method of PairSimilarityExtractor (it is included in 
	 * vector-representation)   
	 */
	public PairwiseSumKernel(Kernel baseKernel, boolean intraPairSimProduct){
		this.addIntraPairSimProduct = intraPairSimProduct;
		this.baseKernel=baseKernel;		
	}

	public PairwiseSumKernel(){

	}

	/**
	 * @return the addIntraPairSimProduct
	 */
	public boolean getIntraPairSimProduct() {
		return addIntraPairSimProduct;
	}

	/**
	 * Sets whether adding or not to the kernel combination an extra term equivalent to the 
	 * multiplication of the intra-pair similarities, i.e.:
	 * \(BK(x_1,x_2) \cdot BK(y_1,y_2)\) 
	 * <p>
	 * NOTE: the additional intra-pair similarity term corresponds to adding a feature \(BK(x_1,x_2)\) to the example \(< x_1, x_2 >\).
	 * Instead of enabling this additional term, it would be more efficient to explicitly adding a new vector representation to example
	 * that includes that feature. This can be easily done using the manipulate method of PairSimilarityExtractor (it is included in 
	 * vector-representation)   
	 * 
	 * @param intraPairSimProduct whether adding or not the 
	 * multiplication of the intra-pair similarities
	 * 
	 */
	public void setIntraPairSimProduct(boolean intraPairSimProduct) {
		this.addIntraPairSimProduct = intraPairSimProduct;
	}

	@Override
	public float kernelComputationOverPairs(Example exA1, Example exA2, Example exB1,
			Example exB2) {
		float kernelValue = this.baseKernel.innerProduct(exA1, exB1) + this.baseKernel.innerProduct(exA2, exB2) 
				+ this.baseKernel.innerProduct(exA1, exB2) + this.baseKernel.innerProduct(exA2, exB1);
		if(addIntraPairSimProduct){
			return kernelValue + this.baseKernel.innerProduct(exA1, exA2) * this.baseKernel.innerProduct(exB1, exB2);
		}
		return kernelValue;
	}

}
