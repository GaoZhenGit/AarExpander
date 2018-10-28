package com.codetend;

import com.beust.jcommander.JCommander;

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
            AarHandler aarHandler = new AarHandler(param.aarList, param.outputDir);
            aarHandler.handle();
        }
    }
}
