package org.srtmanager.util;

public class FileUtils {
    public static String getOutputPath(String inputFilename) {

        if (inputFilename.contains(".")){
            int indexExtension = inputFilename.lastIndexOf('.');

            String[] filenameParts = {inputFilename.substring(0, indexExtension), inputFilename.substring(indexExtension+1)};

            return filenameParts[0] + "-subbed" + '.' + filenameParts[1];
        }
        else {
            return inputFilename + "-subbed";
        }
    }
}
