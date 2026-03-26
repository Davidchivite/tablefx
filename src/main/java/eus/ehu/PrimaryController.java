package eus.ehu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PrimaryController {

    private static final String POKE_API_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final int MIN_POKEMON_ID = 1;
    private static final int MAX_POKEMON_ID = 1025;

    private int currentPokemonId = 25;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea typesArea;

    @FXML
    private TextField heightField;

    @FXML
    private ImageView pokemonImage;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private void initialize() {
        loadPokemon(currentPokemonId);
    }

    @FXML
    private void handlePrevious() {
        if (currentPokemonId > MIN_POKEMON_ID) {
            loadPokemon(currentPokemonId - 1);
        }
    }

    @FXML
    private void handleNext() {
        if (currentPokemonId < MAX_POKEMON_ID) {
            loadPokemon(currentPokemonId + 1);
        }
    }

    private void loadPokemon(int pokemonId) {
        setLoadingState(true);

        Thread requestThread = new Thread(() -> {
            try {
                String responseBody = request(String.valueOf(pokemonId));
                JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();

                int id = root.get("id").getAsInt();
                String name = root.get("name").getAsString();
                int height = root.get("height").getAsInt();

                List<String> types = new ArrayList<>();
                JsonArray typesArray = root.getAsJsonArray("types");
                for (JsonElement typeElement : typesArray) {
                    JsonObject typeObject = typeElement.getAsJsonObject().getAsJsonObject("type");
                    if (typeObject != null && typeObject.has("name")) {
                        types.add(typeObject.get("name").getAsString());
                    }
                }

                StringBuilder typesText = new StringBuilder();
                for (int i = 0; i < types.size(); i++) {
                    String typeName = types.get(i);
                    typesText.append(capitalize(typeName));
                    if (i < types.size() - 1) {
                        typesText.append("\n");
                    }
                }

                String resolvedSpriteUrl = "";
                JsonObject sprites = root.getAsJsonObject("sprites");
                if (sprites != null && sprites.has("front_default") && !sprites.get("front_default").isJsonNull()) {
                    resolvedSpriteUrl = sprites.get("front_default").getAsString();
                }
                final String spriteUrl = resolvedSpriteUrl;

                javafx.application.Platform.runLater(() -> {
                    currentPokemonId = id;
                    idField.setText(String.valueOf(id));
                    nameField.setText(capitalize(name));
                    typesArea.setText(typesText.toString());
                    heightField.setText(height + " dm");
                    if (spriteUrl.isEmpty() || "null".equals(spriteUrl)) {
                        pokemonImage.setImage(null);
                    } else {
                        pokemonImage.setImage(new Image(spriteUrl, true));
                    }
                    setLoadingState(false);
                });
            } catch (RuntimeException ex) {
                javafx.application.Platform.runLater(() -> setLoadingState(false));
            }
        });

        requestThread.setDaemon(true);
        requestThread.start();
    }

    public static String request(String id) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(POKE_API_URL + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Request failed with HTTP code " + response.code());
            }
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setLoadingState(boolean loading) {
        previousButton.setDisable(loading || currentPokemonId <= MIN_POKEMON_ID);
        nextButton.setDisable(loading || currentPokemonId >= MAX_POKEMON_ID);
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
