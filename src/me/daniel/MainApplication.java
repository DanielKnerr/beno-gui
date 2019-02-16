package me.daniel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import me.daniel.Constants;
import me.daniel.Controllers.MainController;

import java.io.File;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Main.fxml"));
        loader.setController(new MainController());
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Beno GUI");
        stage.setScene(scene);
        stage.show();

        (new File(Constants.CONFIGS_DIR)).mkdirs();
        (new File(Constants.OUTPUT_DIR)).mkdirs();
        (new File(Constants.BINARY_DIR)).mkdirs();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
