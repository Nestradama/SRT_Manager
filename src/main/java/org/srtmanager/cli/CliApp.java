package org.srtmanager.cli;

import org.srtmanager.model.EncodingJob;
import org.srtmanager.model.EncodingJobListener;
import org.srtmanager.model.GpuEncoder;
import org.srtmanager.model.SubtitleTrack;
import org.srtmanager.service.EncodingService;
import org.srtmanager.service.FfmpegService;
import org.srtmanager.service.GpuDetector;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CliApp {

    public static void cliMode() {
        Scanner scanner = new Scanner(System.in);
        String videoPath = "(None currently selected)";
        String srtPath = "(None currently selected)";
        String selectedEncoder = null;

        int fontSize = 16;
        int marginV = 10;

        while (true) {
            List<String> options = new ArrayList<>();
            options.add("1. Choose a video        " + videoPath + "\n");
            options.add("2. Choose a .srt file    " + srtPath + "\n");
            options.add("3. List subtitles integrated with the video\n");
            options.add("4. Preview movie frame (hardcode)\n");
            options.add("5. Hardcode subtitles (Engraves the subtitles on the video)\n");
            options.add("6. Softcode subtitles (Integrates subtitles along the video)\n");
            options.add("7. Quit");

            for (String option : options) {
                System.out.println(option);
            }
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1":
                    System.out.println("Please enter your video path");
                    userInput = scanner.nextLine();
                    videoPath = userInput;
                    break;
                case "2":
                    System.out.println("Please enter your SRT file path");
                    userInput = scanner.nextLine();
                    srtPath = userInput;
                    break;

                case "3":
                    if (isValidPath(videoPath)) {
                        List<SubtitleTrack> tracks = FfmpegService.detectSubtitles(videoPath);
                        if (tracks.isEmpty()) {
                            System.out.println("No embedded subtitle tracks found.");
                        } else {
                            for (SubtitleTrack track : tracks) {
                                System.out.println("Track " + track.index()
                                        + " | codec: " + track.codec()
                                        + " | language: " + track.language()
                                        + " | title: " + track.title());
                            }
                        }
                    } else {
                        System.out.println("Please choose a valid video first (option 1).");
                    }
                    break;

                case "4":
                    if (isValidPath(videoPath) && isValidPath(srtPath)) {
                        System.out.println("Preview at which second? (e.g. 1)");
                        try {
                            double previewSecond = Double.parseDouble(scanner.nextLine());
                            String previewPath = FfmpegService.extractPreviewFrame(
                                    videoPath, srtPath, previewSecond, fontSize, marginV);
                            System.out.println("Preview frame written to: " + previewPath);
                        } catch (NumberFormatException e) {
                            System.out.println("That was not a valid number.");
                        }
                    } else {
                        System.out.println("Please choose a valid video and SRT first (options 1 and 2).");
                    }
                    break;

                case "5":
                    if (isValidPath(videoPath) && isValidPath(srtPath)) {
                        List<GpuEncoder> encoders = GpuDetector.detectUsable();
                        System.out.println("Please choose your encoder");
                        for (int i = 1; i < encoders.size() + 1; i++) {
                            GpuEncoder encoder = encoders.get(i - 1);
                            System.out.println(i + ". " + encoder.id() + (encoder.isGpu() ? " (GPU)" : ""));
                        }
                        selectedEncoder = encoders.get(scanner.nextInt() - 1).id();
                        scanner.nextLine();
                    } else {
                        if (!isValidPath(videoPath)) {
                            System.out.println("Video path is not valid, please retry (option 1).");
                        }
                        if (!isValidPath(srtPath)) {
                            System.out.println("SRT path is not valid, please retry (option 2).");
                        }
                        break;
                    }

                    System.out.println("Please enter a font size (Default: 16)");
                    try {
                        fontSize = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Font size has been set to Default (16 px)");
                        fontSize = 16;
                    }

                    System.out.println("Please enter a margin (Space between the bottom and the subtitles (Default: 10 px)");
                    try {
                        marginV = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Margin has been set to Default (10)");
                        marginV = 10;
                    }

                    System.out.println("Do you wish to proceed with those settings: \n" +
                            "Video path:" + videoPath + "\n" +
                            "Encoder:" + selectedEncoder + "\n" +
                            "Font size:" + fontSize + "\n" +
                            "Margin:" + marginV + "\n" +
                            "[Y/N]");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        List<String> command = FfmpegService.buildHardcodeCommand(
                                videoPath, srtPath, selectedEncoder, fontSize, marginV);
                        runEncoding(command, videoPath);
                    }
                    break;

                case "6":
                    if (isValidPath(videoPath) && isValidPath(srtPath)) {
                        List<String> command = FfmpegService.buildSoftcodeCommand(videoPath, srtPath);
                        runEncoding(command, videoPath);
                    } else {
                        System.out.println("Please choose a valid video and SRT first (options 1 and 2).");
                    }
                    break;

                case "7":
                    return;
            }
        }
    }

    private static void runEncoding(List<String> command, String videoPath) {
        EncodingJob encodingJob = new EncodingJob();
        double videoDuration = FfmpegService.getDuration(videoPath);

        encodingJob.setListener(new EncodingJobListener() {
            public void onProgressChanged(double progress) {
                System.out.printf("\rProgress: %.1f%%", progress * 100);
            }

            public void onStatusChanged(String status) {
                System.out.println();
                System.out.println("Status: " + status);
            }
        });

        Thread encoding = new Thread(() -> EncodingService.run(command, encodingJob, videoDuration));
        encoding.start();
    }

    private static boolean isValidPath(String filePath) {
        try {
            return Files.exists(Path.of(filePath));
        } catch (InvalidPathException | NullPointerException e) {
            return false;
        }
    }
}
