// CONTROL COMPONENT INT TEST . JAVA

package cat.calidos.morfeu.control.injection;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.HashMap;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import cat.calidos.morfeu.webapp.injection.ControlComponent;
import cat.calidos.morfeu.webapp.injection.DaggerControlComponent;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ControlComponentIntTest {

@Rule public MockitoRule mockitoRule = MockitoJUnit.rule(); 
@Mock ServletContext context;

HashMap<String, String> emptyParams = new HashMap<String, String>(0);


@Test
public void testPingControl() {

	ControlComponent controlComponent = DaggerControlComponent.builder()
																.withPath("/ping")
																.method(DaggerControlComponent.GET)
																.withParams(emptyParams)
																.andContext(context)
																.build();
	assertTrue("Should match /ping path", controlComponent.matches());
	assertEquals("OK", controlComponent.process());
	assertEquals(DaggerControlComponent.TEXT, controlComponent.contentType());
	
}


@Test
public void testPingControlWithParam() {

	ControlComponent controlComponent = DaggerControlComponent.builder()
																.withPath("/ping/param")
																.method(DaggerControlComponent.GET)
																.withParams(emptyParams)
																.andContext(context)
																.build();
	assertTrue("Should match /ping/param path", controlComponent.matches());
	assertEquals("OK param", controlComponent.process());
	assertEquals(DaggerControlComponent.TEXT, controlComponent.contentType());

}


@Test
public void testNoMatch() {

	ControlComponent controlComponent = DaggerControlComponent.builder()
																.withPath("/foo")
																.method(DaggerControlComponent.GET)
																.withParams(emptyParams)
																.andContext(context)
																.build();

	assertFalse(controlComponent.matches());
	assertEquals(DaggerControlComponent.TEXT, controlComponent.contentType());

}


@Test
public void testContext() {

	when(context.getAttribute("counter")).thenReturn(0);

	ControlComponent controlComponent = DaggerControlComponent.builder()
																.withPath("/counter")
																.method(DaggerControlComponent.GET)
																.withParams(emptyParams)
																.andContext(context)
																.build();
	assertTrue("Should match /counter path", controlComponent.matches());
	assertEquals("1", controlComponent.process());
	assertEquals(DaggerControlComponent.TEXT, controlComponent.contentType());

}


}

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
