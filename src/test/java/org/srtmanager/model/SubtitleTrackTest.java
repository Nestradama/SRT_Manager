package org.srtmanager.model;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class SubtitleTrackTest {
    @Test
    void creation(){
        SubtitleTrack track = new SubtitleTrack(0, "SubRip", "eng", "The Movie: The Movie");
        assertThat(track).isNotNull();}

    @Test
    void accessors(){
        SubtitleTrack entry = new SubtitleTrack(0, "SubRip", "eng", "The Movie: The Movie");
        assertThat(entry.index()).isEqualTo(0);
        assertThat(entry.codec()).isEqualTo("SubRip");
        assertThat(entry.language()).isEqualTo("eng");
        assertThat(entry.title()).isEqualTo("The Movie: The Movie");
    }

    @Test
    void equality(){
        SubtitleTrack firstTrack = new SubtitleTrack(0, "SubRip", "eng", "The Movie: The Movie");
        SubtitleTrack secondTrack = new SubtitleTrack(0, "SubRip", "eng", "The Movie: The Movie");
        SubtitleTrack wrongTrack = new SubtitleTrack(0, "SubRip", "fr", "The Movie: The Movie");

        assertThat(firstTrack).isEqualTo(secondTrack);
        assertThat(firstTrack).isNotEqualTo(wrongTrack);
    }
}
