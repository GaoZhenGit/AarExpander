package com.codetend;

import com.beust.jcommander.Parameter;

import java.util.List;

public class InnerParam {
    @Parameter(names = "-help", description = "show how to use")
    public boolean isHelp = false;
    @Parameter(names = "-aarList", description = "list of aar that need to merge, the first one is consisderd main one, splited by \",\"")
    public List<String> aarList;
    @Parameter(names = "-output", description = "output dir")
    public String outputDir;
    @Parameter(names = "-needZip", description = "the merged result into aar file")
    public boolean needZip = false;
    @Parameter(names = "-keepJar", description = "keep the original jar in aar")
    public boolean keepJar = false;
}
