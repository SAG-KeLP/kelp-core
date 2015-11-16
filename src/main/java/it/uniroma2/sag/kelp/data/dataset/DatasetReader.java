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
package it.uniroma2.sag.kelp.data.dataset;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

/**
 * A utility class to read dataset in the platform format.
 * 
 * @author Simone Filice
 */
public class DatasetReader {
	protected BufferedReader inputBuffer;
	protected String nextRow;
	private boolean hasNextRow;
	private String filename;

	public DatasetReader(String filename) throws IOException {
		this.inputBuffer=openBufferedReader(filename);
		this.hasNextRow = true;
		this.nextRow = this.inputBuffer.readLine();
		this.checkRowValidity();
		this.filename = filename;
	}

	protected BufferedReader openBufferedReader(String filename) throws IOException,
			FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader reader = null;
		GZIPInputStream gzis = null;
		if (filename.endsWith(".gz")) {
			gzis = new GZIPInputStream(new FileInputStream(filename));
			reader = new InputStreamReader(gzis, "UTF8");
		} else {
			reader = new InputStreamReader(new FileInputStream(filename),
					"UTF8");
		}

		return new BufferedReader(reader);
	}

	/**
	 * Resets the reading such that the next example will be the first one
	 * 
	 * @throws IOException
	 */
	public void restartReading() throws IOException {
		this.close();
		this.inputBuffer = openBufferedReader(filename);
		this.hasNextRow = true;
		this.nextRow = this.inputBuffer.readLine();
		this.checkRowValidity();
	}

	protected void checkRowValidity() {
		if (this.nextRow == null || this.nextRow.trim().length() == 0) {
			this.hasNextRow = false;
		}
	}

	/**
	 * Checks whether there is at least another example to read
	 * @return
	 */
	public boolean hasNext() {
		return this.hasNextRow;
	}

	/**
	 * Returns the next example
	 * 
	 * @return the next example
	 * @throws IOException
	 * @throws InstantiationException
	 */
	public Example readNextExample() throws IOException, InstantiationException, ParsingExampleException {
		if (!this.hasNextRow) {
			throw new IOException(
					"DatasetIO Exception: There is no example to read!");
		}
		Example example;
		try {
			example = ExampleFactory.parseExample(nextRow);
		} catch (ParsingExampleException e) {
			throw new ParsingExampleException(e, nextRow);
		}
		this.nextRow = this.inputBuffer.readLine();
		this.checkRowValidity();
		return example;
	}

	/**
	 * Closes the reading buffer
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		inputBuffer.close();
	}
}
