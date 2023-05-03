package com.recipesearch.recipefinder;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //creates GUI stuff
        Label resultLabel = new Label();
        Label label = new Label("Enter a recipe query:");
        TextField textField = new TextField();
        Button searchButton = new Button("Search");
        Button quitButton = new Button("Quit");

        //creates scrollable pane to display search results
        resultLabel.setMaxHeight(Double.MAX_VALUE);
        resultLabel.setMaxWidth(Double.MAX_VALUE);
        ScrollPane scrollPane = new ScrollPane(resultLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");



        // adds GUI stuff and WebView to vertical box
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-border-color: #BDBDBD; -fx-border-width: 1px;");
        vbox.getChildren().addAll(label, textField, searchButton, quitButton, scrollPane);


        //creates a scene, then makes it the primary stage
        Scene scene = new Scene(vbox, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        //creates instance of RecipeSearch
        RecipeSearch recipeSearch = new RecipeSearch(resultLabel);

        //makes the buttons actually function
        searchButton.setOnAction(event -> recipeSearch.search(textField.getText()));
        quitButton.setOnAction(event -> primaryStage.close());
    }
}
