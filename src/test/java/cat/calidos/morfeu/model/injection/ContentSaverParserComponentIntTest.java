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

package cat.calidos.morfeu.model.injection;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import cat.calidos.morfeu.model.Cell;
import cat.calidos.morfeu.model.Composite;
import cat.calidos.morfeu.model.Validable;
import cat.calidos.morfeu.problems.ConfigurationException;
import cat.calidos.morfeu.problems.FetchingException;
import cat.calidos.morfeu.problems.ParsingException;
import cat.calidos.morfeu.problems.ValidationException;
import cat.calidos.morfeu.utils.Config;
import cat.calidos.morfeu.utils.Tezt;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ContentSaverParserComponentIntTest extends ModelTezt {


private URI modelURI;
private URI modelFetchableURI;
private String content;
private URI contentURI;


@Before
public void setup() throws Exception {

	String modelPath = "test-resources/models/test-model.xsd";
	modelURI = new URI(modelPath);
	modelFetchableURI = new URI("target/test-classes/"+modelPath);

	String contentPath = "test-resources/documents/document1.xml";
	contentURI = new URI(contentPath);
	String fullContentPath = testAwareFullPathFrom(contentPath);
	URI fullContentURI = new URI(fullContentPath);
	content = IOUtils.toString(fullContentURI, Config.DEFAULT_CHARSET);

}


@Test
public void testValidateString() throws Exception {

	ContentSaverParserComponent contentComponent = DaggerContentSaverParserComponent
													.builder()
													.from(content)
													.having(contentURI)
													.model(modelURI)
													.withModelFetchedFrom(modelFetchableURI)
													.build();
	
	Validable validator = contentComponent.validator().get();
	assertNotNull(validator);
	validator.validate();	// this would throw an exception
	assertTrue("'Content saver parser' did not validate a valid XML string", validator.isValid());

	Composite<Cell> rootCells = contentComponent.content().get();
	assertNotNull(rootCells);
	
	assertEquals("Wrong size of content root from 'content saver parser' parsed XML string", 1, rootCells.size());
	Cell test = rootCells.child("test(0)");
	assertEquals("Wrong root node name from 'content saver parser' parsed XML string", "test", test.getName());
	
}


@Test
public void testNonValidString() throws Exception {
	
	String contentPath = "test-resources/documents/nonvalid-document.xml";
	String fullContentPath = testAwareFullPathFrom(contentPath);
	URI fullContentURI = new URI(fullContentPath);
	String content = IOUtils.toString(fullContentURI, Config.DEFAULT_CHARSET);
	
	Validable validator = DaggerContentSaverParserComponent
							.builder()
							.from(content)
							.having(new URI(contentPath))
							.model(modelURI)
							.withModelFetchedFrom(modelFetchableURI)
							.build()
							.validator()
							.get();
	
	try {
		System.err.println("Please ignore next ParsingException, it is expected as we are testing non valid str");
		validator.validate();
	} catch (ValidationException e) {
		assertTrue("Wrong exception message parsing of 'content saver parser'", e.getMessage().contains("notvalid"));
	}

}

}
