package hse.dm_lab.model;

import java.util.Objects;

public class Item {
    private Integer id;
    private String name;
    private Integer price;
    private String recipe;

    public Item() {}

    public Item(Integer id, String name, Integer price, String recipe) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.recipe = recipe;
    }

    public Item(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        if (this.id.equals(item.getId())) return true;
        return id.equals(item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(price, item.price) &&
                Objects.equals(recipe, item.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, recipe);
    }

}
