package org.srtmanager.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.srtmanager.model.EncodingJob;
import org.srtmanager.model.EncodingJobListener;

import java.util.ArrayList;
import java.util.List;

public class EncodingServiceTest {
    @Test
    void run() throws InterruptedException {
        RecordingListener stub = new RecordingListener();
        long deadline = System.currentTimeMillis() + 10000;
        String videoPath = "MockSources/video_with_subs.mp4";
        String srtPath = "MockSources/MockSRT.srt";

        List<String> hardcodeCommand = FfmpegService.buildHardcodeCommand(videoPath,
                srtPath, "libx264", 15, 15);
        EncodingJob encodingJob = new EncodingJob();
        encodingJob.setListener(stub);

        Thread encoding = new Thread(() ->
                EncodingService.run(hardcodeCommand, encodingJob, FfmpegService.getDuration(videoPath)));
        encoding.start();
        while (!encodingJob.status().equals("done") && (System.currentTimeMillis() < deadline)) {
            Thread.sleep(100);
        }

        assertThat(encodingJob.status()).isEqualTo("done");

        assertThat(stub.recordedProgress).isNotEmpty();
        if (stub.recordedProgress.size() >= 2) {
            assertThat(stub.recordedProgress.getFirst()).isLessThan(stub.recordedProgress.getLast());
        }
        assertThat(stub.recordedProgress.getLast()).isCloseTo(1.0, Percentage.withPercentage(35));
    }

    static class RecordingListener implements EncodingJobListener {
        List<Double> recordedProgress = new ArrayList<>();
        String recordedStatus = null;

        public void onProgressChanged(double value) {
            recordedProgress.add(value);
        }

        public void onStatusChanged(String status) {
            recordedStatus = status;
        }
    }
}
