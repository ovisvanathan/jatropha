package com.exalto.UI.painter;


/**
 * @version CVS $Id: CompilingClassLoader.java 433543 2006-08-22 06:22:54Z crossley $
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AllPermission;
import java.security.ProtectionDomain;
import java.security.Permissions;
import java.security.*;

/**
This class is almost a trivial subclass of URLClassLoader.

The one thing that I'm baffled by is that if I overload loadClass(),
then newly loaded classes have their class loader set to this class.
If I don't, then new classes have their class loader set to the
primordial class loader.

I've no idea why this behaves this way. I've tested it under Linux JDK 1.2
and Windows JDK 1.3 beta.

I need newly loaded classes to have their class loader set to
FileSystemClassLoader so that can be reloaded when the user clicks
"Re-Exercise".

Why not just find the class file in the file system and call
defineClass() like almost all other custom class loaders do? Because
it gets real tricky when you're trying to load classes from signed jar
files and the like. I figured that since URLClassLoader has already
gone to all the trouble to do these things for me, why not use it?

The only downside so far is that classes loaded from the CLASSPATH,
and not the TestClassPath, have their class loader set to the
primordial one, which means they can't be re-exercised properly. This
is why we have the concept of the TestClassPath.

But even with this problem, the benefits outweigh the drawbacks. I get
to reuse a lot of JDK code which works properly even under tricky
security situations.

Please let me know if you have some insight into this
situation. david@enhydratest.com

@author
@version $Revision: 1.15 $ $Date: 2005/06/13 09:28:34 $ */
public class FileSystemClassLoader extends URLClassLoader {
  private String classPath;
  private ArrayList storageUnits;
  private boolean debug = true;
  private ClassLoader exemptClassLoader;
  private ArrayList exemptPackagePrefixList = new ArrayList();
  private boolean second = false;

  public FileSystemClassLoader(URL[] urls, ClassLoader exemptClassLoader) {
    super(urls, exemptClassLoader);
    this.exemptClassLoader = exemptClassLoader;
//      System.out.println("FSCL's excempt CL = " + exemptClassLoader.getClass().getName());
    String classPath = System.getProperty("java.class.path", ".");
//      if (debug) System.out.println("*** CP is " + classPath);
//      setClassPath(classPath);
  } // constructor

//    public FileSystemClassLoader(URL[] urls) {
//      super(urls);
//      second = true;
//    } // constructor


//    public void exemptPackagePrefix(String prefix) {
//      exemptPackagePrefixList.add(prefix + ".");
//    }
  
  public FileSystemClassLoader(URL [] urls) {
	  super(urls);
  }

	public Class loadClass(File file) throws ClassNotFoundException {
		try {
			final FileInputStream inputStream = new FileInputStream(file);
			final long length = file.length();

			if (length > Integer.MAX_VALUE) {
				throw new ClassNotFoundException();
			} // if

			// a safe cast
			final int size = (int) length;

			final byte[] bytes = new byte[ (int) size];

			if (inputStream.read(bytes) != size) {
				throw new ClassNotFoundException();
			} // if
      
      Class result = defineClass(null, bytes, 0, size);
      resolveClass(result);
      return result;
		} // try
		catch (FileNotFoundException e) {
			throw new ClassNotFoundException(e.getMessage());
		} // catch
		catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage());
		} // catch
	} // loadClass()

//      public Class loadClass(String name) throws ClassNotFoundException {
//  //        System.out.println("$$$ loadClass(" + name + ")");
//        return super.loadClass(name, true);
//      } // loadClass()

//      public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        System.out.println("loadClass(" + name + ", " + resolve + ")");
//        return super.loadClass(name, resolve);
//      } // loadClass()


  public String toString() { return "FileSystemClassLoader"; }

//    public void setClassPath(String classPath) {
//      if (!classPath.equals(this.classPath)) {
//        this.classPath = classPath;
//        storageUnits = new ArrayList();
                      
//        // turn the class paths, separated by File.pathSeparator, into an ArrayList of .jar files.
//        StringTokenizer tokens = new StringTokenizer(classPath, File.pathSeparator);
//        while (tokens.hasMoreTokens()) {
//          StorageUnit unit;
//          String nextToken = tokens.nextToken();
//          File f = new File(nextToken);
//          if (f.isDirectory()) {
//            String newurl = "file:" + nextToken;
//            if (nextToken.length() > 0 && nextToken.charAt(nextToken.length() - 1) != File.separatorChar) {
//              newurl = newurl + File.separatorChar;
//            } // if

//  //            System.out.println("adding directory url: " + newurl);
//  //            try {
//  //            addURL(new URL(newurl));
//  //            } // try
//  //            catch (Exception e) {
//  //              e.printStackTrace();
//  //            } // catch

//  //            System.out.println("CP directory: " + nextToken);
//            unit = new DirectoryStorageUnit(f);
//          } // if
//          else {
//            String newurl = "file:" + nextToken;
//  //            System.out.println("adding file url: " + newurl);
//  //            try {
//  //              addURL(new URL(newurl));
//  //            } // try
//  //            catch (Exception e) {
//  //              e.printStackTrace();
//  //            } // catch

//  //            System.out.println("CP jar file: " + nextToken);
//            unit = new JarStorageUnit(nextToken);
//          } // else
//          storageUnits.add(unit);
//        } // while
//      } // if
//    } // setClassPath()
  
//    /**
//    Enumerates all known storage units, searching for the named file, and returns
//    a non-null stream if it can be found.
//    */
//    private byte[] /*InputStream*/ getStream(String path, CodeSourceHolder csh) {
//      final int size = storageUnits.size();

//      for (int index = 0; index < size; ++index) {
//        StorageUnit unit = (StorageUnit) storageUnits.get(index);
//  //        InputStream stream = unit.getStream(path, oi);
//        byte[] stream =  unit.getStream(path, csh);

//        if (stream != null) {
//  //          System.out.println("getStream() success on " + 
//  //                             ((StorageUnit) storageUnits.get(index)).getName());
//          return stream;
//        } // if
//      } // for index

//      return null;
//    } // getStream()
              
//    private byte[] loadClassData(String name, CodeSourceHolder csh) {
//      System.out.println("*** loadClassData(" + name + ")");
//      // load the class data from the class path.
//      InputStream stream = null;

//      try {
//        String entryName = name.replace('.', '/') + ".class";
//  //        OutInteger oi = new OutInteger();
//  //        stream = getStream(entryName, oi);
//        return getStream(entryName, csh);//, oi);

//          if (stream != null) {
//            if (name.startsWith("BigDummy")) System.out.println("@@@@ trying to read from jar");
//            long size = oi.getSize();
//            if (name.startsWith("BigDummy")) System.out.println("@@@@ size = " + size);
//            byte[] data = new byte[ (int) size];
//            int dataread = stream.read(data);
//            if (name.startsWith("BigDummy")) System.out.println("@@@@ dataread = " + dataread);
//            return data;
//  //            DataInputStream input = new DataInputStream(stream);
//  //            input.readFully(data);
//  //            input.close();
//  //            return data;
//          } // if
//          else System.out.println("*** failed to load " + entryName);





//        } // if
//        else {

//        System.out.println("*** trying to load: " + entryName);

//          totalBytes = read(byte[] b,
//                  int off,
//                  int len)
//           throws IOException

//          final File f = new File(entryName);
//          if (f.exists()) {
//            final int size = (int) f.length();
//            if (size > 0) {
//              byte[] data = new byte[size];
//              DataInputStream input = new DataInputStream(new FileInputStream(f));
//              input.readFully(data);
//              input.close();
//              return data;
//            } // if
//          } // if
//  //        } // else
//      } // try
//  //      catch (IOException ioe) {
//  //        ioe.printStackTrace();
//  //        // return null below
//  //      } // catch
//      finally {
//        if (stream != null) {
//          try {
//            stream.close();
//          } // try
//          catch (IOException e) {
//            // ignore. do nothing.
//          } // catch
//        } // if
//      }
    
//  //      return null;
//    } // loadClassData()

//    public Class findClass(String name) throws ClassNotFoundException {
//      System.out.println("************ findClass(" + name + ") ************");
//      return Integer.class;
//    }

  /**
  According the JDK source, loadClass is supposed to be declared as synchronzied.
  For reasons unknown, if this method is commented out, all classes loaded by
  this class loader will have their class loader set to primordial class loader,
  NOT this class (FileSystemClassLoader).
  */
  public synchronized Class loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
    return super.loadClass(name, resolve);
  } // loadClass()


//        System.out.println("$$$$ loadClass(" + name + ", " + resolve + ")");
//        return super.loadClass(name, resolve);

    // first, check if class name's package matches one of the exempt package prefixes
//      final int size = exemptPackagePrefixList.size();
//      for (int index = 0; index < size; ++index) {
//        if (name.startsWith( (String) exemptPackagePrefixList.get(index)) ) {
//          // name is exempt from special class loading. Use the stock class loader.
//          if (debug) System.out.println("*** exempt: " + name);
//          Class result = exemptClassLoader.loadClass(name);
//          System.out.println("exempt: " + result.getName() + ", CL = " + result.getClassLoader().getClass().getName());
//          return result;
//  //          return super.findClass(name);
//        } // if
//      } // for index

//      System.out.println("loading " + name);

//      /*

//      Class c = findLoadedClass(name);
//      if (c == null) {
//        // don't look load system classes in our class loader, because this can mess up Java.
//        boolean direct = true;
//        if ((!name.startsWith("java.")) && (!name.startsWith("javax."))) {
//          direct = false;
//          CodeSourceHolder csh = new CodeSourceHolder();
//          byte[] data = loadClassData(name, csh);
//          if (data == null) System.out.println("*********** loadClassData failed on " + name);
//          if (debug && name.equals("BigDummy")) System.out.println("###(loadClassData(), got back: " + data);
//          if (data != null) {
//            System.out.println("defineClass on data length " + data.length);
//  //            try {
//  //            FileOutputStream fos = new FileOutputStream("testme");
//  //            fos.write(data);
//  //            fos.close();
//  //            } catch (IOException e) {e.printStackTrace();}
//  //            URL u = null;
//  //            try {
//  //            u = new URL("file:.");
//  //            }
//  //            catch (MalformedURLException e) {
//  //              // do nothing
//  //            }
//  //            CodeSource codeSource = new CodeSource(u,

//            Permissions permissions = new Permissions();
//            permissions.add(new AllPermission());
//            ProtectionDomain pd = new ProtectionDomain(csh.codeSource, permissions);

//  //            SecureClassLoader scl = new SecureClassLoader(this);
//  //            c = scl.defineClass(name, data, 0, data.length, 0);
//            CodeSource cs = null;

//            c = defineClass(name, data, 0, data.length, null); // null was cs

//  //            Class superc = super.loadClass(name, resolve);
//  //            if (c == superc) System.out.println("c == superc");
//  //            try {
//  //              Object o = c.newInstance();
//  //              System.out.println("c.newInstance() = " + o);
//  //            }
//  //            catch (Exception e) {e.printStackTrace();}
//  //            System.out.println("c = " + c + ", superc = " + superc);

//            if (c == null) System.out.println("******* defineClass() returned null");
//            if (debug && name.equals("BigDummy")) System.out.println("#### after defineClass(), got back c: " + c);
//          } // if
//        } // if
//        if (c == null) {
//  //          System.out.println("*** using stock class loader on " + name);// + ", direct = " + direct);



//  //          System.out.println("*** using stock class loader (" + FileSystemClassLoader.class.getClassLoader() + ") on " + name);// + ", direct = " + direct);
//          return super.loadClass(name, resolve);
//  //          ClassLoader loader = FileSystemClassLoader.class.getClassLoader();
//  //          if (loader != null) {
//  //            return loader.loadClass(name);
//  //          } // if
//        } // if
//        if (c == null) {
//          System.out.println("*** findSystemClass on " + name);
//          c = findSystemClass(name);
//        } // if
//        if (c == null) {
//          System.out.println("*** throwing ClassNotFoundException");
//          throw new ClassNotFoundException(name);
//        } // if
//      } // if
//      else {
//        System.out.println("*** found loaded class: " + name);
//      } // else
           
//      if (resolve) {
//        resolveClass(c);
//      } // if
                      
//      if (c == null) {
//        System.out.println("*** loadClass() returns null on: " + name);
//      }

//      return c;

//        */

//    } // loadClass()


  ///////////////////
  // inner classes //
  ///////////////////

//    private abstract class StorageUnit {
//      abstract byte[] /*InputStream*/ getStream(String path, CodeSourceHolder csh);
//      abstract String getName();
//    } // class StorageUnit

//    /**
//              Retrieves class files from a directory.
//              */
//    private class DirectoryStorageUnit extends StorageUnit {
//      private File directory;

//      public String getName() { return "dir storage: " + directory; }

//      DirectoryStorageUnit(File directory) {
//        this.directory = directory;
//      } // constructor

//      byte[] /*InputStream*/ getStream(String path, CodeSourceHolder csh) {
//        try {
//        File f = new File(directory, path);
//        if (f.exists()) {
//  //          oi.setSize(f.length());
//          FileInputStream fis = new FileInputStream(f);
//          if (fis != null) {
//            byte[] result = new byte[ (int) f.length()];
//            fis.read(result);
//            fis.close();
//            return result;
//          } // if
//        } // if
//        }
//        catch (IOException e) {
//          // return null below
//        }

//        return null;

//  //        return (f.exists() ? new FileInputStream(f) : null);
//      } // getStream()
//    } // class DirectoryStorageUnit

//    /**
//              Retrieves class files from a jar file.
//              */
//    private class JarStorageUnit extends StorageUnit {
//  //      private File archivePath;
//  //      private ZipFile archive;
//      String jarFile;

//      public String getName() { return "jar storage: " + jarFile; }

//      JarStorageUnit(String jarFile) {
//        this.jarFile = jarFile;
//  //        archive = null;
//      } // constructor

//      byte[] /*InputStream*/ getStream(String path, CodeSourceHolder csh) {
//  //        System.out.println("#### jar " + jarFile + " getStream: " + path);
//        try {

//        // extracts just sizes only. 
//        final HashMap htSizes = new HashMap();
//        final ZipFile zf = new ZipFile(jarFile);
//        final Enumeration e = zf.entries();
//        while (e.hasMoreElements()) {
//          ZipEntry ze=(ZipEntry)e.nextElement();
//          htSizes.put(ze.getName(), new Integer((int)ze.getSize()));
//        } // while
//        zf.close();
//        // end



//        // have to reload the archive from disk every time in case it changes
//  //csh        ZipFile archive = new ZipFile(jarFile);
//        JarFile archive = new JarFile(jarFile);

//        FileInputStream fis = new FileInputStream(jarFile);
//  //csh        ZipInputStream zis = new ZipInputStream(fis);
//  //csh        ZipEntry ze;
//        JarInputStream zis = new JarInputStream(fis);
//        JarEntry ze;

//  //csh        while ((ze = zis.getNextEntry()) != null) {
//        while ((ze = zis.getNextJarEntry()) != null) {
//          if (ze.isDirectory()) continue;

//          if (ze.getName().equals(path)) {
//            // found our boy
//  //            System.out.println("!!!! ze name: " + ze.getName());
//  //            System.out.println("!!!! archive size = " + new File(jarFile).length());
//  //            System.out.println("!!!! entry size = " + ze.getSize());
//  //            System.out.println("!!!! entry compressed size = " + ze.getCompressedSize());

//  //            oi.setSize(ze.getSize());

//  //            java.security.cert.Certificate[] certs = ze.getCertificates();
//  //            int len = certs == null ? 0 : certs.length;
//  //            if (len != 0)
//  //              System.out.println("got " + len + " certs on " + ze.getName());
//  //            csh.codeSource = new CodeSource(null, certs);

//            int size = (int) ze.getSize();
//            if (size == -1) {
//              size = ((Integer) htSizes.get(ze.getName())).intValue();
//  //              oi.setSize(size);
//  //              System.out.println("!!!! remapped size to " + size);
//            } // if
//            byte[] result = new byte[size];

//            int rb=0;
//            int chunk=0;
          
//            // fixme: why on earth do we have to loop like this?
//            while ( ((int)size - rb) > 0) {
//              chunk = zis.read(result, rb,(int)size - rb);
//              if (chunk==-1) {
//                break;
//              }
//              rb += chunk;
//            } // while

//  //            int got = zis.read(result);
//  //            System.out.println("!!!! read " + rb + " bytes from " + path);
//            zis.close();
//            fis.close();
//            archive.close();

//            return result;
//          } // if

//        } // while
//        } // try
//        catch (IOException e) {
//          // return null below
//        }

//        /*
//        ZipEntry entry = archive.getEntry(path);

//        if (entry != null) {
//          if (debug) System.out.println("!!!! archive size = " + new File(jarFile).length());
//          if (debug) System.out.println("!!!! entry size = " + entry.getSize());
//          oi.setSize(entry.getSize());
//          InputStream input = archive.getInputStream(entry);
//          ZipInputStream zis = new ZipInputStream(input);

//          final int size = (int) entry.getSize();
//          byte[] result = new byte[size];

//            int rb=0;
//            int chunk=0;
          
//            // fixme: why on earth do we have to loop like this?
//            while ( ((int)size - rb) > 0) {
//              chunk = zis.read(result, rb,(int)size - rb);
//              if (chunk==-1) {
//                break;
//              }
//              rb += chunk;
//            } // while


//  //          int got = input.read(result);
//          if (debug) System.out.println("read " + rb + " bytes from " + path);
//          input.close();
//          zis.close();
//          archive.close();
//          return result;
//        } // if
//        else {
//          if (debug) System.out.println("!!!! ZipEntry is null");
//        } // else

//        archive.close();
//        */

//        return null;
//      } // getStream()
//    } // class JarStorageUnit

//    class OutInteger {
//      long size;

//      void setSize(long newsize) { size = newsize; }
//      long getSize() { return size; }
//    } // class OutInteger

  //////////////////////////

//    public java.net.URL findResource(String name) {
//      System.out.println("#### findResource(" + name + ") called");
//      return super.findResource(name);
//    }

//    public Enumeration findResources(String name) throws IOException {
//      System.out.println("#### findResources(" + name + ") called");
//      return super.findResources(name);
//    }

//    public java.net.URL getResource(String name) {
//      System.out.println("#### getResource(" + name + ") called");
//      return super.getResource(name);
//    }

//    public Enumeration getResources(String name) throws IOException {
//      System.out.println("#### getResources(" + name + ") called");
//      return super.getResources(name);
//    }

//    public InputStream getResourceAsStrream(String name) {
//      System.out.println("#### getResourceAsStream(" + name + ") called");
//      return super.getResourceAsStream(name);
//    }

//    public String findLibrary(String library) {
//      System.out.println("#### findLibrary(" + library + ") called");
//      return super.findLibrary(library);
//    }


//    public Class findClass(String name) throws ClassNotFoundException {
//  //      try {
//      System.out.println("************* findClass() called: " + name);
//      if (true || second) {
//        System.out.println("************* calling super.findClass()");
//        Class c = super.findClass(name);
//        System.out.println("************* super.findClass() returned: " + c.getName());
//        return c;
//      }
//        throw new ClassNotFoundException();
//      }


//      final String testClassPath = System.getProperty("TestClassPath");
//      System.out.println("*** TestClassPath = " + testClassPath);
//      FileSystemClassLoader fileSystemClassLoader;
//      if (testClassPath != null) {
//        final StringTokenizer tokenizer = new StringTokenizer(testClassPath, File.pathSeparator);
//        String token;
//        final URL[] urls = new URL[tokenizer.countTokens()];
//        int index = 0;
//        while (tokenizer.hasMoreTokens()) {
//          token = tokenizer.nextToken();
          
//          String fileUrl = "file:" + token;
//          if (token.length() > 0 && token.charAt(token.length() - 1) != File.separatorChar) {
//            File fileToken = new File(token);
//            if (fileToken.isDirectory()) {
//              fileUrl = fileUrl + File.separatorChar;
//            } // if
//          } // if

//          urls[index] = new URL(fileUrl);
//          System.out.println(urls[index]);
//          ++index;
//        } // while
//        fileSystemClassLoader = new FileSystemClassLoader(urls);
//      } // if
//      else {
//        fileSystemClassLoader = new FileSystemClassLoader(new URL[0]);
//      } // else
//      System.out.println("*** calling loadClass() on " + name);
//      return fileSystemClassLoader.loadClass(name);
//      }
//      catch (Exception e) {
//        e.printStackTrace();
//        throw new ClassNotFoundException(e.getMessage());
//      } // catch
//    } // findClass()

//    public Class findClass(String name) throws ClassNotFoundException {
//      System.out.println("#### findClass(" + name + ") called");
//      return super.findClass(name);
//    }

//    class CodeSourceHolder {
//      public CodeSource codeSource = null;
//    }

} // class FileSystemClassLoader
