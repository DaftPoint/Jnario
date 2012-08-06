package org.jnario.ui.quickfix;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.typing.ITypeProvider;
import org.jnario.ui.quickfix.FeatureCallTargetTypeProvider;

@SuppressWarnings("all")
public class IsUndefinedMethod {
  @Inject
  private FeatureCallTargetTypeProvider _featureCallTargetTypeProvider;
  
  @Inject
  private ITypeProvider _iTypeProvider;
  
  public boolean callsUndefinedMethod(final XMemberFeatureCall featureCall) {
    JvmType _targetType = this._featureCallTargetTypeProvider.targetType(featureCall);
    boolean _isUnknown = this.isUnknown(_targetType);
    if (_isUnknown) {
      return false;
    }
    final JvmIdentifiableElement method = featureCall.getFeature();
    boolean _equals = Objects.equal(method, null);
    if (_equals) {
      return false;
    }
    boolean _eIsProxy = method.eIsProxy();
    if (_eIsProxy) {
      return true;
    }
    boolean _not = (!(method instanceof JvmOperation));
    if (_not) {
      return false;
    }
    return this.calls(featureCall, ((JvmOperation) method));
  }
  
  public boolean calls(final XMemberFeatureCall featureCall, final JvmOperation operation) {
    final EList<JvmFormalParameter> left = operation.getParameters();
    final EList<XExpression> right = featureCall.getMemberCallArguments();
    int _size = left.size();
    int _size_1 = right.size();
    boolean _notEquals = (_size != _size_1);
    if (_notEquals) {
      return true;
    }
    int i = 0;
    EList<JvmFormalParameter> _parameters = operation.getParameters();
    int _size_2 = _parameters.size();
    boolean _lessThan = (i < _size_2);
    boolean _while = _lessThan;
    while (_while) {
      {
        JvmFormalParameter _get = left.get(i);
        JvmTypeReference _parameterType = _get.getParameterType();
        XExpression _get_1 = right.get(i);
        JvmTypeReference _type = this._iTypeProvider.getType(_get_1);
        boolean _notEquals_1 = (!Objects.equal(_parameterType, _type));
        if (_notEquals_1) {
          return true;
        }
        int _plus = (i + 1);
        i = _plus;
      }
      EList<JvmFormalParameter> _parameters_1 = operation.getParameters();
      int _size_3 = _parameters_1.size();
      boolean _lessThan_1 = (i < _size_3);
      _while = _lessThan_1;
    }
    return false;
  }
  
  private boolean isUnknown(final EObject obj) {
    boolean _or = false;
    boolean _equals = Objects.equal(obj, null);
    if (_equals) {
      _or = true;
    } else {
      boolean _eIsProxy = obj.eIsProxy();
      _or = (_equals || _eIsProxy);
    }
    return _or;
  }
}
