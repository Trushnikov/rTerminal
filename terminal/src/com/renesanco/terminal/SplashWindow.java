package com.renesanco.terminal;

import java.awt.Taskbar;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.ImageIcon;

public class SplashWindow extends Application {

    @Override
    public void start(Stage stage) {
        stage = new Stage(StageStyle.TRANSPARENT);

        /* app icon */
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        /* try to set icon in taskbar of MacOS */
        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(new ImageIcon(getClass().getResource("/mac_icon.png")).getImage());
        } catch (Exception ex) {
        }

        Image img = new Image(getClass().getResourceAsStream("/splash.png"));
        ImageView backImage = new ImageView(img);
        backImage.setFitHeight(img.getHeight());
        backImage.setFitWidth(img.getWidth());
        Pane pane = new Pane(backImage);

        Pane labelPane = new Pane();
        Label lbl = new Label(AppSettings.APP_VERSION + " / Jan 2025");
        lbl.setAlignment(Pos.CENTER);
        lbl.setMinWidth(img.getWidth());
        labelPane.setStyle("-fx-background-color:transparent;");
        labelPane.getChildren().add(lbl);
        labelPane.setMinHeight(img.getHeight());
        labelPane.setMinWidth(img.getWidth());

        StackPane stackPane = new StackPane(pane, labelPane);
        stackPane.setStyle("-fx-background-color:transparent;");

        lbl.setLayoutY(labelPane.getMinHeight() - 30);

        Scene scene = new Scene(stackPane, img.getWidth(), img.getHeight(), Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setIconified(false);
        stage.setOpacity(0);
        stage.show();

        Timer timer = new Timer();
        MyTimerTask task = new MyTimerTask(stage, timer);
        timer.schedule(task, 0, 50);
    }

    private class MyTimerTask extends TimerTask {

        Stage parentStage;
        Timer timer;
        double opacity = 0.0;

        public MyTimerTask(Stage stage, Timer timer) {
            parentStage = stage;
            this.timer = timer;
        }

        @Override
        public void run() {
            Platform.runLater(() -> {
                if (opacity < 1.0) {
                    opacity += 0.02;
                }
                parentStage.setOpacity((opacity <= 1.0) ? opacity : 1.0);
                if (opacity >= 1.0) {
                    this.timer.cancel();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    MainWindow wnd = new MainWindow();
                    wnd.start(new Stage());
                    parentStage.close();
                }
            });
        }
    };

    public static void main(String[] args) {
        launch();
    }
}
