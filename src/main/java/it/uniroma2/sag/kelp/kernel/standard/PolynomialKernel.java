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

/**
 * Polynomial Kernel: given a kernel K, the polynomial formula Kp is
 * 
 * \(Kp(x,y)=(a*K(x,y)+b)^d\)
 * 
 * @author Simone Filice
 */
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.KernelComposition;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("poly")
public class PolynomialKernel extends KernelComposition {

	private float degree;
	private float a = 1f;	
	private float b = 0f;
	
	
	public PolynomialKernel(float degree, float a, float b, Kernel inputSpace) {
		this.baseKernel = inputSpace;
		this.degree=degree;
		this.a=a;
		this.b=b;
		
	}
	
	public PolynomialKernel(float degree, Kernel inputSpace) {
		this(degree, 1, 0, inputSpace);
	}

	public PolynomialKernel(){
		
	}
	
	@Override
	protected float kernelComputation(Example exA, Example exB) {
		float dotProduct=this.baseKernel.innerProduct(exA, exB);
		
		if(this.degree == (int)this.degree){
			return it.uniroma2.sag.kelp.utils.Math.pow((this.a*dotProduct+this.b), (int)degree);
		}
		return (float)Math.pow((this.a*dotProduct+this.b), degree);
	}

	/**
	 * @return the degree
	 */
	public float getDegree() {
		return degree;
	}

	/**
	 * @param degree the degree to set
	 */
	public void setDegree(float degree) {
		this.degree = degree;
	}

	/**
	 * @return the a
	 */
	public float getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(float a) {
		this.a = a;
	}

	/**
	 * @return the b
	 */
	public float getB() {
		return b;
	}

	/**
	 * @param b the b to set
	 */
	public void setB(float b) {
		this.b = b;
	}

}
