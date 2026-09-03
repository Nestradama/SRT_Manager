package org.srtmanager.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Test;

/**
 * Loads the real main.fxml and asserts the controller binds.
 * Needs JavaFX to find a render pipeline (a writable native cache).
 * When that isn't available the test is skipped, not failed.
 */
public class MainControllerFxmlTest {

    private boolean toolkitAvailable() {
        try {
            javafx.application.Platform.startup(() -> {});
            return true;
        } catch (IllegalStateException alreadyRunning) {
            return true;
        } catch (Throwable noToolkit) {
            return false;
        }
    }

    @Test
    void mainFxmlLoadsAndBindsController() throws Exception {
        assumeTrue(toolkitAvailable(), "JavaFX toolkit unavailable (no render pipeline)");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        javafx.scene.Parent root = loader.load();
        MainController controller = loader.getController();

        assertThat(root).isNotNull();
        assertThat(controller).isNotNull();
    }
}
