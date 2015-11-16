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

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It is a factory that provides methods for instantiating a representation
 * described in a textual format The factory is able to automatically support
 * all the implementations of the class <code>Representation</code> that have an
 * empty constructor and that have been included in the project (as local class
 * or imported via Maven)
 * 
 * @author Simone Filice
 */
public class RepresentationFactory {

	private static Logger logger = LoggerFactory
			.getLogger(RepresentationFactory.class);
	private static RepresentationFactory instance = null;

	private final Map<String, Class<? extends Representation>> representationImplementations;
	static {
		try {
			instance = new RepresentationFactory();
		} catch (Exception e) {
			logger.error("RepresentationFactory cannot be initialized: "
					+ e.getMessage());
			System.exit(0);
		}
		;
	}

	private RepresentationFactory() throws InstantiationException,
			IllegalAccessException {
		representationImplementations = discoverAllRepresentationImplementations();

		logger.debug("RepresentationFactory Implementations: {}",
				this.representationImplementations);
	}

	/**
	 * Retrieves all the implementations of the class
	 * <code>Representation</code> included in the current project
	 * 
	 * @return a Map of pairs representation type identifier - representation
	 *         class
	 */
	private Map<String, Class<? extends Representation>> discoverAllRepresentationImplementations()
			throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections("it");

		Set<Class<? extends Representation>> classes = reflections
				.getSubTypesOf(Representation.class);
		HashMap<String, Class<? extends Representation>> representationimplementatios = new HashMap<String, Class<? extends Representation>>();

		for (Class<? extends Representation> implementation : classes) {
			if (Modifier.isAbstract(implementation.getModifiers())) {
				continue;
			}
			String representationAbbreviation = getRepresentationIdentifier(implementation);
			representationimplementatios.put(representationAbbreviation,
					implementation);
		}
		return representationimplementatios;
	}

	/**
	 * Returns an instance of the class <code>RepresentatioFactory</code>
	 * 
	 * @return an instance of the class <code>RepresentatioFactory</code>
	 */
	public static RepresentationFactory getInstance() {
		return instance;
	}

	/**
	 * Initializes and returns the representation described in
	 * <code>representationBody</code>
	 * 
	 * @param representationType
	 *            the identifier of the representation class to be instantiated
	 * @param representationBody
	 *            the the textual description of the representation to be
	 *            instantiated
	 * @return the representation described in <code>representationBody</code>
	 */
	public Representation parseRepresentation(String representationType,
			String representationBody) throws InstantiationException {

		// int beginHeaderIndex =
		// representationDescription.indexOf(BEGIN_REPRESENTATION);
		// int endHeaderIndex = representationDescription.indexOf(DELIMITER,
		// beginHeaderIndex + BEGIN_REPRESENTATION.length());
		//
		// String representationHeader =
		// representationDescription.substring(beginHeaderIndex +
		// BEGIN_REPRESENTATION.length(), endHeaderIndex);
		//
		// int endRepresentation =
		// representationDescription.indexOf(END_REPRESENTATION);
		//
		// String representationBody =
		// representationDescription.substring(endHeaderIndex+1,
		// endRepresentation).trim();

		Class<? extends Representation> representationClass = this.representationImplementations
				.get(representationType);
		if (representationClass == null) {
			throw new IllegalArgumentException("unrecognized representation "
					+ representationType);
		}

		try {
			Representation representation = representationClass.newInstance();
			representation.setDataFromText(representationBody);
			return representation;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InstantiationException("Cannot initialize "
					+ representationType + " representations: "
					+ " missing empty constructor of the class "
					+ representationClass.getSimpleName());
		}

	}

	/**
	 * Returns the identifier of a given class
	 * 
	 * @param c the class whose identifier is requested 
	 * @return the class identifier
	 */
	public static String getRepresentationIdentifier(
			Class<? extends Representation> c) {
		String representationAbbreviation;
		if (c.isAnnotationPresent(JsonTypeName.class)) {
			JsonTypeName info = c.getAnnotation(JsonTypeName.class);
			representationAbbreviation = info.value();

		} else {
			representationAbbreviation = c.getSimpleName().toUpperCase();
		}
		return representationAbbreviation;
	}

}
