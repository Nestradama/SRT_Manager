package org.srtmanager.model;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class EncodingJobTest {
    @Test
    void creation(){
        EncodingJob job = new EncodingJob();
        assertThat(job.progress()).isEqualTo(-1.0);
        assertThat(job.status()).isEqualTo("idle");
    }

    @Test
    void progressChange(){
        RecordingListener stub = new RecordingListener();
        EncodingJob job = new EncodingJob();
        job.setListener(stub);
        job.setProgress(0.5);
        assertThat(stub.recordedProgress).isEqualTo(0.5);
    }
    @Test
    void statusChange(){
        RecordingListener stub = new RecordingListener();
        EncodingJob job = new EncodingJob();
        job.setListener(stub);
        job.setStatus("idle");
        assertThat(stub.recordedStatus).isEqualTo("idle");
    }

    static class RecordingListener implements EncodingJobListener{
        double recordedProgress = -1.0;
        String recordedStatus = null;
        public void onProgressChanged(double value){
            recordedProgress = value;
        }
        public void onStatusChanged(String status){
            recordedStatus = status;
        }
    }
}
