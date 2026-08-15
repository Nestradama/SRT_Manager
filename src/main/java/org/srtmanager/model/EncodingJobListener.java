package org.srtmanager.model;

public interface EncodingJobListener {
    void onProgressChanged(double progress);
    void onStatusChanged(String status);
}