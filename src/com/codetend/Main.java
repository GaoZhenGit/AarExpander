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
            System.out.println("-aarList main.aar,yyy.aar,zzz.aar -output xxx -needZip");
            return;
        } else {
//            AarHandler aarHandler = new AarHandler(param.aarList, param.outputDir);
//            aarHandler.handle();
            File outputDir = new File(param.outputDir);
            FileUtil.delete(outputDir);
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
            // 用于合并目录，届时删除
            File tempOutputDir = new File(param.outputDir, "output");
            tempOutputDir.mkdirs();
            for (File item : aarDirs) {
                FileUtil.copyDir(item, tempOutputDir);
            }
            AarMerger.merge(aarDirs, new File(tempOutputDir, "AndroidManifest.xml"));
            if (param.needZip) {
                ZipUtil.zip(tempOutputDir, new File(outputDir, outputDir.getName() + ".aar"), true);
                for (File item : aarDirs) {
                    FileUtil.delete(item);
                }
                FileUtil.delete(tempOutputDir);
            } else {
                FileUtil.copyDir(tempOutputDir, outputDir);
                for (File item : aarDirs) {
                    FileUtil.delete(item);
                }
                FileUtil.delete(tempOutputDir);
            }
        }
    }
}
