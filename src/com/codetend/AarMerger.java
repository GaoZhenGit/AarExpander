package com.codetend;

import com.android.manifmerger.IMergerLog;
import com.android.manifmerger.ManifestMerger;

import java.io.File;
import java.util.List;

public class AarMerger {
    private static final String MANIFEST_FILE_NAME = "AndroidManifest.xml";
    public static boolean merge(List<File> aarDirList, File outputFile) {
        if (aarDirList.size() < 2) {
            return false;
        }
        File mainAarFile = new File(aarDirList.get(0), MANIFEST_FILE_NAME);
        File[] libraryAarFile = new File[aarDirList.size() - 1];
        for (int i = 1; i < aarDirList.size(); i++) {
            libraryAarFile[i - 1] = new File(aarDirList.get(i), MANIFEST_FILE_NAME);
        }

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
                outputFile,
                mainAarFile,
                libraryAarFile,
                null, null);
        return true;
    }
}
