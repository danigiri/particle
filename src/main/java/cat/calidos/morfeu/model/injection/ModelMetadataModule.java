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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.xsom.XSAnnotation;

import cat.calidos.morfeu.model.Metadata;
import dagger.Module;
import dagger.Provides;


/** Model Metadata helper module to enrich the model definitions with useful information
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Module
public class ModelMetadataModule {

private static String DESC_FIELD = "mf:desc";
private static String PRESENTATION_FIELD = "mf:presentation";
private static String THUMB_FIELD = "mf:thumb";
private static String UNDEFINED = "";


@Provides
Metadata provideMetadata(LinkedList<Node> annotationNodes, @Named("Fallback") @Nullable Metadata fallback) {
	
	Optional<String> desc = contentOf(annotationNodes, DESC_FIELD);
	Optional<String> presentation = contentOf(annotationNodes, PRESENTATION_FIELD);
	Optional<String> thumb = contentOf(annotationNodes, THUMB_FIELD);
	
	if (fallback==null) {

		return new Metadata(desc, presentation, thumb);
	
	} else {

		return new Metadata(desc, presentation, thumb, fallback);
		
	} 
}

@Provides
LinkedList<Node> annotationNode(@Nullable XSAnnotation annotation) {
	
	LinkedList<Node> annotationNodes = new LinkedList<Node>();
	if (annotation!=null) {
		Node annotationRootNode = (Node)annotation.getAnnotation(); // as we are using the DomAnnotationParserFactory from XSOM
		annotationNodes.add(annotationRootNode);
	}
	
	return annotationNodes;
	
}


//reverse breadth-first search, as the dom annotation parser adds all sibling nodes in reverse order
private Optional<String> contentOf(LinkedList<Node> annotationNodes, @Named("tag") String tag) {
	
	String content = null;

	while (annotationNodes.size()>0 && content==null) {
		
		Node currentNode = annotationNodes.pop();
		if (currentNode.getNodeName().equals(tag)) {
			content = currentNode.getTextContent();
		} else {
			if (currentNode.hasChildNodes()) {
				NodeList childNodes = currentNode.getChildNodes();
				for (int i=0;i<childNodes.getLength();i++) {
					annotationNodes.add(childNodes.item(i));
				}
			}
		}
		
	}
	
	// content may have lots of leading/trailing whitespace stuff, we'll leave that outside our scope
	return Optional.ofNullable(content);
	
}


}
