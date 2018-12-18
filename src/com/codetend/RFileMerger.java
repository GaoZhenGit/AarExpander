package com.codetend;

import com.codetend.api.IMerge;
import com.codetend.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by gz on 2018/12/17.
 */
public class RFileMerger implements IMerge {

    private static final String RFILE_NAME = "R.txt";
    private List<File> mRFileList = new ArrayList<>();
    private List<RItem> mRItemLIst = new ArrayList<>();

    @Override
    public boolean merge(List<File> aarDirList, File outputFile) {
        if (aarDirList.size() < 2) {
            return false;
        }
        for (File aarFile : aarDirList) {
            File rFile = new File(aarFile, RFILE_NAME);
            if (rFile.exists()) {
                mRFileList.add(rFile);
                mRItemLIst.addAll(readRFile(rFile));
            }
        }
        if (mRItemLIst.isEmpty()) {
            return true;
        }
        // sort for all the item in all R.txt files
        sortRItem(mRItemLIst);
        // reset all the id one by one
        resetId(mRItemLIst);
        // write the file into output file
        return writeIdToFile(mRItemLIst, outputFile);
    }

    private static List<RItem> readRFile(File file) {
        List<RItem> result = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] rItemStr = line.split(" ");
                if (rItemStr.length == 4) {
                    RItem rItem = new RItem();
                    rItem.type = rItemStr[1];
                    rItem.name = rItemStr[2];
                    rItem.hexId = Integer.parseInt(rItemStr[3].replace("0x", ""), 16);
                    result.add(rItem);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void sortRItem(List<RItem> rItems) {
        Collections.sort(rItems, new Comparator<RItem>() {
            @Override
            public int compare(RItem o1, RItem o2) {
                int typeCompareResult = o1.type.compareTo(o2.type);
                if (typeCompareResult != 0) {
                    return typeCompareResult;
                } else if ((typeCompareResult = o1.name.compareTo(o2.name)) != 0) {
                    return typeCompareResult;
                } else {
                    return o1.hexId - o2.hexId;
                }
            }
        });
    }

    private static void resetId(List<RItem> rItems) {
        int pp = Integer.parseInt("7f", 16);
        int tt = Integer.parseInt("00", 16);
        int nnnn = Integer.parseInt("0000", 16);
        String lastType = "";
        for (RItem item : rItems) {
            if (!item.type.equals(lastType)) {
                tt++;
                nnnn = Integer.parseInt("0000", 16);
            } else {
                nnnn++;
            }
            item.setId(pp, tt, nnnn);
            lastType = item.type;
        }
    }

    private static boolean writeIdToFile(List<RItem> rItems, File file) {
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (RItem item : rItems) {
                printWriter.print(String.format("int %s %s 0x%x", item.type, item.name, item.hexId));
                printWriter.print("\n");
            }
            printWriter.flush();
            printWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class RItem {
        public String type;
        public String name;
        public int hexId;

        @Override
        public String toString() {
            return type + " " + name + " " + Integer.toHexString(hexId);
        }

        public void setId(int pp, int tt, int nnnn) {
            String id = String.format("%02x", pp) +
                    String.format("%02x", tt) +
                    String.format("%04x", nnnn);
            hexId = Integer.parseInt(id, 16);
        }
    }

    public static void main(String[] args) {
        List<File> aarList = new ArrayList<>();
        aarList.add(new File("./temp/gamesdk-framework-shellbase-7.1.5.61.aar-dir"));
        aarList.add(new File("./temp/ngad-sdk-release-2.4.40.aar-dir"));
        new RFileMerger().merge(aarList, new File("./temp/output/R.txt"));
    }
}

