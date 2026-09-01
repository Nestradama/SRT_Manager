package org.srtmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;

import org.assertj.core.internal.Bytes;
import org.junit.jupiter.api.Test;
import org.srtmanager.model.SubtitleTrack;
import org.srtmanager.util.FfmpegPaths;

public class FfmpegServiceTest {

    @Test
    void buildHardcodeCommandWithWindowsPath() {
        String video = "C:/videos/test.mp4";
        String srt = "C:\\Users\\Me\\Subtitles\\test.srt";
        List<String> result = FfmpegService.buildHardcodeCommand(video, srt, "libx264", 18, 24);

        assertThat(result).contains("subtitles='C\\:/Users/Me/Subtitles/test.srt':force_style='FontSize=18,MarginV=24',format=yuv420p");
    }

    @Test
    void buildHardcodeCommand() {
        String video = "C:/videos/test.mp4";
        String srt = "C:/subtitles/test.srt";
        String encoder = "libx264";
        int fontSize = 18;
        int marginV = 24;

        List<String> result = FfmpegService.buildHardcodeCommand(video, srt, encoder, fontSize, marginV);

        assertThat(result).containsExactly(FfmpegPaths.ffmpegPath(),
                "-y",
                "-i", "C:/videos/test.mp4",
                "-vf", "subtitles='C\\:/subtitles/test.srt':force_style='FontSize=18,MarginV=24',format=yuv420p",
                "-c:v", "libx264",
                "-c:a", "copy",
                "C:/videos/test-subbed.mp4");
    }

    @Test
    void buildHardcodeCommandWithResize() {
        String video = "C:/videos/test.mp4";
        String srt = "C:/subtitles/test.srt";
        String encoder = "libx264";
        int fontSize = 18;
        int marginV = 24;

        List<String> result = FfmpegService.buildHardcodeCommand(video, srt, encoder, fontSize, marginV, 1280, 720);

        assertThat(result).containsExactly(FfmpegPaths.ffmpegPath(),
                "-y",
                "-i", "C:/videos/test.mp4",
                "-vf", "scale=1280:720:force_original_aspect_ratio=decrease,"
                        + "pad=1280:720:(ow-iw)/2:(oh-ih)/2,"
                        + "subtitles='C\\:/subtitles/test.srt':force_style='FontSize=18,MarginV=24',format=yuv420p",
                "-c:v", "libx264",
                "-c:a", "copy",
                "C:/videos/test-subbed.mp4");
    }

    @Test
    void buildHardcodeCommandWithZeroResizeFallsBackToOriginal() {
        String video = "C:/videos/test.mp4";
        String srt = "C:/subtitles/test.srt";

        
        List<String> resized = FfmpegService.buildHardcodeCommand(video, srt, "libx264", 18, 24, 0, 0);
        List<String> plain = FfmpegService.buildHardcodeCommand(video, srt, "libx264", 18, 24);

        assertThat(resized).isEqualTo(plain);
    }

    @Test
    void buildSoftcodeCommand() {
        String video = "C:/videos/test.mp4";
        String srt = "C:/subtitles/test.srt";

        List<String> result = FfmpegService.buildSoftcodeCommand(video, srt);

        assertThat(result).containsExactly(FfmpegPaths.ffmpegPath(),
                "-y",
                "-i", "C:/videos/test.mp4",
                "-i", "C:/subtitles/test.srt",
                "-c", "copy",
                "-c:s", "mov_text",
                "-map", "0",
                "-map", "1",
                "C:/videos/test-subbed.mp4");
    }

    @Test
    void detectSubtitles() {
        List<SubtitleTrack> tracks = FfmpegService.detectSubtitles("MockSources/video_with_subs.mp4");
        assertThat(tracks).hasSize(1);
        assertThat(tracks.getFirst().codec()).isEqualTo("mov_text");
        assertThat(tracks.getFirst().index()).isEqualTo(0);
    }

    @Test
    void getDuration() {
        String videoPath = "MockSources/video_with_subs.mp4";

        assertThat(FfmpegService.getDuration(videoPath)).isCloseTo(3.0, within(0.2));
    }

    @Test
    void extractEmbeddedTrack() throws IOException {
        String filePath = FfmpegService.extractEmbeddedTrack("MockSources/video_with_subs.mp4", 0);

        assertThat(new File(filePath)).exists();

        String content = Files.readString(Path.of(filePath));

        assertThat(content).contains("-->");

    }

    @Test
    void extractPreviewFrame() throws IOException {
        String filePath = FfmpegService.extractPreviewFrame("MockSources/video_with_subs.mp4", "MockSources/MockSRT.srt", 1, 18, 24);

        assertThat(new File(filePath)).exists();

        assertThat(Files.size(Path.of(filePath))).isGreaterThanOrEqualTo(8);

        byte[] bytes = Files.readAllBytes(Path.of(filePath));
        byte[] pngSignature = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        assertThat(Arrays.copyOfRange(bytes, 0, 8)).isEqualTo(pngSignature);
    }
}
