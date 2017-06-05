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

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class UIDocument {

public UIDocument() {
	// TODO Auto-generated constructor stub
}

//public void shouldAppear() {
//	$("#document-info").should(appear);
//}

public void shouldBeVisible() {
	$("#document-info").shouldBe(visible);
}


public static void shouldNotBeVisible() {
	$("#document-info").shouldNotBe(visible);
}


public String title() {
	return $("#document-name").getText();
	
}


public String desc() {
	return $("#document-desc").getText();
}

public boolean isValid() {
	return $("#document-valid").getText().equals("VALID");
}

}
