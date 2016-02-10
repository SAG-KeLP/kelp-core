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


package it.uniroma2.sag.kelp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class contains util methods related to file reading and writing
 * 
 * @author Simone Filice
 */
public class FileUtils {
	
	
	/**
	 * Creates an InputStream for reading a file. It is able to create
	 * input streams for plain text documents and for .gz documents
	 * 
	 * @param filePath the path of the file to be read
	 * @return the input stream associated to the file specified in <code>filePath</code>
	 * @throws IOException
	 */
	public static InputStream createInputStream(String filePath) throws IOException{
		if (filePath.endsWith(".gz")) {
			return new GZIPInputStream(new FileInputStream(new File(filePath)));
		} else {
			return new FileInputStream(new File(filePath));
		}
	}
	
	/**
	 * Creates an OutputStream for reading a file. It is able to create
	 * output streams for plain text documents and for .gz documents
	 * 
	 * @param filePath the path of the file to be written
	 * @return the output stream associated to the file specified in <code>filePath</code>
	 * @throws IOException
	 */
	public static OutputStream createOutputStream(String filePath) throws IOException{
		if (filePath.endsWith(".gz")) {
			return new GZIPOutputStream(new FileOutputStream(new File(filePath)));
		} else {
			return new FileOutputStream(new File(filePath));
		}
	}

}
