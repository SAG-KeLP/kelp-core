/*
 * Copyright 2014-2016 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.NumericLabel;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.data.representation.vector.SparseVector;

import java.io.IOException;

/**
 * A utility class to read dataset in the libsvm/liblinear/svmLight format.
 * 
 * @author Simone Filice
 */
public class LibsvmDatasetReader extends DatasetReader{
	
	public static final String COMMENT_SEPARATOR = "#";
	public static final String COMMENT_REPRESENTATION_NAME = "comment";
	
	private enum LibsvmTask{
		CLASSIFICATION,
		REGRESSION
	}
	
	private String representationName;
	private LibsvmTask task;
	private StringLabel regressionProperty;

	/**
	 * Constructor for reading dataset in libsvm/liblinear/svmLight format for classification tasks.
	 * 
	 * @param filename the path of the file to be read
	 * @param representationName the name of the vector representation each example consists of
	 * @throws IOException
	 */
	public LibsvmDatasetReader(String filename, String representationName) throws IOException {
		super(filename);
		this.representationName = representationName;
		this.task = LibsvmTask.CLASSIFICATION;
	}
	
	/**
	 * Constructor for reading dataset in libsvm/liblinear/svmLight format for regression tasks.
	 * 
	 * @param filename the path of the file to be read
	 * @param representationName the name of the vector representation each example consists of
	 * @param regressionPropertyName the name of the regression property to be learned 
	 * @throws IOException
	 */
	public LibsvmDatasetReader(String filename, String representationName, StringLabel regressionPropertyName) throws IOException {
		super(filename);
		this.representationName = representationName;
		this.task = LibsvmTask.REGRESSION;
		this.regressionProperty = regressionPropertyName;
	}

	
	@Override
	public Example readNextExample() throws IOException, InstantiationException {
		if (!this.hasNext()) {
			throw new IOException(
					"DatasetIO Exception: There is no example to read!");
		}
		Example example = new SimpleExample();
		int endLabelIndex = this.nextRow.indexOf(' ');
		Label label;
		if(this.task==LibsvmTask.CLASSIFICATION){
			label = new StringLabel(nextRow.substring(0, endLabelIndex));
		}else{
			label = new NumericLabel(regressionProperty, Float.parseFloat(nextRow.substring(0, endLabelIndex)));
		}
		
		String text = nextRow.substring(endLabelIndex+1);
		
		int separatorIndex = text.indexOf(COMMENT_SEPARATOR);
		if(separatorIndex!=-1){
			text = text.substring(0, separatorIndex);
		}
		SparseVector vector = new SparseVector();
		vector.setDataFromText(text);
		example.addLabel(label);
		example.addRepresentation(representationName, vector);
		this.nextRow = this.inputBuffer.readLine();
		this.checkRowValidity();
		return example;
	}

}
