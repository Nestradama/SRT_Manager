package org.srtmanager.service;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.srtmanager.model.SubtitleTrack;

public class FfmpegServiceTest {
    @Test
    void buildHardcodeCommand() {
        String video = "C:/videos/test.mp4";
        String srt = "C:/subtitles/test.srt";
        String encoder = "libx264";
        int fontSize = 18;
        int marginV = 24;

        List<String> result = FfmpegService.buildHardcodeCommand(video, srt, encoder, fontSize, marginV);

        assertThat(result).containsExactly("ffmpeg",
                "-y",
                "-i", "C:/videos/test.mp4",
                "-vf", "subtitles='C:/subtitles/test.srt':force_style='FontSize=18,MarginV=24',format=yuv420p",
                "-c:v", "libx264",
                "-c:a", "copy",
                "C:/videos/test-subbed.mp4");
    }

    @Test
    void buildSoftcodeCommand() {
        String video = "C:/videos/test.mp4";
        String srt = "C:/subtitles/test.srt";

        List<String> result = FfmpegService.buildSoftcodeCommand(video, srt);

        assertThat(result).containsExactly("ffmpeg",
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
    void detectSubtitles(){
        List<SubtitleTrack> tracks = FfmpegService.detectSubtitles("MockSources/video_with_subs.mp4");
        assertThat(tracks).hasSize(1);
        assertThat(tracks.getFirst().codec()).isEqualTo("mov_text");
        assertThat(tracks.getFirst().index()).isEqualTo(1);
    }
}
