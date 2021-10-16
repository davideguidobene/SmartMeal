package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Classe che rappresenta i prodotti del locale
 */
public class Product implements Comparable<Product>{
    @NonNull
    private final String name;
    @NonNull
    private final Money price;
    // Categoria del prodotto
    @NonNull
    private final FoodCategory category;
    @NonNull
    private final String description;

    public Product(@NonNull String name,@NonNull Money price,@NonNull FoodCategory category,@NonNull String description) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    @NonNull
    public String getName() {
        return name;
    }
    @NonNull
    public Money getPrice() {
        return price;
    }
    @NonNull
    public String getDescription() {
        return description;
    }
    @NonNull
    public FoodCategory getCategory() {
        return category;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category=" + category +
                '}';
    }

    // Due prodotti sono uguali se hanno lo stesso nome e hanno la stessa categoria
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                category == product.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }

    // Prodotti confrontabili rispetto alla categoria. Se hanno stessa categoria, sono confrontabili rispetto al nome
    // (lessicografico)
    @Override
    public int compareTo(Product o) {
        if(category==o.category)
            return name.compareTo(o.name);
        return category.compareTo(o.category);
    }
}
