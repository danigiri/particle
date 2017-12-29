/*
 *    Copyright 2017 Daniel Giribet
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package cat.calidos.morfeu.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class Metadata implements Locatable {

protected final static Logger log = LoggerFactory.getLogger(Metadata.class);
		
private static final String DEFAULT_DESC = "";
private static final String DEFAULT_THUMB = "DEFAULT";
private static final String DEFAULT_CELL_PRESENTATION = "DEFAULT";
public static String DEFAULT_PRESENTATION = "CELL";

private URI uri;	// pre-calculated default
private String desc;
private String presentation;
private String cellPresentation;
private String thumb;
private Map<String, String> defaultValues;

public static final String DEFAULT_VALUE_PREFIX = "@";
//private static String UNDEFINED = "";

public Metadata(URI uri, 
				String desc, 
				String presentation, 
				String cellPresentation, 
				String thumb, 
				Map<String, String> defaultValues) {
	this(uri, 
			Optional.ofNullable(desc), 
			Optional.ofNullable(presentation), 
			Optional.ofNullable(cellPresentation), 
			Optional.ofNullable(thumb),
			defaultValues);
}


public Metadata(URI uri, 
				Optional<String> desc, 
				Optional<String> presentation, 
				Optional<String> cellPresentation,
				Optional<String> thumb,
				Map<String, String> defaultValues) {

	this.uri = uri;
	this.desc = desc.orElse(DEFAULT_DESC);
	this.presentation = presentation.orElse(DEFAULT_PRESENTATION);
	this.cellPresentation = cellPresentation.orElse(DEFAULT_CELL_PRESENTATION);
	this.thumb = thumb.orElse(DEFAULT_THUMB);
	this.defaultValues = defaultValues;
	
}


public Metadata(URI uri, 
				Optional<String> desc, 
				Optional<String> pres, 
			    Optional<String> cellPresentation,
				Optional<String> thumb,
				Map<String, String> defaultValues,
				Metadata fallback) {
	this(uri,
		 desc.orElse(fallback.getDesc()),
		 pres.orElse(fallback.getPresentation()),
		 cellPresentation.orElse(fallback.getCellPresentation()),
		 thumb.orElse(fallback.getThumb()),
		 defaultValues);
}


public String getDesc() {
	return desc;
}


public String getPresentation() {
	return presentation;
}


public String getCellPresentation() {
	return cellPresentation;
}


public String getThumb() {
	return thumb;
}


public Map<String, String> getDefaultValues() {
	return defaultValues;
}


@Override
public URI getURI() {
	return uri;
}


@Override
public String getName() {

	// TODO Auto-generated method stub
	return null;
}


/* (non-Javadoc)
* @see java.lang.Object#toString()
*//////////////////////////////////////////////////////////////////////////////
@Override
public String toString() {

	return "Metadata:{uri:"+uri+", desc:'"+desc+"', thumb:'"+thumb+"', presentation:'"+presentation+"'}";
}



}
