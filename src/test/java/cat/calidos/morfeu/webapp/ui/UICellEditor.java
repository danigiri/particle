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

package cat.calidos.morfeu.webapp.ui;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.Optional;

import com.codeborne.selenide.SelenideElement;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class UICellEditor extends UIWidget<UICellEditor> {

private UIContent content;


public UICellEditor(UIContent content) {

	super($("#cell-editor"));

	this.content = content;

}


public static void shouldNotBeVisible() {
	$("#cell-editor").shouldNotBe(visible);
}


public UICellEditor clickSave() {

	$("#cell-editor-save-button").click();	// can access from global id
	
	return this;

}


public UICellEditor clickDiscard() {

	$("#cell-editor-discard-button").click();	// can access from global id
	
	return this;

}


public boolean isRemoveValueVisible() {
	
	return $("#cell-editor-remove-value-button").exists();
	
}


public UICellEditor clickRemoveValue() {
	
	$("#cell-editor-remove-value-button").click();
	
	return this;
	
}


public boolean isCreateValueVisible() {
	
	return $("#cell-editor-create-value-button").exists();
	
}


public UICellEditor clickCreateValue() {
	
	$("#cell-editor-create-value-button").click();
	
	return this;
	
}


public Optional<String> value() {

		SelenideElement valueElement = element.$(".cell-editor-value");

		return valueElement.exists() ? Optional.of(valueElement.getValue()) :  Optional.empty();

}


public UICellEditor enterText(String value) {

	if (isCreateValueVisible()) {
		throw new UnsupportedOperationException("Cannot set the value when text field is not visible");
	}

	// cannot get selenide to select the element (textarea is missing in the setValue method)
	// so we send the keys which works
	// content.pressTAB(); (do not seem to need it in the latest selenium and selenide versions
	int l = value().get().length();
	for (int i=0; i<l; i++) { 
		pressBackspace(); 
	}
	content.pressKey(value);

	return this;
	
}


public UICellData cellData() {
	return new UICellData(element);	// cell data and cell editor are very similar
}

}
