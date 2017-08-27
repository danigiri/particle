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

import java.net.URI;

import cat.calidos.morfeu.model.injection.ModelMetadataComponent;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class CellModel extends RemoteResource {

protected Type type;
protected boolean isSimple = true;
protected String presentation;


public CellModel(URI u, String name, String desc, Type type, String presentation) {
	//TODO: come up with a useful description or leave empty
	super(u, name, desc);
	
	this.type = type;
	this.presentation = presentation;
	
}


public boolean isSimple() {
	return isSimple;
}


public boolean isComplex() {
	return !isSimple();
}


public ComplexCellModel asComplex() {
	throw new ClassCastException("Tried to access simple cell model as complex ("+getName()+")");
}


public Type getType() {
	return type;
}


public String getPresentation() {
	return (presentation.equals(ModelMetadataComponent.UNDEFINED_VALUE)) ? type.getPresentation() : presentation;
}


/* (non-Javadoc)
* @see java.lang.Object#toString()
*//////////////////////////////////////////////////////////////////////////////
@Override
public String toString() {
	return "["+name+", ("+type+")]";
}




}
