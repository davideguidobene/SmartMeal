package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta i tavoli del locale
 */
public abstract class Table implements Comparable<Table>, Serializable { // Togliere l'abstract ?
    // possibile costruttore package-private chiamato da un'altra classe

    @NonNull
    private final String id ;

    protected Table(@NonNull String id){
        this.id = id;
    }

    @NonNull
    public String getId(){
        return id ;
    }

    // Comparo rispetto l'id (ordine lessicografico). Ordinamento naturale di Table
    public int compareTo(Table other){
        return id.compareTo(other.id) ;
    }

    // Uguaglianza rispetto id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(id, table.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                '}';
    }
}
