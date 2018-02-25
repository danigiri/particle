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

import { Component, Input, OnInit } from "@angular/core";

import { Cell } from "../cell.class";
import { CellModel } from "../cell-model.class";

@Component({
    moduleId: module.id,
    selector: 'attribute-data-editor',
    template: `
        
        <div *ngIf="hasValue_()" class="attribute-data attribute-data-editor input-group-sm mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text attribute-data-name" id="">{{cellModel.name}}<ng-container *ngIf="cellModel.minOccurs==1">*</ng-container>:</span>
          </div>
        <input type="text" 
                class="form-control attribute-data-value" 
                attr.aria-label="{{cellModel.name}}" 
                attr.aria-describedby="{{cellModel.desc}}" 
                [(ngModel)]="this.value" />
</div>

        

    `,
    styles:[`
                .attribute-data {}
                .attribute-data-editor {}
                .attribute-data-name {}
                .attribute-data-value {}
    `]
})


export class AttributeDataEditorComponent {

@Input() cellModel: CellModel;
@Input() parentCell: Cell;

//do we have a value to show?
private hasValue_(): boolean {
 return this.parentCell && this.parentCell.attributes 
         && this.parentCell.attributes.find(a => a.name==this.cellModel.name)!=undefined;
}


get value(): string {
 return this.parentCell.attributes.find(a=> a.name==this.cellModel.name).value;
}


set value(v: string) {
   let attributeCell = this.parentCell.attributes.find(a=> a.name==this.cellModel.name);
   attributeCell.value = v;
}


}