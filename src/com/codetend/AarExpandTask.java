package com.codetend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AarExpandTask implements Callable<Boolean> {
    private File mLibsDir;
    private File mAarFile;
    private File mOutputDir;
    private List<File> mJarFiles = new ArrayList<>();
    private File mAssetsDir;
    private File mTempJarDir;

    public AarExpandTask(File aarFile, File outputDir) {
        mAarFile = aarFile;
        mOutputDir = outputDir;
        mAssetsDir = new File(mOutputDir, "assets");
        mTempJarDir = new File(mOutputDir, "temp-libs");
        mLibsDir = new File(mOutputDir, "libs");
    }

    @Override
    public Boolean call() {
        return expand();
    }

    public boolean expand() {
        return checkFile() &&
                unZip() &&
                unZipJarClasses() &&
                fetchAssets() &&
                assembleJar();
    }

    private boolean checkFile() {
        if (!mOutputDir.exists()) {
            return mOutputDir.mkdirs();
        } else {
            FileUtil.delete(mOutputDir);
            return mOutputDir.mkdirs();
        }
    }

    private boolean unZip() {
        return !UnzipUtil.unzip(mAarFile, "", "", mOutputDir).isEmpty();
    }

    private boolean unZipJarClasses() {
        File[] aarItems = mOutputDir.listFiles();
        if (aarItems != null) {
            for (File item : aarItems) {
                if (item.getName().endsWith(".jar")) {
                    mJarFiles.add(item);
                }
            }
        }
        aarItems = mLibsDir.listFiles();
        if (aarItems != null) {
            for (File item : aarItems) {
                if (item.getName().endsWith(".jar")) {
                    mJarFiles.add(item);
                }
            }
        }
        boolean result = true;
        for (File item : mJarFiles) {
            result = result && !UnzipUtil.unzip(item, "", "", mTempJarDir).isEmpty();
        }
        return result;
    }

    private boolean fetchAssets() {
        return FileUtil.moveDir(new File(mTempJarDir, "assets"), mAssetsDir);
    }

    private boolean assembleJar() {
        boolean result = true;
        for (File file : mJarFiles) {
            result = result && FileUtil.delete(file);
        }
        File targetJarFile = new File(mOutputDir, mAarFile.getName().replace(".aar", ".jar"));
        result = result && UnzipUtil.zip(mTempJarDir, targetJarFile, true);
        result = result && FileUtil.delete(mTempJarDir);
        result = result && FileUtil.move(targetJarFile, new File(mLibsDir, targetJarFile.getName()));
        return result;
    }

    public static void main(String[] args) {
        new AarExpandTask(new File("D:\\JavaProject\\AarExpander\\gamesdk-framework-shellbase-7.1.5.61.aar"),
                new File("D:\\JavaProject\\AarExpander\\temp")).expand();
    }
}
