package com.bevelio.bevelio.commons.utils;
 
import java.io.BufferedOutputStream;
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 

public class Decompress {
	private String zipFile; 
	  private String location; 
	 
	  public Decompress(String zipFile, String location) { 
		  this.zipFile = zipFile; 
		  this.location = location; 
	 

	  } 
	 
	 
	  private static final int BUFFER_SIZE = 4096;
	    /**
	     * Extracts a zip file specified by the zipFilePath to a directory specified by
	     * destDirectory (will be created if does not exists)
	     * @param zipFilePath
	     * @param destDirectory
	     * @throws IOException
	     */
	    public void unzipSpeed() throws IOException {
	        File destDir = new File(location);
	        if (!destDir.exists())
	            destDir.mkdir();
	       
	        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
	        ZipEntry entry = zipIn.getNextEntry();
	        // iterates over entries in the zip file
	        while (entry != null) {
	            String filePath = location + File.separator + entry.getName();
	            System.out.println("Decompressing >> Unzipping " + filePath); 
	            if (!entry.isDirectory()) {
	                // if the entry is a file, extracts it
	            	extractFile(zipIn, filePath);
	            } else {
	                // if the entry is a directory, make the directory
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
	        
	        
	    }

	    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        while ((read = zipIn.read(bytesIn)) != -1) {
	            bos.write(bytesIn, 0, read);
	        }
	        bos.close();
	    }
	}
 

