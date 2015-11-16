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

package it.uniroma2.sag.kelp.utils;

/**
 * Implements static utility methods for mathematical operations and statistics
 * 
 * @author Simone Filice
 *
 */
public class Math {
	
	private static final int SOFTMAX_FACTOR = 100;
	
	/**
	 * It evaluates the power of a number 
	 * 
	 * @param base the base
	 * @param exponent the exponent
	 * @return \(base^{exponent}\)
	 */
	public static float pow(float base, int exponent){
		if(java.lang.Math.abs(exponent)>40){
			return (float)java.lang.Math.pow(base, exponent);
		}else{
			if(exponent == 0){
				return 1;
			}
			
			if(exponent<0){
				base=1f/base;
				exponent=-exponent;
			}
			float value = base; 
			for(int i=1; i<exponent; i++){
				value*=base;
			}
			return value;
		}
	}
	
	/**
	 * Computes the arithmetic mean \(\bar{x}\) of the input values \(x_1, \ldots x_n\)
	 * <p>
	 * \(\bar{x} = \frac{1}{n}  \sum_{i=1}^{n}x_i\)
	 * 
	 * @param values the input values on which computing the arithmetic mean
	 * @return the mean
	 */
	public static float getMean(float [] values){
		if(values.length==0){
			return 0;
		}
		float sum = 0;
		for(float value : values){
			sum+=value;
		}
		return sum/=values.length;
	}
	
	/**
	 * Estimates the unbiased sample variance \(\sigma^2\) of population using some samples 
	 * \(x_1, \ldots x_n\) whose estimated mean is \(\bar{x}\)
	 * <p>
	 * \(\sigma^2 = \frac{1}{n-1}  \sum_{i=1}^{n}(x_i -\bar{x_i})^2\)
	 * 
	 * 
	 * @param values the samples of the population whose variance must be estimated
	 * @return the unbiased sample variance
	 */
	public static float getVariance(float [] values){
		if(values.length<2){
			return 0;
		}
		float sum = 0;
		float mean = getMean(values);
		for(float value : values){
			float difference = value - mean;
			sum+= difference*difference;
		}
		
		return sum/(values.length-1);
	}
	
	/**
	 * Estimates the unbiased standard deviation \(\sigma\) of population using some samples 
	 * \(x_1, \ldots x_n\) whose estimated mean is \(\bar{x}\)
	 * <p>
	 * \(\sigma = \sqrt{\frac{1}{n-1}  \sum_{i=1}^{n}(x_i -\bar{x_i})^2}\)
	 * 
	 * 
	 * @param values the samples of the population whose standard deviation must be estimated
	 * @return the unbiased sample standard deviation
	 */
	public static double getStandardDeviation(float [] values){
		return java.lang.Math.sqrt(getVariance(values));
	}
	
	
	/**
	 * Approximates the max of two values with the following formula:
	 * 	\(softmax(a,b) = \frac{log(e^{Fa} + e^{Fb})}{F}\) 
	 * <p>
	 * where F=100
	 * <p>
	 * This approximation is necessary when the max function is needed in a kernel
	 * to preserve its semi-positiveness (because the max does break this property)
	 * 
	 * 
	 * @param a the first value
	 * @param b the second value
	 * @return the approximation of <code>max(a,b)</code>
	 */
	public static float softmax(float a, float b){
		double expA = java.lang.Math.exp(SOFTMAX_FACTOR*a);
		double expB =  java.lang.Math.exp(SOFTMAX_FACTOR*b);
		return (float)(java.lang.Math.log(expA + expB)/SOFTMAX_FACTOR);
	}
}
