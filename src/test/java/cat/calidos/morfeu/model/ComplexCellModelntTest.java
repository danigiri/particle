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

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import cat.calidos.morfeu.model.injection.ModelTezt;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ComplexCellModelntTest extends ModelTezt {


@Test
public void testSimpleToComplex() throws Exception {
	
	URI modelURI = new URI("target/test-classes/test-resources/models/test-model.xsd");
	CellModel cellModel = complexCellModelFrom(modelURI, "test");
	
	assertTrue(cellModel.isComplex());
	ComplexCellModel extractedCellModel = ComplexCellModel.from(cellModel);
	assertNotNull(extractedCellModel);
	
}


}