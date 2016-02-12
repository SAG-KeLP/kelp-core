package it.uniroma2.sag.kelp.data.dataset;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.utils.FileUtils;

public class DatasetWriter {

	private BufferedWriter writer;
	private OutputStream outS;

	public DatasetWriter(String outputFilePath)
			throws FileNotFoundException, IOException {
		outS = FileUtils.createOutputStream(outputFilePath);
		writer = new BufferedWriter(new OutputStreamWriter(outS, "utf8"));
	}

	public void writeNextExample(Example e) throws IOException {
		writer.append(e.toString() + "\n");
	}

	public void close() throws IOException {
		writer.close();
	}

}
