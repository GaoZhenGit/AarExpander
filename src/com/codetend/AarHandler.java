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
            File classesFile = new File(mOutputDir, "classes.jar");
            File classesFileN = new File(mLibsDir, aarFile.getName().replace(".aar", ".jar"));
            FileUtil.move(classesFile, classesFileN);
            File manifestFile = new File(mOutputDir, "AndroidManifest.xml");
            File manifestFileN = new File(mManifestDir, aarFile.getName().replace(".aar", ".xml"));
            FileUtil.move(manifestFile, manifestFileN);
            if (i == 0) {
                mMainManifestFile = manifestFileN;
            }
        }
    }
}
