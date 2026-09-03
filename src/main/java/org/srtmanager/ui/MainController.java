package org.srtmanager.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.srtmanager.model.EncodingJob;
import org.srtmanager.model.EncodingJobListener;
import org.srtmanager.model.GpuEncoder;
import org.srtmanager.model.SubtitleTrack;
import org.srtmanager.service.EncodingService;
import org.srtmanager.service.FfmpegService;
import org.srtmanager.service.GpuDetector;

import java.io.File;
import java.util.List;

public class MainController implements EncodingJobListener {

    @FXML private TextField videoField;
    @FXML private TextField srtField;
    @FXML private ListView<SubtitleTrack> tracksList;
    @FXML private Button browseSrtButton;
    @FXML private RadioButton softcodeRadio;
    @FXML private RadioButton hardcodeRadio;
    @FXML private TextField previewSecondField;
    @FXML private ImageView previewImage;
    @FXML private VBox settingsPane;
    @FXML private ComboBox<GpuEncoder> encoderBox;
    @FXML private Slider fontSlider;
    @FXML private Label fontValueLabel;
    @FXML private Slider marginSlider;
    @FXML private Label marginValueLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private String videoPath;
    private String srtPath;           
    private GpuEncoder selectedEncoder;
    private int resizeWidth;
    private int resizeHeight;

    @FXML
    private void initialize() {
        bindSliderToLabel(fontSlider, fontValueLabel);
        bindSliderToLabel(marginSlider, marginValueLabel);
        softcodeRadio.setDisable(tracksList.getItems().isEmpty());
        configureEncoderDisplay();

        
        Thread encoders = new Thread(() -> {
            try {
                List<GpuEncoder> usable = GpuDetector.detectUsable();
                Platform.runLater(() -> {
                    encoderBox.getItems().setAll(usable);
                    if (usable.isEmpty()) {
                        statusLabel.setText("No usable encoder found for this machine.");
                    }
                });
            } catch (Exception e) {
                reportError("Encoder detection failed: " + e.getMessage());
            }
        });
        encoders.setDaemon(true);
        encoders.start();
    }

    private void reportError(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private static void bindSliderToLabel(Slider slider, Label label) {
        slider.valueProperty().addListener((obs, oldVal, newVal) ->
                label.setText(String.valueOf(newVal.intValue())));
    }

    private void configureEncoderDisplay() {
        encoderBox.setConverter(new javafx.util.StringConverter<GpuEncoder>() {
            @Override public String toString(GpuEncoder encoder) { return encoder == null ? null : encoder.displayName(); }
            @Override public GpuEncoder fromString(String s) { return null; }
        });
    }

    static int[] resizeDimensions(String label) {
        return switch (label) {
            case "1080p" -> new int[]{1920, 1080};
            case "720p"  -> new int[]{1280, 720};
            case "480p"  -> new int[]{854, 480};
            case "360p"  -> new int[]{640, 360};
            default      -> new int[]{0, 0};
        };
    }

    @FXML
    private void onBrowseVideo(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select a video");
        File file = chooser.showOpenDialog(videoField.getScene().getWindow());
        if (file != null) {
            videoPath = file.getAbsolutePath();
            videoField.setText(videoPath);
            
            new Thread(() -> {
                try {
                    List<SubtitleTrack> tracks = FfmpegService.detectSubtitles(videoPath);
                    Platform.runLater(() -> {
                        tracksList.getItems().setAll(tracks);
                        softcodeRadio.setDisable(tracks.isEmpty());
                        if (tracks.isEmpty()) {
                            statusLabel.setText("No embedded subtitle tracks found.");
                        }
                    });
                } catch (Exception e) {
                    reportError("Track detection failed: " + e.getMessage());
                }
            }, "detect-tracks").start();
        }
    }

    @FXML
    private void onBrowseSrt(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SubRip", "*.srt"));
        File file = chooser.showOpenDialog(srtField.getScene().getWindow());
        if (file != null) {
            srtPath = file.getAbsolutePath();
            srtField.setText(srtPath);
        }
    }

    @FXML
    private void onUseEmbeddedTrack(ActionEvent event) {
        SubtitleTrack track = tracksList.getSelectionModel().getSelectedItem();
        if (track == null) {
            statusLabel.setText("Select a track first.");
            return;
        }
        
        
        new Thread(() -> {
            try {
                String tempSrt = FfmpegService.extractEmbeddedTrack(videoPath, track.index());
                Platform.runLater(() -> {
                    srtPath = tempSrt;
                    srtField.setText("Embedded track #" + track.index() + " (" + tempSrt + ")");
                    browseSrtButton.setDisable(true);
                    statusLabel.setText("Embedded track extracted.");
                });
            } catch (Exception e) {
                reportError("Embedded track extraction failed: " + e.getMessage());
            }
        }, "extract-track").start();
    }

    @FXML
    private void onPreview(ActionEvent event) {
        if (!ready()) return;
        double second;
        try {
            second = Double.parseDouble(previewSecondField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Preview second is not a number.");
            return;
        }
        int fontSize = (int) Math.round(fontSlider.getValue());
        int margin = (int) Math.round(marginSlider.getValue());
        new Thread(() -> {
            try {
                String png = FfmpegService.extractPreviewFrame(videoPath, srtPath, second, fontSize, margin);
                Image image = new Image(new File(png).toURI().toString());
                Platform.runLater(() -> previewImage.setImage(image));
            } catch (Exception e) {
                reportError("Preview failed: " + e.getMessage());
            }
        }, "preview").start();
    }

    @FXML
    private void onEncode(ActionEvent event) {
        if (!ready()) return;
        if (softcodeRadio.isSelected()) {
            runEncoding(FfmpegService.buildSoftcodeCommand(videoPath, srtPath));
        } else {
            GpuEncoder encoder = encoderBox.getValue();
            if (encoder == null) {
                statusLabel.setText("Choose an encoder.");
                return;
            }
            int fontSize = (int) Math.round(fontSlider.getValue());
            int margin = (int) Math.round(marginSlider.getValue());
            List<String> command = (resizeWidth > 0 && resizeHeight > 0)
                    ? FfmpegService.buildHardcodeCommand(videoPath, srtPath, encoder.id(), fontSize, margin, resizeWidth, resizeHeight)
                    : FfmpegService.buildHardcodeCommand(videoPath, srtPath, encoder.id(), fontSize, margin);
            runEncoding(command);
        }
    }

    private void runEncoding(List<String> command) {
        EncodingJob job = new EncodingJob();
        job.setListener(this);
        progressBar.setProgress(0);
        statusLabel.setText("Running...");
        new Thread(() -> {
            try {
                double duration = FfmpegService.getDuration(videoPath);
                EncodingService.run(command, job, duration);
            } catch (Exception e) {
                reportError("Encoding failed: " + e.getMessage());
            }
        }, "encode").start();
    }

    private boolean ready() {
        if (videoPath == null) {
            statusLabel.setText("Choose a video first.");
            return false;
        }
        if (srtPath == null) {
            statusLabel.setText("Choose an SRT file or an embedded track.");
            return false;
        }
        return true;
    }

    @FXML
    private void onResize(ActionEvent event) {
        String label = ((ToggleButton) event.getSource()).getText();
        int[] dims = resizeDimensions(label);
        resizeWidth = dims[0];
        resizeHeight = dims[1];
        statusLabel.setText("Resize: " + label);
    }

    
    @Override
    public void onProgressChanged(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    @Override
    public void onStatusChanged(String status) {
        Platform.runLater(() -> {
            statusLabel.setText(status);
            if ("done".equals(status)) {
                progressBar.setProgress(1);
            }
        });
    }
}
