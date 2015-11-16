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

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.Vector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is the model for a binary linear method consisting of an explicit hyperplane
 *  
 * @author      Simone Filice
 */
@JsonTypeName("binarylinearmodel")
public class BinaryLinearModel extends BinaryModel{


	private Vector hyperplane;

	private String representation;

	/**
	 * @return the hyperplane
	 */
	public Vector getHyperplane() {
		return hyperplane;
	}

	/**
	 * @param hyperplane the hyperplane to set
	 */
	public void setHyperplane(Vector hyperplane) {
		this.hyperplane = hyperplane;
	}

	/**
	 * @return the identifier of the representation on which the model operates
	 */
	public String getRepresentation() {
		return representation;
	}

	/**
	 * @param representation the identifier of the representation to set. The model operates
	 * on that representation
	 */
	public void setRepresentation(String representation) {
		this.representation = representation;
	}

	@Override
	public void reset() {
		this.bias = 0;
		if(this.hyperplane!= null){
			this.hyperplane = this.hyperplane.getZeroVector();
		}		
	}

	@Override
	public void addExample(float weight, Example example) {

		Representation rep = example.getRepresentation(representation);
		if(rep instanceof Vector){
			Vector vector = (Vector) rep;
			if(this.hyperplane==null){
				this.hyperplane = vector.getZeroVector();
			}				
			this.hyperplane.add(weight, vector);				

		}else{
			throw new IllegalArgumentException("The given example does not have a Vector representation identified with " + representation);
		}


	}

	@Override
	public float getSquaredNorm(Example example) {
		Vector vector = (Vector) example.getRepresentation(representation);

		return vector.getSquaredNorm();
	}
	
	@JsonIgnore
	@Override
	public float getSquaredNorm(){
		return this.hyperplane.getSquaredNorm();
	}

}
