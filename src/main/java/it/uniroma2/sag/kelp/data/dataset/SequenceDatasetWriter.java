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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.utils.FileUtils;

/**
 * * The methods of this class allows to write <code>SequenceExample</code>s
 * into a file
 * 
 * @author Danilo Croce
 *
 */
public class SequenceDatasetWriter {

	private BufferedWriter writer;
	private GZIPOutputStream zip;
	private String outputFilePath;

	/**
	 * @param outputFilePath
	 *            The path of the output file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SequenceDatasetWriter(String outputFilePath) throws FileNotFoundException, IOException {
		this.outputFilePath = outputFilePath;
		OutputStream outputStream = FileUtils.createOutputStream(outputFilePath);
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
	}

	/**
	 * Close the output file
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		writer.close();
		if (outputFilePath.endsWith(".gz")) {
			zip.close();
		}
	}

	/**
	 * Write the next example into the file
	 * 
	 * @param e
	 *            the example to be written
	 * @throws IOException
	 */
	public void writeNextExample(Example e) throws IOException {
		writer.append(e.toString() + "\n\n");
	}

}
