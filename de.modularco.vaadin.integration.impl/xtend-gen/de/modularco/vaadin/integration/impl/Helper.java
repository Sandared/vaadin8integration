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
    final String[] segmentsPath = path.split(delimiter);
    final String[] segmentsOther = other.split(delimiter);
    int matchingSegments = (-1);
    int _size = ((List<String>)Conversions.doWrapArray(segmentsOther)).size();
    int _size_1 = ((List<String>)Conversions.doWrapArray(segmentsPath)).size();
    boolean _greaterThan = (_size > _size_1);
    if (_greaterThan) {
      return matchingSegments;
    }
    int _size_2 = ((List<String>)Conversions.doWrapArray(segmentsOther)).size();
    int _size_3 = ((List<String>)Conversions.doWrapArray(segmentsPath)).size();
    boolean _lessThan = (_size_2 < _size_3);
    if (_lessThan) {
      int _size_4 = ((List<String>)Conversions.doWrapArray(segmentsOther)).size();
      int _minus = (_size_4 - 1);
      boolean _equals = "*".equals(segmentsOther[_minus]);
      boolean _not = (!_equals);
      if (_not) {
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
