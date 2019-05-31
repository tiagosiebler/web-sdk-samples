package com.microstrategy.samples.util;

import java.io.*;

public class FileHelper {
  /*
   * Helper function to save an in-memory object to file
   */
  public static void saveByteArrayToFile(byte[] byteArray, String targetFilePath) {
    BufferedOutputStream bos = null;
    FileOutputStream fos = null;
    
    try {
      fos = new FileOutputStream(targetFilePath);
      bos = new BufferedOutputStream(fos);
      bos.write(byteArray);
      
    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
