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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.predictionfunction.model.BinaryLinearModel;
import it.uniroma2.sag.kelp.predictionfunction.model.Model;

/**
 * It linear binary classifier operating directly on an explicit vector space
 *  
 * @author      Simone Filice
 */
@JsonTypeName("linearClassifier")
public class BinaryLinearClassifier extends BinaryClassifier{

	private Logger logger = LoggerFactory.getLogger(BinaryClassifier.class);
	
	private BinaryLinearModel model;	
	

	@Override
	public BinaryLinearModel getModel() {
		return model;
	}
	
	@Override
	public void setModel(Model model) {
		this.model = (BinaryLinearModel)model;
	}


	@Override
	public BinaryMarginClassifierOutput predict(Example example) {		
		Vector vector = (Vector)example.getRepresentation(model.getRepresentation());
		
		float prediction = model.getBias();	
		if(vector==null){
			logger.warn("vector null");
		}
		if(model.getHyperplane()==null){
			//logger.warn("hyperplane null");
			return new BinaryMarginClassifierOutput(positiveClass, prediction);
		}
		prediction+=vector.innerProduct(model.getHyperplane());
		return new BinaryMarginClassifierOutput(positiveClass, prediction);
	}

	@Override
	public void reset() {
		this.model.reset();
		
	}
	
	
}
