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

package it.uniroma2.sag.kelp.data.example;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.LabelFactory;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.RepresentationFactory;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is a factory that provides methods for instantiating an example described
 * in a textual format The expected inputs for examples with N labels and M
 * representations are String of the form: Label_1 Label_2 ... Label_n
 * |BR_1:I_1| ... |ER_1| |BR_2:I_2| ... |ER_2| ... |BR_m:I_m| ... |ER_m| Where
 * the R_i are identifiers of the representation types (some R_i can be the
 * same) while the I_i are the specific representation identifier (I_i!=I_j for
 * all i,j) for instance R_1=DV (dense vector) I_1=bow
 * 
 * @author Simone Filice
 */
public class ExampleFactory {
	private static Logger logger = LoggerFactory.getLogger(ExampleFactory.class);

	public static final String LABEL_SEPARATOR = " ";
	public static final String REPRESENTATION_TYPE_NAME_SEPARATOR = ":";
	public static final String REPRESENTATION_SEPARATOR = " ";
	public static final String DELIMITER = "|";
	public static final String BEGIN_REPRESENTATION = DELIMITER + "B";
	public static final String END_REPRESENTATION = DELIMITER + "E";
	public static final String BEGIN_PAIR = DELIMITER + "<" + DELIMITER;
	public static final String END_PAIR = DELIMITER + ">" + DELIMITER;
	public static final String PAIR_SEPARATOR = DELIMITER + "," + DELIMITER;

	private static RepresentationFactory representationFactory = RepresentationFactory.getInstance();;

	/**
	 * Parse a single <code>Representation</code> from its string representation
	 * 
	 * @param textualRepresentation
	 *            the string to be parsed
	 * @return
	 * @throws InstantiationException
	 */
	public static Entry<String, Representation> parseSingleRepresentation(String textualRepresentation)
			throws InstantiationException, ParsingExampleException {
		int beginHeaderIndex = textualRepresentation.indexOf(BEGIN_REPRESENTATION);
		int endHeaderIndex = textualRepresentation.indexOf(DELIMITER, beginHeaderIndex + BEGIN_REPRESENTATION.length());
		String representationHeader = textualRepresentation.substring(beginHeaderIndex + BEGIN_REPRESENTATION.length(),
				endHeaderIndex);
		int endRepresentation = textualRepresentation.indexOf(END_REPRESENTATION);
		String representationBody = textualRepresentation.substring(endHeaderIndex + 1, endRepresentation).trim();
		logger.debug("representation header: " + representationHeader);
		logger.debug("representation body: " + representationBody);

		int descriptionSeparator = representationHeader.indexOf(REPRESENTATION_TYPE_NAME_SEPARATOR);
		String representationType;
		String representationName;
		if (descriptionSeparator == -1) {
			representationType = representationHeader;
			representationName = null;
		} else {
			representationType = representationHeader.substring(0, descriptionSeparator);
			representationName = representationHeader.substring(descriptionSeparator + 1);
		}
		logger.debug("representation type: " + representationType);
		logger.debug("representation name: " + representationName);
		String potentialGarbage = (textualRepresentation.substring(0, beginHeaderIndex)).trim();
		if (!potentialGarbage.isEmpty()) {
			if (!(potentialGarbage.equals(BEGIN_PAIR) || potentialGarbage.equals(END_PAIR)
					|| potentialGarbage.equals(PAIR_SEPARATOR) || potentialGarbage.equals(">|"))) {
				String garbage = textualRepresentation.substring(0, beginHeaderIndex);
				throw new ParsingExampleException(
						"WARNING: there are chars (" + garbage + ") before representation " + representationName);
			}
		}
		Representation representation = representationFactory.parseRepresentation(representationType,
				representationBody);
		Entry<String, Representation> entry = new AbstractMap.SimpleEntry<String, Representation>(representationName,
				representation);
		return entry;
	}

	public static String getTextualRepresentation(Representation representation) {
		return getTextualRepresentation(representation, null);
	}

	public static String getTextualRepresentation(Representation representation, String identifier) {
		String ret = "";
		String representationType = RepresentationFactory.getRepresentationIdentifier(representation.getClass());
		String identifierPart = "";
		if (identifier != null) {
			identifierPart = ExampleFactory.REPRESENTATION_TYPE_NAME_SEPARATOR + identifier;
		}
		ret += ExampleFactory.BEGIN_REPRESENTATION + representationType + identifierPart + ExampleFactory.DELIMITER
				+ " " + representation.getTextFromData() + ExampleFactory.END_REPRESENTATION + representationType
				+ ExampleFactory.DELIMITER;
		return ret;
	}

	public static Example parseExample(String exampleDescription)
			throws InstantiationException, ParsingExampleException {
		// System.out.println("INPUT: " + exampleDescription);
		int beginFirstRepIndex = exampleDescription.indexOf(DELIMITER);
		String labelsPart = exampleDescription.substring(0, beginFirstRepIndex).trim();
		String representationsPart = exampleDescription.substring(beginFirstRepIndex).trim();
		// System.out.println("Label part: " + labelsPart);
		// System.out.println("representation part: " + representationsPart);
		Example example;
		if (representationsPart.startsWith(BEGIN_PAIR)) {
			example = parseExamplePair(representationsPart);
		} else {
			example = parseSimpleExample(representationsPart);
		}
		// ADDING LABELS
		String[] stringLabels = labelsPart.split(LABEL_SEPARATOR);
		for (String labelDescription : stringLabels) {
			if (labelDescription.trim().length() > 0) {
				Label label = LabelFactory.parseLabel(labelDescription.trim());
				example.addLabel(label);
			}
		}
		return example;
	}

	private static HashMap<String, Representation> parseRepresentations(String representationsPart)
			throws InstantiationException, ParsingExampleException {
		HashMap<String, Representation> representations = new HashMap<String, Representation>();
		String representationRemaining = representationsPart.trim();
		int representationCount = 0;
		while (representationRemaining.length() > 0) {
			int endRepresentationStartIndex = representationRemaining.indexOf(END_REPRESENTATION);
			int endRepresentationEndIndex = representationRemaining.indexOf(DELIMITER, endRepresentationStartIndex + 1);

			String representationDescription = representationRemaining.substring(0, endRepresentationEndIndex + 1);
			Entry<String, Representation> entry = parseSingleRepresentation(representationDescription);

			representationRemaining = representationRemaining.substring(endRepresentationEndIndex + 1).trim();
			String representationName = entry.getKey();
			if (representationName == null) {
				representationName = Integer.toString(representationCount);
			}
			if (representations.containsKey(representationName)) {
				throw new ParsingExampleException(
						"WARNING: representation " + representationName + " has been overwritten");
			}
			representations.put(representationName, entry.getValue());
			representationCount++;
		}
		return representations;
	}

	private static SimpleExample parseSimpleExample(String representationsPart)
			throws InstantiationException, ParsingExampleException {
		SimpleExample example = new SimpleExample();
		HashMap<String, Representation> representations = parseRepresentations(representationsPart);
		example.setRepresentations(representations);

		return example;
	}

	/**
	 * Initializes and returns the example described in
	 * <code>exampleDescription</code>
	 * 
	 * @param exampleDescription
	 *            the the textual description of the example to be instantiated
	 * @return the example described in <code>exampleDescription</code>
	 * @throws ParsingExampleException
	 */
	private static ExamplePair parseExamplePair(String examplePairDescription)
			throws InstantiationException, ParsingExampleException {
		int begin = examplePairDescription.indexOf(BEGIN_PAIR) + BEGIN_PAIR.length();
		int end = examplePairDescription.lastIndexOf(END_PAIR);

		String pairWithoutBrackets = examplePairDescription.substring(begin, end).trim();

		int pairSeparatorIndex = 0;
		int beginPairIndex = 0;
		int pairSeparatorCount = 0;
		int beginPairCount = 1;
		while (pairSeparatorCount != beginPairCount) {
			pairSeparatorIndex = pairWithoutBrackets.indexOf(PAIR_SEPARATOR, pairSeparatorIndex);
			if (pairSeparatorIndex == -1) {
				throw new InstantiationException("Imbalanced example pair!");
			}
			pairSeparatorCount++;
			while (true) {
				beginPairIndex = pairWithoutBrackets.indexOf(BEGIN_PAIR, beginPairIndex);
				if (beginPairIndex == -1 || beginPairIndex > pairSeparatorIndex) {
					break;
				}
				beginPairCount += 1;
				beginPairIndex += BEGIN_PAIR.length();
			}

			pairSeparatorIndex += PAIR_SEPARATOR.length();
		}
		String leftExampleDescr = pairWithoutBrackets.substring(0, pairSeparatorIndex - PAIR_SEPARATOR.length()).trim();
		String rightExampleDescr = pairWithoutBrackets.substring(pairSeparatorIndex).trim();
		Example leftExample = parseExample(leftExampleDescr);
		Example rightExample = parseExample(rightExampleDescr);
		ExamplePair pair = new ExamplePair(leftExample, rightExample);
		if (examplePairDescription.length() > end + END_PAIR.length()) {
			String pairDirectRepresentations = examplePairDescription.substring(end + 1);
			HashMap<String, Representation> representations = parseRepresentations(pairDirectRepresentations);
			pair.setRepresentations(representations);
		}

		return pair;
	}
}
