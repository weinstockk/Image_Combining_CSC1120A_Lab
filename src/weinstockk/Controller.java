/*
 * Course: CSC-1120A
 * Lab 5 - Mean Image Median Revisited
 * Name: Keagan Weinstock
 * Last Updated: 02/12/2024
 */

package weinstockk;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

/**
 * Controller class for the Lab3.fxml file
 */
public class Controller {
    @FXML
    private Button addImage;
    @FXML
    private ImageView view;
    @FXML
    private Label notes;
    @FXML
    private HBox scroll;

    private Image output;
    private final ArrayList<Image> inputImages;
    private final ArrayList<String> inputNames;

    /**
     * Constructor For the Controller initializing lists
     */
    public Controller() {
        inputImages = new ArrayList<>();
        inputNames = new ArrayList<>();
    }

    @FXML
    private void calculateMean() {
        try {
            display(MeanImageMedian.generateImage(inputImages.toArray(new Image[0]), "mean"));
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: Image Mean Calculated");
        } catch (IllegalArgumentException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Two Images of the same size required");
            alert.setContentText("Add more pictures");
            alert.show();
        }
    }

    @FXML
    private void calculateMedian() {
        try {
            display(MeanImageMedian.generateImage(inputImages.toArray(new Image[0]), "median"));
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: Image Median Calculated");
        } catch (IllegalArgumentException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Three Images of the same size required");
            alert.setContentText("Add more pictures");
            alert.show();
        }
    }

    @FXML
    private void calculateMin() {
        try {
            display(MeanImageMedian.applyTransform(inputImages.toArray(new Image[0]), e -> {
                int minValue = 0;
                int index = 0;
                for (int i = 0; i < e.length; i++) {
                    if (minValue >= e[i]) {
                        index = i;
                        minValue = e[i];
                    }
                }
                return e[index];
            }));
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: Image Mean Calculated");
        } catch (IllegalArgumentException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Two Images of the same size required");
            alert.setContentText("Add more pictures");
            alert.show();
        }
    }

    @FXML
    private void calculateMax() {
        try {
            display(MeanImageMedian.applyTransform(inputImages.toArray(new Image[0]), e -> {
                int maxValue = 0;
                int index = 0;
                for (int i = 0; i < e.length; i++) {
                    if (maxValue <= e[i]) {
                        index = i;
                        maxValue = e[i];
                    }
                }
                return e[index];
            }));
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: Image Mean Calculated");
        } catch (IllegalArgumentException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Two Images of the same size required");
            alert.setContentText("Add more pictures");
            alert.show();
        }
    }

    @FXML
    private void calculateRandom() {
        try {
            display(MeanImageMedian.applyTransform(inputImages.toArray(new Image[0]), e -> {
                Random random = new Random();
                int index = random.nextInt(e.length);
                return e[index];
            }));
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: Image Mean Calculated");
        } catch (IllegalArgumentException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Two Images of the same size required");
            alert.setContentText("Add more pictures");
            alert.show();
        }
    }

    @FXML
    private void saveFile() {
        File imageFile;
        try {
            FileChooser saver = new FileChooser();
            Window stage = addImage.getScene().getWindow();
            imageFile = saver.showSaveDialog(stage);
            try {
                MeanImageMedian.writeImage(imageFile.toPath(), output);
            } catch (IllegalArgumentException e) {
                MeanImageMedian.writeImage(Path.of(imageFile + ".png"), output);
            }
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: Image Successfully Saved");
        } catch (IllegalArgumentException | IOException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Not Found");
            alert.setHeaderText("File Not Found");
            alert.setContentText("Your File May Be Corrupted");
            alert.show();
        } catch (NullPointerException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Not Found");
            alert.setHeaderText("File Not Found");
            alert.setContentText("You Closed Out Without Choosing A File");
            alert.show();
        }
    }

    @FXML
    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        Window stage = addImage.getScene().getWindow();
        File imageFile = chooser.showOpenDialog(stage);
        try {
            Image image = MeanImageMedian.readImage(imageFile.toPath());
            inputImages.add(image);
            String fileName = getEndOfString(imageFile.getPath());
            inputNames.add(fileName);
            inputDisplay();
            notes.setTextFill(Color.GREEN);
            notes.setText("Status: " + fileName + " Successfully Added");
        } catch (IOException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid File");
            alert.setHeaderText("Invalid File");
            alert.setContentText("Your File May Be Corrupted");
            alert.show();
        } catch (IllegalArgumentException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid File Type");
            alert.setHeaderText("Only PPM, PNG, JPG");
            alert.show();
        } catch (NullPointerException e) {
            error();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Not Found");
            alert.setHeaderText("File Not Found");
            alert.setContentText("You Closed Out Without Choosing A File");
            alert.show();
        }
    }

    private void inputDisplay() {
        final int scrollHeight = 125;
        final int spacing = 5;
        final int labelSpacing = 2;
        int index = 0;

        VBox inputDisplay = null;
        for (Image image : inputImages) {
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(scrollHeight);

            Button remove = new Button("Remove");
            Label fileName = new Label(inputNames.get(index));
            fileName.setAlignment(Pos.CENTER);

            HBox input = new HBox(fileName, remove);
            HBox.setMargin(fileName, new Insets(labelSpacing));


            VBox inputBox = new VBox(imageView, input);
            input.setAlignment(Pos.BOTTOM_RIGHT);
            VBox.setMargin(input, new Insets(spacing));

            final int noGap = 0;
            inputDisplay = new VBox(inputBox);
            VBox.setMargin(inputBox, new Insets(noGap, spacing, noGap, noGap));

            int finalIndex = index;
            remove.setOnAction(e -> {
                if (inputImages.size() == 1) {
                    scroll.getChildren().clear();
                    inputImages.clear();
                } else {
                    scroll.getChildren().remove(finalIndex);
                    inputImages.remove(finalIndex);
                    inputNames.remove(finalIndex);
                }


            });
            index++;
        }
        scroll.getChildren().addAll(inputDisplay);
    }

    private void display(Image image) {
        view.setImage(image);
        output = image;
    }

    private void error() {
        notes.setTextFill(Color.RED);
        notes.setText("Status: ERROR");
    }

    private static String getEndOfString(String inputString) {
        int lastIndexOfSlash = inputString.lastIndexOf("\\");

        if (lastIndexOfSlash != -1) {
            return inputString.substring(lastIndexOfSlash + 1);
        } else {
            return inputString;
        }
    }
}
