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

package it.uniroma2.sag.kelp.kernel.vector;

import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.kernel.DirectKernel;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Linear Kernel for <code>Vector</code>s <br>
 * It executes the dot product between two <code>Vector</code> representations
 * 
 * @author Simone Filice
 */

@JsonTypeName("linear")
public class LinearKernel extends DirectKernel<Vector> {

	public LinearKernel(String representationIdentifier) {
		super(representationIdentifier);

	}

	public LinearKernel() {

	}

	@Override
	public float kernelComputation(Vector repA, Vector repB) {
		return repA.innerProduct(repB);
	}
}