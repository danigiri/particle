// YAML TEXTUAL TO XML PROCESSOR TEST . JAVA

package cat.calidos.morfeu.transform;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.JsonNode;

import cat.calidos.morfeu.model.CellModel;
import cat.calidos.morfeu.model.ComplexCellModel;
import cat.calidos.morfeu.model.injection.ModelTezt;
import cat.calidos.morfeu.transform.JsonNodeCellModel;
import cat.calidos.morfeu.transform.YAMLTextualToXMLProcessor;

/**
*	@author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class YAMLTextualToXMLProcessorIntTest extends ModelTezt {

@Mock
JsonNode node;
private CellModel stuff; 


@Before
public void setup() throws Exception {

	URI modelURI = new URI("target/test-classes/test-resources/models/test-model.xsd");
	ComplexCellModel test = cellModelFrom(modelURI, "test").asComplex();
	stuff = test.children().child("row").asComplex().children().child("col").asComplex().children().child("stuff");

}

@Test
public void testOutputWithContent() throws Exception {


	String content = "stuff content";
	when(node.size()).thenReturn(content.length());
	when(node.asText()).thenReturn(content);

	JsonNodeCellModel nodeCellModel = new JsonNodeCellModel(node, stuff);
	YAMLTextualToXMLProcessor processor = new YAMLTextualToXMLProcessor("\t", nodeCellModel);
	assertEquals("\t<stuff>"+content+"</stuff>\n", processor.output());

}


@Test
public void testOutputWithEmptyContent() {

	String content = "";
	when(node.size()).thenReturn(content.length());
	when(node.asText()).thenReturn(content);

	JsonNodeCellModel jsonNodeCellModel = new JsonNodeCellModel(node, stuff);
	YAMLTextualToXMLProcessor processor = new YAMLTextualToXMLProcessor("\t", jsonNodeCellModel);
	assertEquals("\t<stuff/>\n", processor.output());

}


}


/*
 *    Copyright 2019 Daniel Giribet
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

