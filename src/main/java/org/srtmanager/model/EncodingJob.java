package org.srtmanager.model;

public class EncodingJob {
    private double progress = -1.0;
    private String status = "idle";
    private EncodingJobListener listener;

    public void setListener(EncodingJobListener listener){
        this.listener = listener;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        if (this.listener != null){this.listener.onProgressChanged(progress);}

    }
    public void setStatus(String status) {
        this.status = status;
        if (this.listener != null){this.listener.onStatusChanged(status);}
    }

    public double progress(){return this.progress;}
    public String status(){return this.status;}


}
