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

package it.uniroma2.sag.kelp.data.manipulator;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.Normalizable;
import it.uniroma2.sag.kelp.data.representation.Representation;

/**
 * This manipulator scales every selected representation to be a unit vector in its
 * explicit feature space
 * <p>
 * Note: only representations implementing <code>Normalizable</code> interface can be normalized
 * 
 *  @author Simone Filice
 */
public class NormalizationManipolator implements Manipulator{

	private String[] representationsToNormalize;
	
	/**
	 * @param representationsToNormalize the identifiers of the representations to be normalized.
	 * 		if no String is passed the manipulator will normalize every representation implementing
	 * 		<code>Normalizable</code>
	 */
	public NormalizationManipolator(String ... representationsToNormalize){

		this.representationsToNormalize = representationsToNormalize;
	}
	
	@Override
	public void manipulate(Example example) {
		if(representationsToNormalize.length==0){
			
			for(Representation rep : example.getRepresentations().values()){
				if(rep instanceof Normalizable){
					((Normalizable)rep).normalize();
				}
			}
		}else{
			for(String representation : representationsToNormalize){
				Representation rep = example.getRepresentation(representation);
				if(rep!=null){
					((Normalizable)rep).normalize();
				}
			}
		}
		
	}

}
