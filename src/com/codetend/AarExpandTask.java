package com.codetend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AarExpandTask implements Callable<Boolean> {
    private File mLibsDir;
    private File mAarFile;
    private File mOutputDir;
    private List<File> mMainJarFiles = new ArrayList<>();
    private List<File> mLibJarFiles = new ArrayList<>();
    private List<File> mAllJarFiles = new ArrayList<>();
    private File mAssetsDir;
    private File mTempJarDir;
    private boolean mKeepJar;

    public AarExpandTask(File aarFile, File outputDir, boolean keepJar) {
        mAarFile = aarFile;
        mOutputDir = outputDir;
        mAssetsDir = new File(mOutputDir, "assets");
        mTempJarDir = new File(mOutputDir, "temp-libs");
        mLibsDir = new File(mOutputDir, "libs");
        mKeepJar = keepJar;
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
        return !ZipUtil.unzip(mAarFile, "", "", mOutputDir).isEmpty();
    }

    private boolean unZipJarClasses() {
        File[] aarItems = mOutputDir.listFiles();
        if (aarItems != null) {
            for (File item : aarItems) {
                if (item.getName().endsWith(".jar")) {
                    mMainJarFiles.add(item);
                }
            }
        }
        aarItems = mLibsDir.listFiles();
        if (aarItems != null) {
            for (File item : aarItems) {
                if (item.getName().endsWith(".jar")) {
                    mLibJarFiles.add(item);
                }
            }
        }
        mAllJarFiles.addAll(mLibJarFiles);
        mAllJarFiles.addAll(mMainJarFiles);
        boolean result = true;
        for (File item : mAllJarFiles) {
            result = result && !ZipUtil.unzip(item, "", "", mTempJarDir).isEmpty();
        }
        return result;
    }

    private boolean fetchAssets() {
        return FileUtil.moveDir(new File(mTempJarDir, "assets"), mAssetsDir);
    }

    private boolean assembleJar() {
        boolean result = true;
        if (mKeepJar) {
            for (File file : mAllJarFiles) {
                File tempJarFile = new File(file.getParentFile(), file.getName() + "-tmp");
                ZipUtil.removeItems(file, "assets", "", tempJarFile);
                result = result && FileUtil.delete(file);
                result = result && FileUtil.move(tempJarFile, file);
            }
            for (File file : mMainJarFiles) {
                File targetJarFile = new File(mLibsDir, mAarFile.getName().replace(".aar", "-" + file.getName()));
                FileUtil.move(file, targetJarFile);
            }
            result = result && FileUtil.delete(mTempJarDir);
            return result;
        } else {
            for (File file : mAllJarFiles) {
                result = result && FileUtil.delete(file);
            }
            File targetJarFile = new File(mOutputDir, mAarFile.getName().replace(".aar", ".jar"));
            result = result && ZipUtil.zip(mTempJarDir, targetJarFile, true);
            result = result && FileUtil.delete(mTempJarDir);
            result = result && FileUtil.move(targetJarFile, new File(mLibsDir, targetJarFile.getName()));
            return result;
        }
    }

    public static void main(String[] args) {
        new AarExpandTask(new File("D:\\JavaProject\\AarExpander\\ngad-sdk-release-2.4.30.aar"),
                new File("D:\\JavaProject\\AarExpander\\temp"), false).expand();
    }
}
