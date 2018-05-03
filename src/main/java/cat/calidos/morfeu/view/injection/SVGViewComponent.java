/*
 *    Copyright 2018 Daniel Giribet
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

package cat.calidos.morfeu.view.injection;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Component(modules=SVGViewModule.class)
public interface SVGViewComponent {

String render();

@Component.Builder
interface Builder {

	@BindsInstance Builder from(@Named("Text") String text);
	@BindsInstance Builder withHeader(@Named("Header") Optional<String> header);
	@BindsInstance Builder truncate(Boolean truncate);
	SVGViewComponent build();
}

}
