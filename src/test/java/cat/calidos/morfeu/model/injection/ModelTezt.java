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

package cat.calidos.morfeu.model.injection;

import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

import cat.calidos.morfeu.model.CellModel;
import cat.calidos.morfeu.model.ComplexCellModel;
import cat.calidos.morfeu.model.Model;
import cat.calidos.morfeu.model.Type;
import cat.calidos.morfeu.problems.ConfigurationException;
import cat.calidos.morfeu.problems.FetchingException;
import cat.calidos.morfeu.problems.ParsingException;


/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ModelTezt {

@Rule public MockitoRule mockitoRule = MockitoJUnit.rule();


protected Model parseModelFrom(URI u) throws ConfigurationException, 
											 InterruptedException, 
											 ExecutionException, ParsingException, FetchingException {
		
	XSSchemaSet schemaSet = parseSchemaFrom(u);
		
	List<CellModel> rootCellModels = ModelModule.buildRootCellModels(schemaSet, u);
	String desc = ModelModule.descriptionFromSchemaAnnotation(schemaSet);
	return ModelModule.produceModel(u, desc, schemaSet, rootCellModels);
		
}

protected XSSchemaSet parseSchemaFrom(URI uri)
		throws InterruptedException, ExecutionException, ConfigurationException, ParsingException, FetchingException {

	XSOMParser parser = DaggerSchemaParserComponent.builder().build().produceXSOMParser().get();

	return ModelModule.parseModel(uri, parser);

}


protected Type provideElementType(XSElementDecl elem) {

	return DaggerTypeComponent.builder()	//awfully convenient to inject the dependencies, ok on integration tests
			.withDefaultName("default-type-name")
			.withXSType(elem.getType())
			.build()
			.type();
	
}


protected CellModel cellModelFrom(URI u, String name) throws Exception {

	XSSchemaSet schemaSet = parseSchemaFrom(u);
	XSElementDecl elem = schemaSet.getElementDecl(Model.MODEL_NAMESPACE, name);

	return DaggerCellModelComponent.builder().withElement(elem).withParentURI(u).build().cellModel();
	
}


protected ComplexCellModel complexCellModelFrom(URI u, String name) throws Exception {
	
	CellModel cellModel = cellModelFrom(u, name);
	
	return ComplexCellModel.from(cellModel);	// from simple to complex
	
}


}
