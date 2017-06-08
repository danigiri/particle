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

package cat.calidos.morfeu.model;

import java.util.List;


/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public interface Composite<T> {

/** @return all children of this composite object */
public List<T> children();


/** we may not have any (we are a composite instance, it's just that we don't have children at the moment) */
public boolean hasChildren();


/** @return child element at position i */
public T child(int i);


public T child(String name);


/** Clear all the children and return the old list */
public List<T> clearChildren();


/** add this child and return true if it was added or false if it was replacedt */
public boolean addChild(String key, T c);

}
