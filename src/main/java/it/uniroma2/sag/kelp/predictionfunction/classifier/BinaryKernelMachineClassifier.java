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
package it.uniroma2.sag.kelp.predictionfunction.classifier;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryKernelMachineModel;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;
import it.uniroma2.sag.kelp.predictionfunction.model.SupportVector;

import com.fasterxml.jackson.annotation.JsonTypeName;
/**
 * It is a kernel-base binary classifier
 * 
 * @author      Simone Filice
 */
@JsonTypeName("kernelMachineClassifier")
public class BinaryKernelMachineClassifier extends BinaryClassifier{
	
	private BinaryKernelMachineModel model;
	
	public BinaryKernelMachineClassifier(){
		
	}
	
	public BinaryKernelMachineClassifier(BinaryKernelMachineModel model, Label label){
		this.model = model;
		this.positiveClass = label;
	}	
	
	
	/**
	 * Classifies an example applying the following formula:
	 * y(x) = \sum_{i \in SV}\alpha_i k(x_i, x) + b
	 * 
	 * @return the classification result (i.e. the distance from the implicit classification hyperplane)
	 */
	@Override
	public BinaryMarginClassifierOutput predict(Example example) {
				
		float prediction = model.getBias();
		
		for(SupportVector sv : model.getSupportVectors()){
			prediction += sv.getWeight() * model.getKernel().innerProduct(example, sv.getInstance());
		}
		
		BinaryMarginClassifierOutput output = new BinaryMarginClassifierOutput(positiveClass, prediction);
		
		return output;
	}

	/**
	 * Returns the model
	 * 
	 * @return the model
	 */
	@Override
	public BinaryKernelMachineModel getModel(){
		return model;
	}
	
	/**
	 * @param model the model to set
	 */
	@Override
	public void setModel(Model model) {
		this.model = (BinaryKernelMachineModel)model;
	}

	@Override
	public void reset() {
		this.model.reset();		
	}
	
	
}
