/*
 * Copyright 2016 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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

package it.uniroma2.sag.kelp.data.dataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.example.SequenceExample;
import it.uniroma2.sag.kelp.utils.FileUtils;

/**
 * The methods of this class allows to read <code>SequenceExample</code>s from a
 * file
 * 
 * @author Danilo Croce
 */
public class SequenceDatasetReader {

	/**
	 * 
	 */
	private BufferedReader inputBuffer;

	private String filename;

	public SequenceDatasetReader(String filename) throws IOException {
		this.inputBuffer = openBufferedReader(filename);
	}

	/**
	 * Closes the reading buffer
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		inputBuffer.close();
	}

	/**
	 * @param filePath
	 *            The path of the file containing the dataset
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private BufferedReader openBufferedReader(String filePath)
			throws IOException, FileNotFoundException, UnsupportedEncodingException {
		InputStream inputStream = FileUtils.createInputStream(filePath);
		return new BufferedReader(new InputStreamReader(inputStream));
	}

	/**
	 * Returns the next example
	 * 
	 * @return the next example
	 * @throws IOException
	 * @throws InstantiationException
	 */
	public SequenceExample readNextExample() throws IOException, InstantiationException, ParsingExampleException {

		String line = null;
		SequenceExample res = new SequenceExample();
		while ((line = this.inputBuffer.readLine()) != null) {
			if (line.trim().length() < 1) {
				return res;
			}

			Example example = ExampleFactory.parseExample(line);
			res.add(example);
		}
		if (res.getExamples().isEmpty()) {
			return null;
		}
		return res;

	}

	/**
	 * Resets the reading such that the next example will be the first one
	 * 
	 * @throws IOException
	 */
	public void restartReading() throws IOException {
		this.close();
		this.inputBuffer = openBufferedReader(filename);
	}

}
