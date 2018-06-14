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

import { Component, AfterViewInit, Inject, Input, OnInit } from "@angular/core";
import { Observable, Subject, Subscription } from "rxjs";

import { RemoteDataService } from "../services/remote-data.service";
import { RemoteObjectService } from "../services/remote-object.service";

import { CellDocument, CellDocumentJSON } from "../cell-document.class";
import { Content, ContentJSON } from "../content.class";

import { KeyListenerWidget } from "../key-listener-widget.class";

import { SnippetContentRequestEvent } from "../events/snippet-content-request.event";
import { SnippetDocumentRequestEvent } from "../events/snippet-document-request.event";
import { StatusEvent } from "../events/status.event";
import { EventService } from "../events/event.service";

@Component({
	moduleId: module.id,
	selector: "snippets",
	template: `
	    <ul class="list-group">
	        <li *ngFor="let snippet of snippets | async">{{snippet.name}}</li>
	    </ul>
	`,
	styles:[`

	`]
})

export class SnippetsComponent extends KeyListenerWidget implements AfterViewInit {

@Input() snippetStubs: CellDocument[];	 // stubs that come from the catalogue

_snippets: Array<CellDocument>;
_snippetsSubject: Subject<Array<CellDocument>>;
snippets: Observable<Array<CellDocument>>;

protected snippetDocumentSubs: Subscription;

	
constructor(eventService: EventService,
            @Inject("RemoteJSONDataService") private snippetDocumentService: RemoteDataService,
            @Inject("SnippetContentService") private snippetContentService: RemoteObjectService<Content, ContentJSON> 
			) {
	super(eventService);
}


ngAfterViewInit() {
	Promise.resolve(null).then(() => this.fetchSnippets()); 
}


// fetch all snippet documents
private fetchSnippets() {

	if (this.snippetStubs.length>0) {
	    this._snippets = [];
	    this._snippetsSubject = new Subject();
	    this.snippets = this._snippetsSubject.asObservable();
		this.events.service.publish(new StatusEvent("Fetching snippets"));
		this.snippetDocumentSubs = this.subscribe(this.events.service.of(SnippetDocumentRequestEvent)
		        .subscribe(
		            req => this.loadSnippetDocument(this.snippetStubs[req.index], req.index)
		));
		this.events.service.publish(new SnippetDocumentRequestEvent(0));
    }

}


// load all snippet documents which in turn will be used to fetch all snippets
private loadSnippetDocument(snippetStub: CellDocument, index: number) {
   
	let uri = "/morfeu/"+snippetStub.uri;
	console.log("Loading snippet document %s", uri);
	this.snippetDocumentService.get<CellDocument>(uri).subscribe(
	        snippetDoc => this.loadSnippetContent(snippetDoc, index),
			error => this.events.problem(error.message),
			() => {
			}
	);

}

// }

private loadSnippetContent(snippet: CellDocument, index: number) {

	let snippetURI = "/morfeu/dyn/snippets/"+snippet.contentURI+"?model="+snippet.modelURI;
	console.log("Loading snippet content %s", snippetURI);
	this.snippetContentService.get(snippetURI, Content).subscribe( (snippetContent:Content) => {
	    this._snippets.push(snippet);
	    this._snippetsSubject.next(this._snippets);
	},
	error => this.events.problem(error.message),    // error is of the type HttpErrorResponse
	() => {
	    
        if (index<this.snippetStubs.length-1) {
                this.events.service.publish(new SnippetDocumentRequestEvent(index+1));
        } else {
                this.unsubscribe(this.snippetDocumentSubs);
        this.events.ok();   // this means we don't see errors for that long unfortunately
        }
	});

}


commandPressedCallback(command: string) {}


numberPressedCallback(num: number) {}

}


