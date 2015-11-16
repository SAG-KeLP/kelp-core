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

import java.util.List;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.Prediction;

/**
 * It is the output of a generic Regressor
 * 
 * @author Simone Filice
 *
 */
public interface RegressionOutput extends Prediction{

	/**
	 * Returns all the properties on which the regressor has to provide predictions 
	 * 
	 * @return all the properties on which the regressor has to provide predictions 
	 */
	public List<Label> getAllProperties();
}
