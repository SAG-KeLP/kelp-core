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

package it.uniroma2.sag.kelp.data.representation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * It is a generic way to represent an object that is intended to be exploited
 * through Machine Learning techniques. The same object can be represented in
 * different representation, each one relative to a different feature space
 * 
 * @author Simone Filice
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(RepresentationTypeResolver.class)
public interface Representation extends Serializable {


	/**
	 * Initializes a Representation using its textual description provided in
	 * <code>representationDescription</code>
	 * 
	 * @param representationDescription
	 *            the textual description of the representation to be
	 *            initialized
	 */
	@JsonProperty("content")
	public void setDataFromText(String representationDescription)
			throws Exception;

	/**
	 * Returns a textual representation of the data stored in this
	 * representation
	 * 
	 * @return a textual representation of the data stored in this
	 *         representation
	 */
	@JsonProperty("content")
	public String getTextFromData();
	
	/**
	 * Evaluates whether a representation is compatible with this one, e.g.., they have 
	 * same type and pass additional checks that are type-dependent
	 * 
	 * @param rep the representation to be compared with this one
	 * @return whether this representation is compatible with the input one
	 */
	public boolean isCompatible(Representation rep);

}
