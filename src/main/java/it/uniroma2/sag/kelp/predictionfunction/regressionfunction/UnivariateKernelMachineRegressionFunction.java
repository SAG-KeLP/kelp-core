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
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryKernelMachineModel;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;
import it.uniroma2.sag.kelp.predictionfunction.model.SupportVector;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is a univariate regression prediction function consisting of an implicit
 * hyperplane in a Reproducing Kernel Hilbert Space. Univariate
 * means that the output is a single real value associated to a specific property.
 * 
 * @author Simone Filice
 *
 */
@JsonTypeName("kernelMachineRegressor")
public class UnivariateKernelMachineRegressionFunction extends UnivariateRegressionFunction{

	private BinaryKernelMachineModel model;

	public UnivariateKernelMachineRegressionFunction(){
		this.model = new BinaryKernelMachineModel();
	}

	@Override
	public UnivariateRegressionOutput predict(Example example) {
		float prediction = model.getBias();

		for(SupportVector sv : model.getSupportVectors()){
			prediction += sv.getWeight() * model.getKernel().innerProduct(example, sv.getInstance());
		}
		
		return new UnivariateRegressionOutput(this.property, prediction);
	}

	@Override
	public BinaryKernelMachineModel getModel() {
		return this.model;
	}

	@Override
	public void setModel(Model model) {
		this.model = (BinaryKernelMachineModel) model;		
	}


}
