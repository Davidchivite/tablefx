package eus.ehu;

import com.google.gson.annotations.SerializedName;


public class Pokemon {
    private String name;
    private int id;
    private int height;
    private Sprites sprites;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    //Nested Sprites class
    public static class Sprites {
        @SerializedName("front_default")
        private String front_default;

        public String getFrontDefault() {
            return front_default;
        }

        public void setFrontDefault(String frontDefault) {
            this.front_default = frontDefault;
        }

        public String getFront_default() {
            return front_default;
        }

        public void setFront_default(String front_default) {
            this.front_default = front_default;
        }
    }

}
