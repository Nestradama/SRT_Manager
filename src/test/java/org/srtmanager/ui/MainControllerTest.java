package org.srtmanager.ui;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class MainControllerTest {

    @Test
    void resizeOrigMeansNoResize(){
        assertThat(MainController.resizeDimensions("Orig")).containsExactly(0, 0);
    }

    @Test
    void resize1080p(){
        assertThat(MainController.resizeDimensions("1080p")).containsExactly(1920, 1080);
    }

    @Test
    void resize720p(){
        assertThat(MainController.resizeDimensions("720p")).containsExactly(1280, 720);
    }

    @Test
    void resize480p(){
        assertThat(MainController.resizeDimensions("480p")).containsExactly(854, 480);
    }

    @Test
    void resize360p(){
        assertThat(MainController.resizeDimensions("360p")).containsExactly(640, 360);
    }

    @Test
    void unknownLabelUploadsToNoResize(){
        assertThat(MainController.resizeDimensions("4k")).containsExactly(0, 0);
    }
}
