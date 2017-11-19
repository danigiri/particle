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
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;

import cat.calidos.morfeu.model.Metadata;
import cat.calidos.morfeu.model.Model;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class GlobalMetadataModuleIntTest extends ModelTezt {

@Test
public void testGlobalMetadataFromModel() throws Exception {
	
	URI modelURI = new URI("target/test-classes/test-resources/models/test-model.xsd");
	XSSchemaSet schemaSet = parseSchemaFrom(modelURI);
	
	XSSchema schema = schemaSet.getSchema(Model.MODEL_NAMESPACE);
	XSAnnotation annotation = schema.getAnnotation();
	
	Map<URI, Metadata> globalMetadata = GlobalModelMetadataModule.provideGlobalModelMetadata(annotation);
	assertNotNull("global metadata parser should not return null", globalMetadata);
	assertEquals("global metadata should have two entries", 2, globalMetadata.size());

	URI dataURI = new URI("/test/row/col/data");
	Metadata dataMetadata = globalMetadata.get(dataURI);	
	assertNotNull("global metadata parser should not return data cell metadata", dataMetadata);
	assertEquals("Globally provided description of 'data'", dataMetadata.getDesc());
	assertEquals("assets/images/data-thumb.svg", dataMetadata.getThumb());

	URI data2URI = new URI("/test/row/col/data2");
	Metadata data2Metadata = globalMetadata.get(data2URI);	
	assertNotNull("global metadata parser should not return data2 cell metadata", data2Metadata);
	assertEquals("assets/images/data2-thumb.svg", data2Metadata.getThumb());

	
}

}
