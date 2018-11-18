package com.codetend;

import com.beust.jcommander.JCommander;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        InnerParam param = new InnerParam();
        JCommander.newBuilder()
                .addObject(param)
                .build()
                .parse(args);
        if (param.isHelp) {
            System.out.println("-aarList main.aar,yyy.aar,zzz.aar");
            return;
        } else {
//            AarHandler aarHandler = new AarHandler(param.aarList, param.outputDir);
//            aarHandler.handle();
            List<File> aarDirs = new ArrayList<>();
            ThreadPoolHandler.Item threadPool = ThreadPoolHandler.create();
            for (String aarPath : param.aarList) {
                File aarFile = new File(aarPath);
                File aarDir = new File(param.outputDir, aarFile.getName());
                aarDirs.add(aarDir);
                AarExpandTask aarExpandTask = new AarExpandTask(aarFile, aarDir);
                threadPool.submit(aarExpandTask);
            }
            threadPool.startWait();
            File outputDir = new File(param.outputDir, "output");
            outputDir.mkdirs();
            for (File item : aarDirs) {
                FileUtil.copyDir(item, outputDir);
            }
            AarMerger.merge(aarDirs, new File(outputDir, "AndroidManifest.xml"));
            FileUtil.copyDir(outputDir, new File(param.outputDir));
            for (File item : aarDirs) {
                FileUtil.delete(item);
            }
            FileUtil.delete(outputDir);
        }
    }
}
