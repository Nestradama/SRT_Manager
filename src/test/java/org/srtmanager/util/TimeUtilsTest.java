package org.srtmanager.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class TimeUtilsTest {
    
    @Test
    void convertZerosToSrtFormat() {
        assertThat(TimeUtils.toSrtTimestamp(0)).isEqualTo("00:00:00,000");
    }

    @Test
    void convertSecondsToSrtFormat() {
        assertThat(TimeUtils.toSrtTimestamp(52)).isEqualTo("00:00:52,000");
    }

    @Test
    void convertHoursToSrtFormat() {
        assertThat(TimeUtils.toSrtTimestamp(3600)).isEqualTo("01:00:00,000");
    }

    @Test
    void convertMillisecondsToSrtFormat() {
        assertThat(TimeUtils.toSrtTimestamp(0.5)).isEqualTo("00:00:00,500");
    }

    @Test
    void convertAllToSrtFormat() {
        assertThat(TimeUtils.toSrtTimestamp(7152.1)).isEqualTo("01:59:12,100");
    }

    @Test
    void convertTripleDigitsHourToSrtFormat() {
        assertThat(TimeUtils.toSrtTimestamp(360000)).isEqualTo("100:00:00,000");
    }

    

    @Test
    void fromSrtFormatSeconds() {
        assertThat(TimeUtils.fromSrtTimestamp("00:00:56,000")).isEqualTo(56.0);
    }

    @Test
    void fromSrtFormatMinutes() {
        assertThat(TimeUtils.fromSrtTimestamp("00:02:00,000")).isEqualTo(120.0);

    }

    @Test
    void fromSrtFormatHours() {
        assertThat(TimeUtils.fromSrtTimestamp("01:00:00,000")).isEqualTo(3600.0);
    }

    @Test
    void fromSrtFormatTripleDigitsHour() {
        assertThat(TimeUtils.fromSrtTimestamp("100:00:00,000")).isEqualTo(360000.0);
    }

    @Test
    void fromSrtFormatComplex() {
        assertThat(TimeUtils.fromSrtTimestamp("02:25:50,300")).isEqualTo(8750.3);
    }

    @Test
    void fromSrtDifferentSeparator(){
        assertThat(TimeUtils.fromSrtTimestamp("02:25:50.300", ".")).isEqualTo(8750.3);

    }

    
    @Test
    void fromSrtFailure() {
        assertThatThrownBy(() -> TimeUtils.fromSrtTimestamp("Banana")).isInstanceOf(IllegalArgumentException.class);
    }

}
