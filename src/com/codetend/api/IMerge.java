package com.codetend.api;

import java.io.File;
import java.util.List;

/**
 * Created by gz on 2018/12/17.
 */
public interface IMerge {
    boolean merge(List<File> aarDirList, File outputFile);
}
