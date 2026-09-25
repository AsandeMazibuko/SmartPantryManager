package com.AsandeMazibuko402311204.smartpantrymanager.data;

public class Recipe {private long id;
    private String name;
    private String instructions;
    private java.util.List<RecipeIngredient> ingredients = new java.util.ArrayList<>();

    public Recipe(long id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getInstructions() { return instructions; }
    public java.util.List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(java.util.List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}
