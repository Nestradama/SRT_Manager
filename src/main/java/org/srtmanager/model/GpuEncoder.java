package org.srtmanager.model;

public record GpuEncoder(String id, String label, boolean isGpu) {

    public String displayName() {
        return isGpu ? id + " (Recommended)" : id;
    }
}
