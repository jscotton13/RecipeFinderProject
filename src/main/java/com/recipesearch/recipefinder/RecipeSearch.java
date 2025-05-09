package com.recipesearch.recipefinder;

import javafx.geometry.Orientation;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RecipeSearch {
    private static final String APP_ID = "ff82b71b";
    private static final String APP_KEY = "f61f429f0f13b25ad8d3c7958d6160e4";
    private final Label resultLabel;

    public RecipeSearch(Label resultLabel) {
        this.resultLabel = resultLabel;
    }

    public void search(String query) {
        try {
            //encodes query to be used in api url
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // Construct the Edamam v2 API URL
            String url = "https://api.edamam.com/api/recipes/v2"
                    + "?type=public"
                    + "&q=" + encodedQuery
                    + "&app_id=" + APP_ID
                    + "&app_key=" + APP_KEY;

            //connects to API and makes GET request
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            //reads API's response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            String json = responseBuilder.toString();

            //parses JSON response from API
            JSONObject jsonObject = new JSONObject(json);
            JSONArray hits = jsonObject.getJSONArray("hits");
            int count = hits.length();

            //creates flow pane that'll hold the recipes found
            FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 10, 10);
            flowPane.setPrefWrapLength(500);
            flowPane.setMaxWidth(Double.MAX_VALUE);

            //loops through each recipe in the response then displays them in the flow pane
            for (int i = 0; i < count; i++) {
                JSONObject hit = hits.getJSONObject(i);
                JSONObject recipe = hit.getJSONObject("recipe");
                display(recipe, flowPane);
            }

            //creates a scroll pane to hold the flow pane
            ScrollPane scrollPane = new ScrollPane(flowPane);

            //allows for user to scroll, also makes a physical scroll bar if window is too small to contain all the text
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            //set the result label to display the scroll pane
            resultLabel.setText("");
            resultLabel.setGraphic(scrollPane);
        }
        catch (Exception e) {
            e.printStackTrace();
            }
    }

    public void display(JSONObject recipe, FlowPane flowPane) {
        //gets recipe info from JSON object
        String label = recipe.getString("label");
        String url = recipe.getString("url");
        String imageUrl = recipe.getString("image");
        int yield = recipe.getInt("yield");
        JSONArray ingredientsArray = recipe.getJSONArray("ingredientLines");

        //creates an image view to display the recipe image
        Image image = new Image(imageUrl);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(200);

        //creates a box that holds recipe info
        VBox vbox = new VBox();

        //adds the recipe name, done in bold for emphasis
        Label labelView = new Label(label);
        labelView.setStyle("-fx-font-weight: bold");
        vbox.getChildren().add(labelView);

        //creates a hyperlink directly to the recipe
        Hyperlink link = new Hyperlink(url);
        link.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        vbox.getChildren().add(link);

        //creates a label to display how many servings recipe makes
        Label yieldView = new Label("Servings: " + yield);
        vbox.getChildren().add(yieldView);

        //creates a label to display the ingredients
        Label ingredientsView = new Label("Ingredients:");
        vbox.getChildren().add(ingredientsView);

        //loops through ingredients in ingredientArray, then adds it to vbox
        for (int i = 0; i < ingredientsArray.length(); i++) {
            String ingredient = ingredientsArray.getString(i);
            Label ingredientLabel = new Label("- " + ingredient);
            vbox.getChildren().add(ingredientLabel);
        }

        //creates hbox to hold recipe image, also holds vbox with recipe info
        HBox hbox = new HBox();
        hbox.getChildren().addAll(imageView, vbox);
        hbox.setSpacing(10);
        flowPane.getChildren().add(hbox);

        //creates separator between recipes
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(flowPane.getWidth() - 10);
        flowPane.getChildren().add(separator);
    }

}
