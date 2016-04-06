package com.exalto.UI.util;

import org.apache.log4j.Level;


// Referenced classes of package org.apache.log4j:
//            Priority

public class OutputLevel extends Level
{

    public OutputLevel(int level, String levelStr, int syslogEquivalent)
    {
        super(level, levelStr, syslogEquivalent);
    }

   

    public static OutputLevel toLevel(int val, Level defaultLevel)
    {
         return Output;
    }

    public static OutputLevel toLevel(String sArg, Level defaultLevel)
    {
        
            return Output;
        
       
    }

    public static final OutputLevel Output = new OutputLevel(60000, "OUTPUT",
0);

}
