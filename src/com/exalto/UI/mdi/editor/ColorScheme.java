/**
 * Copyright (c) 2006, Sun Microsystems, Inc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following 
 *     disclaimer in the documentation and/or other materials provided 
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.exalto.UI.mdi.editor;

import java.awt.Color;

/**
 *
 * @author sky
 */
public final class ColorScheme {
    private static final int COLORS[] = new int[] {
         0,  81, 212,     141, 180, 242,
        44, 161,  11,     150, 236, 134,
       230,  23,  23,     247, 170, 170,
       176,  39, 174,     229, 170, 234,
        73,  43, 161,     191, 182, 235
    };
    private static final ColorScheme[] SCHEMES;
    
    private final Color innerColor;
    private final Color outerColor;
    

    static {
        int schemeCount = COLORS.length / 6;
        SCHEMES = new ColorScheme[schemeCount];
        for (int i = 0; i < schemeCount; i++) {
            int x = i * 6;
            SCHEMES[i] = new ColorScheme(
                    new Color(COLORS[x], COLORS[x + 1], COLORS[x + 2]),
                    new Color(COLORS[x + 3], COLORS[x + 4], COLORS[x + 5]));
        }
    }
    
    public static int getColorSchemeCount() {
        return SCHEMES.length;
    }
    
    public static ColorScheme getScheme(int index) {
        return SCHEMES[Math.min(SCHEMES.length - 1, index)];
    }
    
    private ColorScheme(Color outerColor, Color innerColor) {
        this.innerColor = innerColor;
        this.outerColor = outerColor;
    }
    
    public Color getInnerColor() {
        return innerColor;
    }
    
    public Color getOuterColor() {
        return outerColor;
    }
}
