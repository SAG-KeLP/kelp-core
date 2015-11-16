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
import it.uniroma2.sag.kelp.data.example.ExamplePair;
import it.uniroma2.sag.kelp.kernel.KernelComposition;

/**
 * It is a kernel operating on ExamplePairs applying a simpler kernel to the pair elements
 * 
 * @author Simone Filice
 */
public abstract class KernelOnPairs extends KernelComposition{
	
	@Override
	protected float kernelComputation(Example exA, Example exB) {
		
		if(!(exA instanceof ExamplePair) || !(exB instanceof ExamplePair)){
			throw new java.lang.IllegalArgumentException("Invalid object: expected two ExamplePairs to compute any kernel over pairs");
		}
		ExamplePair pairA = (ExamplePair)exA;
		ExamplePair pairB = (ExamplePair)exB;
		Example exA1 = pairA.getLeftExample();
		Example exA2 = pairA.getRightExample();
		Example exB1 = pairB.getLeftExample();
		Example exB2 = pairB.getRightExample();	
		
		return this.kernelComputationOverPairs(exA1, exA2, exB1, exB2);
	}

	
	/**
	 * Returns the kernel computation
	 * 
	 * @param exA1 the first element of the first pair
	 * @param exA2 the second element of the first pair
	 * @param exB1 the first element of the second pair
	 * @param exB2 the second element of the second pair
	 * @return the result of the kernel computation
	 */
	public abstract float kernelComputationOverPairs(Example exA1, Example exA2, Example exB1, Example exB2);

}
