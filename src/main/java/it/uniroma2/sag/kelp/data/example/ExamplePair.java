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

package it.uniroma2.sag.kelp.data.example;

import it.uniroma2.sag.kelp.data.manipulator.Manipulator;
import it.uniroma2.sag.kelp.data.representation.Representation;

import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * It is the instance of an example pair, i.e. an object that consists of
 * a pair \(<x_i,x_j>\) where \(x_i\) and \(x_j\) are examples themselves. This
 * kind of example results widely used in tasks like re-reranking or entailment
 * 
 * @author Simone Filice
 */
@JsonTypeName("pair")
public class ExamplePair extends SimpleExample{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8709822366703676966L;
	private Example leftExample;
	private Example rightExample;


	public ExamplePair(){
		super();
	}

	public ExamplePair(Example left, Example right){
		this();
		this.leftExample = left;
		this.rightExample = right;
	}

	/**
	 * Returns the left example in the pair
	 * 
	 * @return the left example in the pair
	 */
	public Example getLeftExample() {
		return this.leftExample;
	}

	/**
	 * Returns the right example in the pair
	 * 
	 * @return the right example in the pair
	 */
	public Example getRightExample(){
		return this.rightExample;
	}

	//	@Override
	//	public Vector getZeroVector(String representationIdentifier) {
	//		return this.leftExample.getZeroVector(representationIdentifier);
	//	}

	@Override
	public String toString(){
		String ret = this.getTextualLabelPart();
		ret += ExampleFactory.BEGIN_PAIR + leftExample.toString() + ExampleFactory.PAIR_SEPARATOR + rightExample.toString() + ExampleFactory.END_PAIR;
		for(Entry<String, Representation> entry: this.getRepresentations().entrySet()){
			Representation representation = entry.getValue();
			String identifier = entry.getKey();

			ret += ExampleFactory.getTextualRepresentation(representation, identifier)					
					+ ExampleFactory.REPRESENTATION_SEPARATOR;

		}
		return ret;
	}

	/**
	 * Manipulates this example according to the provided <code>manipulator</code>. The manipulation strategy is applied
	 * both to the representations stored directly in this ExamplePair and the ones stored in the left and right
	 * examples
	 * 
	 * @param manipulator the manipulator
	 */
	@Override
	public void manipulate(Manipulator manipulator){
		super.manipulate(manipulator);
		this.leftExample.manipulate(manipulator);
		this.rightExample.manipulate(manipulator);
	}

	@Override
	public boolean isCompatible(Example example){

		if(example instanceof ExamplePair){
			ExamplePair that = (ExamplePair)example;
			if(!super.isCompatible(example)){
				return false;
			}
			if(!this.getLeftExample().isCompatible(that.leftExample)){
				logger.error("example pairs are incompatible because their left parts are incompatible");
				return false;
			}
			if(!this.getRightExample().isCompatible(that.getRightExample())){
				logger.error("example pairs are incompatible because their right parts are incompatible");
				return false;
			}
		}else{
			logger.error("examples are incompatible because they are not both ExamplePairs");
			return false;
		}
		return true;
	}
}
