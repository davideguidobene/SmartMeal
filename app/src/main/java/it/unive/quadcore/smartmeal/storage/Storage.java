package it.unive.quadcore.smartmeal.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.location.Location;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.FoodCategory;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.model.Menu;
import it.unive.quadcore.smartmeal.model.Money;
import it.unive.quadcore.smartmeal.model.Product;

/**
 * Classe che raggruppa metodi statici per l'interazione con la memoria secondaria
 */
public class Storage {
    //private static final String TAG = "Storage";

    protected static boolean initialized = false;

    // Shared Preferences. La prima di deafult, ovvero si mettono i settings dell'applicazione. La seconda di uso generico.
    @Nullable
    protected static SharedPreferences defaultSharedPreferences;
    @Nullable
    protected static SharedPreferences sharedPreferences;

    @Nullable
    private static LocalDescription localDescription = null;

    @NonNull
    private static final String SHARED_PREFERENCES_NAME = "SharedPreferences";

    @NonNull
    private static final String APPLICATION_MODE_SHARED_PREFERENCE_KEY = "ApplicationMode";
    @NonNull
    protected static final String NAME_SHARED_PREFERENCE_KEY = "Name";

    @NonNull
    private static final String LOCAL_NAME = "The boat restourant";
    @NonNull
    private static final String LOCAL_PRESENTATION = "A delicious and friendly pub in a boat-like location.";
    private static final double LOCAL_LATITUDE = 45.49140;
    private static final double LOCAL_LONGITUDE = 11.75941;

    /**
     * Rende non instanziabile questa classe.
     */
    Storage() {}

    // Metodo da applicare all'inizio dell'esecuzione dell'applicazione, dalla MainActivity. Dopo avere invocato tale metodo si possono
    // chiamare tutti gli altri.
    // Si passa l'Activity per le shared preferences. Un'alternativa a ciò è passare l'Activity ad ogni metodo come input.
    // Si usa l'activity per creare le shared preferences.
    public static void initializeStorage(@NonNull Activity activity){ // Alter ego di getInstance
        if(initialized)
            throw new StorageException("The storage has alredy been initialized");

        // Shared Preference di deafult. Usata per i settings dell'applicazione.
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        // Shared Preference di uso generico.
        sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES_NAME , Context.MODE_PRIVATE);

        initialized=true;
    }

    @NonNull
    public static ApplicationMode getApplicationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        Objects.requireNonNull(defaultSharedPreferences);

        // Preference non esistente (primo uso della preference). Metto valore di default
        if(!defaultSharedPreferences.contains(APPLICATION_MODE_SHARED_PREFERENCE_KEY)) {
            /*SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putString(APPLICATION_MODE_SHARED_PREFERENCE_KEY,ApplicationMode.UNDEFINED.name());
            editor.apply();*/
            setApplicationMode(ApplicationMode.UNDEFINED);
        }
        // Prende l'ApplicationMode dallo storage : è codificato come stringa
        String applicationModeString = defaultSharedPreferences.getString(APPLICATION_MODE_SHARED_PREFERENCE_KEY,ApplicationMode.UNDEFINED.name());

        ApplicationMode applicationMode ;
        try{
            applicationMode = ApplicationMode.valueOf(applicationModeString); // Da stringa a Enum
            return applicationMode;
        }catch(IllegalArgumentException e) { // La stringa non codifica un ApplicationMode
            throw new StorageException("The storage contains an invalid application mode");
        }
    }

    public static void setApplicationMode(@NonNull ApplicationMode applicationMode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        Objects.requireNonNull(defaultSharedPreferences);

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Scrivo l'ApplicationMode nello storage. Se non esiste tale preference viene creata.
        String applicationModeString = applicationMode.name(); // toString in alternativa
        editor.putString(APPLICATION_MODE_SHARED_PREFERENCE_KEY,applicationModeString);
        editor.apply();
    }


    @NonNull
    public static String getName() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        ApplicationMode applicationMode = getApplicationMode();

        if(applicationMode==ApplicationMode.UNDEFINED)
            throw new StorageException("In the application mode UNDEFINED does not exist a name");

        Objects.requireNonNull(sharedPreferences);

        // Preference non esistente
        if(!sharedPreferences.contains(NAME_SHARED_PREFERENCE_KEY)) {

            if(applicationMode==ApplicationMode.MANAGER){ // Se è gestore setto il nome al nome del locale
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(NAME_SHARED_PREFERENCE_KEY, LOCAL_NAME);
                editor.apply();
            }
            else {   // Lancio eccezione
                throw new StorageException("The name of the client was not found in storage"); // Eccezione o valore di default?
            }
        }

        // Prendo il nome dallo storage
        return sharedPreferences.getString(NAME_SHARED_PREFERENCE_KEY,"Username");
    }


    // La descrizione del locale non è memorizzata in modo permanente, ma è semplicemente generata in memoria primaria.
    @NonNull
    public static LocalDescription getLocalDescription() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        // Istanza già creata
        if(localDescription!=null)
            return localDescription;


        // Creiamo i prodotti
        Set<Product> products = generateProducts();

        // Creiamo il menu
        Menu menu = new Menu(products);

        //Creiamo Local Description
        Location location = new Location("");
        location.setLatitude(LOCAL_LATITUDE);
        location.setLongitude(LOCAL_LONGITUDE);

        localDescription = new LocalDescription(LOCAL_NAME,LOCAL_PRESENTATION, location ,menu);

        return localDescription;
    }


    public static void clear(){
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        Objects.requireNonNull(defaultSharedPreferences);
        Objects.requireNonNull(sharedPreferences);

        SharedPreferences.Editor editor_def = defaultSharedPreferences.edit();
        editor_def.clear();
        editor_def.apply();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    /**
     * Permette di verificare se Storage è gia stata inizializzata
     * (tramite il metodo `initializeStorage(Activity)`)
     * e di conseguenza se è pronta all'uso.
     *
     * @return true se è già stata inizializzata, false altrimenti
     */
    public static boolean isInitialized() {
        return initialized;
    }


    // Generazione dei prodotti
    @NonNull
    private static Set<Product> generateProducts(){
        Set<Product> products = new TreeSet<>();

        Product product = new Product("Tomato soup",new Money(200), FoodCategory.STARTERS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("French onion soup",new Money(250), FoodCategory.STARTERS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Tomato salad",new Money(290), FoodCategory.SALADS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Chicken salad",new Money(330), FoodCategory.SALADS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("German sausage and chips",new Money(650), FoodCategory.MAIN_COURSES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Grilled fish and potatoes",new Money(625), FoodCategory.MAIN_COURSES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Italian cheese and tomato pizza",new Money(480), FoodCategory.MAIN_COURSES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Thai chicken and rice",new Money(590), FoodCategory.MAIN_COURSES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Vegetable pasta",new Money(480), FoodCategory.MAIN_COURSES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Roast chicken and potatoes",new Money(590), FoodCategory.MAIN_COURSES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Cheeseburger",new Money(320), FoodCategory.SIDE_DISHES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Vegetable omelette",new Money(325), FoodCategory.SIDE_DISHES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Cheese and tomato sandwich",new Money(320), FoodCategory.SIDE_DISHES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Burger",new Money(290), FoodCategory.SIDE_DISHES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Chicken sandwich",new Money(350), FoodCategory.SIDE_DISHES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Cheese omelette",new Money(350), FoodCategory.SIDE_DISHES,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Mineral water",new Money(100), FoodCategory.DRINKS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Fresh orange juice",new Money(120), FoodCategory.DRINKS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Soft drinks",new Money(130), FoodCategory.DRINKS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("English tea",new Money(90), FoodCategory.DRINKS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Irish cream coffee",new Money(90), FoodCategory.DRINKS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Fruit salad and cream",new Money(220), FoodCategory.DESSERTS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Ice cream",new Money(200), FoodCategory.DESSERTS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Lemon cake",new Money(220), FoodCategory.DESSERTS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Cheese and biscuit",new Money(220), FoodCategory.DESSERTS,"Tonno, Asparagi, Cipolle");
        products.add(product);
        product = new Product("Chocolate chake",new Money(250), FoodCategory.DESSERTS,"Tonno, Asparagi, Cipolle");
        products.add(product);

        return products;
    }
}
