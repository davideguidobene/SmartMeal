package it.unive.quadcore.smartmeal.storage;


import android.content.SharedPreferences;


import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.ManagerTable;


public final class ManagerStorage extends Storage {

    @NonNull
    private static final String TABLES_SHARED_PREFERENCE_KEY = "Tables";
    @NonNull
    private static final String MAX_NOTIFICATION_NUMBER_SHARED_PREFERENCE_KEY = "MaxNotificationNumber";

    private static final int MAX_NOTIFICATION_NUMBER = 5;

    /**
     * Rende non instanziabile questa classe.
     */
    private ManagerStorage() {}

    // Genera i tavoli di default
    @NonNull
    private static Set<String> generateTablesStrings(){

        Set<String> tables = new TreeSet<>();
        char supp = 'A';
        while(supp<'A'+1){ //
            for(int i=0;i<=9;i++)
                tables.add(""+supp+i);
            supp+=1;
        }

        return tables;
    }

    // Possibilità di non tenere i tavoli in memoria secondaria ma generarli e basta
    @NonNull
    public static Set<ManagerTable> getTables() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        if(getApplicationMode()!=ApplicationMode.MANAGER)
            throw new StorageException("You must be a manager to do this operation");

        Objects.requireNonNull(sharedPreferences);

        // Preference non esistente. Primo accesso a tale preference. Scrivo valore di deafult
        if(!sharedPreferences.contains(TABLES_SHARED_PREFERENCE_KEY)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(TABLES_SHARED_PREFERENCE_KEY,generateTablesStrings());
            editor.apply();
        }
        Set<String> tablesString = sharedPreferences.getStringSet(TABLES_SHARED_PREFERENCE_KEY,new TreeSet<>());

        Set<ManagerTable> tables = new TreeSet<>();
        for(String tableId : tablesString){
            ManagerTable table = new ManagerTable(tableId);
            tables.add(table);
        }

        return tables;
    }


    // Ritorna il numero massimo di notifiche in coda di uno stesso utente
    // Possibilità di non tenere tale numero in memoria secondaria ma generarlo e basta
    public static int getMaxNotificationNumber(){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        if(getApplicationMode()!=ApplicationMode.MANAGER)
            throw new StorageException("You must be a manager to do this operation");

        Objects.requireNonNull(sharedPreferences);

        // Preference non esistente. Primo accesso a tale preference. Scrivo valore di deafult
        if(!sharedPreferences.contains(MAX_NOTIFICATION_NUMBER_SHARED_PREFERENCE_KEY)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MAX_NOTIFICATION_NUMBER_SHARED_PREFERENCE_KEY,MAX_NOTIFICATION_NUMBER);
            editor.apply();
        }

        return sharedPreferences.getInt(MAX_NOTIFICATION_NUMBER_SHARED_PREFERENCE_KEY,MAX_NOTIFICATION_NUMBER);
    }

    // Ritorna la password reale cifrata
    @NonNull
    private static String getEncryptedPassword(){
        /* String password = "Password";

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String res = new String(digest, StandardCharsets.UTF_8);;
        System.out.println(res);
        return res;*/

        // Stringa cifrata di "Password". E' la password cifrata.
        return "��>��|9��O,oa.���[\u0010&��N\u00199�#�8�\"\u001A";
    }

    // Cifra la password ricevuta
    @NonNull
    private static String encryptPassword(@NonNull String password){

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(md).update(password.getBytes());
        byte[] digest = md.digest();
        return new String(digest, StandardCharsets.UTF_8);
    }

    // Verifica se la password inserita è corretta
    public static boolean checkPassword(@NonNull String password){
        if(getApplicationMode()!=ApplicationMode.UNDEFINED)
            throw new StorageException("You must be undefined to do this operation");

        // Confronto password reale cifrata con password inserita cifrata
        String realEncryptedPassword = getEncryptedPassword();

        String encryptedPassword = encryptPassword(password);

        return encryptedPassword.equals(realEncryptedPassword);
    }
}
