// CATALOGUE UI TEST . JAVA

package cat.calidos.morfeu.webapp;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;

import com.codeborne.selenide.ElementsCollection;

import cat.calidos.morfeu.webapp.ui.UICatalogue;
import cat.calidos.morfeu.webapp.ui.UICatalogueEntry;
import cat.calidos.morfeu.webapp.ui.UICatalogues;
import cat.calidos.morfeu.webapp.ui.UIContent;
import cat.calidos.morfeu.webapp.ui.UIProblem;

import static com.codeborne.selenide.Selenide.*;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class CatalogueUITest extends UITezt {

private static final int CATALOGUE_NOT_FOUND_INDEX = 4;
private static final int EXPECTED_CATALOGUES_COUNT = 5;
private static final int EXPECTED_DOCUMENTS_SIZE = 8;


@Test
public void catalogueListTest() throws Exception {

	open(appBaseURL);

	List<UICatalogueEntry> catalogueEntries = UICatalogues.openCatalogues().shouldAppear().allCatalogueEntries();
	assertEquals(EXPECTED_CATALOGUES_COUNT, catalogueEntries.size());
	assertEquals("Wrong catalogue content", "Catalogue 1", catalogueEntries.get(0).name());
	assertEquals("Wrong catalogue content", "Catalogue 2", catalogueEntries.get(1).name());
	assertEquals("Wrong catalogue content", "Catalogue 1 yaml", catalogueEntries.get(2).name());
	assertEquals("Wrong catalogue content", "Catalogue 1 json", catalogueEntries.get(3).name());
	assertEquals("Wrong catalogue content", "Catalogue not found", catalogueEntries.get(4).name());
	UIProblem.shouldNotBeVisible();

}


@Test
public void catalogueDetailTest() throws Exception {

	open(appBaseURL);

	UICatalogue catalogue = clickOnCatalogue(0);

	assertEquals("Wrong catalogue selected", "Catalogue 1", catalogue.getName());
	assertEquals("Wrong catalogue selected", "First Catalogue", catalogue.getDesc());

	// test listing of documents
	ElementsCollection documentEntries = catalogue.getDocuments();
	documentEntries.shouldHaveSize(EXPECTED_DOCUMENTS_SIZE);
	assertEquals("Wrong catalogue content", "Document 1", documentEntries.get(0).getText());
	assertEquals("Wrong catalogue content", "Document 2", documentEntries.get(1).getText());
	assertEquals("Wrong catalogue content", "Document 3", documentEntries.get(2).getText());
	assertEquals("Wrong catalogue content", "Document 4", documentEntries.get(3).getText());
	assertEquals("Wrong catalogue content", "Document 5", documentEntries.get(4).getText());
	assertEquals("Wrong catalogue content", "Document with non-valid content", documentEntries.get(5).getText());
	assertEquals("Wrong catalogue content", "Document with non-valid model", documentEntries.get(6).getText());
	assertEquals("Wrong catalogue content", "Document with not-found content", documentEntries.get(7).getText());

}


@Test
public void catalogueDetailErrorTest() {

	// notice here we are not using the helper method to open a catalogue given that they don't expect errors
	open(appBaseURL);
	UICatalogues catalogues = UICatalogues.openCatalogues().shouldAppear();
	UICatalogue.shouldNotBeVisible();
	UIProblem.shouldNotBeVisible();

	List<UICatalogueEntry> catalogueEntries = catalogues.allCatalogueEntries();
	catalogueEntries.get(CATALOGUE_NOT_FOUND_INDEX).click();

	UIProblem problem = UIProblem.problem().shouldAppear();
	assertTrue(problem.getText().contains("Not Found"));
	UIContent.shouldNotBeVisible();

	catalogueEntries.get(0).click();
	problem.shouldDisappear();

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
