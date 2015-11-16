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

import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.Kernel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is the model for a binary kernel machine consisting of an implicit hyperplane
 * in the Reproducing Kernel Hilbert Space.
 *  
 * @author      Simone Filice
 */
@JsonTypeName("binarykernelmodel")
public class BinaryKernelMachineModel extends BinaryModel implements KernelMachineModel{
	
	private Kernel kernel = null;
	private List<SupportVector> supportVectors;
	
	@JsonIgnore
	private TLongIntMap fromIdToPosition;
	
	public BinaryKernelMachineModel(Kernel kernel){
		this.setKernel(kernel);
		this.supportVectors = new ArrayList<SupportVector>();
		fromIdToPosition = new TLongIntHashMap();
		//fromIdToPosition = new TLongIntHashMap(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0, -1);
		
	}
	
	public BinaryKernelMachineModel(){
		this.supportVectors = new ArrayList<SupportVector>();
		fromIdToPosition = new TLongIntHashMap();
	}	

	@Override
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}
	
	/**
	 * Returns all the support vectors
	 * 
	 * @return all the support vectors
	 */
	public List<SupportVector> getSupportVectors() {
		return supportVectors;
	}

	/**
	 * @param supportVectors the supportVectors to set
	 */
	public void setSupportVectors(List<SupportVector> supportVectors) {
		this.supportVectors = supportVectors;
		this.fromIdToPosition.clear();
		for(int i=0; i<supportVectors.size(); i++){
			SupportVector sv = supportVectors.get(i);
			this.fromIdToPosition.put(sv.getInstance().getId(), i+1);
		}
	}
		
	/**
	 * Adds a support vector
	 * NOTE: it does not check whether a support vector with the same instance of the
	 * given supportVector is already in the model
	 * 
	 * @param supportVector the new support vector to be added
	 */
	public void addSupportVector(SupportVector supportVector) {
		this.fromIdToPosition.put(supportVector.getInstance().getId(), this.getNumberOfSupportVectors()+1);
		this.supportVectors.add(supportVector);
		
	}

	@Override
	public void reset() {
		this.bias = 0;
		this.supportVectors.clear();	
		this.fromIdToPosition.clear();
	}

	@Override
	public void addExample(float weight, Example example) {
		SupportVector sv = this.getSupportVector(example);
		if(sv == null){
			sv = new SupportVector(weight, example);
			this.addSupportVector(sv);		
		}else{
			sv.incrementWeight(weight);
		}	
		
	}
	
	@Override
	public float getSquaredNorm(Example example) {
		return this.kernel.squaredNorm(example);
	}

	/**
	 * Returns the support vector associated to a given instance, null the instance
	 * is not a support vector in this model
	 * 
	 * @param instance the instance whose corresponding support vector must be retrieved
	 * @return the support vector corresponding to <code>instance</code>
	 */
	public SupportVector getSupportVector(Example instance){
		int index = this.fromIdToPosition.get(instance.getId());
		if(index==0){
			return null;
		}
		return this.supportVectors.get(index-1);
	}
	
	/**
	 * Returns the index of the vector associated to a given instance, null the instance
	 * is not a support vector in this model
	 * 
	 * @param instance the instance whose corresponding support vector must be retrieved
	 * @return the index of the support vector corresponding to <code>instance</code>
	 */
	public Integer getSupportVectorIndex(Example instance){
		int index = this.fromIdToPosition.get(instance.getId());
		if(index==0){
			return null;
		}
		return index-1;
	}
	
	@Override
	public boolean isSupportVector(Example instance){
		return this.getSupportVector(instance)!=null;
	}
	
	/**
	 * Substitutes the support vector in position <code>position</code> with
	 * <code>sv</code>
	 * 
	 * @param sv the new support vector to be added
	 * @param position the position in which <code>sv</code> must be added  
	 */
	public void setSupportVector(SupportVector sv, int position){
		this.fromIdToPosition.remove(this.supportVectors.get(position).getInstance().getId());
		this.supportVectors.set(position, sv);
		this.fromIdToPosition.put(sv.getInstance().getId(), position);
	}
	
	public void substituteSupportVector(int index, Example newInstance, float newWeight){
		//int position = this.fromIdToPosition.remove(oldSv.getInstance().getId());
		this.supportVectors.get(index).setInstance(newInstance);
		this.supportVectors.get(index).setWeight(newWeight);
		this.fromIdToPosition.put(newInstance.getId(), index);
	}

	@Override
	public int getNumberOfSupportVectors() {
		return this.supportVectors.size();
	}

	@Override
	@JsonIgnore
	public float getSquaredNorm() {
		float sum=0;
		for(SupportVector sv1 : this.supportVectors){
			for(SupportVector sv2 : this.supportVectors){
				sum+=sv1.getWeight()*sv2.getWeight()*this.kernel.innerProduct(sv1.getInstance(), sv2.getInstance());
			}
		}
		return sum;
	}
}
