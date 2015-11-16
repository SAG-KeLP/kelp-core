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
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.NumericLabel;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.data.representation.vector.DenseVector;

import java.io.IOException;

/**
 * A utility class to read dataset in the csv format.
 * 
 * @author Simone Filice
 */
public class CsvDatasetReader extends DatasetReader{
	
	public enum LabelPosition{
		NO_LABEL,
		FIRST_COLUMN,
		LAST_COLUMN,
		
	}
	
	private String representationName;
	private StringLabel regressionProperty = null;
	private LabelPosition labelPosition;
	


	/**
	 * Constructor for reading dataset in csv format.
	 * 
	 * @param filename the path of the file to be read
	 * @param representationName the name of the vector representation each example consists of
	 * @param skipFirstLine if true the first line is skipped (to be used when it contains the header)
	 * @param labelInFirstColumn if true the first column is assumed to be a classification label, 
	 * if false the examples are without label (probably for clustering tasks)
	 * @throws IOException
	 */
	public CsvDatasetReader(String filename, String representationName, boolean skipFirstLine, LabelPosition labelPosition) throws IOException {
		super(filename);
		this.representationName = representationName;
		this.labelPosition = labelPosition;
		if(skipFirstLine){
			if (!this.hasNext()) {
				throw new IOException(
						"DatasetIO Exception: There is no example to read!");
			}
			this.nextRow = this.inputBuffer.readLine();
			this.checkRowValidity();
		}
	}
	
	/**
	 * Constructor for reading dataset in csv format for regression tasks (the regression value is assumed to
	 * be in the first column).
	 * 
	 * @param filename the path of the file to be read
	 * @param representationName the name of the vector representation each example consists of
	 * @param skipFirstLine if true the first line is skipped (to be used when it contains the header)
	 * @param regressionPropertyName the name of the regression property to be learned  (it is assumed that the first 
	 * column contains the regression value)
	 * @throws IOException
	 */
	public CsvDatasetReader(String filename, String representationName, boolean skipFirstLine, LabelPosition labelPosition,StringLabel regressionPropertyName) throws IOException {
		super(filename);

		this.representationName = representationName;
		this.labelPosition = labelPosition;
		this.regressionProperty = regressionPropertyName;
		if(skipFirstLine){
			if (!this.hasNext()) {
				throw new IOException(
						"DatasetIO Exception: There is no example to read!");
			}
			this.nextRow = this.inputBuffer.readLine();
			this.checkRowValidity();
		}
	}

	@Override
	public Example readNextExample() throws IOException, InstantiationException {
		if (!this.hasNext()) {
			throw new IOException(
					"DatasetIO Exception: There is no example to read!");
		}
		Example example = new SimpleExample();
		
		String representationPart = this.nextRow;
		if(labelPosition!=LabelPosition.NO_LABEL){

			String labelPart;
			if(labelPosition==LabelPosition.FIRST_COLUMN){
				int index = this.nextRow.indexOf(',');
				if(index==-1){
					index = this.nextRow.indexOf(';');
				}
				labelPart = this.nextRow.substring(0, index);
				representationPart = this.nextRow.substring(index+1, this.nextRow.length());
			}else{
				int index = this.nextRow.lastIndexOf(',');
				if(index==-1){
					index = this.nextRow.lastIndexOf(';');
				}
				labelPart = this.nextRow.substring(index+1, this.nextRow.length());
				representationPart = this.nextRow.substring(0, index);
			}
			
			Label label;
			if(this.regressionProperty==null){
				label = new StringLabel(labelPart);
			}else{
				label = new NumericLabel(regressionProperty, Float.parseFloat(labelPart));
			}
			
			example.addLabel(label);
		}
		
		
		DenseVector vector = new DenseVector();
		vector.setDataFromText(representationPart);
		
		example.addRepresentation(representationName, vector);
		this.nextRow = this.inputBuffer.readLine();
		this.checkRowValidity();
		return example;
	}

}
