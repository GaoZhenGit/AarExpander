package com.codetend;

import com.beust.jcommander.JCommander;
import com.codetend.util.FileUtil;
import com.codetend.util.ThreadPoolHandler;
import com.codetend.util.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        InnerParam param = new InnerParam();
        JCommander jCommander =JCommander.newBuilder()
                .addObject(param)
                .build();
        if (args == null|| args.length == 0) {
            jCommander.usage();
            return;
        } else {
            jCommander.parse(args);
        }
        if (param.isHelp) {
            jCommander.usage();
            return;
        } else {
            File outputDir = new File(param.outputDir);
            // 1.clear the output dir if need
            if (param.force) {
                FileUtil.delete(outputDir);
            }
            // 2.unzip all the aar files by using multiple threads.
            List<File> aarDirs = new ArrayList<>();
            ThreadPoolHandler.Item threadPool = ThreadPoolHandler.create();
            for (String aarPath : param.aarList) {
                File aarFile = new File(aarPath);
                File aarDir = new File(param.outputDir, aarFile.getName() + "-dir");
                aarDirs.add(aarDir);
                AarExpandTask aarExpandTask = new AarExpandTask(aarFile, aarDir, param.keepJar);
                threadPool.submit(aarExpandTask);
            }
            threadPool.startWait();
            // 3.create a temp dir and copy all the files in unzip aar dirs, which will be delete after finish.
            File tempOutputDir = new File(param.outputDir, "output");
            tempOutputDir.mkdirs();
            for (File item : aarDirs) {
                FileUtil.copyDir(item, tempOutputDir);
            }
            // 4.merge AndroidManifest.xml
            new AarManifestMerger().merge(aarDirs, new File(tempOutputDir, "AndroidManifest.xml"));
            // 5.R.txt merge
            new RFileMerger().merge(aarDirs, new File(tempOutputDir, "R.txt"));
            // 6.assemble the outputs
            if (param.needZip) {
                // zip the files into a new aar file, and then delete the temp dir.
                ZipUtil.zip(tempOutputDir, new File(outputDir, outputDir.getName() + ".aar"), true);
                for (File item : aarDirs) {
                    FileUtil.delete(item);
                }
                FileUtil.delete(tempOutputDir);
            } else {
                // copy the result output files into real output dir, and then delete the temp dir.
                FileUtil.copyDir(tempOutputDir, outputDir);
                for (File item : aarDirs) {
                    FileUtil.delete(item);
                }
                FileUtil.delete(tempOutputDir);
            }
        }
    }
}
