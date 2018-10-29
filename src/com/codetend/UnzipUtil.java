package com.codetend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipUtil {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        unzip(new File("./gamesdk-framework-shellbase-7.1.5.61.aar"),
                "", "", new File("./temp"));
    }
}
