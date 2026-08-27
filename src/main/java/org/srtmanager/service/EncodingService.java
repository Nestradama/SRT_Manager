package org.srtmanager.service;

import org.srtmanager.model.EncodingJob;
import org.srtmanager.util.TimeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodingService {
    static void run(List<String> command, EncodingJob encodingJob, double videoDuration) {
        encodingJob.setStatus("running");

        try {
            Process ffmpeg = new ProcessBuilder(command).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpeg.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("time=")) {
                    String timeInLine = extractProgressTime(line);
                    double timeProgress = TimeUtils.fromSrtTimestamp(timeInLine, ".");
                    encodingJob.setProgress(timeProgress / videoDuration);
                }
            }
            int valueReturn = ffmpeg.waitFor();
            if (valueReturn == 0){
                encodingJob.setStatus("done");
            } else {
                throw new RuntimeException("Ffmpeg was unable to finish the job. exit code " + valueReturn);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractProgressTime(String ffmpegTimeLine) {
        Pattern pattern = Pattern.compile("time=(\\d{2}:\\d{2}:\\d{2}\\.\\d+)");
        Matcher matcher = pattern.matcher(ffmpegTimeLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("The timestamp is invalid");
        }
    }
}