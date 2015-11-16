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

package it.uniroma2.sag.kelp.predictionfunction.regressionfunction;

import java.util.Arrays;
import java.util.List;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryModel;

/**
 * It is a univariate regression prediction function. Univariate
 * means that the output is a single real value associated to a specific property.
 * 
 * @author Simone Filice
 *
 */
public abstract class UnivariateRegressionFunction implements RegressionFunction{

	protected Label property;
	
	/**
	 * @param positiveClass the label associated to the positive class
	 */
	@Override
	public void setLabels(List<Label> labels) {
		if(labels.size()!=1){
			throw new IllegalArgumentException("A UnivariateRegressor can predict a single property");
		}
		else{
			this.property=labels.get(0);
			
		}
	}
	
	/**
	 * @return the label associated to the property
	 */
	@Override
	public List<Label> getLabels() {
		return Arrays.asList(property);
	}
		
	@Override
	public abstract UnivariateRegressionOutput predict(Example example);
	
	/**
	 * @return the model
	 */
	public abstract BinaryModel getModel();

	
	@Override
	public void reset() {
		this.getModel().reset();
		
	}

}
