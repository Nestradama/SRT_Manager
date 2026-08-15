package org.srtmanager.util;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {
    @Test
    void getOutputVideoFilenameMp4(){
        assertThat(FileUtils.getOutputPath("video-example.mp4")).isEqualTo("video-example-subbed.mp4");
    }
    @Test
    void getOutputVideoFilenameOther(){
        assertThat(FileUtils.getOutputPath("video.mkv")).isEqualTo("video-subbed.mkv");
    }

    @Test
    void getOutputVideoFilenameComplex(){
        assertThat(FileUtils.getOutputPath("video.lets.go-to-disneyland.mp4")).isEqualTo("video.lets.go-to-disneyland-subbed.mp4");
    }

    @Test
    void getOutputVideoFilenameNoExtension(){
        
        assertThat(FileUtils.getOutputPath("video")).isEqualTo("video-subbed");
    }
}
