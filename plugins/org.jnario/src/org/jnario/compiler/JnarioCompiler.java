/*******************************************************************************
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jnario.compiler;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;
import static org.eclipse.xtext.util.Strings.convertToJavaString;
import static org.jnario.jvmmodel.DoubleArrowSupport.isDoubleArrow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.core.compiler.XtendCompiler;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XBinaryOperation;
import org.eclipse.xtext.xbase.XClosure;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XNullLiteral;
import org.eclipse.xtext.xbase.XSwitchExpression;
import org.eclipse.xtext.xbase.XbaseFactory;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.jnario.Assertion;
import org.jnario.MockLiteral;
import org.jnario.Should;
import org.jnario.ShouldThrow;
import org.jnario.lib.Assert;
import org.jnario.util.MockingSupport;
import org.jnario.util.SourceAdapter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * @author Sebastian Benz - Initial contribution and API
 */
public class JnarioCompiler extends XtendCompiler {

	@Inject
	private JnarioExpressionHelper expressionHelper;
	
	@Inject ISerializer serializer;

	@Override
	public void internalToConvertedExpression(XExpression obj,
			ITreeAppendable appendable) {
		if (obj instanceof Assertion) {
			_toJavaExpression((Assertion) obj, appendable);
		} else if (obj instanceof Should) {
			_toJavaExpression((Should) obj, appendable);
		} else if (obj instanceof ShouldThrow) {
			_toJavaExpression((ShouldThrow) obj, appendable);
		} else if (obj instanceof MockLiteral) {
			_toJavaExpression((MockLiteral) obj, appendable);
		} else {
			super.internalToConvertedExpression(obj, appendable);
		}
	}

	@Override
	public void doInternalToJavaStatement(XExpression obj,
			ITreeAppendable appendable, boolean isReferenced) {
		if (obj instanceof Assertion) {
			_toJavaStatement((Assertion) obj, appendable, isReferenced);
		} else if (obj instanceof Should) {
			_toJavaStatement((Should) obj, appendable, isReferenced);
		} else if (obj instanceof ShouldThrow) {
			_toJavaStatement((ShouldThrow) obj, appendable, isReferenced);
		} else if (obj instanceof MockLiteral) {
			_toJavaStatement((MockLiteral) obj, appendable, isReferenced);
		} else
			super.doInternalToJavaStatement(obj, appendable, isReferenced);
	}

	public void _toJavaStatement(ShouldThrow should, ITreeAppendable b,	boolean isReferenced) {
		if (should.getType() == null || should.getType().getType() == null) {
			return;
		}
		String expectedException = b.declareSyntheticVariable(should, "expectedException");
		b.newLine().append("boolean ").append(expectedException).append(" = false;").newLine();
		String message = b.declareSyntheticVariable(should, "message");
		b.append("String ").append(message).append(" = \"\";");
		b.newLine().append("try{").increaseIndentation();
		toJavaStatement(should.getExpression(), b, false);
		b.newLine().append(message).append(" = \"Expected \" + ")
		.append(should.getType().getType())
		.append(".class.getName() + \" for ")
		.append(javaStringNewLine())
		.append("     ")
		.append(serialize(should.getExpression()).replace("\n", "\n    ")).append(javaStringNewLine())
		.append(" with:\"");
		appendValues(should.getExpression(), b, new HashSet<String>());
		b.append(";");
		b.decreaseIndentation().newLine().append("}catch(").increaseIndentation()
				.append(should.getType().getType()).append(" e){").newLine()
				.append(expectedException).append(" = true;")
				.decreaseIndentation().newLine().append("}");
		b.newLine()
		.append(assertType(should))
		.append(".assertTrue(").append(message).append(", ")
		.append(expectedException).append(");");
	}
	
	
	
	public void _toJavaStatement(Should should, ITreeAppendable b,
			boolean isReferenced) {
		_toShouldExpression(should, b, should.isNot());
	}

	private void _toShouldExpression(XBinaryOperation should,
			ITreeAppendable b, boolean isNot) {
		if(should.getRightOperand() instanceof XNullLiteral){
			_toShouldBeNullExpression(should, b, isNot);
		}else{
			toShouldBeExpression(should, b, isNot);
		}
	}

	private void toShouldBeExpression(XBinaryOperation should,
			ITreeAppendable b, boolean isNot) {
		super._toJavaStatement(should, b, true);
		b.newLine().append(assertType(should));
		if (isNot) {
			b.append(".assertFalse(");
		} else {
			b.append(".assertTrue(");
		}
		generateMessageFor(should, b);
		b.append(" + \"" + javaStringNewLine() + "\", ");
		super._toJavaExpression(should, b);
		b.append(");").newLine();
	}

	private void _toShouldBeNullExpression(XBinaryOperation should,
			ITreeAppendable b, boolean isNot) {
		super.toJavaStatement(should.getLeftOperand(), b, true);
		b.newLine().append(assertType(should));
		if (isNot) {
			b.append(".assertNotNull(");
		} else {
			b.append(".assertNull(");
		}
		generateNullMessageFor(should, b);
		b.append(" + \"" + javaStringNewLine() + "\", ");
		super.toJavaExpression(should.getLeftOperand(), b);
		b.append(");").newLine();
	}


	protected XFeatureCall createFeatureCall(
			JvmIdentifiableElement nullValueMatcher) {
		XFeatureCall featureCall = XbaseFactory.eINSTANCE.createXFeatureCall();
		featureCall.setFeature(nullValueMatcher);
		return featureCall;
	}

	protected JvmIdentifiableElement getNullValueMatcher(XBinaryOperation should) {
		return getMethod(should, org.jnario.lib.Should.class.getName(), "nullValue");
	}

	protected JvmIdentifiableElement getMethod(XBinaryOperation should, String type, String methodName, String...argumentTypes) {
		JvmGenericType coreMatchersType = (JvmGenericType) jvmType(type, should);
		if(coreMatchersType == null){
			return null;
		}
		Iterable<JvmOperation> operations = Iterables.filter(coreMatchersType.getMembers(), JvmOperation.class);
		for (JvmOperation jvmOperation : operations) {
			if(methodName.equals(jvmOperation.getSimpleName()) && hasArguments(jvmOperation, argumentTypes)){
				return jvmOperation;
			}
		}
		return null;
	}

	private boolean hasArguments(JvmOperation jvmOperation,
			String[] argumentTypes) {
		if(jvmOperation.getParameters().size() != argumentTypes.length){
			return false;
		}
		for (int i = 0; i < argumentTypes.length; i++) {
			String argumentType = argumentTypes[i];
			JvmTypeReference actual = getTypeComputationServices().getTypeReferences().getTypeForName(argumentType, jvmOperation);
			JvmTypeReference expected = jvmOperation.getParameters().get(i).getParameterType();
//			System.out.println(expected.getQualifiedName() + "=>" + actual.getQualifiedName());
			if(!expected.getQualifiedName().equals(actual.getQualifiedName())){
				return false;
			}
		}
		return true;
	}

	private String javaStringNewLine() {
		return convertToJavaString("\n");
	}

	public void _toJavaExpression(MockLiteral expr, ITreeAppendable b) {
		JvmType mockito = getTypeComputationServices().getTypeReferences().findDeclaredType(MockingSupport.CLASS_NAME, expr);
		b.append(mockito).append(".mock(");
		b.append(expr.getType()).append(".class");
		b.append(")");
	}
	
	public void _toJavaStatement(MockLiteral expr, ITreeAppendable b, boolean isReferenced) {
		generateComment(expr, b, isReferenced);
	}
	
	public void _toJavaExpression(Should should, ITreeAppendable b) {
		b.append("true");
	}

	public void _toJavaExpression(ShouldThrow should, ITreeAppendable b) {
		b.append("true");
	}

	public void _toJavaStatement(Assertion assertion, ITreeAppendable b,
			boolean isReferenced) {
		if (assertion.getExpression() == null) {
			return;
		}
		generateSingleAssertion(assertion.getExpression(), b);
	}

	public void _toJavaExpression(Assertion assertion, ITreeAppendable b) {
		b.append("true");
	}

	private void generateSingleAssertion(XExpression expr, ITreeAppendable b) {
		toJavaStatement(expr, b, true);
		b.newLine();
		b.append(assertType(expr));
		b.append(".assertTrue(");
		generateMessageFor(expr, b);
		b.append(" + \"" + javaStringNewLine() + "\", ");
		toJavaExpression(expr, b);
		b.append(");");
		b.newLine();
	}

	private JvmType assertType(XExpression expr) {
		return jvmType(Assert.class, expr);
	}

	private boolean isVoid(XExpression expr) {
		JvmTypeReference type = getType(expr);
		return getTypeComputationServices().getTypeReferences().is(type, Void.TYPE);
	}
	
	private JvmType jvmType(Class<?> type, EObject context) {
		return jvmType(type.getName(), context);
	}

	private JvmType jvmType(String type, EObject context) {
		JvmTypeReference jvmTypeReference = getTypeComputationServices().getTypeReferences().getTypeForName(type, context);
		if(jvmTypeReference == null){
			return null;
		}
		return jvmTypeReference.getType();
	}

	public void generateMessageFor(Should should, ITreeAppendable b) {
		b.append("\"\\nExpected ");
		b.append(serialize(should));
		b.append(" but\"");
		Set<String> valueExpressions = newHashSet();

		XExpression left = should.getLeftOperand();
		toLiteralValue(left, b, valueExpressions);
		appendValues(left, b, valueExpressions);

		XExpression right = should.getRightOperand();
		toLiteralValue(right, b, valueExpressions);
		appendValues(right, b, valueExpressions);

		if (valueExpressions.isEmpty()) {
			b.append(" + \" did not.\"");
		}
	}

	private void generateNullMessageFor(XBinaryOperation should, ITreeAppendable b) {
		b.append("\"\\nExpected ");
		b.append(serialize(should));
		b.append("\\n     but is \"");
		toValue(should.getLeftOperand(), b);
	}

	private void generateMessageFor(XExpression expression, ITreeAppendable b) {
		b.append("\"\\nExpected ");
		b.append(serialize(expression));
		b.append(" but\"");
		Set<String> valueExpressions = newHashSet();
		appendValues(expression, b, valueExpressions);
		if (valueExpressions.isEmpty()) {
			b.append(" + \" did not.\"");
		}
	}

	private void appendValues(XExpression expression, ITreeAppendable b,
			Set<String> valueExpressions) {
		Iterator<XExpression> subExpressions = allSubExpressions(expression);
		if (subExpressions.hasNext()) {
			while (subExpressions.hasNext()) {
				XExpression subExpression = subExpressions.next();
				appendActualValues(subExpression, b, valueExpressions);
			}
		} else {
			toLiteralValue(expression, b, valueExpressions);
		}
	}

	protected String serialize(XExpression expression) {
		INode node = findNode(expression);
		if(node == null){
			return "";
		}
		String result = node.getText();
		result = result.trim();
		result = removeSurroundingParentheses(result);
		return convertToJavaString(result);
	}

	private INode findNode(XExpression expression) {
		INode node = getNode(expression);
		if(node != null) {
			return node;
		}
		EObject source = SourceAdapter.find(expression);
		while(node == null && isExpressions(source)){
			node = getNode(source);
			source = source.eContainer();
		}
		return node;
	}

	private boolean isExpressions(EObject source) {
		return source != null && source instanceof XExpression;
	}

	protected String removeSurroundingParentheses(String result) {
		if (result.startsWith("(") && result.endsWith(")")) {
			result = result.substring(1, result.length() - 1);
		}
		return result.trim();
	}

	protected void appendActualValues(XExpression expression,
			ITreeAppendable b, Set<String> valueExpressions) {
		toLiteralValue(expression, b, valueExpressions);
		Iterator<XExpression> subExpressions = allSubExpressions(expression);
		while (subExpressions.hasNext()) {
			XExpression subExpression = subExpressions.next();
			appendActualValues(subExpression, b, valueExpressions);
		}
	}

	protected Iterator<XExpression> allSubExpressions(XExpression expression) {
		Predicate<XExpression> noSwitchCases = new Predicate<XExpression>() {
			public boolean apply(XExpression e) {
				return !(e.eContainer() instanceof XSwitchExpression);
			}
		};
		Predicate<XExpression> noLiteralExpressions = new Predicate<XExpression>() {
			public boolean apply(XExpression expr) {
				return !expressionHelper.isLiteral(expr);
			}
		};
		Iterable<XExpression> subExpressions = filter(expression.eContents(), XExpression.class);
		
		subExpressions = filter(subExpressions, noLiteralExpressions);
		subExpressions = filter(subExpressions, noSwitchCases);
		return subExpressions.iterator();
	}

	protected void toLiteralValue(XExpression expression, ITreeAppendable b,
			Set<String> valueMappings) {
		if (expressionHelper.isLiteral(expression)) {
			return;
		}
		if (isVoid(expression)) {
			return;
		}
		if(isClosure(expression)){
			return;
		}
		toValue(expression, b, valueMappings);
	}

	private void toValue(XExpression expression, ITreeAppendable b,
			Set<String> valueMappings) {
		String expr = serialize(expression);
		if (expr.isEmpty() || valueMappings.contains(expr)) {
			return;
		}
		valueMappings.add(expr);
		b.append("\n + \"\\n     ");
		b.append(expr);
		b.append(" is \"");
		toValue(expression, b);
	}

	private void toValue(XExpression expression, ITreeAppendable b) {
		b.append(" + new ");
		b.append("org.hamcrest.StringDescription");
		b.append("().appendValue(");
		toJavaExpression(expression, b);
		b.append(").toString()");
	}
	
	private boolean isClosure(XExpression expression) {
		JvmTypeReference type = getType(expression);
		return type.getQualifiedName().startsWith("org.eclipse.xtext.xbase.lib.Functions");
	}

	@Override
	protected boolean isVariableDeclarationRequired(XExpression expr,
			ITreeAppendable b) {
		if (expr instanceof Assertion) {
			return false;
		}
		return super.isVariableDeclarationRequired(expr, b);
	}


	@Override
	protected void _toJavaStatement(XAbstractFeatureCall expr,
			ITreeAppendable b, boolean isReferenced) {
		if(!isDoubleArrow(expr)){
			super._toJavaStatement(expr, b, isReferenced);
			return;
		}
		XBinaryOperation doubleArrow = (XBinaryOperation) expr;
		if(doubleArrow.getRightOperand() instanceof XClosure){
			super._toJavaStatement(expr, b, isReferenced);
			return;
		}else{
			_toShouldExpression((XBinaryOperation) expr, b, false);
		}
	}

}