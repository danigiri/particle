/*
 *    Copyright 2016 Daniel Giribet
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class CatalogueUITest {

private static final int CATALOGUE_SIZE = 3;
private static final String URL_PROPERTY = "app-url";
private static final String DEFAULT_URL = "http://localhost:8080/morfeu";
private static final String BROWSER_PROPERTY = "selenide.browser";
private static final String DEFAULT_BROWSER = "chrome";
private static final String DRIVER_LOCATION_PROPERTY = "webdriver.chrome.driver";
private static final String DEFAULT_DRIVER_LOCATION = "/Applications/chromedriver";

private static String appBaseURL;
private static WebDriver driver;

@BeforeClass
public static void setUpClass() throws Exception {
	

	defineSystemVariable(BROWSER_PROPERTY, DEFAULT_BROWSER);
	defineSystemVariable(DRIVER_LOCATION_PROPERTY, DEFAULT_DRIVER_LOCATION);

	appBaseURL = defineSystemVariable(URL_PROPERTY, DEFAULT_URL);

	driver = new ChromeDriver();
}


@Test
public void catalogueListTest() throws Exception {

	// catalogue list appears and has three entries
	open(appBaseURL);

	
	ElementsCollection catalogueEntries = UICatalogues.openCatalogues()
													  .shouldAppear()
													  .getCatalogueEntries();
	assertEquals("Wrong catalogue content", "Catalogue 1", catalogueEntries.get(0).getText());
	assertEquals("Wrong catalogue content", "Catalogue 2", catalogueEntries.get(1).getText());
	assertEquals("Wrong catalogue content", "Catalogue not found", catalogueEntries.get(2).getText());
	catalogueEntries.shouldHaveSize(CATALOGUE_SIZE);
	UIProblem.shouldNotBeVisible();
	
}



@Test
public void catalogueDetailTest() throws Exception {

	// click on catalogue list entry and it appears
	UICatalogues catalogues = UICatalogues.openCatalogues()
										  .shouldAppear();
	UICatalogue.shouldNotBeVisible();
	UIProblem.shouldNotBeVisible();
	$("#document-list").shouldNotBe(visible);
	
	UICatalogue catalogue = catalogues.clickOn(0);
	catalogue.shouldAppear();

	$("#document-list").should(appear);

	assertEquals("Wrong catalogue selected", "Catalogue 1", catalogue.getName());
	assertEquals("Wrong catalogue selected", "First Catalogue", catalogue.getDesc());
	
	// test listing of documents
	ElementsCollection documentEntries = $$(".document-list-entry");
	documentEntries.shouldHaveSize(4);
	assertEquals("Wrong catalogue content", "Document 1\nxml", documentEntries.get(0).getText());
	assertEquals("Wrong catalogue content", "Document with non-valid content\nxml", documentEntries.get(1).getText());
	assertEquals("Wrong catalogue content", "Document with non-valid model\nxml", documentEntries.get(2).getText());
	assertEquals("Wrong catalogue content", "Document with not-found content\nyaml", documentEntries.get(3).getText());
	
}


@Test
public void catalogueDetailErrorTest() {
	
	open(appBaseURL);
	$("#catalogue-list").should(appear);
	$("#catalogue").shouldNotBe(visible);
	UIProblem.shouldNotBeVisible();
	
	ElementsCollection catalogueEntries = $$(".catalogue-list-entry");
	catalogueEntries.get(2).click();

	$("#problem").should(appear);
	assertTrue($("#problem").getText().contains("Not Found"));

	catalogueEntries.get(0).click();
	$("#problem").should(disappear);
	
}

@AfterClass
public static void tearDownClass() {
	
	//Close the browser
	if (driver!=null) {
		driver.quit();
	}
	
}


private static String defineSystemVariable(String systemProperty, String defaultValue) {

	String value = System.getProperty(systemProperty);
	if (value==null) {
		value = defaultValue;
		System.setProperty(systemProperty, defaultValue);
	}
	return value;
}

}
