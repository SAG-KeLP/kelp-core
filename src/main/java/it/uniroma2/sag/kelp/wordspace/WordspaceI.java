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

package it.uniroma2.sag.kelp.wordspace;

import it.uniroma2.sag.kelp.data.representation.Vector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * This interface provides methods for retrieving vectors associated to words
 * 
 * @author Simone Filice
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(WordspaceTypeResolver.class)
public interface WordspaceI {
	
	
	/**
	 * Stores the vector associated to a word. This vector will be returned 
	 * from the method <code>getVector</code>
	 * 
	 * @param word the word
	 * @param vector the vector associated to <code>word</code>
	 */
	public void addWordVector(String word, Vector vector);
	
	/**
	 * Returns the vector associated to the given word
	 * 
	 * @param word the word whose corresponding vector must be retrieved
	 * @return the vector associated to <code>word</code>, null if <code>word</code>
	 * is not in the vocabulary of this wordspace
	 */
	public Vector getVector(String word);
	
	/**
	 * Returns the complete set of words in the vocabulary (words having an associated vector in this wordspace)
	 * 
	 * @return the set of all the words in the vocabulary
	 */
	@JsonIgnore
	public char[][] getDictionaryDanilo();

}
