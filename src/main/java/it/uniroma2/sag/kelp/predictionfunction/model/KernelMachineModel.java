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

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;

/**
 * It is the model for a Kernel Machine Method
 *  
 * @author      Simone Filice
 */
public interface KernelMachineModel extends Model{
	
	@JsonIgnore
	public int getNumberOfSupportVectors();
	
	/**
	 * Returns whether <code>instance</code> is a support vector in this model
	 * 
	 * @param instance the instance
	 * @return whether <code>instance</code> is a support vector in this model
	 */
	public boolean isSupportVector(Example instance);
	
	/**
	 * @return the kernel
	 */
	public Kernel getKernel();
	
	/**
	 * @param kernel the kernel to set
	 */
	public void setKernel(Kernel kernel);

}
