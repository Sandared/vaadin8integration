package de.modularco.vaadin.integration.impl;

import java.util.List;
import org.eclipse.xtext.xbase.lib.Conversions;

@SuppressWarnings("all")
public class Helper {
  public final static String UNIQUE_ID_PROPERTY = "de.unia.smds.vaadin.integration.uid";
  
  /**
   * Often the servlet paths of UIs are given like this: /test </br>
   * Vaadin sends requests to /test/UILD/... which would result in errors, so we use this method to append paths like /test with /* </br>
   * so that they can handle those requests
   */
  public static String getVaadinUrlPattern(final String urlPattern) {
    boolean _endsWith = urlPattern.endsWith("/");
    if (_endsWith) {
      return (urlPattern + "*");
    }
    boolean _endsWith_1 = urlPattern.endsWith("/*");
    if (_endsWith_1) {
      return urlPattern;
    }
    if (((!urlPattern.endsWith("/")) && (!urlPattern.endsWith("/*")))) {
      return (urlPattern + "/*");
    }
    throw new IllegalArgumentException();
  }
  
  /**
   * This method returns an integer representing the similarity of relative paths as it is done with servlet paths </br>
   * e.g. /test/* and /test/api would return a value of 1 as test is equal and the shorter path ends with an * </br>
   * /test/ and /test/api would return -1 as they do not match in sense of servlet pattern matching
   */
  public static int matchingPathSegments(final String path, final String other, final String delimiter) {
    if ((((Objects.equal(path, null) || Objects.equal(other, null)) || Objects.equal(delimiter, null)) || (delimiter.length() != 1))) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("path: ");
      _builder.append(path);
      _builder.append(", other: ");
      _builder.append(other);
      _builder.append(", delimiter: ");
      _builder.append(delimiter);
      throw new IllegalArgumentException(_builder.toString());
    }
    String delimiterToUse = delimiter;
    boolean _contains = "\\^$.|?*+()[{".contains(delimiterToUse);
    if (_contains) {
      delimiterToUse = ("\\" + delimiterToUse);
    }
    String[] segmentsPath = path.split(delimiterToUse);
    String[] segmentsOther = other.split(delimiterToUse);
    boolean _endsWith = path.endsWith("*");
    boolean _not = (!_endsWith);
    if (_not) {
      segmentsPath = ObjectArrays.<String>concat(segmentsPath, "");
    }
    boolean _endsWith_1 = other.endsWith("*");
    boolean _not_1 = (!_endsWith_1);
    if (_not_1) {
      segmentsOther = ObjectArrays.<String>concat(segmentsOther, "");
    }
    int matchingSegments = (-1);
    final String[] _converted_segmentsOther = (String[])segmentsOther;
    int _size = ((List<String>)Conversions.doWrapArray(_converted_segmentsOther)).size();
    final String[] _converted_segmentsPath = (String[])segmentsPath;
    int _size_1 = ((List<String>)Conversions.doWrapArray(_converted_segmentsPath)).size();
    boolean _greaterThan = (_size > _size_1);
    if (_greaterThan) {
      return matchingSegments;
    }
    final String[] _converted_segmentsOther_1 = (String[])segmentsOther;
    int _size_2 = ((List<String>)Conversions.doWrapArray(_converted_segmentsOther_1)).size();
    final String[] _converted_segmentsPath_1 = (String[])segmentsPath;
    int _size_3 = ((List<String>)Conversions.doWrapArray(_converted_segmentsPath_1)).size();
    boolean _lessThan = (_size_2 < _size_3);
    if (_lessThan) {
      final String[] _converted_segmentsOther_2 = (String[])segmentsOther;
      int _size_4 = ((List<String>)Conversions.doWrapArray(_converted_segmentsOther_2)).size();
      int _minus = (_size_4 - 1);
      boolean _equals = "*".equals(segmentsOther[_minus]);
      boolean _not_2 = (!_equals);
      if (_not_2) {
        return matchingSegments;
      }
    }
    for (int i = 0; (i < ((List<String>)Conversions.doWrapArray(segmentsOther)).size()); i++) {
      {
        final String s1 = segmentsPath[i];
        final String s2 = segmentsOther[i];
        boolean _equals_1 = s1.equals(s2);
        if (_equals_1) {
          matchingSegments++;
        } else {
          boolean _equals_2 = "*".equals(s2);
          if (_equals_2) {
            return matchingSegments;
          } else {
            return (-1);
          }
        }
      }
    }
    return matchingSegments;
  }
}
