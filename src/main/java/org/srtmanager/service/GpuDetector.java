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

    
    private static final List<String> USABLE_ENCODERS = List.of(
            "libx264", "libx265",
            "h264_nvenc", "h264_qsv", "h264_amf",
            "hevc_nvenc", "hevc_qsv", "hevc_amf",
            "av1_nvenc", "av1_qsv", "av1_amf"
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

    
    public static List<GpuEncoder> detectUsable() {
        List<GpuEncoder> all = detect();
        List<GpuEncoder> usable = new ArrayList<>();
        for (GpuEncoder encoder : all) {
            if (USABLE_ENCODERS.contains(encoder.id()) && canEncode(encoder.id())) {
                usable.add(encoder);
            }
        }
        return usable;
    }

    
    private static boolean canEncode(String encoderId) {
        try {
            Process probe = new ProcessBuilder(
                    FfmpegPaths.ffmpegPath(),
                    "-f", "lavfi", "-i", "color=s=64x64:d=0.1",
                    "-c:v", encoderId,
                    "-f", "null", "-")
                    .redirectErrorStream(true).start();
            probe.getInputStream().readAllBytes();
            return probe.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
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
