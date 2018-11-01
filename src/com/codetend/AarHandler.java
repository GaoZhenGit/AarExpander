package com.codetend;

import com.android.manifmerger.IMergerLog;
import com.android.manifmerger.ManifestMerger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AarHandler {
    private List<String> mAarList;
    private File mOutputDir;
    private File mLibsDir;
    private File mManifestDir;
    private File mMainManifestFile;
    private List<File> mOtherManifestFile;
    public AarHandler(List<String> aarList, String outputDir) {
        this.mAarList = aarList;
        mOutputDir = new File(outputDir);
        if (mOutputDir.exists()) {
            mOutputDir.delete();
        }
        mOutputDir.mkdirs();
        mLibsDir = new File(mOutputDir, "libs");
        mManifestDir = new File(mOutputDir, "manifest");
        mOtherManifestFile = new ArrayList<>();
    }

    public void handle() {
        mManifestDir.mkdirs();
        for (int i = 0; i < mAarList.size(); i++) {
            String aarItem = mAarList.get(i);
            File aarFile = new File(aarItem);
            UnzipUtil.unzip(aarFile, "", "", mOutputDir);
            if (!mLibsDir.exists()) {
                mLibsDir.mkdirs();
            }
            // 将classes.jar转移到lib里面,重命名为aar的名字
            File classesFile = new File(mOutputDir, "classes.jar");
            File classesFileN = new File(mLibsDir, aarFile.getName().replace(".aar", ".jar"));
            FileUtil.move(classesFile, classesFileN);
            // 将AndroidManifest转移到manifest文件夹里,防止下一个aar覆盖掉
            File manifestFile = new File(mOutputDir, "AndroidManifest.xml");
            File manifestFileN = new File(mManifestDir, aarFile.getName().replace(".aar", ".xml"));
            FileUtil.move(manifestFile, manifestFileN);
            if (i == 0) {
                mMainManifestFile = manifestFileN;
            } else {
                mOtherManifestFile.add(manifestFileN);
            }
            // 释放lib中的assets资源
            File[] libFiles = mLibsDir.listFiles();
            if (libFiles != null) {
                for (File file : libFiles) {
                    UnzipUtil.unzip(file, "assets/", "", mOutputDir);
                }
            }
        }
        // 合并AndroidManifest
        mergeManifest();
    }

    private void mergeManifest() {
        ManifestMerger manifestMerger = new ManifestMerger(new IMergerLog() {
            @Override
            public void error(Severity severity, FileAndLine fileAndLine, String s, Object... objects) {
                System.err.println("==========="+ severity.name() +"============");
                System.err.println(fileAndLine.toString());
                System.err.println(String.format(s, objects));
                System.err.println("============================");
            }

            @Override
            public void conflict(Severity severity, FileAndLine fileAndLine, FileAndLine fileAndLine1, String s, Object... objects) {
                System.err.println("==========="+ severity.name() +"============");
                System.err.println(fileAndLine.toString());
                System.err.println(fileAndLine1.toString());
                System.err.println(String.format(s, objects));
                System.err.println("============================");
            }
        }, null);
        manifestMerger.process(
                new File(mOutputDir,"AndroidManifest.xml"),
                mMainManifestFile,
                mOtherManifestFile.toArray(new File[1]),
                null, null);
    }
}
