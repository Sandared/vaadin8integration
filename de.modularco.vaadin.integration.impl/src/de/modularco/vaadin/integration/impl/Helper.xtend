package de.modularco.vaadin.integration.impl

import com.google.common.primitives.Bytes
import com.google.common.collect.ObjectArrays

class Helper {
	
	public static val UNIQUE_ID_PROPERTY = 'de.unia.smds.vaadin.integration.uid'
	
	public static val VAADIN_VERSION_PREFIX = 'vaadin-8.2.1'
	
	private static val VAADIN_ROOT_ALIAS_FORMAT = "/%s/VAADIN/%s"
    private static val VAADIN_ROOT_FORMAT = "/VAADIN/%s"

    private static val VAADIN_THEME_ALIAS_FORMAT = "/%s/VAADIN/themes/%s"
    private static val VAADIN_WIDGETSET_ALIAS_FORMAT = "/%s/VAADIN/widgetsets/%s"

    private static val VAADIN_THEME_PATH_FORMAT = "/VAADIN/themes/%s"
    private static val VAADIN_WIDGETSET_PATH_FORMAT = "/VAADIN/widgetsets/%s"


    /**
     * Returns the alias for the theme given a the theme name and a path prefix.
     *
     * @param themeName
     *            the theme name
     * @param pathPrefix
     *            the prefix for the /VAADIN/ folder
     * @return the alias
     */
    public static def getThemeAlias(String themeName, String pathPrefix) {
        return VAADIN_THEME_ALIAS_FORMAT.format(pathPrefix, themeName)
    }

    /**
     * Returns the expected/default path of the theme folder in the source
     * bundle.
     *
     * @param themeName
     *            the name of the theme
     * @return the path of the theme folder in the source bundle
     */
    public static def String getThemePath(String themeName) {
        return VAADIN_THEME_PATH_FORMAT.format(themeName)
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
    public static def String getWidgetsetAlias(String widgetsetName,
            String pathPrefix) {
        return VAADIN_WIDGETSET_ALIAS_FORMAT.format(pathPrefix,
                widgetsetName);
    }

    /**
     * Returns the expected/default path of the widgetset folder in the source
     * bundle.
     *
     * @param widgetsetName
     *            the name of the widgetset
     * @return the path of the widgetset folder in the source bundle
     */
    public static def String getWidgetsetPath(String widgetsetName) {
        return VAADIN_WIDGETSET_PATH_FORMAT.format(widgetsetName);
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
    public static def String getRootResourceAlias(String resourceName,
            String pathPrefix) {
        return VAADIN_ROOT_ALIAS_FORMAT.format(pathPrefix,
                resourceName);
    }

    /**
     * Returns the expected/default path of the resource in the source bundle.
     *
     * @param resourceName
     *            the name of the resource
     * @return the path of the resource in the source bundle
     */
    public static def String getRootResourcePath(String resourceName) {
        return VAADIN_ROOT_FORMAT.format(resourceName);
    }
	
	/**
	 * Often the servlet paths of UIs are given like this: /test </br>
	 * Vaadin sends requests to /test/UILD/... which would result in errors, so we use this method to append paths like /test with /* </br>
	 * so that they can handle those requests 
	 */
	static def String getVaadinUrlPattern(String urlPattern) {
		if(urlPattern.endsWith("/"))
			return urlPattern + "*";
		if(urlPattern.endsWith("/*"))
			return urlPattern;
		if(!urlPattern.endsWith("/") && !urlPattern.endsWith("/*"))
			return urlPattern + "/*";
		throw new IllegalArgumentException();
	}
	
	/**
	 * This method returns an integer representing the similarity of relative paths as it is done with servlet paths </br>
	 * e.g. /test/* and /test/api would return a value of 1 as test is equal and the shorter path ends with an * </br>
	 * /test/ and /test/api would return -1 as they do not match in sense of servlet pattern matching
	 */
	static def int matchingPathSegments(String path, String other, String delimiter){
		if(path == null || other == null || delimiter == null || delimiter.length != 1)
			throw new IllegalArgumentException('''path: «path», other: «other», delimiter: «delimiter»''')
	
		var delimiterToUse = delimiter
		
		// delimiter is a regex so escape special characters
		if('\\^$.|?*+()[{'.contains(delimiterToUse))
			delimiterToUse = '\\' + delimiterToUse
		
		var segmentsPath = path.split(delimiterToUse)
		var segmentsOther = other.split(delimiterToUse)
		
		// Make sure that both paths are equal in segment length. 
		// If for example the first is /test and the other is /test/*
		// /test would have segment length 2 and will be added 1 extra empty segment
		// thus it will have 3 segments as well as /test/*
		if(!path.endsWith('*')) 
			segmentsPath = ObjectArrays.concat(segmentsPath, '')
		if(!other.endsWith('*')) 
			segmentsOther = ObjectArrays.concat(segmentsOther, '')
		
		var matchingSegments = -1
		
		if(segmentsOther.size > segmentsPath.size)
			return matchingSegments
		if(segmentsOther.size < segmentsPath.size)
			if(!'*'.equals(segmentsOther.get(segmentsOther.size -1)))
				return matchingSegments
		
		for(var i = 0; i < segmentsOther.size; i++){
			val s1 = segmentsPath.get(i)
			val s2 = segmentsOther.get(i)
			if(s1.equals(s2))
				matchingSegments++
			else{
				if('*'.equals(s2)){
					return matchingSegments
				}
				else{
					return -1
				}
			}
		}
		return matchingSegments
	}
}
