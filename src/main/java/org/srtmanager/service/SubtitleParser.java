package org.srtmanager.service;

import org.srtmanager.model.SubtitleEntry;
import org.srtmanager.util.TimeUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.util.Objects;

public class SubtitleParser {
    public static List<SubtitleEntry> parseSrtFile(String fp) {
        List<String> fileContent = null;
        List<SubtitleEntry> parsedSubtitleFile = new ArrayList<>();

        List<Double> entryTimestamps = new ArrayList<>();
        List<String> entryContent = new ArrayList<>();

        boolean constructionFlag = false;

        try {
            Path filepath = Path.of(fp);
            fileContent = Files.readAllLines(filepath);
        } catch (IOException e) {
            System.out.println(e);
        }

        for (String line : Objects.requireNonNull(fileContent)) {
            if (line.contains("-->")) {
                entryTimestamps.add(TimeUtils.fromSrtTimestamp(line.split(" --> ")[0],","));
                entryTimestamps.add(TimeUtils.fromSrtTimestamp(line.split(" --> ")[1],","));
                constructionFlag = true;
            } else if (!line.isBlank() && constructionFlag) {
                entryContent.add(line);
            } else if (line.isBlank() && !entryTimestamps.isEmpty()) {
                finishEntry(parsedSubtitleFile, entryTimestamps, entryContent);
                constructionFlag = false;
            }
        }
        if (constructionFlag) {
            finishEntry(parsedSubtitleFile, entryTimestamps, entryContent);
        }
        return parsedSubtitleFile;
    }
    private static void finishEntry(List<SubtitleEntry> parsedSubtitleFile, List<Double>entryTimestamps, List<String>entryContent){
        parsedSubtitleFile.add(new SubtitleEntry(entryTimestamps.get(0), entryTimestamps.get(1),
                String.join("\n", entryContent)));
        entryContent.clear();
        entryTimestamps.clear();
    }
}

