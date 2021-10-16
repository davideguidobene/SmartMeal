package it.unive.quadcore.smartmeal.model;

import android.location.Location;

import androidx.annotation.NonNull;

/**
 * Classe che rappresenta la descrizione del locale
 */
public class LocalDescription {

    @NonNull
    private final String name;
    @NonNull
    private final String presentation ;
    @NonNull
    private final Location location;
    @NonNull
    private final Menu menu;

    public LocalDescription(@NonNull String name,@NonNull String presentation,@NonNull Location location,@NonNull Menu menu){
        this.name = name;
        this.presentation = presentation;
        this.location = location;
        //this.imageID = imageID;
        this.menu = menu;
    }
    @NonNull
    public String getName(){
        return name;
    }
    @NonNull
    public String getPresentation(){
        return presentation;
    }
    @NonNull
    public Location getLocation(){
        return location;
    }
    @NonNull
    public Menu getMenu(){
        return menu;
    }

    @NonNull
    @Override
    public String toString() {
        return "LocalDescription{" +
                "name='" + name + '\'' +
                ", presentation='" + presentation + '\'' +
                ", location=" + location +
               // ", image=" + imageID +
                ", menu=" + menu +
                '}';
    }
}
