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

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryLinearModel;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is a univariate regression prediction function consisting of an explicit hyperplane. Univariate
 * means that the output is a single real value associated to a specific property.
 * 
 * @author Simone Filice
 *
 */
@JsonTypeName("linearRegressor")
public class UnivariateLinearRegressionFunction extends UnivariateRegressionFunction{

	private BinaryLinearModel model;
	
	@Override
	public UnivariateRegressionOutput predict(Example example) {
		Vector vector = (Vector)example.getRepresentation(model.getRepresentation());

		float prediction = model.getBias();	
		if(model.getHyperplane()==null){
			return new UnivariateRegressionOutput(this.property, prediction);
		}
		prediction+=vector.innerProduct(model.getHyperplane());
		return new UnivariateRegressionOutput(this.property, prediction);
	}

	@Override
	public BinaryLinearModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		this.model = (BinaryLinearModel)model;		
	}

}
