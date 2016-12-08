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


import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';

import { Catalogue } from './catalogue';

@Injectable()
export class CatalogueService {
    
    catalogues: Observable<Catalogue[]>;
    private url: string;
 
    constructor(private http: Http) {
        this.url = '/test-resources/catalogues.json';
      }
    
    getAll() : Observable<Catalogue[]> {
        console.log("CatalogueServic::getAll"); 
        return this.http.get(this.url)
            .map(response => response.json());
    }
}