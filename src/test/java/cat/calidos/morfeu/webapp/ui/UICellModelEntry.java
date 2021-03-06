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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.NoSuchElementException;

import com.codeborne.selenide.SelenideElement;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class UICellModelEntry extends UIWidget<UICellModelEntry> {

private static final String ACTIVE = "cell-model-active";
private static final String SELECTED = "cell-model-selected";
private static final String POSITION_CLASS = "cell-model-position-";
private static final String THUMB = "src";

private UIModel model;
private Optional<UICellModelEntry> parent;
private int level;


public UICellModelEntry(SelenideElement e, UIModel model, Optional<UICellModelEntry> parent, int level) {

	super(e);
	
	this.model = model;
	this.parent = parent;
	this.level = level;

}


public UIWidget<UICellModelEntry> click() {
	
	element.click();
	
	return this;
	
}


public UIWidget<UICellModelEntry> clickOnArrow() {

	element.$(".toggle-children").click();

	return this;
	
}


public UICellModelEntry hover() {

	SelenideElement thumb = element.$(".cell-model-thumb");
	try {
		Thread.sleep(10);
	} catch (Exception e) {}
	thumb.hover();

	return this;

}


public UICellModelEntry select() {
	
	LinkedList<UICellModelEntry> path = new LinkedList<UICellModelEntry>();
	UICellModelEntry parentVisitor = this;
	while (parentVisitor!=null) {
		path.push(parentVisitor);
		parentVisitor = parentVisitor.parent().orElse(null);
	}
	model.pressKey(UIModel.MODEL_MODE_KEY);
	path.stream().forEachOrdered(cm -> model.pressKey(cm.position()+""));

	return this;
	
}


public UICellModelEntry activate() {

	if (this.isSelected()) {
		model.pressKey(UIModel.ACTIVATE_KEY);
	}

	return this;

}


public int position() {

	String class_ = class_();
	String[] classes = element.$(".cell-model-entry").attr(CLASS).split(" ");	// get the appropriate div
	int i = 0;
	int position = -1;
	while (position==-1 && i<classes.length) {
		String candidate = classes[i];
		if (candidate.startsWith(POSITION_CLASS)) {
			position = Integer.parseInt(candidate.substring(POSITION_CLASS.length(), candidate.length()));
		}
		i++;
	}
		
	return position;

}


public Optional<UICellModelEntry> parent() {
	return parent;
}


public String name() {
	return element.$(".cell-model-name").getText();
}


public String desc() {
	return element.$(".cell-model-desc").getText();
}


public String thumb() {
	return element.$(".cell-model-thumb").getAttribute(THUMB);
}


public List<UICellModelEntry> children() {	
	return element.$$(".tree-node-level-"+(level+1))
					.stream()
					.map(e -> new UICellModelEntry(e, model, Optional.of(this), level+1)).collect(Collectors.toList());
}


public UICellModelEntry child(String name) {
	return children().stream().filter((UICellModelEntry cme ) -> cme.name().equals(name)).findAny().get();
}


public boolean isCollapsed() {
	element.$(".tree-node-collapsed");	// wait for dom updates
	return element.attr("class").contains("tree-node-collapsed");
}


public boolean isExpanded() {
	element.$(".tree-node-expanded");	// wait for dom updates
	return element.attr("class").contains("tree-node-expanded");
}


public boolean isSelected() {
	return element.$(".cell-model-thumb").attr("class").contains(SELECTED);
}


public boolean isActive() {
	return element.$(".cell-model-thumb").attr("class").contains(ACTIVE);
}


public UICellData cellInfo() {
	
	if (!isActive()) {
		throw new NoSuchElementException("Trying the to get the info of an inactive cell model");
	}

	return new UICellData();	// at the moment there is only one info :)

}


/* (non-Javadoc)
* @see java.lang.Object#toString()
*//////////////////////////////////////////////////////////////////////////////
@Override
public String toString() {
	return "{"+name()+","+element+"}";
}


}
