package org.srtmanager.model;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class GpuEncoderTest {
    @Test
    void creation(){
        GpuEncoder encoder = new GpuEncoder("h264_nvenc", "NVidia", true);
        assertThat(encoder).isNotNull();
    }

    @Test
    void accessors(){
        GpuEncoder encoder = new GpuEncoder("h264_nvenc", "NVidia", true);
        assertThat(encoder.id()).isEqualTo("h264_nvenc");
        assertThat(encoder.label()).isEqualTo("NVidia");
        assertThat(encoder.isGpu()).isEqualTo(true);
    }

    @Test
    void equality(){
        GpuEncoder firstEncoder = new GpuEncoder("h264_nvenc", "NVidia", true);
        GpuEncoder secondEncoder = new GpuEncoder("h264_nvenc", "NVidia", true);
        GpuEncoder wrongEncoder = new GpuEncoder("h264_nvenc", "NVidia", false);

        assertThat(firstEncoder).isEqualTo(secondEncoder);
        assertThat(firstEncoder).isNotEqualTo(wrongEncoder);
    }

    @Test
    void displayNameAddsRecommendedForGpuEncoders(){
        GpuEncoder gpu = new GpuEncoder("h264_nvenc", "NVidia", true);
        GpuEncoder cpu = new GpuEncoder("libx264", "libx264 H.264", false);

        assertThat(gpu.displayName()).isEqualTo("h264_nvenc (Recommended)");
        assertThat(cpu.displayName()).isEqualTo("libx264");
    }

}
