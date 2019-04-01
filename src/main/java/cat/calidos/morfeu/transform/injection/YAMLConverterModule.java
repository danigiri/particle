package cat.calidos.morfeu.transform.injection;

import javax.inject.Named;

import com.fasterxml.jackson.databind.JsonNode;

import cat.calidos.morfeu.model.Model;
import cat.calidos.morfeu.transform.Converter;
import cat.calidos.morfeu.transform.JsonNodeCellModel;
import cat.calidos.morfeu.transform.StackContext;
import dagger.Module;
import dagger.Provides;

/**
*	@author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Module
public class YAMLConverterModule {

@Provides
public String xml(Converter<JsonNodeCellModel, String> converter) {
	return converter.process();
}

@Provides
Converter<JsonNodeCellModel, String> converter(@Named("PopulatedContext") StackContext<JsonNodeCellModel> context) {
	return new Converter<JsonNodeCellModel, String>(context);
}

@Provides @Named("PopulatedContext")
StackContext<JsonNodeCellModel> populatedContext(StackContext<JsonNodeCellModel> context,
													JsonNode yaml, 
													Model model) {

	DaggerYAMLCellToXMLProcessorComponent.builder()
											.fromNode(yaml)
											.givenCase("yaml-to-xml")
											.parentCellModel(model)
											.build()
											.processors()
											.forEach(context::push);

	return context;

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

