package me.daniel.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import me.daniel.Constants;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private HBox main;

    @FXML
    private ComboBox<String> comboBoxRenderer, comboBoxMethod;

    @FXML
    private Label btnCancel, btnSave, btnLoad;

    @FXML
    private Button btnRender;

    @FXML
    private TextField inputWidth, inputHeight, centerX, centerY, heightY, templateName, maxIter;

    @FXML
    private ImageView outputImage;

    private ObservableList<String> listRenderers = FXCollections.observableArrayList("CPU", "GPU");
    private ObservableList<String> listMethods = FXCollections.observableArrayList();

    private double context_click_x, context_click_y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxRenderer.setItems(listRenderers);
        comboBoxRenderer.getSelectionModel().selectFirst();

        updateMethods();
        setDefault();

        comboBoxRenderer.setOnAction(e -> updateMethods());

        setHoverEffect(btnCancel);
        setHoverEffect(btnSave);
        setHoverEffect(btnLoad);

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem center = new MenuItem("Zentrieren");
        MenuItem zoomIn = new MenuItem("Zoom +50%");
        MenuItem zoomOut = new MenuItem("Zoom -50%");
        contextMenu.getItems().addAll(center, zoomIn, zoomOut);

        center.setOnAction(e -> {
            int x = (int) (context_click_x / 640 * Integer.parseInt(inputWidth.getText()));
            int y = (int) (context_click_y / 640 * Integer.parseInt(inputHeight.getText()));
            BigDecimal out_x = new BigDecimal(0), out_y = new BigDecimal(0);

            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec(Constants.BINARY_DIR + File.separator + "util --config=" + Constants.CONFIGS_DIR + File.separator + templateName.getText() + ".config --x=" + x + " --y="+y);
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                int line_num = 0;
                String line;
                while ((line = in.readLine()) != null) {
                    if (line_num == 0) {
                        out_x = new BigDecimal(line);
                    } else if (line_num == 1) {
                        out_y = new BigDecimal(line);
                    }
                    line_num++;
                }
                process.waitFor();
                centerX.setText(String.valueOf(out_x));
                centerY.setText(String.valueOf(out_y));
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
        });

        zoomIn.setOnAction(e -> {
            double value = Double.parseDouble(heightY.getText());
            value = value - value * 0.5;
            heightY.setText(String.valueOf(value));
        });

        zoomOut.setOnAction(e -> {
            double value = Double.parseDouble(heightY.getText());
            value = value + value * 0.5;
            heightY.setText(String.valueOf(value));
        });

        outputImage.setOnMousePressed(e -> {
            if (e.isSecondaryButtonDown()) {
                context_click_x = e.getX();
                context_click_y = e.getY();
                contextMenu.show(outputImage, e.getScreenX(), e.getScreenY());
            }
        });

        main.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) {
                writeConfig();
                render();
            }
        });

        btnRender.setOnAction(e -> {
            writeConfig();
            render();
        });
    }

    private void setDefault() {
        inputWidth.setText("400");
        inputHeight.setText("400");

        centerX.setText("-0.75");
        centerY.setText("0.0");
        heightY.setText("1.5");
        templateName.setText("tmp");
        maxIter.setText("100");
    }

    private void writeConfig() {
        List<String> lines = new ArrayList<>();
        lines.add("width: " + inputWidth.getText());
        lines.add("height: " + inputHeight.getText());
        lines.add("center_x: " + centerX.getText());
        lines.add("center_y: " + centerY.getText());
        lines.add("height_y: " + heightY.getText());
        lines.add("max_iterations: " + maxIter.getText());
        lines.add("palette: {0, 0, 0, 0.0} {255, 0, 50, 0.8} {255, 210, 0, 1.0}");
        lines.add("output_path: " + Constants.OUTPUT_DIR + File.separator + templateName.getText());

        Path file = Paths.get( Constants.CONFIGS_DIR + File.separator + templateName.getText() + ".config");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHoverEffect(Label label) {
        label.setOnMouseEntered(e -> label.setTextFill(Paint.valueOf("#CCCCCC")));
        label.setOnMouseExited(e -> label.setTextFill(Paint.valueOf("#AAAAAA")));
    }

    private void updateMethods() {
        listMethods.clear();
        switch (comboBoxRenderer.getSelectionModel().getSelectedItem()) {
            case "GPU":
                listMethods.addAll("NIC");
                break;
            case "CPU":
                listMethods.addAll("NIC");
                break;
        }
        comboBoxMethod.setItems(listMethods);
        comboBoxMethod.getSelectionModel().selectFirst();
    }

    private void render()  {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec(Constants.BINARY_DIR + File.separator + "renderer --config=" + Constants.CONFIGS_DIR + File.separator + templateName.getText() + ".config");
            process.waitFor();
        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
        }

        try {
            String location = Constants.OUTPUT_DIR + File.separator + templateName.getText() + ".png";
            outputImage.setImage(new Image(new FileInputStream(location)));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
