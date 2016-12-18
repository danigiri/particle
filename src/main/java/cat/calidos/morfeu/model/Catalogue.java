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

package cat.calidos.morfeu.model;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////
public class Catalogue implements Locatable {

	protected URI uri;
	protected List<Document> documents;
	protected List<Cell> templatePartikles;
	
	@Inject public Catalogue(String catalogueJson) {
	}
	
	public URI getUri() {
		return uri;
	}

}