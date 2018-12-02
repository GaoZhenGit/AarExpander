package com.codetend;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static List<File> unzip(File zipFile, String prefix, String suffix, File outputDir) {
        List<File> result = new ArrayList<>();
        try {
            ZipFile zip = new ZipFile(zipFile, Charset.forName("utf8"));
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String itemName = zipEntry.getName();
                if (itemName.startsWith(prefix) && itemName.endsWith(suffix)) {
                    if (itemName.endsWith("/")) {
                        File dir = new File(outputDir, itemName);
                        dir.mkdirs();
                        continue;
                    }
                    InputStream inputStream = zip.getInputStream(zipEntry);
                    File entryFile = new File(outputDir, itemName);
                    entryFile.getParentFile().mkdirs();
                    FileOutputStream outputStream = new FileOutputStream(entryFile);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    result.add(entryFile);
                }
            }
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean zip(File sourceFile, File desFile, boolean KeepDirStructure) {
        ZipOutputStream zos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(desFile);
            zos = new ZipOutputStream(fos);
            compress(sourceFile, zos, "", KeepDirStructure);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> removeItems(File zipFile, String prefix, String suffix, File desFile) {
        List<String> removedFiles = new ArrayList<>();
        try {
            ZipFile zip = new ZipFile(zipFile, Charset.forName("utf8"));
            FileOutputStream fos = new FileOutputStream(desFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String itemName = zipEntry.getName();
                if (!itemName.startsWith(prefix) || !itemName.endsWith(suffix)) {
                    zos.putNextEntry(zipEntry);
                    InputStream inputStream = zip.getInputStream(zipEntry);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.flush();
                    zos.closeEntry();
                    inputStream.close();
                    removedFiles.add(itemName);
                }
            }
            zos.close();
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return removedFiles;
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) {
        try {
            byte[] buf = new byte[8192];
            if (sourceFile.isFile()) {
                // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                zos.putNextEntry(new ZipEntry(name));
                // copy文件到zip输出流中
                int len;
                FileInputStream in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                // Complete the entry
                zos.closeEntry();
                in.close();
            } else {
                File[] listFiles = sourceFile.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    // 需要保留原来的文件结构时,需要对空文件夹进行处理
                    if (KeepDirStructure && isStringEmpty(name)) {
                        // 空文件夹的处理
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        // 没有文件，不需要文件的copy
                        zos.closeEntry();
                    }
                } else {
                    for (File file : listFiles) {
                        // 判断是否需要保留原来的文件结构
                        if (KeepDirStructure) {
                            // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                            // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                            compress(file, zos, name + (isStringEmpty(name) ? "" : "/") + file.getName(), KeepDirStructure);
                        } else {
                            compress(file, zos, file.getName(), KeepDirStructure);
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static boolean isStringEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static void main(String[] args) {
//        unzip(new File("./gamesdk-framework-shellbase-7.1.5.61.aar"),
//                "", "", new File("./temp"));
//        zip(new File("D:\\JavaProject\\AarExpander\\temp\\temp-libs"), new File("D:\\JavaProject\\AarExpander\\temp\\abc.zip"), true);
        removeItems(new File("D:\\JavaProject\\AarExpander\\gamesdk-framework-shellbase-7.1.5.61.aar"),
                "assets", "", new File("D:\\JavaProject\\AarExpander\\gamesdk.aar"));
    }
}
