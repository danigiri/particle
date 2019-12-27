// PRESENTATION . COMPONENT . TS (NOT USED AT THE MOMENT)

import {AfterViewInit, Component, Inject, Input, OnDestroy} from '@angular/core';
import {Observable, Subject} from 'rxjs';


import {RemoteDataService} from '../../services/remote-data.service';

import {Cell} from '../../cell.class';
import {CellModel} from '../../cell-model.class';

import {EventListener} from '../../events/event-listener.class';
import {CellChangedEvent} from '../../events/cell-changed.event';
import {EventService} from '../../services/event.service';

@Component({
	moduleId: module.id,
	selector: "presentation",
	template: `
		<!-- TODO: add inner html type? -->
		<iframe *ngIf="this.getPresentationType()==='IFRAME'"
			class="cell cell-html"
			[src]="getPresentation() | safe: 'resourceUrl' "
		></iframe>
		<div class="cell cell-html" *ngIf="this.getPresentationType()==='HTML'">
			<div [innerHTML]="html$ | async | safe: 'html'"></div>
		</div>
		
	`,
})
// sets the ui to be too slow as the iframe blocks rendering
//	changeDetection: ChangeDetectionStrategy.OnPush

export class PresentationComponent extends EventListener implements AfterViewInit, OnDestroy {

private html_updates = 0;

// if showing a cell with values or we are showing a cellmodel
@Input() cell?: Cell;
@Input() cellModel?: CellModel;

presentation: String;
html$?: Subject<String>;


constructor(eventService: EventService, @Inject("RemoteDataService") private presentationService: RemoteDataService) {
	super(eventService);
	//console.debug('PresentationComponent::constructor() - %s', this.cell ? this.cell.getURI() : '');
}


ngAfterViewInit() {

	//console.debug('PresentationComponent::ngAfterViewInit() - %s', this.cell ? this.cell.getURI() : '');

	if (this.getPresentationType()==='HTML') {

		//console.debug('PresentationComponent::ngAfterViewInit() HTML pres (%s)', this.getPresentationMethod());
		this.html$ = new Subject();
		this.updateHTMLPresentation();	// update at least once to show default model preview or also the
										// first time for the cell

		// we update from events if we are showing inner html and we have cell content to present  
		if (this.cell) {

		// FIXME: is there a potential race condition where this
		// method calls pile up on each other on the get text?
			this.subscribe(this.events.service.of<CellChangedEvent>('CellChangedEvent')
					.subscribe(() => this.updateHTMLPresentation())
			);
		}
	}

}


getPresentationType(): string {

	const cellModel = this.cell === undefined ? this.cellModel : this.cell.cellModel;

	return cellModel.getPresentationType();

}


private getPresentation(): string {
	return this.cell===undefined ? this.cellModel.getPresentation() : this.cell.getPresentation();
}


private getPresentationAllContent(): string {
	return this.cell===undefined ? this.cellModel.getPresentationAllContent() : this.cell.getPresentationAllContent();
}

private getPresentationMethod(): string {
	return this.cell===undefined ? this.cellModel.cellPresentationMethod : this.cell.cellModel.cellPresentationMethod;
}


private updateHTMLPresentation() {

	const presentationURL = this.getPresentation(); //'/dyn/preview/html/aaa;color=ff00ff';

	let presentationContent: Observable<String>;
	if (this.getPresentationMethod()=='POST') {
		const allPresentationContent = this.getPresentationAllContent();
		presentationContent = this.presentationService.postText(presentationURL, allPresentationContent);
	} else {
		presentationContent = this.presentationService.getText(presentationURL);
	}
	presentationContent.subscribe(
			innnerHTML => {
				Promise.resolve(null).then(() => {
							console.debug('[%i] P %s', this.html_updates, presentationURL);
							this.html$.next(innnerHTML);
				});
			},
			error => console.error('Could not get HTML presentation at %s', presentationURL)
	);

}


}

/*
 *    Copyright 2019 Daniel Giribet
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
