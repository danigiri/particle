// TYPE COMPONENT . JAVA

package cat.calidos.morfeu.model.injection;

import java.net.URI;

import javax.annotation.Nullable;
import javax.inject.Named;

import com.sun.xml.xsom.XSType;

import dagger.BindsInstance;
import dagger.Component;

import cat.calidos.morfeu.model.Type;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Component(modules=TypeModule.class)
public interface TypeComponent {

Type type();
@Named("Empty") Type emptyType();

@Component.Builder
interface Builder {

	@BindsInstance Builder withXSType(@Nullable XSType xsType);
	@BindsInstance Builder withDefaultName(String name);
	@BindsInstance Builder andURI(@Nullable URI uri); // when we do not have an XSType to extract the uri from

	TypeComponent build();

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
