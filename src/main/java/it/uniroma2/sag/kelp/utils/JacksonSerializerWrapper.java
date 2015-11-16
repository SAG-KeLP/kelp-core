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

package it.uniroma2.sag.kelp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * It is a serializer, i.e. an object that is able to convert objects into a
 * String representation, preserving all their properties. It embeds the Jackson
 * Serializer.
 * 
 * @author Simone Filice, Danilo Croce
 *
 */
public class JacksonSerializerWrapper implements ObjectSerializer {
	private static ObjectMapper mapper;
	private static final ObjectWriter ow;
	static {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		ow = mapper.writer().withDefaultPrettyPrinter();
	}

	@Override
	public <T> T readValue(String content, Class<T> valueType)
			throws IOException, JsonParseException, JsonMappingException {
		return mapper.readValue(content, valueType);
	}
	
	@Override
	public <T> T readValue(File file, Class<T> valueType) throws IOException,
			JsonParseException, JsonMappingException {
		return mapper.readValue(file, valueType);
	}

	@Override
	public String writeValueAsString(Object object)
			throws JsonProcessingException {
		return ow.writeValueAsString(object);
	}

	@Override
	public void writeValueOnFile(Object object, String filePath)
			throws IOException {
		String toWrite = writeValueAsString(object);
		PrintStream ps = new PrintStream(filePath, "UTF-8");
		ps.println(toWrite);
		ps.flush();
		ps.close();
	}

	@Override
	public void writeValueOnGzipFile(Object object, String filePath)
			throws IOException {
		FileOutputStream out = new FileOutputStream(new File(filePath));
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		OutputStreamWriter writer = new OutputStreamWriter(gzip);
		// writer.write(toWrite.toCharArray());
		ow.writeValue(gzip, object);
		writer.flush();
		gzip.finish();
		writer.close();
		out.flush();
		out.close();

	}

}
