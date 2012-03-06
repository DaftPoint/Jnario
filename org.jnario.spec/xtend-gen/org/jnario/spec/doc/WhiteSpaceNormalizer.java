package org.jnario.spec.doc;

import java.util.Arrays;
import java.util.List;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.CharacterExtensions;
import org.eclipse.xtext.xbase.lib.IntegerExtensions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class WhiteSpaceNormalizer {
  public String normalize(final CharSequence input) {
    boolean _or = false;
    boolean _equals = ObjectExtensions.operator_equals(input, null);
    if (_equals) {
      _or = true;
    } else {
      int _length = input.length();
      boolean _equals_1 = IntegerExtensions.operator_equals(_length, 0);
      _or = BooleanExtensions.operator_or(_equals, _equals_1);
    }
    if (_or) {
      return "";
    }
    String _string = input.toString();
    String[] _split = _string.split("\n");
    List<String> _asList = Arrays.<String>asList(_split);
    Iterable<String> lines = _asList;
    boolean _isEmpty = IterableExtensions.isEmpty(lines);
    if (_isEmpty) {
      return "";
    }
    String _head = IterableExtensions.<String>head(lines);
    String firstLine = _head;
    String _trim = firstLine.trim();
    int _length_1 = _trim.length();
    boolean _equals_2 = IntegerExtensions.operator_equals(_length_1, 0);
    boolean _while = _equals_2;
    while (_while) {
      {
        Iterable<String> _drop = IterableExtensions.<String>drop(lines, 1);
        lines = _drop;
        String _head_1 = IterableExtensions.<String>head(lines);
        firstLine = _head_1;
        boolean _equals_3 = ObjectExtensions.operator_equals(firstLine, null);
        if (_equals_3) {
          return "";
        }
      }
      String _trim_1 = firstLine.trim();
      int _length_2 = _trim_1.length();
      boolean _equals_3 = IntegerExtensions.operator_equals(_length_2, 0);
      _while = _equals_3;
    }
    String _whitespaceAtBeginning = this.whitespaceAtBeginning(firstLine);
    final String whitespace = _whitespaceAtBeginning;
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder result = _stringBuilder;
    final Procedure1<String> _function = new Procedure1<String>() {
        public void apply(final String it) {
          String _remove = WhiteSpaceNormalizer.this.remove(it, whitespace);
          result.append(_remove);
          result.append("\n");
        }
      };
    IterableExtensions.<String>forEach(lines, _function);
    String _string_1 = result.toString();
    String _trim_1 = _string_1.trim();
    return _trim_1;
  }
  
  public String whitespaceAtBeginning(final String string) {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder whitespace = _stringBuilder;
    char[] _charArray = string.toCharArray();
    for (final char c : _charArray) {
      boolean _isWhitespace = Character.isWhitespace(c);
      if (_isWhitespace) {
        whitespace.append(c);
      } else {
        String _string = whitespace.toString();
        return _string;
      }
    }
    return "";
  }
  
  public String remove(final String input, final String toReplace) {
    int _length = input.length();
    int _length_1 = toReplace.length();
    boolean _lessThan = IntegerExtensions.operator_lessThan(_length, _length_1);
    if (_lessThan) {
      return input;
    }
    int i = 0;
    int _length_2 = toReplace.length();
    boolean _lessThan_1 = IntegerExtensions.operator_lessThan(i, _length_2);
    boolean _while = _lessThan_1;
    while (_while) {
      {
        char _charAt = input.charAt(i);
        char _charAt_1 = toReplace.charAt(i);
        boolean _notEquals = CharacterExtensions.operator_notEquals(_charAt, _charAt_1);
        if (_notEquals) {
          return input;
        }
        int _plus = IntegerExtensions.operator_plus(i, 1);
        i = _plus;
      }
      int _length_3 = toReplace.length();
      boolean _lessThan_2 = IntegerExtensions.operator_lessThan(i, _length_3);
      _while = _lessThan_2;
    }
    int _length_3 = toReplace.length();
    String _substring = input.substring(_length_3);
    return _substring;
  }
}