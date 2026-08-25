package org.srtmanager.util;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class FfmpegPathsTest {
    @Test
    void FfmpegPaths(){
        assertThat(Files.exists(Path.of(FfmpegPaths.ffmpegPath()))).isTrue();
        assertThat(Files.exists(Path.of(FfmpegPaths.ffprobePath()))).isTrue();
    }
}
