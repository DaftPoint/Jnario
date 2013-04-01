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
 * Goal which compiles Xtend2 test sources.
 *
 * @author Michael Clay - Initial contribution and API
 */
public class XtendTestCompile extends AbstractXtend2CompilerMojo {
	/**
	 * Location of the generated test files.
	 * 
	 * @parameter default-value="${basedir}/src/test/generated-sources/xtend"
	 * @required
	 */
	private String testOutputDirectory;
	/**
	 * Location of the temporary compiler directory.
	 * 
	 * @parameter default-value="${project.build.directory}/xtend-test"
	 * @required
	 */
	private String testTempDirectory;

	@Override
	protected void internalExecute(XtendBatchCompiler xtend2BatchCompiler) throws MojoExecutionException {
		compileTestSources(xtend2BatchCompiler);
	}

	@SuppressWarnings("unchecked")
	protected void compileTestSources(XtendBatchCompiler xtend2BatchCompiler) throws MojoExecutionException {
		List<String> testCompileSourceRoots = Lists.newArrayList(project.getTestCompileSourceRoots());
		String testClassPath = concat(File.pathSeparator, getTestClassPath());
		project.addTestCompileSourceRoot(testOutputDirectory);
		compile(xtend2BatchCompiler, testClassPath, testCompileSourceRoots, testOutputDirectory);
	}

	@SuppressWarnings("unchecked")
	protected List<String> getTestClassPath() {
		Set<String> classPath = Sets.newLinkedHashSet();
		classPath.add(project.getBuild().getTestSourceDirectory());
		try {
			classPath.addAll(project.getTestClasspathElements());
		} catch (DependencyResolutionRequiredException e) {
			throw new WrappedException(e);
		}
		addDependencies(classPath, project.getTestArtifacts());
		return newArrayList(filter(classPath, FILE_EXISTS));
	}

	@Override
	protected String getTempDirectory() {
		return testTempDirectory;
	}

}
