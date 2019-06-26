
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';	// new angular 5 http client

import {PipesModule } from '../pipes/pipes.module';

import {AttributeDataEditorComponent } from './attribute-data-editor.component';
import {CellEditorComponent } from './cell-editor/cell-editor.component';
import {CellHeaderComponent } from './cell-header.component';
import {PresentationComponent } from './presentation/presentation.component';

import {RemoteDataService } from '../services/remote-data.service';
import {RemoteObjectService } from '../services/remote-object.service';

import {Content, ContentJSON} from '../content.class';
import {Model, ModelJSON } from '../model.class';

import { EventService } from '../services/event.service';

@NgModule({
	declarations: [
					AttributeDataEditorComponent,
					CellEditorComponent,
					CellHeaderComponent,
					PresentationComponent
	],
	imports: [
				CommonModule,
				FormsModule,
				PipesModule
	],
	exports: [
					AttributeDataEditorComponent,
					CellEditorComponent,
					CellHeaderComponent,
					PresentationComponent
	],
	providers: [
				{
					provide: "ContentService",
					useFactory: (http: HttpClient) => (new RemoteObjectService<Content, ContentJSON>(http)),
					deps: [HttpClient]
				},
				EventService,
				{
					provide: "ModelService",
					useFactory: (http: HttpClient) => (new RemoteObjectService<Model, ModelJSON>(http)),
					deps: [HttpClient]
				},
				{
					provide: "RemoteDataService",
					useFactory: (http: HttpClient) => (new RemoteDataService(http)),
					deps: [HttpClient]
				}
	]

})

export class ComponentsModule {}

/*
 *	  Copyright 2019 Daniel Giribet
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