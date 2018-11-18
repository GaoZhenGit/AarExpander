package com.codetend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
     public static boolean copy(File source, File dest){
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
         if (copy(source, dest)){
             return source.delete();
         } else {
             return false;
         }
     }

     public static boolean moveDir(File sourceDir, File destDir) {
         File[] items = sourceDir.listFiles();
         if (items != null) {
             for (File item : items) {
                 if (item.isDirectory()) {
                     File newDir = new File(destDir, item.getName());
                     boolean result = newDir.mkdirs() && moveDir(item, newDir);
                     if (!result) {
                         return false;
                     }
                 } else {
                     boolean result = move(item, new File(destDir, item.getName()));
                     if (!result) {
                         return false;
                     }
                 }
             }
         } else {
             return true;
         }
         return sourceDir.delete();
     }

     public static boolean copyDir(File sourceDir, File destDir) {
         File[] items = sourceDir.listFiles();
         if (items != null) {
             for (File item : items) {
                 if (item.isDirectory()) {
                     File newDir = new File(destDir, item.getName());
                     boolean result = (newDir.exists() || newDir.mkdirs()) && copyDir(item, newDir);
                     if (!result) {
                         return false;
                     }
                 } else {
                     boolean result = copy(item, new File(destDir, item.getName()));
                     if (!result) {
                         return false;
                     }
                 }
             }
         }
         return true;
     }

     public static boolean delete(File dir) {
         if (dir.isDirectory()) {
             File[] items = dir.listFiles();
             if (items != null) {
                 for (File item : items) {
                     boolean result = delete(item);
                     if (!result) {
                         return false;
                     }
                 }
             }
             return dir.delete();
         } else {
             return dir.delete();
         }
     }

     public static List<File> list(File dir) {
         List<File> result = new ArrayList<>();
         if (!dir.isDirectory()) {
             return result;
         } else {
             File[] listFile = dir.listFiles();
             if (listFile == null) {
                 return result;
             } else {
                 for (File item : listFile) {
                     if (item.isDirectory()) {
                         result.addAll(list(item));
                     } else {
                         result.add(item);
                     }
                 }
             }
         }
         return result;
     }
}
