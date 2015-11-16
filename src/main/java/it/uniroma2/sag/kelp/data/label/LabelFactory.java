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

package it.uniroma2.sag.kelp.data.label;

/**
 * It is a factory that provides methods for instantiating labels described in a
 * textual format
 * 
 * @author Simone Filice
 */
public class LabelFactory {
	public static final String NAME_VALUE_SEPARATOR = ":";

	/**
	 * Initializes and returns the label described in
	 * <code>labelDescription</code>
	 * 
	 * @param labelDescription
	 *            the the textual description of the label to be instantiated
	 * @return the label described in <code>labelDescription</code>
	 */
	public static Label parseLabel(String labelDescription) {
		Label label;
		int index = labelDescription.lastIndexOf(NAME_VALUE_SEPARATOR);
		if (index == -1) {
			label = new StringLabel(labelDescription);
		} else {
			String name = labelDescription.substring(0, index);
			float number = Float.parseFloat(labelDescription
					.substring(index + 1));
			Label regressionProperty = parseLabel(name);
			label = new NumericLabel(regressionProperty, number);
		}
		return label;
	}
}
