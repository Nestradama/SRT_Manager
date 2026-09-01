package org.srtmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.srtmanager.model.SubtitleTrack;
import org.srtmanager.util.FfmpegPaths;
import org.srtmanager.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FfmpegService {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> buildHardcodeCommand(String videoPath, String srtPath,
                                                    String encoderId, int fontSize, int marginV) {
        return buildHardcodeCommand(videoPath, srtPath, encoderId, fontSize, marginV, 0, 0);
    }

    
    public static List<String> buildHardcodeCommand(String videoPath, String srtPath,
                                                    String encoderId, int fontSize, int marginV,
                                                    int width, int height) {
        List<String> hardcodeCommand = new ArrayList<>();

        hardcodeCommand.add(FfmpegPaths.ffmpegPath());
        hardcodeCommand.add("-y");

        hardcodeCommand.add("-i");
        hardcodeCommand.add(videoPath);

        String escapedSrt = srtPath.replace("\\", "/").replace(":", "\\:");
        String filters = (width > 0 && height > 0)
                ? "scale=" + width + ":" + height + ":force_original_aspect_ratio=decrease," +
                  "pad=" + width + ":" + height + ":(ow-iw)/2:(oh-ih)/2," +
                  buildSubtitleFilter(escapedSrt, fontSize, marginV)
                : buildSubtitleFilter(escapedSrt, fontSize, marginV);

        hardcodeCommand.add("-vf");
        hardcodeCommand.add(filters);

        hardcodeCommand.add("-c:v");
        hardcodeCommand.add(encoderId);

        hardcodeCommand.add("-c:a");
        hardcodeCommand.add("copy");

        hardcodeCommand.add(FileUtils.getOutputPath(videoPath));

        return hardcodeCommand;
    }

    public static List<String> buildSoftcodeCommand(String videoPath, String srtPath) {
        List<String> softcodeCommand = new ArrayList<>();

        softcodeCommand.add(FfmpegPaths.ffmpegPath());
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


    public static List<SubtitleTrack> detectSubtitles(String videoPath) {
        try {
            Process ffprobe = new ProcessBuilder(FfmpegPaths.ffprobePath(), "-v", "error", "-print_format", "json", "-show_streams", videoPath).start();
            String foundSubtitles = new String(ffprobe.getInputStream().readAllBytes());
            ffprobe.waitFor();
            JsonNode streamsFound = mapper.readTree(foundSubtitles);
            JsonNode streams = streamsFound.path("streams");
            List<SubtitleTrack> subtitles = new ArrayList<>();
            int subtitleIndex = 0;
            for (JsonNode stream : streams) {
                if (stream.path("codec_type").asText().equals("subtitle")) {
                    SubtitleTrack subtitleTrack = new SubtitleTrack(
                            subtitleIndex,
                            stream.path("codec_name").asText(),
                            stream.path("tags").path("language").asText(),
                            stream.path("tags").path("title").asText()
                    );
                    subtitles.add(subtitleTrack);
                    subtitleIndex++;
                }
            }
            return subtitles;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getDuration(String videoPath) {
        try {
            Process ffprobe = new ProcessBuilder(FfmpegPaths.ffprobePath(), "-v", "error", "-print_format", "json", "-show_entries", "format=duration", videoPath).start();
            String foundDuration = new String(ffprobe.getInputStream().readAllBytes());
            ffprobe.waitFor();
            JsonNode durationFound = mapper.readTree(foundDuration);

            return Double.parseDouble(durationFound.path("format").path("duration").asText());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static String extractEmbeddedTrack(String videoPath, int index) {

        try {
            Path tempfile = Files.createTempFile("", ".srt");

            Process ffmpeg = new ProcessBuilder(FfmpegPaths.ffmpegPath(), "-y", "-i", videoPath,
                    "-map", "0:s:" + index, "-c:s", "srt", tempfile.toString()).redirectErrorStream(true).start();
            ffmpeg.getInputStream().readAllBytes();
            ffmpeg.waitFor();

            return tempfile.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractPreviewFrame(String videoPath, String srtPath, double timestampSeconds, int fontSize, int marginFromBottom) {
        try {
            Path tempfile = Files.createTempFile("", ".png");
            String escapedSrt = srtPath.replace("\\", "/").replace(":", "\\:");

            Process ffmpeg = new ProcessBuilder(FfmpegPaths.ffmpegPath(), "-y", "-ss",
                    String.valueOf(timestampSeconds), "-i", videoPath, "-vf",
                    buildSubtitleFilter(escapedSrt, fontSize, marginFromBottom),
                    "-copyts", "-frames:v", "1", tempfile.toString()).redirectErrorStream(true).start();
            ffmpeg.getInputStream().readAllBytes();
            ffmpeg.waitFor();

            return tempfile.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    
    private static String buildSubtitleFilter(String escapedSrt, int fontSize, int marginFromBottom) {
        return "subtitles='" + escapedSrt + "':force_style='FontSize=" + fontSize +
                ",MarginV=" + marginFromBottom + "',format=yuv420p";
    }
}
