package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Classe che rappresenta i clienti del locale
 */
public abstract class Customer {

    // id
    @NonNull
    private final String id;
    // nome
    @NonNull
    private final String name;

    public Customer(@NonNull String id, @NonNull String name) {
        Objects.requireNonNull(id, name);
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    // Uguaglianza rispetto a id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return getId().equals(customer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @NonNull
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
