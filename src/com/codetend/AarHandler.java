package com.codetend;

import java.io.File;
import java.util.List;

public class AarHandler {
    List<String> mAarList;
    File mOutputDir;
    File mLibsDir;
    File mManifestDir;
    File mMainManifestFile;
    public AarHandler(List<String> aarList, String outputDir) {
        this.mAarList = aarList;
        mOutputDir = new File(outputDir);
        if (mOutputDir.exists()) {
            mOutputDir.delete();
        }
        mOutputDir.mkdirs();
        mLibsDir = new File(mOutputDir, "libs");
        mManifestDir = new File(mOutputDir, "manifest");
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
            }
            // 释放lib中的assets资源
            File[] libFiles = mLibsDir.listFiles();
            if (libFiles != null) {
                for (File file : libFiles) {
                    UnzipUtil.unzip(file, "assets/", "", mOutputDir);
                }
            }
        }
        // 恢复主aar的AndroidManifest
        File manifestFile = new File(mOutputDir, "AndroidManifest.xml");
        FileUtil.move(mMainManifestFile, manifestFile);
    }
}
