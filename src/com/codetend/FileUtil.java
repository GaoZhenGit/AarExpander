package com.codetend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
     public static boolean copyFileUsingFileChannels(File source, File dest){
         FileChannel inputChannel = null;
         FileChannel outputChannel = null;
         try {
             inputChannel = new FileInputStream(source).getChannel();
             outputChannel = new FileOutputStream(dest).getChannel();
             outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
         } catch (IOException e) {
             e.printStackTrace();
             return false;
         } finally {
             try {
                 inputChannel.close();
                 outputChannel.close();
             } catch (IOException e) {
                 e.printStackTrace();
                 return false;
             }
         }
         return true;
     }

     public static boolean move(File source, File dest) {
         if (copyFileUsingFileChannels(source, dest)){
             return source.delete();
         } else {
             return false;
         }
     }
}
