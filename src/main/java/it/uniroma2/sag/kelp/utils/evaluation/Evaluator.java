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

package it.uniroma2.sag.kelp.utils.evaluation;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;
import it.uniroma2.sag.kelp.utils.exception.NoSuchPerformanceMeasureException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Evaluator {
	
	protected boolean computed=false;
	
	/**
	 * This method allow to retrieve a performance measure by specifying the name of the method to be used.
	 *  
	 * @param performanceMeasureMethodName the method of the name to be used to retrive the performance measure
	 * @param args the arguments to be passed to the method @param performanceMeasureMethodName.
	 * @return the float representing the requested measure
	 * 
	 * @throws NoSuchPerformanceMeasureException
	 */
	public float getPerformanceMeasure(String performanceMeasureMethodName, Object... args) throws NoSuchPerformanceMeasureException {
		this.compute();
		@SuppressWarnings("rawtypes")
		Class[] methodParameters=null;
		if (args != null) {
			methodParameters = new Class[args.length];
			for (int i=0; i<args.length; ++i) {
				methodParameters[i] = args[i].getClass();
			}
		}
		try {
			String methodName = "get" + performanceMeasureMethodName.substring(0,1).toUpperCase() + performanceMeasureMethodName.substring(1);
			Method method = this.getClass().getMethod(methodName, methodParameters);
			Object invokedResult = method.invoke(this, args);
			Float res = (Float)invokedResult;
			return res.floatValue();
		} catch (SecurityException e) {
			throw new NoSuchPerformanceMeasureException("Evaluator can't find the specified performance measure: " + performanceMeasureMethodName);
		} catch (NoSuchMethodException e) {
			throw new NoSuchPerformanceMeasureException("Evaluator can't find the specified performance measure: " + performanceMeasureMethodName);
		} catch (IllegalArgumentException e) {
			throw new NoSuchPerformanceMeasureException("Evaluator can't call the specified performance measure: " + performanceMeasureMethodName + " with the specified arguments");
		} catch (IllegalAccessException e) {
			throw new NoSuchPerformanceMeasureException("Evaluator can't call the specified performance measure: " + performanceMeasureMethodName + " with the specified arguments");
		} catch (InvocationTargetException e) {
			throw new NoSuchPerformanceMeasureException("Evaluator can't call the specified performance measure: " + performanceMeasureMethodName + " with the specified arguments");
		}
	}
	
	/**
	 * This method should be implemented in the subclasses to update counters useful to compute the performance measure
	 * 
	 * @param test the test example
	 * @param predicted the prediction of the system
	 */
	public abstract void addCount(Example test, Prediction predicted);
	
	/**
	 * This method is intented to force the computation of the performance measure.
	 */
	protected abstract void compute();
	
	/**
	 * This method should reset the state of the evaluator
	 */
	public abstract void clear();
	
	/**
	 * Returns a new instance of this Evaluator
	 * 
	 * @return a new instance of this Evaluator
	 */
	public abstract Evaluator duplicate();
}
