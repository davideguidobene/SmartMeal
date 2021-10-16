package it.unive.quadcore.smartmeal.model;


import androidx.annotation.NonNull;

/**
 * Classe che rappresenta i prezzi
 */
public class Money {
    private final int value;

    public Money(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @NonNull
    public String getEuroString() {
        return String.format("%d", getValue() / 100);
    }

    @NonNull
    public String getCentString() {
        int cent = getValue() % 100;
        return cent <= 9 ? String.format("0%d", cent) : String.format("%d", cent);
    }

    @NonNull
    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
