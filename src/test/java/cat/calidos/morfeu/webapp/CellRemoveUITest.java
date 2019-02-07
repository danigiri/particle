// CELL REMOVE UI TEST . JAVA
package cat.calidos.morfeu.webapp;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cat.calidos.morfeu.webapp.ui.UICatalogues;
import cat.calidos.morfeu.webapp.ui.UICell;
import cat.calidos.morfeu.webapp.ui.UIContent;

/** Testing cell removal
*	@author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class CellRemoveUITest extends UITezt {



private UIContent content;


@Before
public void setup() {

	open(appBaseURL);
	content = UICatalogues.openCatalogues()
							.shouldAppear()
							.clickOn(0)
							.clickOnDocumentNamed("Document 1")
							.content();

}


@Test
public void removeAfterSelection() {

	content.shouldBeVisible();

	UICell test = content.rootCells().get(0);

	UICell col = test.child("row(0)").child("col(0)");
	assertEquals("Should have one child", 1, col.children().size());
	UICell data = col.child("data(0)");
	assertNotNull(data);

	data.select().remove();
	assertEquals("Should have no children after removing", 0, col.children().size());

}


@Test
public void removeAfterActivation() {

	content.shouldBeVisible();
	UICell test = content.rootCells().get(0);

	UICell col = test.child("row(0)").child("col(0)");
	assertEquals("Should have one child", 1, col.children().size());
	UICell data = col.child("data(0)");
	assertNotNull(data);

	data.select().activate().remove();	// should also work
	assertEquals("Should have no children after removing", 0, col.children().size());

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

