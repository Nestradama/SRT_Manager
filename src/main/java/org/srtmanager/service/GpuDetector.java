package org.srtmanager.service;

import org.srtmanager.model.GpuEncoder;
import org.srtmanager.util.FfmpegPaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GpuDetector {

    
    private static final List<String> GPU_MARKERS = List.of(
            "nvenc",   
            "qsv",     
            "amf",     
            "videotoolbox" 
    );

    public static List<GpuEncoder> detect() {
        try {
            Process ffmpeg = new ProcessBuilder(FfmpegPaths.ffmpegPath(), "-encoders").start();
            String foundEncoder = new String(ffmpeg.getInputStream().readAllBytes());
            ffmpeg.waitFor();
            List<GpuEncoder> encoders = getGpuEncoders(foundEncoder);
            return encoders;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("ffmpeg failed: "+ e.getMessage(), e);
        }

    }

    private static List<GpuEncoder> getGpuEncoders(String foundEncoder) {
        List<GpuEncoder> encoders = new ArrayList<>();

        
        String[] lines = foundEncoder.split("\n");

        for (String line : lines) {
            String[] parts = line.trim().split("\\s+");
            if (line.startsWith(" V") && parts.length >= 3 && !parts[1].equals("=")) {
                String id = parts[1];
                String label = parts[2];
                boolean isGpu = isGpuEncoder(id);

                encoders.add(new GpuEncoder(id, label, isGpu));
            }

        }
        return encoders;
    }

    private static boolean isGpuEncoder(String id) {

        for (String marker : GPU_MARKERS) {
            if (id.contains(marker)) {
                return true;
            }
        }
        return false;
    }
}
