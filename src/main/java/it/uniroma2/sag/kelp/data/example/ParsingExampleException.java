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

/**
 * An Exception to model problems when parsing examples
 * 
 * @author Giuseppe Castellucci
 */
public class ParsingExampleException extends Exception {
	private static final long serialVersionUID = 3273436258470592591L;
	private String exampleString = "";

	public ParsingExampleException(String message) {
		super(message);
	}

	public ParsingExampleException(ParsingExampleException e, String nextRow) {
		super(e.getMessage());
		this.exampleString = nextRow;
	}

	public String getExampleString() {
		return exampleString;
	}

	@Override
	public String getMessage() {
		String finalMsg = super.getMessage();
		if (!exampleString.isEmpty())
			finalMsg = finalMsg + " Example String: " + exampleString;
		return finalMsg;
	}
}
