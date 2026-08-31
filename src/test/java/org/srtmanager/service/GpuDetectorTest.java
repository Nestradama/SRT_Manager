package org.srtmanager.service;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.srtmanager.model.GpuEncoder;

import java.util.List;

public class GpuDetectorTest {

    @Test
    void detect(){
        List<GpuEncoder> result = GpuDetector.detect();
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(GpuEncoder::id).contains("libx264");
        assertThat(result).filteredOn(GpuEncoder::id, "libx264").extracting(GpuEncoder::isGpu).containsOnly(false);
    }

    @Test
    void detectUsable(){
        List<GpuEncoder> result = GpuDetector.detectUsable();
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(GpuEncoder::id).contains("libx264");
        assertThat(result).extracting(GpuEncoder::id).doesNotContain("zmbv", "a64multi");
    }
}
