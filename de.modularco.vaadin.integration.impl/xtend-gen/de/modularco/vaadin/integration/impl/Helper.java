package de.modularco.vaadin.integration.impl;

import com.google.common.base.Objects;
import com.google.common.collect.ObjectArrays;
import java.util.List;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;

@SuppressWarnings("all")
public class Helper {
  public final static String UNIQUE_ID_PROPERTY = "de.unia.smds.vaadin.integration.uid";
  
  public final static String VAADIN_VERSION_PREFIX = "vaadin-8.2.1";
  
  private final static String VAADIN_ROOT_ALIAS_FORMAT = "/%s/VAADIN/%s";
  
  private final static String VAADIN_ROOT_FORMAT = "/VAADIN/%s";
  
  private final static String VAADIN_THEME_ALIAS_FORMAT = "/%s/VAADIN/themes/%s";
  
  private final static String VAADIN_WIDGETSET_ALIAS_FORMAT = "/%s/VAADIN/widgetsets/%s";
  
  private final static String VAADIN_THEME_PATH_FORMAT = "/VAADIN/themes/%s";
  
  private final static String VAADIN_WIDGETSET_PATH_FORMAT = "/VAADIN/widgetsets/%s";
  
  /**
   * Returns the alias for the theme given a the theme name and a path prefix.
   * 
   * @param themeName
   *            the theme name
   * @param pathPrefix
   *            the prefix for the /VAADIN/ folder
   * @return the alias
   */
  public static String getThemeAlias(final String themeName, final String pathPrefix) {
    return String.format(Helper.VAADIN_THEME_ALIAS_FORMAT, pathPrefix, themeName);
  }
  
  /**
   * Returns the expected/default path of the theme folder in the source
   * bundle.
   * 
   * @param themeName
   *            the name of the theme
   * @return the path of the theme folder in the source bundle
   */
  public static String getThemePath(final String themeName) {
    return String.format(Helper.VAADIN_THEME_PATH_FORMAT, themeName);
  }
  
  /**
   * Returns the alias for a widgetset given a the widgetset name and a path
   * prefix.
   * 
   * @param widgetsetName
   *            the name of the widgetset
   * @param pathPrefix
   *            the prefix for the /VAADIN/ folder
   * @return the alias
   */
  public static String getWidgetsetAlias(final String widgetsetName, final String pathPrefix) {
    return String.format(Helper.VAADIN_WIDGETSET_ALIAS_FORMAT, pathPrefix, widgetsetName);
  }
  
  /**
   * Returns the expected/default path of the widgetset folder in the source
   * bundle.
   * 
   * @param widgetsetName
   *            the name of the widgetset
   * @return the path of the widgetset folder in the source bundle
   */
  public static String getWidgetsetPath(final String widgetsetName) {
    return String.format(Helper.VAADIN_WIDGETSET_PATH_FORMAT, widgetsetName);
  }
  
  /**
   * Returns the alias for a resource that will placed under the /VAADIN/
   * folder.
   * 
   * @param resourceName
   *            the name of the resource
   * @param pathPrefix
   *            the prefix for the /VAADIN/ folder
   * @return the alias
   */
  public static String getRootResourceAlias(final String resourceName, final String pathPrefix) {
    return String.format(Helper.VAADIN_ROOT_ALIAS_FORMAT, pathPrefix, resourceName);
  }
  
  /**
   * Returns the expected/default path of the resource in the source bundle.
   * 
   * @param resourceName
   *            the name of the resource
   * @return the path of the resource in the source bundle
   */
  public static String getRootResourcePath(final String resourceName) {
    return String.format(Helper.VAADIN_ROOT_FORMAT, resourceName);
  }
  
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
