package it.uniroma2.sag.kelp.data.dataset;

import it.uniroma2.sag.kelp.data.example.Example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

public class DatasetWriter {

	private BufferedWriter writer;
	private GZIPOutputStream zip;
	private String outputFilePath;

	public DatasetWriter(String outputFilePath) throws FileNotFoundException,
			IOException {
		this.outputFilePath = outputFilePath;

		if (outputFilePath.endsWith(".gz")) {
			zip = new GZIPOutputStream(new FileOutputStream(new File(
					outputFilePath)));
			writer = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
		} else {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFilePath), "UTF-8"));
		}
	}
	
	public void writeNextExample(Example e) throws IOException{
		writer.append(e.toString() + "\n");
	}

	public void close() throws IOException {
		writer.close();
		if (outputFilePath.endsWith(".gz")) {
			zip.close();
		}
	}

}
