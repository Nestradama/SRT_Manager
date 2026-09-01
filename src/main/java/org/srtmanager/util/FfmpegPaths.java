package org.srtmanager.util;

public class FfmpegPaths {

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name", "").toLowerCase().contains("win");

    public static String ffmpegPath()  { return IS_WINDOWS ? "ffmpeg.exe"  : "ffmpeg"; }
    public static String ffprobePath() { return IS_WINDOWS ? "ffprobe.exe" : "ffprobe"; }
}