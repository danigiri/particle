/*
 *    Copyright 2018 Daniel Giribet
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
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import cat.calidos.morfeu.model.Document;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class SnippetCellModelFilterTest {

@Rule public MockitoRule mockitoRule = MockitoJUnit.rule(); 


@Test
public void testCellModelFilter() throws Exception {
	
	URI modelURI = new URI("target/test-classes/test-resources/models/test-model.xsd?filter=/test/row/col/stuff");
	URI expected = new URI("target/test-classes/test-resources/models/test-model.xsd/test/row/col/stuff");
	
	Document documentMock = mock(Document.class);
	when(documentMock.getModelURI()).thenReturn(modelURI);
	assertEquals(expected, SnippetModelURIModule.cellModelFilter(documentMock));
	
}

}
