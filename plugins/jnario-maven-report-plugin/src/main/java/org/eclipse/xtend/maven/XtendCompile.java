/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtend.maven;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.xtext.util.Strings.concat;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Goal which compiles Xtend sources.
 * 
 * @author Michael Clay - Initial contribution and API
 */
public class XtendCompile extends AbstractXtend2CompilerMojo {
	/**
	 * Location of the generated source files.
	 * 
	 * @parameter default-value="${basedir}/src/main/generated-sources/xtend"
	 * @required
	 */
	private String outputDirectory;
	/**
	 * Location of the temporary compiler directory.
	 * 
	 * @parameter default-value="${project.build.directory}/xtend"
	 * @required
	 */
	private String tempDirectory;

	@Override
	protected void internalExecute(XtendBatchCompiler xtend2BatchCompiler) throws MojoExecutionException {
		compileSources(xtend2BatchCompiler);
	}

	@SuppressWarnings("unchecked")
	private void compileSources(XtendBatchCompiler xtend2BatchCompiler) throws MojoExecutionException {
		List<String> compileSourceRoots = Lists.newArrayList(project.getCompileSourceRoots());
		String classPath = concat(File.pathSeparator, getClassPath());
		project.addCompileSourceRoot(outputDirectory);
		compile(xtend2BatchCompiler, classPath, compileSourceRoots, outputDirectory);
	}


	@SuppressWarnings("unchecked")
	protected List<String> getClassPath() {
		Set<String> classPath = Sets.newLinkedHashSet();
		classPath.add(project.getBuild().getSourceDirectory());
		try {
			classPath.addAll(project.getCompileClasspathElements());
		} catch (DependencyResolutionRequiredException e) {
			throw new WrappedException(e);
		}
		addDependencies(classPath, project.getCompileArtifacts());
		return newArrayList(filter(classPath, FILE_EXISTS));
	}

	
	@Override
	protected String getTempDirectory() {
		return tempDirectory;
	}

}
