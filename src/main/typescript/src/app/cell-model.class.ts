/*
 *	  Copyright 2018 Daniel Giribet
 *
 *	 Licensed under the Apache License, Version 2.0 (the "License");
 *	 you may not use this file except in compliance with the License.
 *	 You may obtain a copy of the License at
 *
 *		 http://www.apache.org/licenses/LICENSE-2.0
 *
 *	 Unless required by applicable law or agreed to in writing, software
 *	 distributed under the License is distributed on an "AS IS" BASIS,
 *	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	 See the License for the specific language governing permissions and
 *	 limitations under the License.
 */

import { Cell } from "./cell.class";
import { FamilyMember } from "./family-member.interface";
import { NameValue } from "./name-value.interface";
import { CellType } from "./cell-type.class";

import { PresentationParser } from "./presentation-parser.class";

// //// COMPONENT STUFF										////
// //// PRESENT HERE DUE TO LIMITATIONS IN TREE COMPONENT	////
import { CellModelComponent } from "./cell-model.component";
// ////														////

export class CellModel implements NameValue, FamilyMember {

static readonly DEFAULT_EMPTY_VALUE = "";
	
id: string;
isExpanded: boolean;

attributes?: CellModel[];
children: CellModel[];
isReference: boolean;
referenceURI?: string;

// //// COMPONENT STUFF										////
// to circumvent limitations of the angular tree, we establish a relationship with the cell model component //
component: CellModelComponent;
// ////														////

constructor(public schema: number, 
			public URI: string, 
			public name: string, 
			public desc: string, 
			public presentation: string,
			public cellPresentation: string,
			public thumb: string,
			public isSimple: boolean, 
			public type_: CellType,
			public minOccurs: number,
			public isAttribute?: boolean,
			public maxOccurs?: number,
			public defaultValue?: string,
			public identifier?: CellModel
			) {
	this.init();
}


// there are values specific to comply wit the treemodel model, we set them explicitly here 
init() {

	this.id = this.URI;	 // this is guaranteed to be unique 
	this.isExpanded = true;

}


setComponent(c: CellModelComponent) {
	this.component = c;
}


getRawPresentation() {
	return (this.cellPresentation=='DEFAULT') ? "assets/images/cell.svg" : this.cellPresentation;
}


getPresentation() {
	
	let finalPres = this.getRawPresentation();

	if (finalPres.includes("$")) {
			finalPres = PresentationParser.expand(finalPres, "$NAME", this.name);
	}

	return finalPres;

}


/** Mutates the cellModel so any references point to  the original cell model**/
normaliseReferencesWith(rootCellModels: CellModel[]) {
	
	if (this.isReference) {
		let reference:CellModel = this.findCellModelWithURI(rootCellModels, this.referenceURI);
		if (!reference) {
			console.error("Could not find cellModel of reference cellModel:%s", this.name);
		}
		
		// we take the philosophy of completing the cellmodel reference with the missing data (children)
		// we keep rest of the cell model information (like the name, which can be different)
		this.children = reference.children;
		
	} else {
		if (this.children) {
			this.children.forEach(c => c.normaliseReferencesWith(rootCellModels));
		}
		if (this.attributes) {
			this.attributes.forEach(a => a.normaliseReferencesWith(rootCellModels));
		}
	}
	
}


canGenerateNewCell(): boolean {
	// not dragging WELL-type cell models for the moment, as it is quite a complex use case, so we only allow
	// cell models that do not have any children
	// trying to drag WELL-type cell models
	// return this.childrenCount()==0;
	return true;
}


/** Generate a new cell from this model, using defaults if available */
generateCell(): Cell {

	const cellURI = "/"+this.getAdoptionName()+"(0)";  // this is will be changed on adoption
	const desc = "";								   // empty description for the moment
	let newCell: Cell = new Cell(this.schema,
									cellURI,
									this.getAdoptionName(),
									desc,
									this.getAdoptionURI(),
									this.isSimple);
	if (this.defaultValue) {
		newCell.value = this.defaultValue;
	}

	newCell.cellModel = this;						// we associate the cell model straightaway, easy peasy =)

	if (this.attributes) {							// now we set the attributes when we have defaults
		newCell.attributes = this.attributes.filter(a => a.defaultValue)
												.map(a => this.generateAttributeFrom(a));
	}

	return newCell;

}


/** return a deep clone of this cell model, it includes all children and so forth */
deepClone(): CellModel {
	return CellModel.fromJSON(this.toJSON());
}


// either return this or one of the descendants, avoids following references, can return undefined 
findCellModel(uri: string): CellModel {
	return this.findCellModelWithURI(this, uri);
}


//// FamilyMember ////

getURI(): string {
	return this.URI;
}


getAdoptionName(): string {
	return this.name;
}


getAdoptionURI(): string {
	return this.URI;
}


matches(e: FamilyMember): boolean {
	return this.getAdoptionName()==e.getAdoptionName() && this.getAdoptionURI()==e.getAdoptionURI();
}


canAdopt(element: FamilyMember): boolean {
	return this.children.some(c => c.matches(element));
}


childrenCount(): number {
	return this.children ? this.children.length : 0;
}


getParent(): FamilyMember {
	return undefined;	// TODO: we do not need to setup the parent yet
}


equals(m: FamilyMember) {
	return this.getURI()==m.getURI();
}


//// SerialisableToJSON ////

toJSON(): CellModelJSON {

	let serialisedCellModel: CellModelJSON = Object.assign({}, this);

	if (serialisedCellModel.identifier) {
		serialisedCellModel.identifier = this.identifier.name;	// we serialise to the (attribute) name
	}

	if (this.attributes) {
		serialisedCellModel.attributes = this.attributes.map(a => a.toJSON());
	}
	if (this.children) {
		serialisedCellModel.children = this.children.map(c => c.toJSON());
	}

	return serialisedCellModel;

}


static fromJSON(json: CellModelJSON|string): CellModel {

	if (typeof json === 'string') {

		return JSON.parse(json, CellModel.reviver);

	} else {

		let cellModel = Object.create(CellModel.prototype);
		cellModel = Object.assign(cellModel, json); // add parsed attributes like schema, URI, name...
		cellModel.init();							// make sure we have all attributes ok

		if (json.attributes) {
			cellModel = Object.assign(cellModel,
										{attributes: json.attributes.map(a => CellModel.fromJSON(a))});
		}

		// handle the identifier if we have one defined, so we turn it into a reference to the attribute
		if (cellModel.identifier) {
			cellModel.identifier = cellModel.attributes.find(a => a.name==cellModel.identifier);
			if (cellModel.identifier==undefined) {
				console.error("Wrong identifier reference in %s", cellModel.name);
			}
		}

		if (json.children) {
			cellModel = Object.assign(cellModel, 
									  {children: json.children.map(c => CellModel.fromJSON(c))});
		} else {
			cellModel = Object.assign(cellModel, {children: []});  // empty as the Tree class requires it
		}
	
		return cellModel;

	}
}


static reviver(key: string, value: any): any {
	return key === "" ? CellModel.fromJSON(value) : value;
}

//// SerialisableToJSON [end] ////


private generateAttributeFrom(attribute: CellModel): Cell {

	const attrURI = "/"+this.getAdoptionName()+"(0)@"+attribute.getAdoptionName(); // be changed on adoption
	const desc = "";															  // empty description
	const value = (attribute.defaultValue) ? attribute.defaultValue : ""; 
	let newCell: Cell = new Cell(attribute.schema,
									attrURI,
									attribute.getAdoptionName(),
									desc,
									attribute.getAdoptionURI(),
									attribute.isSimple);	// should always be true

	if (attribute.defaultValue) {				   // sanity check, as we only generate		   
		newCell.value = attribute.defaultValue;	   // attributes for defaults for now
	}

	newCell.cellModel = attribute; // associate the cell model straightaway, yo! =)

	return newCell;

}


// given a cell model URI, look for it in a cell model hierarchy, avoids following references
private findCellModelWithURI(cellModels: CellModel[] | CellModel, uri: string): CellModel {

	let cellModel: CellModel;
	let pending: CellModel[] = [];
	if (cellModels instanceof Array) {
		cellModels.forEach(cm => pending.push(cm));
	} else {
		pending.push(cellModels);
	}

	while (!cellModel && pending.length>0) {

		let currentCellModel: CellModel = pending.pop();
		if (currentCellModel.URI==uri) {
			cellModel = currentCellModel;
		} else {
			// Only do a recursive call if current cellModel is not what we look for *and* not a reference.
			// This is to avoid infinite loops in nested structures, a nested reference to a parent
			// will necessarily be a reference cellModel, therefore do not add its children to be processed
			if (!currentCellModel.isReference && currentCellModel.children) {
				currentCellModel.children.forEach(cm => pending.push(cm));
			}
		}
	}

	return cellModel;

}


}


export interface CellModelJSON {

schema: number;
URI: string;
name: string;
desc: string;
presentation: string;
cellPresentation: string;
thumb: string;
isSimple: boolean;
isReference: boolean;
type_: CellType;
minOccurs: number;
maxOccurs?: number;
isAttribute?: boolean;
defaultValue?: string;
identifier?: string | CellModel;	// coming from the JSON it will be a string, coming from an object it will
									// be an reference to the attribute that is the identifier
attributes?: CellModelJSON[];
children?: CellModelJSON[];
referenceURI?: string;

}


