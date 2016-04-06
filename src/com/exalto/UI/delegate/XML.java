package com.exalto.UI.delegate;


/* A set of the constants (tags and attributes) in
   in xhtml. To add a new tag make changes in the
   following:

    1. Add a Tag object for the new tag (line 105)
    2. Add the object to the allTags member array
        (line 190)
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class XML
{

    /**
     * Typesafe enumeration for a XMLtag.
     */
    public static class Tag
    {

        public Tag()
        {
        }

        /**
         * Creates a new <code>Tag</code> with the specified <code>id</code>,
         * and with <code>causesBreak</code> and <code>isBlock</code>
         * set to <code>false</code>.
         *
         * @param id  the id of the new tag
         */
        protected Tag(String id)
        {
            this(id, false, false);
        }

        /**
         * Creates a new <code>Tag</code> with the specified <code>id</code>;
         * <code>causesBreak</code> and <code>isBlock</code> are defined
         * by the user.
         *
         * @param id the id of the new tag
         * @param causesBreak  <code>true</code> if this tag
         *    causes a break to the flow of data
         * @param isBlock <code>true</code> if the tag is used
         *    to add structure to a document
         */
        protected Tag(String id, boolean causesBreak, boolean isBlock)
        {
            name = id;
            this.breakTag = causesBreak;
            this.blockTag = isBlock;
        }

        /**
         * Returns <code>true</code> if this tag is a block
         * tag, which is a tag used to add structure to a
         * document.
         *
         * @return <code>true</code> if this tag is a block
         *   tag, otherwise returns <code>false</code>
         */
        public boolean isBlock()
        {
            return blockTag;
        }

        /**
         * Returns <code>true</code> if this tag causes a
         * line break to the flow of data, otherwise returns
         * <code>false</code>.
         *
         * @return <code>true</code> if this tag causes a
         *   line break to the flow of data, otherwise returns
         *   <code>false</code>
         */
        public boolean breaksFlow()
        {
            return breakTag;
        }

        /**
         * Returns <code>true</code> if this tag is pre-formatted,
         * which is true if the tag is either <code>PRE</code> or
         * <code>TEXTAREA</code>.
         *
         * @return <code>true</code> if this tag is pre-formatted,
         *   otherwise returns <code>false</code>
         */
        /*public boolean isPreformatted() {
            return (this == PRE || this == TEXTAREA);
        }*/ //Aloke

        /**
         * Returns the string representation of the
         * tag.
         *
         * @return the <code>String</code> representation of the tag
         */
        public String toString()
        {
            return name;
        }

        boolean blockTag;
        boolean breakTag;
        String name;
        boolean unknown;

        // --- Tag Names -----------------------------------

        public static final Tag BODY = new Tag("body");
        public static final Tag TITLE = new Tag("title");
        public static final Tag P = new Tag("p");
        public static final Tag HEAD = new Tag("head");
        public static final Tag H1 = new Tag("h1");
        public static final Tag IMPLIED = new Tag("p-implied");
        public static final Tag CONTENT = new Tag("content");
        public static final Tag COMMENT = new Tag("comment");
	 
        static final Tag allTags[] = {
            BODY, TITLE, P, HEAD, H1, IMPLIED, CONTENT, COMMENT
        };
    }

    // There is no unique instance of UnknownTag, so we allow it to be
    // Serializable.
    public static class UnknownTag extends Tag implements Serializable
    {

        /**
         * Creates a new <code>UnknownTag</code> with the specified
         * <code>id</code>.
         * @param id the id of the new tag
         */
        public UnknownTag(String id)
        {
            super(id);
        }

        /**
         * Returns the hash code which corresponds to the string
         * for this tag.
         */
        public int hashCode()
        {
            return toString().hashCode();
        }

        /**
         * Compares this object to the specifed object.
         * The result is <code>true</code> if and only if the argument is not
         * <code>null</code> and is an <code>UnknownTag</code> object
         * with the same name.
         *
         * @param     obj   the object to compare this tag with
         * @return    <code>true</code> if the objects are equal;
         *            <code>false</code> otherwise
         */
        public boolean equals(Object obj)
        {
            if (obj instanceof UnknownTag)
            {
                return toString().equals(obj.toString());
            }
            return false;
        }

        private void writeObject(java.io.ObjectOutputStream s)
                throws IOException
        {
            s.defaultWriteObject();
            s.writeBoolean(blockTag);
            s.writeBoolean(breakTag);
            s.writeBoolean(unknown);
            s.writeObject(name);
        }

        private void readObject(ObjectInputStream s)
                throws ClassNotFoundException, IOException
        {
            s.defaultReadObject();
            blockTag = s.readBoolean();
            breakTag = s.readBoolean();
            unknown = s.readBoolean();
            name = (String) s.readObject();
        }
    }

    public static Tag[] getAllTags()
    {
        Tag[] tags = new Tag[Tag.allTags.length];
        System.arraycopy(Tag.allTags, 0, tags, 0, Tag.allTags.length);
        return tags;
    }

    public static final class Attribute
    {

        Attribute(String id)
        {
            name = id;
        }

        public String toString()
        {
            return name;
        }

        private String name;

        public static final Attribute ENDTAG = new Attribute("endtag");
    }
    /*public static final Attribute SIZE = new Attribute("size");
    public static final Attribute COLOR = new Attribute("color");
    public static final Attribute CLEAR = new Attribute("clear");
    public static final Attribute BACKGROUND = new Attribute("background");
    public static final Attribute BGCOLOR = new Attribute("bgcolor");
    public static final Attribute TEXT = new Attribute("text");
    public static final Attribute LINK = new Attribute("link");
    public static final Attribute VLINK = new Attribute("vlink");
    public static final Attribute ALINK = new Attribute("alink");
    public static final Attribute WIDTH = new Attribute("width");
    public static final Attribute HEIGHT = new Attribute("height");
    public static final Attribute ALIGN = new Attribute("align");
    public static final Attribute NAME = new Attribute("name");
    public static final Attribute HREF = new Attribute("href");
    public static final Attribute REL = new Attribute("rel");
    public static final Attribute REV = new Attribute("rev");
    public static final Attribute TITLE = new Attribute("title");
    public static final Attribute TARGET = new Attribute("target");
    public static final Attribute SHAPE = new Attribute("shape");
    public static final Attribute COORDS = new Attribute("coords");
    public static final Attribute ISMAP = new Attribute("ismap");
    public static final Attribute NOHREF = new Attribute("nohref");
    public static final Attribute ALT = new Attribute("alt");
    public static final Attribute ID = new Attribute("id");
    public static final Attribute SRC = new Attribute("src");
    public static final Attribute HSPACE = new Attribute("hspace");
    public static final Attribute VSPACE = new Attribute("vspace");
    public static final Attribute USEMAP = new Attribute("usemap");
    public static final Attribute LOWSRC = new Attribute("lowsrc");
    public static final Attribute CODEBASE = new Attribute("codebase");
    public static final Attribute CODE = new Attribute("code");
    public static final Attribute ARCHIVE = new Attribute("archive");
    public static final Attribute VALUE = new Attribute("value");
    public static final Attribute VALUETYPE = new Attribute("valuetype");
    public static final Attribute TYPE = new Attribute("type");
    public static final Attribute CLASS = new Attribute("class");
    public static final Attribute STYLE = new Attribute("style");
    public static final Attribute LANG = new Attribute("lang");
    public static final Attribute FACE = new Attribute("face");
    public static final Attribute DIR = new Attribute("dir");
    public static final Attribute DECLARE = new Attribute("declare");
    public static final Attribute CLASSID = new Attribute("classid");
    public static final Attribute DATA = new Attribute("data");
    public static final Attribute CODETYPE = new Attribute("codetype");
    public static final Attribute STANDBY = new Attribute("standby");
    public static final Attribute BORDER = new Attribute("border");
    public static final Attribute SHAPES = new Attribute("shapes");
    public static final Attribute NOSHADE = new Attribute("noshade");
    public static final Attribute COMPACT = new Attribute("compact");
    public static final Attribute START = new Attribute("start");
    public static final Attribute ACTION = new Attribute("action");
    public static final Attribute METHOD = new Attribute("method");
    public static final Attribute ENCTYPE = new Attribute("enctype");
    public static final Attribute CHECKED = new Attribute("checked");
    public static final Attribute MAXLENGTH = new Attribute("maxlength");
    public static final Attribute MULTIPLE = new Attribute("multiple");
    public static final Attribute SELECTED = new Attribute("selected");
    public static final Attribute ROWS = new Attribute("rows");
    public static final Attribute COLS = new Attribute("cols");
    public static final Attribute DUMMY = new Attribute("dummy");
    public static final Attribute CELLSPACING = new Attribute("cellspacing");
    public static final Attribute CELLPADDING = new Attribute("cellpadding");
    public static final Attribute VALIGN = new Attribute("valign");
    public static final Attribute HALIGN = new Attribute("halign");
    public static final Attribute NOWRAP = new Attribute("nowrap");
    public static final Attribute ROWSPAN = new Attribute("rowspan");
    public static final Attribute COLSPAN = new Attribute("colspan");
    public static final Attribute PROMPT = new Attribute("prompt");
    public static final Attribute HTTPEQUIV = new Attribute("http-equiv");
    public static final Attribute CONTENT = new Attribute("content");
    public static final Attribute LANGUAGE = new Attribute("language");
    public static final Attribute VERSION = new Attribute("version");
    public static final Attribute N = new Attribute("n");
    public static final Attribute FRAMEBORDER = new Attribute("frameborder");
    public static final Attribute MARGINWIDTH = new Attribute("marginwidth");
    public static final Attribute MARGINHEIGHT = new Attribute("marginheight");
    public static final Attribute SCROLLING = new Attribute("scrolling");
    public static final Attribute NORESIZE = new Attribute("noresize");

    public static final Attribute COMMENT = new Attribute("comment");
    static final Attribute MEDIA = new Attribute("media");

    static final Attribute allAttributes[] = {
        FACE,
        COMMENT,
        SIZE,
        COLOR,
        CLEAR,
        BACKGROUND,
        BGCOLOR,
        TEXT,
        LINK,
        VLINK,
        ALINK,
        WIDTH,
        HEIGHT,
        ALIGN,
        NAME,
        HREF,
            REL,
            REV,
            TITLE,
            TARGET,
            SHAPE,
            COORDS,
            ISMAP,
            NOHREF,
            ALT,
            ID,
            SRC,
            HSPACE,
            VSPACE,
            USEMAP,
            LOWSRC,
            CODEBASE,
            CODE,
            ARCHIVE,
            VALUE,
            VALUETYPE,
            TYPE,
            CLASS,
            STYLE,
            LANG,
            DIR,
            DECLARE,
            CLASSID,
            DATA,
            CODETYPE,
            STANDBY,
            BORDER,
            SHAPES,
            NOSHADE,
            COMPACT,
            START,
            ACTION,
            METHOD,
            ENCTYPE,
            CHECKED,
            MAXLENGTH,
            MULTIPLE,
            SELECTED,
            ROWS,
            COLS,
            DUMMY,
            CELLSPACING,
            CELLPADDING,
            VALIGN,
            HALIGN,
            NOWRAP,
            ROWSPAN,
            COLSPAN,
            PROMPT,
            HTTPEQUIV,
            CONTENT,
            LANGUAGE,
            VERSION,
            N,
            FRAMEBORDER,
            MARGINWIDTH,
            MARGINHEIGHT,
            SCROLLING,
            NORESIZE,
            MEDIA,
        ENDTAG
    };
    }

    static final Hashtable tagHashtable = new Hashtable(Tag.allTags.length);

    // Maps from AttributeSet key to XML.Tag.
    private static final Hashtable asMapping = new Hashtable(XML.Tag.allTags.length);

    static {

        for (int i = 0; i < Tag.allTags.length; i++ ) {
            tagHashtable.put(Tag.allTags[i].toString(), Tag.allTags[i]);
            StyleContext.registerStaticAttributeKey(Tag.allTags[i]);
        }
        StyleContext.registerStaticAttributeKey(Tag.IMPLIED);
        StyleContext.registerStaticAttributeKey(Tag.CONTENT);
        StyleContext.registerStaticAttributeKey(Tag.COMMENT);

        //Mappings from AttributeSet to XML Tags
        SimpleAttributeSet set;

        //MI
        set = new SimpleAttributeSet();
        set.addAttribute(StyleConstants.Bold, new Boolean("true"));
        set.addAttribute(StyleConstants.Italic, new Boolean("true"));
        asMapping.put(XML.Tag.MI, set);

        //MO
        set = new SimpleAttributeSet();
        set.addAttribute(StyleConstants.Bold, new Boolean("true"));
        asMapping.put(XML.Tag.MO, set);

    }



    public static Tag getTag(String tagName) {

    Object t =  tagHashtable.get(tagName);
    return (t == null ? null : (Tag)t);
    }

    static Tag getTagForStyleConstantsKey(StyleConstants sc) {
        return (Tag)asMapping.get(sc);
    }

    public static int getIntegerAttributeValue(AttributeSet attr,
                           Attribute key, int def) {
    int value = def;
    String istr = (String) attr.getAttribute(key);
    if (istr != null) {
        try {
        value = Integer.valueOf(istr).intValue();
        } catch (NumberFormatException e) {
        value = def;
        }
    }
    return value;
    }

    //  This is used in cases where the value for the attribute has not
    //  been specified.
    //
    public static final String NULL_ATTRIBUTE_VALUE = "#DEFAULT";

    // size determined similar to size of tagHashtable
    private static final Hashtable attHashtable = new Hashtable(77);

    static {

    for (int i = 0; i < Attribute.allAttributes.length; i++ ) {
        attHashtable.put(Attribute.allAttributes[i].toString(), Attribute.allAttributes[i]);
    }
    }

    public static Attribute[] getAllAttributeKeys() {
    Attribute[] attributes = new Attribute[Attribute.allAttributes.length];
    System.arraycopy(Attribute.allAttributes, 0,
             attributes, 0, Attribute.allAttributes.length);
    return attributes;
    }

    public static Attribute getAttributeKey(String attName) {
    Object a = attHashtable.get(attName);
    if (a == null) {
      return null;
    }
    return (Attribute)a;
    }*/
}
