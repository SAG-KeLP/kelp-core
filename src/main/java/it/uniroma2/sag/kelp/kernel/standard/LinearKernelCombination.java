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
import it.uniroma2.sag.kelp.kernel.KernelCombination;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Weighted Linear Combination of Kernels
 * <br>Given the kernels \(K_1 \ldots K_n\), with weights \(c_1 \ldots c_n\), the combination formula is:
 * <br> \(\sum_{i}c_iK_i\)

 * @author      Simone Filice
 */

@JsonTypeName("linearComb")
public class LinearKernelCombination extends KernelCombination{
	private Logger logger = LoggerFactory.getLogger(LinearKernelCombination.class);
	
	private List<Float> weights;
	
	/**
	 * @return the weights
	 */
	public List<Float> getWeights() {
		return weights;
	}

	/**
	 * @param weights the weights to set
	 */
	public void setWeights(List<Float> weights) {
		this.weights = weights;
	}

	public LinearKernelCombination(){
		super();
		this.weights = new ArrayList<Float>();
	}
	
	/**
	 * Adds a kernel with a corresponding weight to the linear combination of kernels
	 * 
	 * @param weight the weight of the kernel to be added
	 * @param kernel the kernel to be added
	 */
	public void addKernel(float weight, Kernel kernel){
		this.weights.add(weight);
		this.toCombine.add(kernel);
	}
	
	
	/**
	 * Scales the weights in order to make their sum being equal to 1 
	 */
	public void normalizeWeights() {
		float sum = 0;
		for(float weight : weights){
			sum+=weight;
		}
	
		for (int i = 0; i < this.weights.size(); i++) {
			float val = this.weights.get(i).floatValue();
			this.weights.set(i, new Float(val / sum));
		}
	}

	@Override
	protected float kernelComputation(Example exA, Example exB) {
		float sum = 0;
		for (int i = 0; i < this.toCombine.size(); i++) {
			logger.debug(i+" "+weights.get(i).floatValue()+"*"+toCombine.get(i).innerProduct(exA, exB));	
			if(weights.get(i).floatValue()!=0)
				sum += weights.get(i).floatValue()
					* toCombine.get(i).innerProduct(exA, exB);
		}
		return sum;
	}
}
