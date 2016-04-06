package com.exalto.UI.util;


public interface MatchResult {
        /**
     * Returns the start index of the match.
     *
     * @return  The index of the first character matched
     *
     * @throws  IllegalStateException
     *          If no match has yet been attempted,
     *          or if the previous match operation failed
     */
    public int start();

    /**
     * Returns the offset after the last character matched.  </p>
     *
     * @return  @return  The offset after the last character matched 
     *
     * @throws  IllegalStateException
     *          If no match has yet been attempted,
     *          or if the previous match operation failed
     */
    public int end();

    
}
