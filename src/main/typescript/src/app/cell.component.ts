// CELL . COMPONENT . TS

import { Component, Input, OnInit, QueryList, ViewChild, ViewChildren } from "@angular/core";

import { FamilyMember } from "./family-member.interface";
import { Cell } from "./cell.class";
import { CellModel } from "./cell-model.class";

import { DropAreaComponent } from "./drop-area.component";
import { SelectableWidget } from "./selectable-widget.class";

import { CellActivateEvent } from "./events/cell-activate.event";
import { CellActivatedEvent } from "./events/cell-activated.event";
import { CellDeactivatedEvent } from "./events/cell-deactivated.event";
import { CellDragEvent } from "./events/cell-drag.event";
import { CellDropEvent } from "./events/cell-drop.event";
import { CellEditEvent } from "./events/cell-edit.event";
import { CellModelDeactivatedEvent } from "./events/cell-model-deactivated.event";
import { CellSelectEvent } from "./events/cell-select.event";
import { CellSelectionClearEvent } from "./events/cell-selection-clear.event";
import { CellModelActivatedEvent } from "./events/cell-model-activated.event";
import { EventService } from "./events/event.service";


@Component({
	moduleId: module.id,
	selector: "cell",
	template: `
			<ng-container [ngSwitch]="true">
				<ng-container *ngSwitchCase="cell.cellModel && cell.cellModel.presentation === 'WELL'">
					<img src="{{getCellPresentation()}}"
						class="img-fluid"
						[class.drag-active]="active"
						[class.drag-inactive]="!active"
						[class.cell-active]="active"
						[class.cell-selected]="selected"
						(mouseenter)="focusOn(cell)"
						(mouseleave)="focusOff(cell)"
						dnd-draggable
						[dragEnabled]="dragEnabled"
						(onDragEnd)="dragEnd(cell)"
						[dragData]="cellDragData()"
					/>
					<div id="{{cell.URI}}"
						class="well container-fluid show-grid cell-level-{{level}} rounded"
						[class.cell-active]="active"
						[class.cell-selected]="selected"
					>
						<!-- drop area to be able to add new rows to this well -->
						<div class="row">
							<div class="col">
								<drop-area *ngIf="parent" [parent]="cell" position="0"></drop-area>
							</div>
						</div>
						<cell *ngFor="let c of cell.children; let i=index"
								[cell]="c"
								[parent]="cell"
								[level]="level+1"
								[position]="i"
								[snippet]="snippet"
						></cell>
					</div>
					<!-- TODO: we probable want a drop area here to be able to add new wells -->
				</ng-container>

				<ng-container *ngSwitchCase="cell.cellModel && cell.cellModel.presentation === 'ROW-WELL'">
					<img src="{{getCellPresentation()}}"
						class="img-fluid"
						[class.drag-active]="active"
						[class.drag-inactive]="!active"
						[class.cell-active]="active"
						[class.cell-selected]="selected"
						(mouseenter)="focusOn(cell)"
						(mouseleave)="focusOff(cell)"
						dnd-draggable
						[dragEnabled]="dragEnabled"
						(onDragEnd)="dragEnd(cell)"
						[dragData]="cellDragData()"
					/>
					<div id="{{cell.URI}}"
						class="row-well row show-grid cell-level-{{level}} rounded"
						[class.cell-active]="active"
						[class.cell-selected]="selected"
					 >
						<!-- add a drop area here if we ever want to dynamically add new columns -->
						<cell *ngFor="let c of cell.children; let i=index"
									[cell]="c"
									[parent]="cell"
									[level]="level+1"
									[position]="i"
									[snippet]="snippet"
						></cell>
					</div>
					<!-- drop area to be able to add new row after this one -->
					<div class="row">
						<div class="col">
							<drop-area *ngIf="parent" [parent]="cell" [position]="position+1"></drop-area>
						</div>
					</div>
				</ng-container>

				<ng-container *ngSwitchCase="cell.cellModel && cell.cellModel.presentation === 'COL-WELL'">
					<!-- col-{{this.cell.columnFieldValue()}} cell-level-{{level}}" -->
					<div id="{{cell.URI}}"
						class="col-well col show-grid cell-level-{{level}} rounded"
						[class.cell-active]="active"
						[class.cell-selected]="selected"
					><!--COL-{{this.cell.columnFieldValue()}}-->
						<!-- drop area here to add anything at the beginning of the column -->
						<drop-area *ngIf="parent" [parent]="cell" [position]="0"></drop-area>
						<cell *ngFor="let c of cell.children; let i=index"
								[cell]="c"
								[parent]="cell"
								[level]="level+1"
								[position]="i"
								[snippet]="snippet"
						></cell>
					</div>
				</ng-container>

				<ng-container *ngSwitchCase="cell.cellModel && cell.cellModel.presentation.startsWith('CELL')">
						<!-- TODO: check the model and the content as well (counts, etc.) -->
						<img id="{{cell.URI}}" *ngIf="cellPresentationIsDefault()"
							class="cell cell-img cell-level-{{level}}"
							src="{{getCellPresentation()}}"
							[class.cell-active]="active"
							[class.cell-selected]="selected"
							(mouseenter)="focusOn(cell)"
							(mouseleave)="focusOff(cell)"
							dnd-draggable
							[dragEnabled]="dragEnabled"
							(onDragEnd)="dragEnd(cell)"
							[dragData]="cellDragData()"
							(dblclick)="doubleClick()"
						/>
						<!-- TODO: add innerhtml type this to inner html? -->
						<iframe *ngIf="!cellPresentationIsDefault()" 
							[src]="getCellPresentation() | safe: 'resourceUrl'"
							[class.cell-active]="active"
							[class.cell-selected]="selected"
							(mouseenter)="focusOn(cell)"
							(mouseleave)="focusOff(cell)"
							dnd-draggable
							[dragEnabled]="dragEnabled"
							(onDragEnd)="dragEnd(cell)"
							[dragData]="cellDragData()"
							(dblclick)="doubleClick()"
							
						></iframe>
						<!-- the position of the drop area is always where droped cells will go -->
						<drop-area *ngIf="parent" [parent]="parent" [position]="position+1"></drop-area>
				</ng-container>

			</ng-container>
	`,
	styles: [`
			.cell {}
			.well {
/*
				padding: 0;
				align-content: stretch;
 */
				}
			.row-well {
				background-color: rgba(255, 0, 0, .05);
			}
			.col-well {
				/*
				padding-right: 0;
				padding-left: 0;
				max-width: 100%;
				width: 100%;
				*/
			}
			.cell-img {
				/*
				width: 100%;
				height: auto;
				*/
				border: 3px solid transparent;	/* So when changed to highlighted, active, it doesn't move */
			}
			.show-grid	{
				background-color: rgba(200, 200, 200, .05);
				border: 3px solid #c8c8c8;
			}
			.cell-active {
				border: 3px solid #f00;
				border-radius: 5px;
			}
			.cell-selected {
				border: 3px dashed #00f;
				border-radius: 5px;
			}
			.cell-dragged {
				opacity: .2;
			}
			.drag-inactive {
				opacity: .6;
			}
			.drag-active {
				opacity: .9;
			}
			.cell-col-1 {
				max-width: 8.3%;
				width: 8.3%;
			}
			.cell-col-2 {
				max-width: 16.6%;
			width: 16.6%;
			}
			 .cell-col-3 {
				 max-width: 25%;
			 width: 25%;
			 }
			 .cell-col-4 {
			 max-width: 33%;
			 width: 33%;
			 }
			 .cell-col-6 {
				 max-width: 41.6%;
			 width: 41.6%;
			 }
			 .cell-col-7 {
				 max-width: 58.3%;
			 width: 58.3%;
			 }
			 .cell-col-8 {
				 width: 66%;
			 }
			 .cell-col-9 {
				 max-width: 75%;
			 }
			 .cell-col-10 {
				 max-width: 75%;
			 }
			 .cell-col-11 {
				 max-width: 91.6%;
			 }
			.cell-col-12 {
				max-width: 100%;
			}
			.cell-level-0 {}
			.cell-level-1 {}
			.cell-level-2 {}
			.cell-level-3 {}
			.cell-level-4 {}
			.cell-level-5 {}
			.cell-level-6 {}
			.cell-level-7 {}
			.cell-level-8 {}
			.cell-level-9 {}
			.cell-level-10 {}
			.cell-level-11 {}
			.cell-level-12 {}
			.cell-level-13 {}
			.cell-level-14 {}
			.cell-level-15 {}
			.cell-level-16 {}
			.cell-level-17 {}
			.cell-level-18 {}
`],
	//
// encapsulation: ViewEncapsulation.Emulated,
})
// `

export class CellComponent extends SelectableWidget implements OnInit {

@Input() parent: FamilyMember;
@Input() cell: Cell;
@Input() snippet?: boolean;
@Input() level: number;
@Input() position: number;

active = false;
dragEnabled = false;

@ViewChildren(CellComponent) children: QueryList<CellComponent>;
@ViewChild(DropAreaComponent) dropArea: DropAreaComponent;		// we only have one of those!!!


constructor(eventService: EventService) {
	super(eventService);
}


ngOnInit() {

	// console.log("[UI] CellComponent::ngOnInit()");

	// Drop a cell to a position under this cell
	this.subscribe(this.events.service.of( CellDropEvent )
			.filter(dc => dc.newParent && dc.newParent===this.cell)
			.subscribe( dc => {
				console.log("-> cell comp gets dropcell event moving '"+dc.cell.name+"' to	"
							+this.cell.URI+" at position ("
							+dc.newPosition+")'");
				this.adoptCellAtPosition(dc.cell, dc.newPosition);
	}));

	// A cell model was deactivated that is compatible with this cell
	this.subscribe(this.events.service.of( CellModelDeactivatedEvent )
			.filter(d => d.cellModel && this.isCompatibleWith(d.cellModel)) // //
			.subscribe( d => {
				// console.log("-> cell comp gets cellmodel deactivated event for '"+d.cellModel.name+"'");
				this.becomeInactive(this.cell);
	}));

	// a cell model activated that is compatible with this cell
	this.subscribe(this.events.service.of( CellModelActivatedEvent )
			.filter( a => a.cellModel && this.isCompatibleWith(a.cellModel))
			.subscribe( a => {
				console.log("-> cell comp gets cellmodel activated event for '"+a.cellModel.name+"'"); //
				this.becomeActive(this.cell);
	}));

	// an outsider component (like a keyboard shortcut) wants to activate this selected cell
	this.subscribe(this.events.service.of( CellActivateEvent )
			.filter(a => this.selected && this.canBeActivated())
			.subscribe( a => {
				console.log("-> cell comp gets cell activate event and proceeds to focus :)");
				// FIXMWE: this allows for multiple activations when conflicting with rollover
				this.focusOn(this.cell);
	}));

	// A different cell was activated and we are active at this moment
	this.subscribe(this.events.service.of( CellActivatedEvent )
			.filter(a => this.active && a.cell!==this.cell)
			.subscribe( a => {
				console.log("-> cell comp gets cell activated event from other cell, we were active, clear");
				this.becomeInactive(this.cell);
	}));

	// External component (like a keyboard shortcut) wants to drag this cell somewhere
	this.subscribe(this.events.service.of( CellDragEvent )
			.filter(a => this.active)
			.subscribe( a => {
				console.log("-> cell comp gets cell drag event and will try to drop to a selection :)");
				this.events.service.publish(new CellDropEvent(this.cell));
	}));

	// Want to edit this cell
	this.subscribe(this.events.service.of( CellEditEvent )
				.filter(edit => !edit.cell && this.isEditable())
				.subscribe( edit => {
					console.log("-> cell comp gets cell edit event and will try to edit :)");
					this.events.service.publish(new CellEditEvent(this.cell));
		}));

}


// we focus on this cell, we want to notify all listeners interested in this type of cell and highlight it
focusOn(cell: Cell) {

	// console.log("[UI] CellComponent::focusOn()");
	this.events.service.publish(new CellActivatedEvent(cell));
	this.becomeActive(cell);
	// TODO: OPTIMISATION we could precalculate the event receptor and do a O(k) if needed
	// to make that happen we can associate the cell-model.class with the component (view) and just do it
	// without events

}


// notify all interested in this type of cell that we do not have the focus any longer, remove highlight
focusOff(cell: Cell) {

	// console.log("[UI] CellComponent::focusOff()");
	this.becomeInactive(cell);
	this.events.service.publish(new CellDeactivatedEvent(cell));

}


// we drag outside any interesting area, we remove focus
dragEnd(cell: Cell) {

	console.log("[UI] CellComponent::dragEnd()");
	// this.isBeingDragged = false;
	this.focusOff(cell);
}


// the drop-area is sending us a cell to adopt
adoptCellAtPosition(newCell: Cell, position: number) {

	console.log("[UI] CellComponent::adoptCellAtPosition("+position+")");
	// deactivate based on old location
	this.events.service.publish(new CellDeactivatedEvent(newCell));
	// must be an orphan before adopting
	if (newCell.parent) {
		newCell.parent.remove(newCell);
	}
	this.cell.adopt(newCell, position);

}


// UI method to highlight the cell
becomeActive(cell: Cell) {

	// console.log("[UI] CellComponent::becomeActive("+cell.URI+")");
	this.active = true;
	this.dragEnabled = true;
	// once we become active, selections are cleared, for instance to select the drag and drop destination
	this.events.service.publish(new CellSelectionClearEvent());

}


// UI method to no longer be highlighted
becomeInactive(cell: Cell) {

	// console.log("[UI] CellComponent::becomeInactive("+cell.URI+")");
	this.active = false;
	this.dragEnabled = false;

}


// are we compatible with this element?
isCompatibleWith(element: FamilyMember): boolean {
	return this.cell.matches(element);
}


// it can be activated (for drag and drop, etc) if it's not a well
// if it's a snippet, we can always activate it, so it can be cloned
canBeActivated(): boolean {
	return !this.cell.cellModel.presentation.includes("COL-WELL") || this.snippet;
}


select(position: number) {

	if (position===this.position) {

		// if we were activated we deactivate ourselves and become selectable again
		if (this.active) {
			this.becomeInactive(this.cell);
		}

		// we were waiting for a selection and we've matched the position, so we select ourselves
		// and unsubscribe from selection as we are not eligible anymore
		console.log("[UI] CellComponent::select("+this.cell.name+"("+this.position+"))");
		this.selected = true;
		this.unsubscribeFromSelection();

		// We temporarly unsubscribe from clear, send a clear event and re-subscribe
		// This means we are the only ones selected now (previous parent will be unselected, for instance)
		this.unsubscribeFromSelectionClear();
		this.events.service.publish(new CellSelectionClearEvent());
		this.subscribeToSelectionClear();

		// now our children are eligible to be selected
		this.children.forEach(c => c.subscribeToSelection());

		// if we have drop areas, they are also selectable now with the appropriate key shortcut
		// it's non trivial to get a list of dropAreas, in the case of wells, we have one drop area and the
		// rest are from our children cells, so we subscribe our explicit first and then the rest, if we have
		// children cells, that is.
		// Diagram:
		// if cell then
		//	  <img>
		//	  <drop-area>  (explicit in the template)
		// else (well)
		//	<cell>
		//	<drop-area> (explicit in the template)
		//		<foreach cell></cell>	(deep in each one there is the drop-area)
		//	</cell>
		// endif
		this.dropArea.subscribeToSelection();
		this.children.forEach(c => c.dropArea.subscribeToSelection());

	} else if (this.cell.parent && position>=this.cell.parent.childrenCount()) {
		console.log("[UI] CellComponent::select(out of bounds)");
	} else {
		this.clearSelection();	 // out of bounds, sorry, clear
	}

}


/** This cell now can be selected or can bubble down selections, and can also be cleared */
subscribeToSelection() {

	this.selectionSubscription = this.subscribe(this.events.service.of( CellSelectEvent )
				.subscribe( cs => this.select(cs.position) )
	);
	this.subscribeToSelectionClear();  // if we are selectable we are also clearable

}


cellPresentationIsDefault(): boolean {
	return this.cell.cellModel.getPresentationType()===CellModel.DEFAULT_PRESENTATION_TYPE;
}

getCellPresentation() {
	return this.cell.getPresentation();
}


private isEditable(): boolean {
	return this.active && !this.cell.cellModel.presentation.includes("COL-WELL") && !this.snippet;
}


// data that is being dragged (and potentially dropped)
private cellDragData() {

	let cellDragData: Cell;
	if (this.snippet) {	// If we are cloning, we deep clone the cell and remove the parent ref
						// as cloning will also clone the reference to the parent.
						// Then it's effectively an orphan when the adoption takes place
		cellDragData = this.cell.deepClone();
		delete cellDragData["parent"];
	} else {
		cellDragData = this.cell;
	}

	return cellDragData;

}

private doubleClick() {
	this.events.service.publish(new CellEditEvent(this.cell));
}

}

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
