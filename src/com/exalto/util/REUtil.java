package com.exalto.util;

import java.util.*;
import java.io.*;
import java.net.*;


public class REUtil {

	//{{{ globToRE() method
	/**
	 * Converts a Unix-style glob to a regular expression.<p>
	 *
	 * ? becomes ., * becomes .*, {aa,bb} becomes (aa|bb).
	 * @param glob The glob pattern
	 */
	public static String globToRE(String glob)
	{
		final Object NEG = new Object();
		final Object GROUP = new Object();
		Stack state = new Stack();

		StringBuffer buf = new StringBuffer();
		boolean backslash = false;

		for(int i = 0; i < glob.length(); i++)
		{
			char c = glob.charAt(i);
			if(backslash)
			{
				buf.append('\\');
				buf.append(c);
				backslash = false;
				continue;
			}

			switch(c)
			{
			case '\\':
				backslash = true;
				break;
			case '?':
				buf.append('.');
				break;
			case '.':
			case '+':
			case '(':
			case ')':
				buf.append('\\');
				buf.append(c);
				break;
			case '*':
				buf.append(".*");
				break;
			case '|':
				if(backslash)
					buf.append("\\|");
				else
					buf.append('|');
				break;
			case '{':
				buf.append('(');
				if(i + 1 != glob.length() && glob.charAt(i + 1) == '!')
				{
					buf.append('?');
					state.push(NEG);
				}
				else
					state.push(GROUP);
				break;
			case ',':
				if(!state.isEmpty() && state.peek() == GROUP)
					buf.append('|');
				else
					buf.append(',');
				break;
			case '}':
				if(!state.isEmpty())
				{
					buf.append(")");
					if(state.pop() == NEG)
						buf.append(".*");
				}
				else
					buf.append('}');
				break;
			default:
				buf.append(c);
			}
		}

		return buf.toString();
	} //}}}
	
	
		public static int compareStrings(String str1, String str2, boolean ignoreCase)
		{
			char[] char1 = str1.toCharArray();
			char[] char2 = str2.toCharArray();
	
			int len = Math.min(char1.length,char2.length);
	
			for(int i = 0, j = 0; i < len && j < len; i++, j++)
			{
				char ch1 = char1[i];
				char ch2 = char2[j];
				if(Character.isDigit(ch1) && Character.isDigit(ch2)
					&& ch1 != '0' && ch2 != '0')
				{
					int _i = i + 1;
					int _j = j + 1;
	
					for(; _i < char1.length; _i++)
					{
						if(!Character.isDigit(char1[_i]))
						{
							//_i--;
							break;
						}
					}
	
					for(; _j < char2.length; _j++)
					{
						if(!Character.isDigit(char2[_j]))
						{
							//_j--;
							break;
						}
					}
	
					int len1 = _i - i;
					int len2 = _j - j;
					if(len1 > len2)
						return 1;
					else if(len1 < len2)
						return -1;
					else
					{
						for(int k = 0; k < len1; k++)
						{
							ch1 = char1[i + k];
							ch2 = char2[j + k];
							if(ch1 != ch2)
								return ch1 - ch2;
						}
					}
	
					i = _i - 1;
					j = _j - 1;
				}
				else
				{
					if(ignoreCase)
					{
						ch1 = Character.toLowerCase(ch1);
						ch2 = Character.toLowerCase(ch2);
					}
	
					if(ch1 != ch2)
						return ch1 - ch2;
				}
			}
	
			return char1.length - char2.length;
		} //}}}

	//{{{ Compare interface
	/**
	 * An interface for comparing objects. This is a hold-over from
	 * they days when jEdit had its own sorting API due to JDK 1.1
	 * compatibility requirements. Use <code>java.util.Comparable</code>
	 * instead.
	 */
	public interface Compare extends Comparator
	{
		int compare(Object obj1, Object obj2);
	} //}}}
	
	/**
		 * Checks if the specified string is a URL.
		 * @param str The string to check
		 * @return True if the string is a URL, false otherwise
		 */
		public static boolean isURL(String str)
		{
			int fsIndex = Math.max(str.indexOf(File.separatorChar),
				str.indexOf('/'));
			if(fsIndex == 0) // /etc/passwd
				return false;
			else if(fsIndex == 2) // C:\AUTOEXEC.BAT
				return false;
	
			int cIndex = str.indexOf(':');
			if(cIndex <= 1) // D:\WINDOWS, or doesn't contain : at all
				return false;
	
			String protocol = str.substring(0,cIndex);
		
			try
			{
				new URL(str);
				return true;
			}
			catch(MalformedURLException mf)
			{
				return false;
			}
		} //}}}

		/**
		 * Constructs an absolute path name from a directory and another
		 * path name. This method is VFS-aware.
		 * @param parent The directory
		 * @param path The path name
		 */
		public static String constructPath(String parent, String path)
		{
			if(isAbsolutePath(path))
				return canonPath(path);
	
			return null;
			
		} //}}}
	
/**
	 * Returns the canonical form of the specified path name. Currently
	 * only expands a leading <code>~</code>. <b>For local path names
	 * only.</b>
	 * @param path The path name
	 * @since jEdit 4.0pre2
	 */
	public static String canonPath(String path)
	{
		if(path.length() == 0)
			return path;

		if(path.startsWith("file://"))
			path = path.substring("file://".length());
		else if(path.startsWith("file:"))
			path = path.substring("file:".length());
		else if(isURL(path))
			return path;

		if(File.separatorChar == '\\')
		{
			// get rid of mixed paths on Windows
			path = path.replace('/','\\');
			// also get rid of trailing spaces on Windows
			int trim = path.length();
			while(path.charAt(trim - 1) == ' ')
				trim--;
			path = path.substring(0,trim);
		}

		if(path.startsWith("~" + File.separator))
		{
			path = path.substring(2);
			String home = System.getProperty("user.home");

			if(home.endsWith(File.separator))
				return home + path;
			else
				return home + File.separator + path;
		}
		else if(path.equals("~"))
			return System.getProperty("user.home");
		else
			return path;
	} //}}}

		/**
		 * Resolves any symbolic links in the path name specified
		 * using <code>File.getCanonicalPath()</code>. <b>For local path
		 * names only.</b>
		 * @since jEdit 4.2pre1
		 */
		public static String resolveSymlinks(String path)
		{
			if(isURL(path))
				return path;
	
			// 18 nov 2003: calling this on a drive letter on Windows causes
			// drive access
			
			try
			{
				return new File(path).getCanonicalPath();
			}
			catch(IOException io)
			{
				return path;
			}
		} //}}}

	/**
	 * Returns if the specified path name is an absolute path or URL.
	 * @since jEdit 4.1pre11
	 */
	public static boolean isAbsolutePath(String path)
	{
		if(isURL(path))
			return true;
		else if(path.startsWith("~/") || path.startsWith("~" + File.separator) || path.equals("~"))
			return true;
		else
			return true;
			
	
	} //}}}

	/**
	 * Returns the last component of the specified path.
	 * This method is VFS-aware.
	 * @param path The path name
	 */
	public static String getFileName(String path)
	{
		return new File(path).getName();
	} //}}}
	

}