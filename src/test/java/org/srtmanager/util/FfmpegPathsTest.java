package org.srtmanager.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

public class FfmpegPathsTest {

    private boolean commandRuns(String command) {
        try {
            Process process = new ProcessBuilder(command, "-version").start();
            process.getInputStream().readAllBytes();
            return process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void FfmpegPaths() {
        assertThat(commandRuns(FfmpegPaths.ffmpegPath())).isTrue();
        assertThat(commandRuns(FfmpegPaths.ffprobePath())).isTrue();
    }
}
