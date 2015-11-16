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

package it.uniroma2.sag.kelp.predictionfunction.model;

import it.uniroma2.sag.kelp.data.label.Label;

import java.util.List;

/**
 * It is a model which aggregates BinaryModels. It is mainly adopted in multiclass learning tasks. 
 * 
 * @author Simone Filice
 *
 */
public class MulticlassModel implements Model{


	protected List<Label> labels;
	private List<BinaryModel> models;
	
	/**
	 * @return the models
	 */
	public List<BinaryModel> getModels() {
		return models;
	}



	/**
	 * @param models the models to set
	 */
	public void setModels(List<BinaryModel> models) {
		this.models = models;
	}



	/**
	 * @return the labels
	 */
	public List<Label> getLabels() {
		return labels;
	}



	/**
	 * @param labels the labels to set
	 */
	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	@Override
	public void reset() {
		for(BinaryModel model : this.models){
			model.reset();
		}
	}

}
