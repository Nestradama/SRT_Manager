package org.srtmanager.service;
import org.srtmanager.model.SubtitleEntry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SubtitleParserTest {
    @Test
    void parseSrtFile() {
        List<SubtitleEntry> result = SubtitleParser.parseSrtFile("src/test/resources/sample.srt");
        assertThat(result).hasSize(11);
        assertThat(result.get(0).text()).isEqualTo("ASCII & Basic Punctuation:\nThe quick brown fox jumps over the lazy dog! (1234567890) #$%^&*~");
    }

    @Test
    void parseMultiline(){
        List<SubtitleEntry> result = SubtitleParser.parseSrtFile("src/test/resources/sample.srt");
        assertThat(result).hasSize(11);
        assertThat(result.get(8).text()).isEqualTo("Symbols, Math, Currency & Emojis:\n" +
                "€ £ ¥ $ ₹ ¢ • © ® ™ ° ± ≠ ≈ ∞ √\n" +
                "Unicode Emojis: \uD83C\uDFAC \uD83C\uDF7F \uD83D\uDE80 ⚠\uFE0F \uD83C\uDF0D \uD83E\uDD16 ☕");
    }

    @Test
    void parseAccents(){
        List<SubtitleEntry> result = SubtitleParser.parseSrtFile("src/test/resources/sample.srt");
        assertThat(result).hasSize(11);
        assertThat(result.get(1).text()).isEqualTo("Western European (Diacritics & Accents):\n" +
                "Hélène a mangé des crêpes à Noël près de l'océan déjà gelé.\n" +
                "Übergrößenträger müssen für Süßigkeiten büßen.");
    }

    @Test
    void multiAlphabet(){
        List<SubtitleEntry> result = SubtitleParser.parseSrtFile("src/test/resources/multi_alphabet_sample.srt");
        assertThat(result).hasSize(8);
        assertThat(result.get(0).text()).isEqualTo("Привет мир! Это проверка кириллицы.");
        assertThat(result.get(1).text()).isEqualTo("Γειά σου Κόσμε! Αυτό είναι μια δοκιμή.");
        assertThat(result.get(2).text()).isEqualTo("مرحباً بالعالم! هذا اختبار باللغة العربية.");
        assertThat(result.get(3).text()).isEqualTo("שלום עולם! זהו מבחן בעברית.");
        assertThat(result.get(4).text()).isEqualTo("नमस्ते दुनिया! यह एक परीक्षण है।");
        assertThat(result.get(5).text()).isEqualTo("你好，世界！这是一次测试。");
        assertThat(result.get(6).text()).isEqualTo("こんにちは世界！これはテストです。");
        assertThat(result.get(7).text()).isEqualTo("안녕 세상! 이것은 테스트입니다.");
    }
}