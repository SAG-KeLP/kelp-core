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

package it.uniroma2.sag.kelp.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * It is a serializer, i.e. an object that is able to convert objects into a String representation, preserving all their
 * properties. The serializer is then able to deserialize those textual representations to instantiate again the original
 * serialized object
 * 
 * @author Simone Filice, Danilo Croce
 *
 */
public interface ObjectSerializer {
	
	/**
	 * Converts an object into a textual representation, preserving all the object properties.
	 * 
	 * @param object the object to be serialized
	 * @return a textual representation of <code>object</code>
	 * @throws IOException
	 */
	public String writeValueAsString(Object object) throws IOException;
	
	/**
	 * Converts an object into a textual representation, preserving all the object properties, 
	 * and write this String into a file.
	 * 
	 * @param object the object to be serialized
	 * @param filePath the path of the file where <code>object</code> must be serialized 
	 * @throws IOException
	 */
	public void writeValueOnFile(Object object, String filePath) throws IOException;
	
	/**
	 * Converts an object into a textual representation, preserving all the object properties, 
	 * and write this String into a GZip file.
	 * 
	 * @param object the object to be serialized
	 * @param filePath the path of the file where <code>object</code> must be serialized 
	 * @throws IOException
	 */
	public void writeValueOnGzipFile(Object object, String filePath) throws IOException;
	
	/**
	 * Deserializes an object that has been previously converted into a textual format 
	 * 
	 * @param content the object in its textual format
	 * @param valueType the class of the object to be deserialized
	 * @return the deserialized object
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T> T readValue(String content, Class<T> valueType)
			throws IOException, JsonParseException, JsonMappingException;
	
	/**
	 * Deserializes an object that has been previously converted into a textual format 
	 * 
	 * @param file the file from which the serialized format must be read
	 * @param valueType the class of the object to be deserialized
	 * @return the deserialized object
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public <T> T readValue(File file, Class<T> valueType)
			throws IOException, JsonParseException, JsonMappingException;
	
}
