package at.htl.common.fx;

import at.htl.common.io.FileUtils;
import com.aquafx_project.AquaFx;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;

import java.io.File;

/**
 * @timeline FxUtils
 * 03.01.2016: MET 001  created class
 * 03.01.2016: MET 020  improved selection of folders and files: chooseDirectory() and chooseFile()
 * 03.01.2016: MET 003  made setMsg() statically available
 * 31.03.2016: MET 010  TextAnimation: implemented method for blinking text
 * 31.03.2016: MET 005  TextAnimation: implemented method for disappearing text
 * 22.04.2016: PHI 120  changed the style of the PopUp-Window
 * 07.05.2016: PHI 035  styled the PopUp-Window
 * 28.05.2016: PHI 010  popUp-Window can be closed with ENTER-key
 * 19.06.2016: PHI 001  the popUp-Window is never in fullscreen and not resizeable.
 */
public class FxUtils {

    public static final String DEFAULT_CHOOSE_DIRECTORY_TITLE = "Select directory";
    public static final String DEFAULT_CHOOSE_FILE_TITLE = "Select file";
    public static final String DEFAULT_INITIAL_DIRECTORY = "user.home";

    /**
     * opens a window in which a folder can be chosen
     *
     * @return selected directory
     */
    @SuppressWarnings("unused")
    public static File chooseDirectory() {
        return chooseDirectory(null, null);
    }

    /**
     * opens a window in which a folder can be chosen
     *
     * @param title            heading of DirectoryChooser
     * @param initialDirectory start directory after opening
     * @return selected directory
     */
    public static File chooseDirectory(String title, String initialDirectory) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(title == null ? DEFAULT_CHOOSE_DIRECTORY_TITLE : title);
        dc.setInitialDirectory(new File(System.getProperty(
                initialDirectory == null ? DEFAULT_INITIAL_DIRECTORY : initialDirectory)));
        return dc.showDialog(new Stage());
    }

    /**
     * opens a window in which a file can be chosen
     *
     * @return selected file
     */
    @SuppressWarnings("unused")
    public static File chooseFile() {
        return chooseFile(null, null, null);
    }

    /**
     * opens a window in which a file can be chosen
     *
     * @param title            heading of FileChooser
     * @param initialDirectory start directory after opening
     * @param extensionFilter  limitation of file extensions
     * @return selected file
     */
    public static File chooseFile(String title, String initialDirectory, String extensionFilter) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title == null ? DEFAULT_CHOOSE_FILE_TITLE : title);
        fc.setInitialDirectory(new File(System.getProperty(
                initialDirectory == null ? DEFAULT_INITIAL_DIRECTORY : initialDirectory)));
        if (extensionFilter != null) {
            fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(extensionFilter));
        }
        return fc.showOpenDialog(new Stage());
    }

    /**
     * Displays text on a Label
     *
     * @param alert Label in which the text should be displayed
     * @param text  specifies the message to show
     * @param error TRUE   if it is an error-message
     *              FALSE  if it is a success-message
     */
    public static void setMsg(Label alert, String text, int status) {
        String color;
        switch (status) {
            case 0: // Error
                color = "red";
                break;
            case 1: // Warning
                color = "yellow";
                break;
            case 2: // Successfully
                color = "limegreen";
                break;
            default:
                color = "white";
                break;
        }

        Platform.runLater(() -> {
            alert.setText(text);
            alert.setStyle("-fx-background-color: " + color);
        });
    }

    /**
     * Creates a blinker
     *
     * @param node node
     * @return TimeLine
     */
    public static Timeline createBlinker(Node node) {
        Timeline blink = new Timeline(
                new KeyFrame(Duration.seconds(0.0), new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(node.opacityProperty(), 0, Interpolator.DISCRETE)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        //blink.setCycleCount(5);
        return blink;
    }

    /**
     * Creates a fade
     *
     * @param node node
     * @return fade transition
     */
    public static FadeTransition createFade(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }

    /**
     * shows a message in a pop-up window.
     *
     * @see   <a href="http://github.com/BeatingAngel/Testumgebung/issues/27">PopUp GitHub Issue</a>
     *
     * @param message   the message to show in the pop-up.
     * @param isSuccess true if success-message and false if error-message
     *
     * @since 1.11.34.060
     */
    public static void showPopUp(String message, boolean isSuccess) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        AquaFx.styleStage(dialog, StageStyle.UNIFIED);
        AnchorPane ap = new AnchorPane();
        ap.setId("messageAnchorPane");

        Label label = new Label();
        label.setId("messageLabel");
        label.setLayoutY(0);
        label.setAlignment(Pos.CENTER);

        Label messageInfo = new Label(message);
        messageInfo.setId("messageInfo");
        messageInfo.setLayoutY(160);
        messageInfo.setLayoutX(10);

        ImageView iv = new ImageView();
        iv.setPickOnBounds(true);
        iv.setPreserveRatio(true);

        if (isSuccess) {
            iv.setImage(new Image("/images/checkOnce.gif"));
            iv.setFitHeight(287);
            iv.setFitWidth(400);
            iv.setLayoutY(-44);
            label.setText("Success Message");
            label.setStyle("-fx-background-color: chartreuse");
        } else {
            iv.setImage(new Image("/images/failure.gif"));
            iv.setFitHeight(135);
            iv.setFitWidth(146);
            iv.setLayoutX(130);
            iv.setLayoutY(34);
            label.setText("Error Message");
            label.setStyle("-fx-background-color: crimson");
        }
        ap.getChildren().addAll(iv, label, messageInfo);

        Scene dialogScene = new Scene(ap, 380, 220);
        dialogScene.getStylesheets().add("styles/sharedStyle.css");
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setFullScreen(false);

        dialogScene.setOnKeyReleased((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                dialog.close();
            }
        });

        dialog.show();
        FileUtils.log(Level.INFO, message);
    }

}
