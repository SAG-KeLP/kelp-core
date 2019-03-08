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

package it.uniroma2.sag.kelp.data.representation;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

/**
 * It is a class implementing <code>TypeIdResolver</code> which will be used by Jackson library during
 * the serialization in JSON and deserialization of <code>Representation</code>s 
 * 
 * @author Simone Filice
 *
 */
public class RepresentationTypeResolver implements TypeIdResolver{

	private final static Logger logger = LoggerFactory.getLogger(RepresentationTypeResolver.class);

	private static Map<String, Class<? extends Representation>> idToClassMapping;
	private static Map<Class<? extends Representation>, String> classToIdMapping;

	static{
		Reflections reflections = new Reflections("it");
		idToClassMapping = new HashMap<String, Class<? extends Representation>>();
		classToIdMapping = new HashMap<Class<? extends Representation>, String>();
		Set<Class<? extends Representation>> classes = reflections.getSubTypesOf(Representation.class);
		for(Class<? extends Representation> clazz : classes){
			if(Modifier.isAbstract( clazz.getModifiers() )){
				continue;
			}			
			String abbreviation;
			if(clazz.isAnnotationPresent(JsonTypeName.class)){
				JsonTypeName info = clazz.getAnnotation(JsonTypeName.class);
				abbreviation = info.value();

			}else{
				abbreviation = clazz.getSimpleName();
			}

			idToClassMapping.put(abbreviation, clazz);
			classToIdMapping.put(clazz, abbreviation);
		}
		logger.info("Representation Implementations: {}", idToClassMapping);
	}

	private JavaType mBaseType;

	public Id getMechanism() {
		return Id.CUSTOM;
	}

	public String idFromBaseType() {
		return idFromValueAndType(null, mBaseType.getRawClass());
	}


	public String idFromValue(Object obj) {
		return idFromValueAndType(obj, obj.getClass());
	}

	public void init(JavaType arg0) {
		mBaseType=arg0;		
	}

	public JavaType typeFromId(DatabindContext context, String arg0) {

		Class<? extends Representation> clazz = idToClassMapping.get(arg0);
		if(clazz!=null){
			JavaType type = context.constructSpecializedType(mBaseType, clazz);
			return type;
		}
		throw new IllegalStateException("cannot find mapping for '" + arg0 + "'");
	}

	public String idFromValueAndType(Object arg0, Class<?> arg1) {		
		return classToIdMapping.get(arg0.getClass());
	}
	
	@Override
	public String getDescForKnownTypeIds() {
		return "";
	}

}
