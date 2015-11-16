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
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.NumericLabel;
import it.uniroma2.sag.kelp.data.manipulator.Manipulator;
import it.uniroma2.sag.kelp.data.representation.Vector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SimpleDataset that represent a whole dataset in memory.
 * 
 * @author Giuseppe Castellucci, Simone Filice
 */
public class SimpleDataset implements Dataset {

	private Logger logger = LoggerFactory.getLogger(SimpleDataset.class);
	private static final long DEFAULT_SEED=1;
	
	private ArrayList<Example> examples;
	private HashMap<Label, Integer> examplesPerClass;
	private int exampleIndex = 0;
	private Random randomGenerator;
	private long initialSeed = DEFAULT_SEED;
	private HashSet<Label> regressionProperties;

	/**
	 * Initializes an empty dataset
	 */
	public SimpleDataset() {
		this.examples = new ArrayList<Example>();
		this.examplesPerClass = new HashMap<Label, Integer>();
		this.randomGenerator = new Random(DEFAULT_SEED);
		this.regressionProperties = new HashSet<Label>();
	}

	/**
	 * Add an example to the dataset
	 * 
	 * @param example
	 *            the example to be added
	 */
	@Override
	public void addExample(Example example) {
		this.examples.add(example);
		Label[] labels = example.getLabels();//labels contains all the classification labels
		for (Label label : labels) {
			if (!this.examplesPerClass.containsKey(label)) {
				this.examplesPerClass.put(label, new Integer(1));
			} else {
				int currentExamples = this.examplesPerClass.get(label)
						.intValue();
				this.examplesPerClass.put(label, new Integer(
						currentExamples + 1));
			}
		}
		
		NumericLabel[] regressionLabels = example.getRegressionLabels();
		for(NumericLabel regressionLabel : regressionLabels){
			this.regressionProperties.add(regressionLabel.getProperty());
		}
	}

	/**
	 * Add all the examples contained in <code>datasetToBeAdded</code>
	 * 
	 * @param datasetToBeAdded
	 *            the dataset containing all the examples to be added
	 */
	public void addExamples(Dataset datasetToBeAdded) {
		for(Example e : datasetToBeAdded.getExamples()){
			this.addExample(e);
		}

	}

	/**
	 * Return the example stored in the <code>exampleIndex</code> position
	 * 
	 * @param exampleIndex
	 *            the index of the example to return
	 * @return the example stored in the <code>exampleIndex</code> position
	 */
	public Example getExample(int exampleIndex) {
		return this.examples.get(exampleIndex);
	}

	@Override
	public boolean hasNextExample() {
		if (this.exampleIndex < this.getNumberOfExamples()) {
			return true;
		}
		return false;
	}

	@Override
	public Example getNextExample() {
		if (!this.hasNextExample()) {
			return null;
		}
		Example example = this.getExample(exampleIndex);
		this.exampleIndex++;
		return example;
	}

	@Override
	public List<Example> getNextExamples(int n) {
		ArrayList<Example> examples = new ArrayList<Example>();
		int endIndex = this.exampleIndex + n;
		if (endIndex > this.examples.size()) {
			endIndex = this.examples.size();
		}
		examples.addAll(this.examples.subList(this.exampleIndex, endIndex));
		return examples;
	}

	@Override
	public void reset() {
		this.exampleIndex = 0;
		this.randomGenerator.setSeed(initialSeed);
	}

	@Override
	public int getNumberOfPositiveExamples(Label positiveClass) {
		Integer number = this.examplesPerClass.get(positiveClass);
		if (number == null) {
			return 0;
		}
		return number.intValue();
	}

	@Override
	public int getNumberOfNegativeExamples(Label positiveClass) {
		return this.getNumberOfExamples()
				- this.getNumberOfPositiveExamples(positiveClass);
	}

	@Override
	public int getNumberOfExamples() {
		return this.examples.size();
	}

	@Override
	public List<Label> getClassificationLabels() {
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.addAll(this.examplesPerClass.keySet());
		return labels;
	}
	
	@Override
	public List<Label> getRegressionProperties() {
		ArrayList<Label> properties = new ArrayList<Label>();
		properties.addAll(regressionProperties);
		return properties;
	}

	/**
	 * Shuffles the examples in the dataset
	 * 
	 * @param randomGenerator
	 *            a random number generator
	 */
	public void shuffleExamples(Random randomGenerator) {
		this.reset();
		Collections.shuffle(this.examples, randomGenerator);
	}

	/**
	 * Returns two datasets created by splitting this dataset accordingly to
	 * <code>percentage</code>. The original distribution of the examples among
	 * the classes is maintained in the two datasets. The examples are split
	 * accordingly to their order. Thus the first dataset consists of the first
	 * <code>percentage</code>% of examples of each class, while the second
	 * dataset consists in all the remaining examples
	 * 
	 * @param percentage
	 *            should be a number in [0,1]
	 * @return two datasets generated by splitting this one
	 */
	public SimpleDataset[] splitClassDistributionInvariant(float percentage) {
		// TODO: sistemare, per esempi non etichettati e per esempi multi label
		SimpleDataset[] datasets = new SimpleDataset[2];
		datasets[0] = new SimpleDataset();
		datasets[1] = new SimpleDataset();
		HashMap<Label, Integer> examplesD0 = new HashMap<Label, Integer>();
		List<Label> labels = this.getClassificationLabels();
		for (Label label : labels) {
			examplesD0.put(label, new Integer(0));
		}
		for (int i = 0; i < this.getNumberOfExamples(); i++) {
			Example currentExample = this.getExample(i);
			Label[] exampleLabels = currentExample.getLabels();
			Label label = exampleLabels[0];
			int currentPositiveExamples = examplesD0.get(label);
			if (currentPositiveExamples < this
					.getNumberOfPositiveExamples(label) * percentage) {
				datasets[0].addExample(currentExample);
				examplesD0.put(label, new Integer(currentPositiveExamples + 1));
			} else {
				datasets[1].addExample(currentExample);
			}
		}
		return datasets;
	}

	/**
	 * Returns two datasets created by splitting this dataset accordingly to
	 * <code>percentage</code>. The examples are split accordingly to their
	 * order without maintaining the original data distribution among the
	 * classes. Thus the first dataset consists of the first
	 * <code>percentage</code>% of examples, while the second dataset consists
	 * in all the remaining examples
	 * 
	 * @param percentage
	 *            should be a number in [0,1]
	 * @return two datasets generated by splitting this one
	 */
	public SimpleDataset[] split(float percentage) {
		SimpleDataset[] datasets = new SimpleDataset[2];
		datasets[0] = new SimpleDataset();
		datasets[1] = new SimpleDataset();
		for (int i = 0; i < this.getNumberOfExamples(); i++) {
			Example currentExample = this.getExample(i);
			if (i < this.getNumberOfExamples() * percentage) {
				datasets[0].addExample(currentExample);
			} else {
				datasets[1].addExample(currentExample);
			}
		}
		return datasets;
	}

	/**
	 * Returns <code>n</code> datasets. Each dataset is a fold storing 1/n of
	 * the total examples. The folds are not overlapped and maintain the
	 * original distribution of the examples among the classes. The example in
	 * this dataset are split into <code>n</code> folds accordingly to their
	 * order, so that for instance the first folds has all the first examples of
	 * each class
	 * 
	 * @param n
	 *            the number of folds to create
	 * @return <code>n</code> datasets each one consisting of 1/n% of the
	 *         examples
	 */
	public SimpleDataset[] nFoldingClassDistributionInvariant(int n) {
		// TODO: sistemare, per esempi non etichettati e per esempi multi label
		SimpleDataset[] datasets = new SimpleDataset[n];
		for (int i = 0; i < n; i++) {
			datasets[i] = new SimpleDataset();
		}
		HashMap<Label, Integer> usedExamples = new HashMap<Label, Integer>();
		List<Label> labels = this.getClassificationLabels();
		for (Label label : labels) {
			usedExamples.put(label, new Integer(0));
		}
		for (int i = 0; i < this.getNumberOfExamples(); i++) {
			Example currentExample = this.getExample(i);
			Label[] exampleLabels = currentExample.getLabels();
			Label label = exampleLabels[0];
			int currentPositiveExamples = usedExamples.get(label);
			int partitionIndex = n * currentPositiveExamples
					/ this.getNumberOfPositiveExamples(label);
			datasets[partitionIndex].addExample(currentExample);
			usedExamples.put(label, new Integer(currentPositiveExamples + 1));
		}

//		for (int i = 0; i < n; i++) {
//			datasets[i].shuffleExamples(new Random());
//		}
		return datasets;
	}

	/**
	 * Returns <code>n</code> datasets. Each dataset is a fold storing 1/n of
	 * the total examples. The folds are not overlapped and do not maintain the
	 * original distribution of the examples among the classes. The example in
	 * this dataset are split into <code>n</code> folds accordingly to their
	 * order, so that for instance the first folds has all the first examples.
	 * 
	 * @param n
	 *            the number of folds to create
	 * @return <code>n</code> datasets each one consisting of 1/n% of the
	 *         examples
	 */
	public SimpleDataset[] nFolding(int n) {
		SimpleDataset[] datasets = new SimpleDataset[n];
		for (int i = 0; i < n; i++) {
			datasets[i] = new SimpleDataset();
		}
		for (int i = 0; i < this.getNumberOfExamples(); i++) {
			Example currentExample = this.getExample(i);
			int partitionIndex = n * i / this.getNumberOfExamples();
			datasets[partitionIndex].addExample(currentExample);
		}
		return datasets;
	}

	@Override
	public List<Example> getExamples() {
		return this.examples;
	}

	/**
	 * This method extracts examples of given {@code labels} from
	 * {@code dataset}
	 * 
	 * @param dataset
	 *            original dataset
	 * @param labels
	 *            labels of interest
	 * @return new dataset with only examples of labels of interest
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static Dataset extractExamplesOfClasses(Dataset dataset,
			List<Label> labels) throws InstantiationException,
			IllegalAccessException {
		// Dataset newDataset = new SimpleDataset();
		Dataset newDataset = dataset.getClass().newInstance();
		for (Example e : dataset.getExamples()) {
			for (Label l : labels) {
				if (e.isExampleOf(l)) {
					newDataset.addExample(e);
					break;
				}
			}
		}
		return newDataset;
	}

	/**
	 * Populate the dataset by reading it from a KeLP
	 * compliant file.
	 * 
	 * @param filename the path of the file to be read
	 * @throws Exception
	 */
	public void populate(String filename) throws Exception {
		DatasetReader reader = new DatasetReader(filename);
		this.populate(reader);
	}
	
	/**
	 * Populate the dataset using the provided <code>reader</code>
	 * 
	 * @param datasetReader the reader
	 * @throws Exception
	 */
	public void populate(DatasetReader reader) throws Exception {
		while (reader.hasNext()) {
			Example example = reader.readNextExample();
			this.addExample(example);
		}
		reader.close();
		if(!this.isConsistent()){
			throw new IOException("the dataset contains incompatible examples");
		}
	}

	@Override
	public Example getRandExample() {
		int index = this.randomGenerator.nextInt(this.getNumberOfExamples());
		return this.examples.get(index);
	}

	/**
	 * @param k the number of examples to be returned
	 * @return a list containing <code>k</code> random examples. 
	 * 
	 * <p>
	 * NOTE: Duplicates are allowed
	 */
	@Override
	public List<Example> getRandExamples(int k) {
		// TODO: it can produce duplicates. Shall we fix it?!?
		List<Example> array = new ArrayList<Example>();
		for(int i=0; i<k;i++){
			array.add(getRandExample());
		}		
		return array;
	}

	@Override
	public SimpleDataset getShuffledDataset() {
		SimpleDataset shuffled = new SimpleDataset();
		ArrayList<Example> copiedExamples = new ArrayList<Example>(this.examples);
		Collections.shuffle(copiedExamples, this.randomGenerator);
		for(Example example : copiedExamples){
			shuffled.addExample(example);
		}
		return shuffled;
	}

	@Override
	public void setSeed(long seed) {
		this.initialSeed = seed;
		this.randomGenerator.setSeed(seed);		
	}

	@Override
	public Vector getZeroVector(String representationIdentifier) {
		Example example = this.examples.get(0);
		Vector vector = (Vector) example.getRepresentation(representationIdentifier);
		return vector.getZeroVector();
	}

	@Override
	public void manipulate(Manipulator... manipulators) {
		for(Example example : this.examples){
			for(Manipulator manipulator : manipulators){
				example.manipulate(manipulator);
			}
		}
		
	}
	
	/**
	 * Save the dataset in a file. <br>
	 * <b>NOTE</b>: if the filename ends with ".gz" the file will be compressed
	 * through GZIP.
	 * 
	 * @param outputFilePath
	 *            the file path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void save(String outputFilePath) throws FileNotFoundException,
			IOException {
		DatasetWriter datasetWriter = new DatasetWriter(outputFilePath);
		for (Example e : getExamples()) {
			datasetWriter.writeNextExample(e);
		}
		datasetWriter.close();
	}
	
	/**
	 * Evaluates whether the examples included in this dataset are compatible 
	 * with each other. In particular it compares the first example with all the
	 * others 
	 *
	 * @return whether this dataset is consistent, i.e., its examples are 
	 * compatible with each others 
	 */
	public boolean isConsistent(){
		if(this.getNumberOfExamples()==0){
			return true;
		}
		Example ex1 = this.getExample(0);
		for(int i=1; i< this.getNumberOfExamples(); i++){
			Example ex = this.getExample(i);
			if(!ex1.isCompatible(ex)){
				logger.error("example " + i + " is incompatible with example 0");
				return false;
			}
		}
		return true;
	}

}
