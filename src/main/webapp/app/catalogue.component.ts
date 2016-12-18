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

import { Component, Input } from '@angular/core';

import { Catalogue } from './catalogue';
import { Document } from './document';
import { CatalogueService } from './catalogue.service';

@Component({
	moduleId: module.id,
	selector: 'catalogue',
	template: `
    <div id="catalogue" class="panel panel-default" *ngIf="catalogue">
        <div class="panel-heading">
          <h4 id="catalogue-name" class="panel-title">{{catalogue.name}}</h4>
        </div>
        <div id="catalogue-desc" class="panel-body">
            {{catalogue.desc}}
        </div>
        <div class="panel-body">
	        <div id="document-list" class="list-group">
                <a *ngFor="let d of catalogue.documents"
                    href="#" 
                    class="document-list-entry list-group-item" 
                    [class.active]="d === currentDocument"
                    (click)="selectdocument(d)">
                    {{d.name}} <span class="badge">{{d.kind}}</span>
                </a> 
        </div>
        </div>
      </div>
	`,
    styles:[`
    #catalogue {}
    #catalogue-name {}
    #catalogue-desc {}
    #document-list {}
    #document-list-entry {}
    `]
	})
	
	//`
export class CatalogueComponent {
	
catalogue: Catalogue;
currentDocument: Document;

constructor(private catalogueService : CatalogueService) {}

@Input() 
set selectedCatalogueUri(selectedCatalogueUri: string) {
    this.catalogueService.getCatalogue(selectedCatalogueUri)
    .subscribe(c => this.catalogue = c);
}

selectdocument(d: Document) {
    console.log("Selected document="+d.uri);
    this.currentDocument = d;
}


}