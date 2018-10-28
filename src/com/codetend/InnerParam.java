package com.codetend;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;

import java.util.List;

public class InnerParam {
    @Parameter(names = "-help")
    public boolean isHelp = false;
    @Parameter(names = "-aarList")
    public List<String> aarList;
    @Parameter(names = "-output")
    public String outputDir;
}
