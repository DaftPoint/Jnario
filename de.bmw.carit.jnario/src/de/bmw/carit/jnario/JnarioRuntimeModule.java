/*
 * generated by Xtext
 */
package de.bmw.carit.jnario;

import org.eclipse.xtext.generator.OutputConfigurationProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.xbase.compiler.JvmModelGenerator;
import org.eclipse.xtext.xbase.compiler.XbaseCompiler;
import org.eclipse.xtext.xbase.impl.FeatureCallToJavaMapping;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.scoping.featurecalls.StaticMethodsFeatureForTypeProvider.ExtensionClassNameProvider;
import org.eclipse.xtext.xbase.typing.ITypeProvider;
import org.eclipse.xtext.xtend2.compiler.Xtend2OutputConfigurationProvider;
import org.eclipse.xtext.xtend2.resource.Xtend2Resource;

import com.google.inject.Binder;
import com.google.inject.name.Names;

import de.bmw.carit.jnario.common.jvmmodel.ExtendedJvmModelGenerator;
import de.bmw.carit.jnario.common.jvmmodel.ExtendedJvmTypesBuilder;
import de.bmw.carit.jnario.common.scoping.JnarioExtensionClassNameProvider;
import de.bmw.carit.jnario.generator.JnarioCompiler;
import de.bmw.carit.jnario.jvmmodel.JnarioFeatureCallToJavaMapping;
import de.bmw.carit.jnario.scoping.JnarioImportedNamespaceScopeProvider;
import de.bmw.carit.jnario.scoping.JnarioScopeProvider;
import de.bmw.carit.jnario.typing.JnarioTypeProvider;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class JnarioRuntimeModule extends de.bmw.carit.jnario.AbstractJnarioRuntimeModule {
	
	@Override
	public Class<? extends ITypeProvider> bindITypeProvider() {
		return JnarioTypeProvider.class;
	}
	
	@Override
	public Class<? extends IScopeProvider> bindIScopeProvider() {
		return JnarioScopeProvider.class;
	}
	
	public Class<? extends XbaseCompiler> bindXbaseCompiler() {
		return JnarioCompiler.class; 
	}
	
	public Class<? extends JvmTypesBuilder> bindJvmTypesBuilder(){
		return ExtendedJvmTypesBuilder.class;
	}
	
	public Class<? extends JvmModelGenerator> bindJvmModelGenerator(){
		return ExtendedJvmModelGenerator.class;
	}
	
	@Override
	public void configureIScopeProviderDelegate(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
		.to(JnarioImportedNamespaceScopeProvider.class);
	}
	
	
	public Class<? extends OutputConfigurationProvider> bindOutputConfigurationProvider() {
		return Xtend2OutputConfigurationProvider.class;
	}
	
	public Class<? extends ExtensionClassNameProvider> bindExtensionClassNameProvider(){
		return JnarioExtensionClassNameProvider.class;
	}
	
	public Class<? extends FeatureCallToJavaMapping> bindFeatureCallToJavaMapping(){
		return JnarioFeatureCallToJavaMapping.class;
	}
	
	@Override
	public Class<? extends XtextResource> bindXtextResource() {
		return Xtend2Resource.class;
	}
}
