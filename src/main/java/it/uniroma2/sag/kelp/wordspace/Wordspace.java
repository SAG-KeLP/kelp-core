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

package it.uniroma2.sag.kelp.wordspace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.ejml.data.DenseMatrix64F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import gnu.trove.map.hash.TLongObjectHashMap;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.vector.DenseVector;
import it.uniroma2.sag.kelp.utils.FileUtils;

/**
 * This is an implementation of a wordspace used for associating words to
 * vectors. In particular this wordspace represents each word as a
 * <code>DenseVector</code>
 * 
 * <p>
 * NOTE: in order to speed-up the computation and to reduce the memory
 * occupation, vectors, instead of being associated to words, are associated to
 * their MD5. This, in some remote cases, can lead to word-collision. If it
 * happens when the wordspace is loaded a WARNING message is provided.
 * 
 * @author Danilo Croce, Simone Filice
 *
 */
@JsonTypeName("wordspace")
public class Wordspace implements WordspaceI {
	private final static Logger logger = LoggerFactory.getLogger(Wordspace.class);
	private String matrixPath;

	/**
	 * The vectors of the Word Space
	 */

	@JsonIgnore
	private TLongObjectHashMap<Vector> vectors;

	/**
	 * The words represented in the Word Space
	 */
	@JsonIgnore
	private TLongObjectHashMap<char[]> words;

	@JsonIgnore
	private MessageDigest wordEncoder;

	public Wordspace() {
		words = new TLongObjectHashMap<char[]>();
		vectors = new TLongObjectHashMap<Vector>();
		try {
			wordEncoder = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public Wordspace(String matrixPath) throws IOException {
		this();
		this.setMatrixPath(matrixPath);
	}

	@Override
	public void addWordVector(String word, Vector vector) {
		long l = md5Encode(word);

		if (vectors.containsKey(l)) {
			logger.error("Warning: collision while reading matrix. The word " + word + " collides with "
					+ String.valueOf(words.get(l)));
		}

		vectors.put(l, vector);
		words.put(l, word.toCharArray());
	}

	private long md5Encode(String str) {
		try {
			byte[] bytesOfMessage = str.getBytes("UTF-8");
			byte[] digest = wordEncoder.digest(bytesOfMessage);
			return ByteBuffer.wrap(digest).getLong();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Loads the word-vector pairs stored in the file whose path is
	 * <code>filename</code> The file can be a plain text file or a .gz archive.
	 * <p>
	 * The expected format is: </br>
	 * number_of_vectors space_dimensionality</br>
	 * word_i [TAB] 1.0 [TAB] 0 [TAB] vector values comma
	 * separated </code> </br>
	 * </br>
	 * Example: </br>
	 * </br>
	 * <code> 3 5</br>
	 * dog::n [TAB] 1.0 [TAB] 0 [TAB] 2.1,4.1,1.4,2.3,0.9</br>
	 * cat::n [TAB] 1.0 [TAB] 0 [TAB] 3.2,4.3,1.2,2.2,0.8</br>
	 * mouse::n [TAB] 1.0 [TAB] 0 [TAB] 2.4,4.4,2.4,1.3,0.92</br>
	 * 
	 * 
	 * @param filename
	 *            the path of the file containing the word-vector pairs
	 * @throws IOException
	 */
	private void populate(String filename) throws IOException {
		InputStream createInputStream = FileUtils.createInputStream(filename);		
		BufferedReader br = new BufferedReader(new InputStreamReader(createInputStream, "utf8"));
		
		String line;
		ArrayList<String> split;
		String label;
		String[] vSplit;

		Pattern iPattern = Pattern.compile(",");
		float[] v = null;

		while ((line = br.readLine()) != null) {
			if (!line.contains("\t"))
				continue;
			float norm2 = 0;
			split = mySplit(line);
			label = split.get(0);
			vSplit = iPattern.split(split.get(3), 0);
			if (v == null)
				v = new float[vSplit.length];
			for (int i = 0; i < v.length; i++) {
				v[i] = Float.parseFloat(vSplit[i]);
				norm2 += v[i] * v[i];
			}
			float norm = (float) Math.sqrt(norm2);
			for (int i = 0; i < v.length; i++) {
				v[i] /= norm;
			}

			DenseMatrix64F featureVector = new DenseMatrix64F(1, v.length);
			for (int i = 0; i < v.length; i++) {
				featureVector.set(0, i, (double) v[i]);
			}

			DenseVector denseFeatureVector = new DenseVector(featureVector);

			addWordVector(label, denseFeatureVector);

		}
		createInputStream.close();
		br.close();
	}

	private ArrayList<String> mySplit(String s) {
		char[] c = (s).toCharArray();
		ArrayList<String> ll = new ArrayList<String>();
		int index = 0;
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\t') {
				ll.add(s.substring(index, i));
				index = i + 1;
			}
		}
		ll.add(s.substring(index, s.length()));
		return ll;
	}

	@Override
	public Vector getVector(String word) {
		long l = md5Encode(word);
		if (!vectors.contains(l))
			return null;
		return vectors.get(l);
	}

	@Override
	public char[][] getDictionaryDanilo() {
		return (char[][]) this.words.values();
	}

	/**
	 * @return the matrixPath
	 */
	public String getMatrixPath() {
		return matrixPath;
	}

	/**
	 * Sets the path of the file where the word vectors are stored and loads
	 * them. The file can be a plain text file or a .gz archive.
	 * <p>
	 * The expected format is: </br>
	 * number_of_vectors space_dimensionality</br>
	 * word_i [TAB] 1.0 [TAB] 0 [TAB] vector values comma
	 * separated </code> </br>
	 * </br>
	 * Example: </br>
	 * </br>
	 * <code> 3 5</br>
	 * dog::n [TAB] 1.0 [TAB] 0 [TAB] 2.1,4.1,1.4,2.3,0.9</br>
	 * cat::n [TAB] 1.0 [TAB] 0 [TAB] 3.2,4.3,1.2,2.2,0.8</br>
	 * mouse::n [TAB] 1.0 [TAB] 0 [TAB] 2.4,4.4,2.4,1.3,0.92</br>
	 * <p>
	 * 
	 * @param matrixPath
	 *            the matrixPath to set
	 * @throws IOException
	 */
	public void setMatrixPath(String matrixPath) throws IOException {
		this.matrixPath = matrixPath;
		this.populate(matrixPath);
	}

	@Override
	public Vector getZeroVector() {
		Vector aVector = (Vector) vectors.values()[0];
		return aVector.getZeroVector();
	}

}
