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

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.NumericLabel;

/**
 * It is the output of a univariate regression prediction function. It basically consist of
 * a single real value which is associated to a specific property 
 * 
 * @author Simone Filice
 *
 */
public class UnivariateRegressionOutput implements RegressionOutput{

	private NumericLabel result;

	public UnivariateRegressionOutput(NumericLabel result){
		this.result =result;  
	}
	
	public UnivariateRegressionOutput(Label property, float predictedValue){
		this.result = new NumericLabel(property, predictedValue);
	}
	
	@Override
	public Float getScore(Label label) {
		if(result.getProperty().equals(label)){
			return result.getValue();
		}
		return null;
	}

	@Override
	public List<Label> getAllProperties() {
		ArrayList<Label> properties = new ArrayList<Label>();
		properties.add(result.getProperty());
		return properties;
	}
	
	
}
