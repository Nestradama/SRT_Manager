package org.srtmanager.model;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class SubtitleEntryTest {
    @Test
    void creation(){
        SubtitleEntry entry = new SubtitleEntry(10.5, 12.75, "Hello");
        assertThat(entry).isNotNull();    }

    @Test
    void accessors(){
        SubtitleEntry entry = new SubtitleEntry(10.5, 12.75, "Hello");
        assertThat(entry.startSeconds()).isEqualTo(10.5);
        assertThat(entry.endSeconds()).isEqualTo(12.75);
        assertThat(entry.text()).isEqualTo("Hello");
    }

    @Test
    void equality(){
        SubtitleEntry firstEntry = new SubtitleEntry(10.5, 12.75, "Hello");
        SubtitleEntry secondEntry = new SubtitleEntry(10.5, 12.75, "Hello");
        SubtitleEntry wrongEntry = new SubtitleEntry(10.6, 22.75, "Goodbye");

        assertThat(firstEntry).isEqualTo(secondEntry);
        assertThat(firstEntry).isNotEqualTo(wrongEntry);
    }
}
