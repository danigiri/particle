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

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Locator;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XmlString;

import cat.calidos.morfeu.model.Attributes;
import cat.calidos.morfeu.model.BasicCellModel;
import cat.calidos.morfeu.model.BasicCellModelReference;
import cat.calidos.morfeu.model.CellModel;
import cat.calidos.morfeu.model.ComplexCellModel;
import cat.calidos.morfeu.model.Composite;
import cat.calidos.morfeu.model.Metadata;
import cat.calidos.morfeu.model.Type;
import cat.calidos.morfeu.model.metadata.injection.DaggerModelMetadataComponent;
import cat.calidos.morfeu.utils.OrderedMap;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;


/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Module
public class CellModelModule {

private static final String DEFAULT_DESC = "";
private static final String NODE_SEPARATOR = "/";
private static final int ATTRIBUTE_MIN = 0;
private static final int ATTRIBUTE_REQUIRED = 1;
private static final int ATTRIBUTE_MAX = 1;
private static final String ATTRIBUTE_SEPARATOR = "@";
private static final String DEFAULT_TYPE_POSTFIX = "-type";
protected final static Logger log = LoggerFactory.getLogger(CellModelModule.class);


@Provides
public static CellModel provideCellModel(Type t,
									    Provider<BasicCellModel> providerCell,
									    Provider<ComplexCellModel> providerComplexCell,
									    Provider<BasicCellModelReference> providerReference,
									    Map<String, CellModel> globals) {

	CellModel cellModel;
	if (globals!=null && globals.containsKey(t.getName())) {

		cellModel = providerReference.get();

	} else {

		if (t.isSimple()) {
			cellModel = providerCell.get();
		} else {
			cellModel = providerComplexCell.get();
		}

	}

	return cellModel;

}


@Provides
public static BasicCellModel buildCellModelFrom(URI u,
												@Named("name") String name, 
												@Named("desc") String desc,
												@Named("MinOccurs") int minOccurs,
												@Named("MaxOccurs") int maxOccurs,
												Optional<String> defaultValue,
												Type t, 
												Metadata metadata,
												Map<String, CellModel> globals) {

	// TODO: add cell description from metadata
	BasicCellModel newCellModel = new BasicCellModel(u, name, desc, t, minOccurs, maxOccurs, defaultValue, metadata);
	updateGlobalsWith(globals, t, newCellModel);
	
	return newCellModel;

}


@Provides
public static ComplexCellModel buildComplexCellModelFrom(URI u,
													   @Named("name") String name,
													   @Named("desc") String desc, 
													   @Named("MinOccurs") int minOccurs,
													   @Named("MaxOccurs") int maxOccurs,
													   Optional<String> defaultValue,
													   Type t,
													   Metadata metadata,
													   Provider<Attributes<CellModel>> attributesProvider,
													   Provider<Composite<CellModel>> childrenProvider,
													   Map<String, CellModel> globals) {
	
	// in this way, we create the cell model, find out if it's global, add it and then generate the
	// attributes and children. This means that if a child references an already defined CellModel (which could
	// include this very one, there will be no infinite loops and the child will be created as a reference to this one 
	ComplexCellModel newComplexCellModel = new ComplexCellModel(u, 
															  name, 
															  desc, 
															  t, 
															  minOccurs, 
															  maxOccurs, 
															  metadata,
															  defaultValue,
															  null,		// attributes
															  null);		// children
	updateGlobalsWith(globals, t, newComplexCellModel);

	newComplexCellModel.setAttributes(attributesProvider.get());
	newComplexCellModel.setChildren(childrenProvider.get());
	
	return newComplexCellModel;
	
}


@Provides
public BasicCellModelReference buildCellModelFromReference(URI u,
														 @Named("name") String name,
														 @Named("MinOccurs") int minOccurs,
														 @Named("MaxOccurs") int maxOccurs,
														 Type t,
														 Metadata metadata,
														 Map<String, CellModel> globals) {
	
	CellModel ref = globals.get(t.getName());
	// if we are a refence but we have not defined custom metadata (through globals, basically) it means
	// that metadata==type.metadata, then we use the reference
	//  otherwise we have defined custom metadata (through globals), then we use that custom
	// TODO: merge reference metadata and global metadata given a priority
	Metadata effectiveMetadata = metadata.equals(t.getMetadata()) ? ref.getMetadata() : metadata;

	return new BasicCellModelReference(u, name, minOccurs, maxOccurs, effectiveMetadata, ref);

}


@Provides @Named("desc")
public String desc(Metadata meta, Type t) {
	return meta.getDesc();
}


@Provides @Named("MinOccurs")
public int minOccurs(XSParticle particle) {
	return particle.getMinOccurs().intValueExact();
}


@Provides @Named("MaxOccurs")
public int maxOccurs(XSParticle particle) {

	BigInteger maxOccurs = particle.getMaxOccurs();
	
	return maxOccurs.equals(BigInteger.valueOf(XSParticle.UNBOUNDED)) ? CellModel.UNBOUNDED : maxOccurs.intValueExact();

}


@Provides
public Optional<String> defaultValue(Metadata metadata) {
	return Optional.ofNullable(metadata.getDefaultValues().get(null)); // null is the key for the cell default value
}


@Provides
public static Type getTypeFrom(XSType type, @Named("TypeDefaultName") String defaultName) {
	
	return DaggerTypeComponent.builder()
								.withDefaultName(defaultName)
								.withXSType(type)
								.build()
								.type();
			
}


@Provides
public static Attributes<CellModel> attributesOf(XSElementDecl elem, 
											   Type t,
											   URI u,
											   Metadata metadata,
											   @Nullable Map<String, CellModel> globals) {

	if (t.isSimple()) {
		return new OrderedMap<CellModel>(0);
	}
	
	XSComplexType complexType = elem.getType().asComplexType();
	Collection<? extends XSAttributeUse> rawAttributes = complexType.getAttributeUses();

	Attributes<CellModel> attributes = new OrderedMap<CellModel>(rawAttributes.size());

	rawAttributes.forEach(a -> {
								XSAttributeDecl attributeDecl = a.getDecl();
								boolean isRequired = a.isRequired();
								CellModel cellModel = attributeCellModelFor(attributeDecl, 
																			isRequired, 
																			u, 
																			metadata, 
																			globals);
								attributes.addAttribute(attributeDecl.getName(), cellModel);
	});

	return attributes;
	
}


@Provides
public static Composite<CellModel> childrenOf(XSElementDecl elem,
											Type t,
											URI u,
											Map<String, CellModel> globals,
											Map<URI, Metadata> globalMetadata) {
	
	// Magic happens here: 
	// BASE CASES:
	//	if we are a simple type we are at a leaf
	//	if we are an empty type we do not have children
	// RECURSIVE CASE:
	//	we go through all the children and we add them
	if (t.isSimple()) {
		return new OrderedMap<CellModel>(0);							// base case, simple type sanity check
	}

	Composite<CellModel> children = new OrderedMap<CellModel>();
	
	XSComplexType complexType = elem.getType().asComplexType();
	XSContentType contentType = complexType.getContentType();
	if (contentType.asEmpty()!=null) {
		return new OrderedMap<CellModel>(0);							// base case, no children, we return
	}
	
	XSParticle particle = contentType.asParticle();						// recursive case, go through all children
	LinkedList<XSParticle> termTypes = new LinkedList<XSParticle>();	// list of all the particles left to process
	termTypes.add(particle);
	while (!termTypes.isEmpty()) {
		particle = termTypes.removeFirst();

		if (particle.getTerm().isModelGroup()) {
			// FIXME: this is reverse order!!!
			// FIXME: we need to see when we have more complex groups like unions and stuff 
			XSModelGroup typeModelGroup = particle.getTerm().asModelGroup();
			typeModelGroup.iterator().forEachRemaining(m -> termTypes.addFirst(m.asParticle())); 
		} else {

			XSElementDecl childElem = particle.getTerm().asElementDecl();
			CellModel childCellModel = DaggerCellModelComponent.builder()
												.fromElem(childElem)
												.fromParticle(particle)
												.withParentURI(u)
												.withGlobalMetadata(globalMetadata)
												.andExistingGlobals(globals)
												.build()
												.cellModel();
			children.addChild(childCellModel.getName(), childCellModel);

		}
	}
	
	return children;
	
}


@Provides
public Locator locatorFrom(XSElementDecl elem) {
	return elem.getLocator();
}


@Provides @Named("name")
public String nameFrom(XSElementDecl elem) {
	return elem.getName();
}

@Provides @Named("URIString")
public String getURIString(@Named("ParentURI") URI parentURI, @Named("name") String name) {
	return parentURI+NODE_SEPARATOR+name;
}


@Provides
public static URI getURIFrom(@Named("URIString") String uri, @Named("name") String name) throws RuntimeException {

	try {

		return new URI(uri);

	} catch (URISyntaxException e) {
		log.error("What the heck, URI '{}' of element '{}' is not valid ", uri, name);
		throw new RuntimeException("Somehow we failed to create URI of element "+name, e);
	}

}


@Provides @Named("TypeDefaultName")
public static String getDefaultTypeName(XSElementDecl elem) {
	return elem.getName()+DEFAULT_TYPE_POSTFIX;
}


@Provides
public static XSType type(XSElementDecl elem) {
	return elem.getType();
}


@Provides
public static Metadata metadata(XSElementDecl elem, URI uri, Type t, Map<URI, Metadata> globalMetadata) {

	// we get the metadata from the current cell model, with fallback from global or from the type

	Metadata fallback = globalMetadata!=null && globalMetadata.containsKey(uri) ? 
						globalMetadata.get(uri) : t.getMetadata();

	return DaggerModelMetadataComponent.builder()
										.from(elem.getAnnotation())
										.withParentURI(uri)
										.andFallback(fallback)
										.build()
										.value();

}


private static CellModel attributeCellModelFor(XSAttributeDecl xsAttributeDecl, 
											 boolean required,
											 URI nodeURI, 
											 Metadata metadata,	// remember this is the cell metadata
											 @Nullable Map<String, CellModel> globals) {

	String name = xsAttributeDecl.getName();
	URI attributeURI = getURIFrom(nodeURI.toString()+ATTRIBUTE_SEPARATOR+name, name);
	Type type = DaggerTypeComponent.builder()
									.withDefaultName(name)
									.withXSType(xsAttributeDecl.getType())
									.build()
									.type();
	int minOccurs = required ? ATTRIBUTE_REQUIRED : ATTRIBUTE_MIN;

	
			
	Metadata attributeMetadata = DaggerModelMetadataComponent.builder()
			.from(xsAttributeDecl.getAnnotation())
			.withParentURI(attributeURI)
			.andFallback(type.getMetadata())
			.build()
			.value();
	
	// default value priorities
	// 1) the Cell metadata, with '<mf:default-value name="@attributename">foo</mf:default-value>'
	// 2) XML schema default="foo" (on optional attributes)
	// 3) the type default value
	Optional<String> defaultValue = Optional.ofNullable(
			metadata.getDefaultValues().get(Metadata.DEFAULT_VALUE_PREFIX+name));
	if (!defaultValue.isPresent()) {
		XmlString defaultValueXMLString = xsAttributeDecl.getDefaultValue();
		defaultValue = (defaultValueXMLString!=null) ? Optional.of(defaultValueXMLString.value) : defaultValue;
	}
	String defaultValueFromType = attributeMetadata.getDefaultValues().get(name);
	defaultValue = (!defaultValue.isPresent()) ? Optional.ofNullable(defaultValueFromType) : defaultValue;
	
	// no references for attributes at the moment
//	if (globals.containsKey(type.getName())) {		// if it's an attribute we keep the local uri
//		cellModel = new BasicCellModelReference(attributeURI, name, globals.get(type.getName()));
//	} else {
		
	// attributes have the presentation of the corresponding type
	return new BasicCellModel(attributeURI, 
								   name, 
								   attributeMetadata.getDesc(), 
								   type, 
								   minOccurs,
								   ATTRIBUTE_MAX,
								   defaultValue,
								   attributeMetadata);
//	}

	
}


/**
* @param t
* @param globals
* @param newCellModel
*////////////////////////////////////////////////////////////////////////////////
private static void updateGlobalsWith(@Nullable Map<String, CellModel> globals, Type t, BasicCellModel newCellModel) {

	if (t.isGlobal() && globals!=null) {
		globals.put(t.getName(), newCellModel);
	}
	
}



////for each weak reference, we look for the referenced cell model and turn it into a proper reference
//@Provides
//public static CellModel normaliseWeakReferences(CellModel cellModel, Map<String, CellModel> globalCellModels) {
//
//	if (cellModel.isReference() && cellModel.asReference().isWeak()) {
//		
//		
//		
//	}
//	
//	// at this point, if it's a reference it means it has been normalised
//	if (cellModel.isSimple() || cellModel.isReference()) {
//		return cellModel;
//	}
//	
//	ComplexCellModel complexCellModel = cellModel.asComplex();
//	complexCellModel.attributes().asList().forEach(a -> normaliseWeakReferences(root, a));
//	complexCellModel.children().asList().forEach(c -> normaliseWeakReferences(root, c));
//	
//	return cellModel;
//}
//


}
