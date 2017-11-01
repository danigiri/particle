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

package cat.calidos.morfeu.webapp;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;

import cat.calidos.morfeu.webapp.ui.UICatalogues;
import cat.calidos.morfeu.webapp.ui.UICell;
import cat.calidos.morfeu.webapp.ui.UIContent;

/** Manipulate cell position
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class CellDragUITest extends UITezt {

private UIContent content;


@Before
public void setup() {
	
	open(appBaseURL);
	
	content = UICatalogues.openCatalogues()
			.shouldAppear()
			.clickOn(0)
			.clickOnDocumentNamed("Document 1")
			.content();
	content.shouldBeVisible();

}


@Test
public void testDragCell() {
	
	UICell test = content.rootCells().get(0);

	// source col has one data child
	UICell sourceCol = test.child("row(0)").child("col(0)");
	assertEquals(1, sourceCol.children().size());
	UICell data = sourceCol.child("data(0)");
	assertTrue(data.isCell());												
	
	// col(1) here has two data2 children, we'll drop the data into the middle
	UICell targetCol = test.child("row(0)").child("col(1)").child("row(0)").child("col(1)");
	assertEquals(2, targetCol.children().size());
	
	data.dragTo(targetCol.dropArea(1));				// drop in the middle
	assertEquals(3, targetCol.children().size());

	// we check that we effectively put it in the middle
	assertNotNull(targetCol.child("data2(0)"));
	assertNotNull(targetCol.child("data(1)"));
	assertNotNull(targetCol.child("data2(2)"));

	// we also check that the source col has no children anymore
	assertTrue(sourceCol.children().isEmpty());
	
}

}
