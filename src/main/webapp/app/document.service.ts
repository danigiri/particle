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


import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Subject }    from 'rxjs/Subject';
import 'rxjs/add/operator/map';

import { Document } from './document.class';


@Injectable()
export class DocumentService {
    
    private documentSource = new Subject<Document>();
    
    announcedDocument$ = this.documentSource.asObservable();
    document: Document;
    
    constructor(private http: Http) {}
    
    
    getDocument(uri:string) {
    
        console.log("DocumentService::getDocument("+uri+")"); 
        
        return this.http.get(uri)
            .map(response => response.json());

    }
    
    
    setDocument(d:Document) {
        console.log("DocumentService::setDocument("+d.name+")"); 
        
        this.documentSource.next(d);
    }
}