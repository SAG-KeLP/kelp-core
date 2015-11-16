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
import it.uniroma2.sag.kelp.data.representation.Vector;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * It standardizes the feature values of a vectorial representation. Let \(x_i\) be
 * the value of the i-th feature whose mean and standard deviation are \(\mu_i\) and \(\sigma_i\) respectively. Then
 * the standardized value is \(\hat{x_i} = (x_i-\mu_i)/\sigma_i\)
 * 
 * @author Simone Filice
 *
 */
public class StandardizationManipulator implements Manipulator{
	
	private String representation;
	private Vector means;//at the end it will contain the negative means
	private Vector stdDevs;//at the end it will contain the inverse of the standard deviations 
	private static final float EPSILON = 0.0000001f;
	
	/**
	 * Constructor
	 * 
	 * @param representation the vectorial representation to be standardized
	 * @param examples the examples on which computing the feature means and standard deviations to be applied during the
	 * standardization process 
	 */
	public StandardizationManipulator(String representation, List<Example> examples){
		this.representation = representation;
		means = ((Vector)examples.get(0).getRepresentation(representation)).getZeroVector();

		for(Example example: examples){
			Vector vector = (Vector) example.getRepresentation(representation);
			means.add(vector);

		}
		means.scale(1f/examples.size());
		stdDevs = means.getZeroVector();
		for(Example example: examples){
			Vector vector = ((Vector) example.getRepresentation(representation)).copyVector();
			vector.add(-1, means);
			vector.pointWiseProduct(vector);
			stdDevs.add(vector);
		}
		
		
		stdDevs.scale(1f/(examples.size()-1));
		
		Map<Object, Number> activeFeatures = stdDevs.getActiveFeatures();
		
//		System.out.println("MEANS: " + means.getTextFromData());
//		System.out.println("VARIANCE: " + stdDevs.getTextFromData());
		
		for(Entry<Object, Number> activeFeature : activeFeatures.entrySet()){
			
			Object key = activeFeature.getKey();
			double value = activeFeature.getValue().doubleValue();
			if(value<EPSILON){
				stdDevs.setFeatureValue(key, 1);
			}else{
				stdDevs.setFeatureValue(key, (float)(1f/(Math.sqrt(value))));
			}
			
		}
		means.scale(-1);
//		System.out.println("DEV STDS INV: " + stdDevs.getTextFromData());
	
	}
	
	
	/**
	 * It standardizes the feature values of <code>vector</code>. Let \(x_i\) be
	 * the value of the i-th feature whose mean and standard deviation are \(\mu_i\) and \(\sigma_i\) respectively. Then
	 * the standardized value is \(\hat{x_i} = (x_i-\mu_i)/\sigma_i\)
	 * 
	 * @param vector the vector to be standardized
	 */
	public void standardize(Vector vector){
		vector.add(means);
		vector.pointWiseProduct(stdDevs);
	}
	
	
	@Override
	public void manipulate(Example example) {
		Vector vector = (Vector) example.getRepresentation(representation);
		if(vector!=null){
			this.standardize(vector);
		}
	}

}
