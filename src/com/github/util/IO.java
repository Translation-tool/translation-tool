
package com.github.util;

//========================================================================
//Copyright (c) 2004-2009 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//All rights reserved. This program and the accompanying materials
//are made available under the terms of the Eclipse Public License v1.0
//and Apache License v2.0 which accompanies this distribution.
//The Eclipse Public License is available at 
//http://www.eclipse.org/legal/epl-v10.html
//The Apache License v2.0 is available at
//http://www.opensource.org/licenses/apache2.0.php
//You may elect to redistribute this code under either of these licenses. 
//========================================================================

import java.io.*;
import java.util.logging.Logger;

/* ======================================================================== */

/** IO Utilities.
* Provides stream handling utilities in
* singleton Threadpool implementation accessed by static members.
*/
public class IO 
{
 private static final Logger LOG = Logger.getLogger(IO.class.getName());
 
 /* ------------------------------------------------------------------- */
 public final static String
     CRLF      = "\015\012";

 /* ------------------------------------------------------------------- */
 public final static byte[]
     CRLF_BYTES    = {(byte)'\015',(byte)'\012'};

 /* ------------------------------------------------------------------- */
 public static int bufferSize = 64*1024;
 
 /* ------------------------------------------------------------------- */

 /* ------------------------------------------------------------------- */
 public static void copy(InputStream in,
                         OutputStream out,
                         long byteCount)
      throws IOException
 {     
     byte buffer[] = new byte[bufferSize];
     int len=bufferSize;
     
     if (byteCount>=0)
     {
         while (byteCount>0)
         {
             int max = byteCount<bufferSize?(int)byteCount:bufferSize;
             len=in.read(buffer,0,max);
             
             if (len==-1)
                 break;
             
             byteCount -= len;
             out.write(buffer,0,len);
         }
     }
     else
     {
         while (true)
         {
             len=in.read(buffer,0,bufferSize);
             if (len<0 )
                 break;
             out.write(buffer,0,len);
         }
     }
 }  
 
 /* ------------------------------------------------------------------- */
 /** Copy Reader to Writer for byteCount bytes or until EOF or exception.
  */
 public static void copy(Reader in,
                         Writer out,
                         long byteCount)
      throws IOException
 {  
     char buffer[] = new char[bufferSize];
     int len=bufferSize;
     
     if (byteCount>=0)
     {
         while (byteCount>0)
         {
             if (byteCount<bufferSize)
                 len=in.read(buffer,0,(int)byteCount);
             else
                 len=in.read(buffer,0,bufferSize);                   
             
             if (len==-1)
                 break;
             
             byteCount -= len;
             out.write(buffer,0,len);
         }
     }
     else if (out instanceof PrintWriter)
     {
         PrintWriter pout=(PrintWriter)out;
         while (!pout.checkError())
         {
             len=in.read(buffer,0,bufferSize);
             if (len==-1)
                 break;
             out.write(buffer,0,len);
         }
     }
     else
     {
         while (true)
         {
             len=in.read(buffer,0,bufferSize);
             if (len==-1)
                 break;
             out.write(buffer,0,len);
         }
     }
 }

 
 /* ------------------------------------------------------------ */
 /** Delete File.
  * This delete will recursively delete directories - BE CAREFULL
  * @param file The file to be deleted.
  */
 public static boolean delete(File file)
 {
     if (!file.exists())
         return false;
     if (file.isDirectory())
     {
         File[] files = file.listFiles();
         for (int i=0;files!=null && i<files.length;i++)
             delete(files[i]);
     }
     return file.delete();
 }

 /* ------------------------------------------------------------ */
 /**
  * closes an input stream, and logs exceptions
  *
  * @param is the input stream to close
  */
 public static void close(InputStream is)
 {
     try
     {
         if (is != null)
             is.close();
     }
     catch (IOException e)
     {
         LOG.finest(e.toString());
     }
 }

 /**
  * closes a reader, and logs exceptions
  * 
  * @param reader the reader to close
  */
 public static void close(Reader reader)
 {
     try
     {
         if (reader != null)
             reader.close();
     } catch (IOException e)
     {
         LOG.finest(e.toString());
     }
 }

 /**
  * closes a writer, and logs exceptions
  * 
  * @param writer the writer to close
  */
 public static void close(Writer writer)
 {
     try
     {
         if (writer != null)
             writer.close();
     } catch (IOException e)
     {
         LOG.finest(e.toString());
     }
 }
 
 /* ------------------------------------------------------------ */
 /**
  * closes an output stream, and logs exceptions
  *
  * @param os the output stream to close
  */
 public static void close(OutputStream os)
 {
     try
     {
         if (os != null)
             os.close();
     }
     catch (IOException e)
     {
         LOG.finest(e.toString());
     }
 }

 /* ------------------------------------------------------------ */
 /** 
  * @return An outputstream to nowhere
  */
 public static OutputStream getNullStream()
 {
     return __nullStream;
 }

 /* ------------------------------------------------------------ */
 /** 
  * @return An outputstream to nowhere
  */
 public static InputStream getClosedStream()
 {
     return __closedStream;
 }
 
 /* ------------------------------------------------------------ */
 /* ------------------------------------------------------------ */
 private static class NullOS extends OutputStream                                    
 {
     @Override
     public void close(){}
     @Override
     public void flush(){}
     @Override
     public void write(byte[]b){}
     @Override
     public void write(byte[]b,int i,int l){}
     @Override
     public void write(int b){}
 }
 private static NullOS __nullStream = new NullOS();

 
 /* ------------------------------------------------------------ */
 /* ------------------------------------------------------------ */
 private static class ClosedIS extends InputStream                                    
 {
     @Override
     public int read() throws IOException
     {
         return -1;
     }
 }
 private static ClosedIS __closedStream = new ClosedIS();
 
 /* ------------------------------------------------------------ */
 /** 
  * @return An writer to nowhere
  */
 public static Writer getNullWriter()
 {
     return __nullWriter;
 }
 
 /* ------------------------------------------------------------ */
 /** 
  * @return An writer to nowhere
  */
 public static PrintWriter getNullPrintWriter()
 {
     return __nullPrintWriter;
 }
 
 /* ------------------------------------------------------------ */
 /* ------------------------------------------------------------ */
 private static class NullWrite extends Writer                                    
 {
     @Override
     public void close(){}
     @Override
     public void flush(){}
     @Override
     public void write(char[]b){}
     @Override
     public void write(char[]b,int o,int l){}
     @Override
     public void write(int b){}
     @Override
     public void write(String s){}
     @Override
     public void write(String s,int o,int l){}
 }
 private static NullWrite __nullWriter = new NullWrite();
 private static PrintWriter __nullPrintWriter = new PrintWriter(__nullWriter);
}
