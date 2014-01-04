package org.jnario.documentation;

import com.google.common.base.Objects;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.core.xtend.XtendAnnotationTarget;
import org.eclipse.xtend.core.xtend.XtendPackage;
import org.eclipse.xtext.documentation.impl.MultiLineCommentDocumentationProvider;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class XtendDocumentationProvider extends MultiLineCommentDocumentationProvider {
  public String getDocumentation(final EObject o) {
    String _xblockexpression = null;
    {
      boolean _shouldBeHandeled = this.shouldBeHandeled(o);
      boolean _not = (!_shouldBeHandeled);
      if (_not) {
        return null;
      }
      String _documentation = super.getDocumentation(o);
      _xblockexpression = (_documentation);
    }
    return _xblockexpression;
  }
  
  public List<INode> getDocumentationNodes(final EObject o) {
    List<INode> _xblockexpression = null;
    {
      boolean _shouldBeHandeled = this.shouldBeHandeled(o);
      boolean _not = (!_shouldBeHandeled);
      if (_not) {
        return CollectionLiterals.<INode>emptyList();
      }
      List<INode> _documentationNodes = super.getDocumentationNodes(o);
      _xblockexpression = (_documentationNodes);
    }
    return _xblockexpression;
  }
  
  /**
   * The Xtend parser constructs a synthetic nested AST element to hold annotations which should be ignored as a documentation provider
   */
  public boolean shouldBeHandeled(final EObject o) {
    boolean _and = false;
    if (!(o instanceof XtendAnnotationTarget)) {
      _and = false;
    } else {
      EStructuralFeature _eContainingFeature = o.eContainingFeature();
      boolean _equals = Objects.equal(_eContainingFeature, XtendPackage.Literals.XTEND_MEMBER__ANNOTATION_INFO);
      _and = ((o instanceof XtendAnnotationTarget) && _equals);
    }
    boolean _not = (!_and);
    return _not;
  }
}
