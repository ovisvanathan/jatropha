package com.exalto.UI.util;


public class Matcher implements MatchResult {
	
	String pattern;
	String searchString;
	
	    /**
     * The range of string that last matched the pattern. If the last
     * match failed then first is -1; last initially holds 0 then it
     * holds the index of the end of the last match (which is where the
     * next search starts).
     */
    int first = -1, last = 0;

	
	
	public Matcher(String pattern, String searchString) {
		this.pattern = pattern;
		this.searchString = searchString;
	}
	
	public boolean find() {

			System.out.println(" searchString " + searchString);

		if(searchString.indexOf(pattern) > -1) {
			System.out.println(" ret true find");
		    return true;
		}    
		return false;
	}
	
	public String pattern() {
		return pattern;
	}

	/**
     * Returns the match state of this matcher as a {@link MatchResult}.
     * The result is unaffected by subsequent operations performed upon this
     * matcher.
     *
     * @return  a <code>MatchResult</code> with the state of this matcher
     */
    public MatchResult toMatchResult() {
        Matcher result = new Matcher(this.pattern, searchString);
        return result;
    }
    
        /**
     * Returns the start index of the previous match.  </p>
     *
     * @return  The index of the first character matched
     *
     * @throws  IllegalStateException
     *          If no match has yet been attempted,
     *          or if the previous match operation failed
     */
    public int start() {
        if (first < 0)
            throw new IllegalStateException("No match available");
        return first;
    }


    /**
     * Returns the offset after the last character matched.  </p>
     *
     * @return  The offset after the last character matched
     *
     * @throws  IllegalStateException
     *          If no match has yet been attempted,
     *          or if the previous match operation failed
     */
    public int end() {
        if (first < 0)
            throw new IllegalStateException("No match available");
        return last;
    }

	
}
