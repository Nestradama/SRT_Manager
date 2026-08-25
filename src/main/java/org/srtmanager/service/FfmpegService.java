package org.srtmanager.service;

import org.srtmanager.util.FileUtils;

import java.util.ArrayList;
import java.util.List;


public class FfmpegService {

    public static List<String> buildHardcodeCommand(String videoPath, String srtPath,
                                                    String encoderId, int fontSize, int marginV) {
        List<String> hardcodeCommand = new ArrayList<>();

        hardcodeCommand.add("ffmpeg");
        hardcodeCommand.add("-y");

        hardcodeCommand.add("-i");
        hardcodeCommand.add(videoPath);

        hardcodeCommand.add("-vf");
        hardcodeCommand.add("subtitles='" + srtPath + "':force_style='FontSize=" + fontSize +
                ",MarginV=" + marginV + "',format=yuv420p");

        hardcodeCommand.add("-c:v");
        hardcodeCommand.add(encoderId);

        hardcodeCommand.add("-c:a");
        hardcodeCommand.add("copy");

        hardcodeCommand.add(FileUtils.getOutputPath(videoPath));

        return hardcodeCommand;
    }

    public static List<String> buildSoftcodeCommand(String videoPath, String srtPath) {
        List<String> softcodeCommand = new ArrayList<>();

        softcodeCommand.add("ffmpeg");
        softcodeCommand.add("-y");

        softcodeCommand.add("-i");
        softcodeCommand.add(videoPath);
        softcodeCommand.add("-i");
        softcodeCommand.add(srtPath);

        softcodeCommand.add("-c");
        softcodeCommand.add("copy");
        softcodeCommand.add("-c:s");
        softcodeCommand.add("mov_text");

        softcodeCommand.add("-map");
        softcodeCommand.add("0");
        softcodeCommand.add("-map");
        softcodeCommand.add("1");
        softcodeCommand.add(FileUtils.getOutputPath(videoPath));

        return softcodeCommand;
    }

}
