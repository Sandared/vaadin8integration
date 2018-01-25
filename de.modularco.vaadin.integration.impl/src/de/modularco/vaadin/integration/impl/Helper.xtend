package de.modularco.vaadin.integration.impl

class Helper {
	public static val UNIQUE_ID_PROPERTY = 'de.unia.smds.vaadin.integration.uid'
	
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
		val segmentsPath = path.split(delimiter)
		val segmentsOther = other.split(delimiter)
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