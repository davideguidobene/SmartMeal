package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Supplier;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.PayloadCallback;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.Objects;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.confirmation.ConfirmationDenied;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.util.BiFunction;

import static it.unive.quadcore.smartmeal.communication.RemoteCustomerHandler.RemoteCustomer;
import static it.unive.quadcore.smartmeal.communication.RequestType.CUSTOMER_NAME;
import static it.unive.quadcore.smartmeal.communication.RequestType.FREE_TABLE_LIST;
import static it.unive.quadcore.smartmeal.communication.RequestType.NOTIFY_WAITER;
import static it.unive.quadcore.smartmeal.communication.RequestType.SELECT_TABLE;

/**
 * ManagerCommunication ha lo scopo di fornire un'interfaccia user-friendly per gestire i clienti e le loro richieste
 */
public class ManagerCommunication extends Communication {
    @NonNull
    private static final String TAG = "ManagerCommunication";

    @NonNull
    private final RemoteCustomerHandler customerHandler;

    @Nullable
    private Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> onRequestFreeTableListCallback;
    @Nullable
    private Function<WaiterNotification, Confirmation<? extends WaiterNotificationException>> onNotifyWaiterCallback;
    @Nullable
    private BiFunction<Customer, Table, Confirmation<? extends TableException>> onSelectTableCallback;
    @Nullable
    private Consumer<Customer> onCustomerLeftRoomCallback;

    @Nullable
    private static ManagerCommunication instance;

    public synchronized static ManagerCommunication getInstance() {
        if (instance == null) {
            instance = new ManagerCommunication();
        }

        return instance;
    }

    private ManagerCommunication() {
        customerHandler = RemoteCustomerHandler.getInstance();
    }


    private ConnectionLifecycleCallback connectionLifecycleCallback() {

        final PayloadCallback payloadCallback = new MessageListener() {

            @Override
            protected void onMessageReceived(@NonNull String endpointId, @NonNull Message message) {
                Objects.requireNonNull(endpointId);
                Objects.requireNonNull(message);

                RequestType requestType = message.getRequestType();

                if (requestType == RequestType.CUSTOMER_NAME) {
                    assert message.getContent() != null;
                    handleCustomerNameMessage(endpointId, message.getContent());
                    return;
                }

                else if (!customerHandler.containsCustomer(endpointId)) {
                    handleCustomerNotRecognized(endpointId);
                    return;
                }

                switch (message.getRequestType()) {
                    case FREE_TABLE_LIST:
                        handleFreeTableListRequest(endpointId);
                        break;
                    case SELECT_TABLE:
                        assert message.getContent() != null;
                        handleSelectTableRequest(endpointId, message.getContent());
                        break;
                    case NOTIFY_WAITER:
                        handleNotifyWaiterRequest(endpointId);
                        break;
                    default:
                        throw new UnsupportedOperationException("Not implemented yet");
                }


            }
        };


        return new ConnectionListener(Objects.requireNonNull(activity), payloadCallback) {

            @Override
            protected void onConnectionSuccess(@NonNull String endpointId) {
                Log.i(TAG, "Connection success");
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                super.onDisconnected(endpointId);
                Objects.requireNonNull(onCustomerLeftRoomCallback);

                if (customerHandler.containsCustomer(endpointId)) {
                    RemoteCustomer customer = customerHandler.getCustomer(endpointId);
                    Log.i(TAG, "Customer disconnected: " + customer);
                    onCustomerLeftRoomCallback.accept(customer);
                    customerHandler.removeCustomer(endpointId);
                } else {
                    Log.i(TAG, "Unrecognized customer tried to disconnect");
                }
            }
        };
    }

    private void handleNotifyWaiterRequest(@NonNull String endpointId) {
        Objects.requireNonNull(onNotifyWaiterCallback);
        WaiterNotification waiterNotification = new WaiterNotification(customerHandler.getCustomer(endpointId));
        Confirmation<? extends WaiterNotificationException> confirmation = onNotifyWaiterCallback.apply(waiterNotification);
        sendMessage(endpointId, new Message(NOTIFY_WAITER, confirmation));
    }

    private void handleSelectTableRequest(@NonNull String endpointId, @NonNull Serializable content) {
        Objects.requireNonNull(onSelectTableCallback);
        Table selectedTable = (Table) content;
        Confirmation<? extends TableException> confirmation = onSelectTableCallback.apply(customerHandler.getCustomer(endpointId), selectedTable);
        sendMessage(endpointId, new Message(SELECT_TABLE, confirmation));
    }

    private void handleCustomerNotRecognized(@NonNull String endpointId) {
        Message confirmationMessage = new Message(CUSTOMER_NAME, new ConfirmationDenied<>(new CustomerNotRecognizedException()));
        sendMessage(endpointId, confirmationMessage);
    }

    private void handleCustomerNameMessage(@NonNull String endpointId, @NonNull Serializable content) {
        Objects.requireNonNull(content);
        String name = (String) content;
        if(customerHandler.containsCustomer(endpointId)){
            Log.i(TAG, "Customer " + endpointId + " was already recognized in Local");
        }
        else {
            customerHandler.addCustomer(endpointId, name);
            Message confirmationMessage = new Message(CUSTOMER_NAME, new Confirmation<CustomerNotRecognizedException>());
            sendMessage(endpointId, confirmationMessage);
        }
    }

    private void handleFreeTableListRequest(@NonNull String toEndpointId) {
        Objects.requireNonNull(onRequestFreeTableListCallback);
        Message response = new Message(FREE_TABLE_LIST, onRequestFreeTableListCallback.get());
        sendMessage(toEndpointId, response);
    }


    /**
     * Avvia la virtual room.
     * Il dispositivo del gestore diventa rilevabile dagli altri dispositivi vicini.
     *
     * @param activity l'activity corrente
     */
    public synchronized void startRoom(@NonNull Activity activity) {
        if(isRoomStarted()) {
            throw new IllegalStateException("Room has been already started");
        }


        Objects.requireNonNull(onCustomerLeftRoomCallback);

        Objects.requireNonNull(onSelectTableCallback);
        Objects.requireNonNull(onNotifyWaiterCallback);
        Objects.requireNonNull(onRequestFreeTableListCallback);

        this.activity = activity;

        final ConnectionLifecycleCallback connectionLifecycleCallback = connectionLifecycleCallback();

        // avvia l'adverising
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(activity)
                .startAdvertising(
                        ManagerStorage.getName(),
                        SERVICE_ID,
                        connectionLifecycleCallback,
                        advertisingOptions
                )
                .addOnSuccessListener((Void unused) -> Log.i(TAG, "Successfully started advertising"))
                .addOnFailureListener((Exception e) -> Log.e(TAG, "Advertising failed"));
    }


    /**
     * La callback `onNotifyWaiterCallback` verrà chiamata nel caso un cliente
     * invii una notifica di richiesta cameriere, con parametro l'oggetto della
     * classe WaiterNotification che la rappresenta. Essa dovrà restitire una Confirmation
     * in caso di successo, una ConfirmationDenied altrimenti.
     *
     * @param onNotifyWaiterCallback callback che implementa la logica da attuare quando un
     *                               cliente invia una notifica di richiesta cameriere
     */
    public void onNotifyWaiter(
            @NonNull Function<
                    WaiterNotification,
                    Confirmation<? extends WaiterNotificationException>>
            onNotifyWaiterCallback) {

        Objects.requireNonNull(onNotifyWaiterCallback);

        this.onNotifyWaiterCallback = onNotifyWaiterCallback;
    }


    /**
     * La callback `onSelectTableCallback` verrà chiamata nel caso un cliente
     * invii un messaggio di selezione tavolo, con parametri:
     * - l'oggetto della classe Customer identifica il cliente;
     * - l'oggetto della classe Table che rappresenta il tavolo selezionato.
     * Essa dovrà restitire una Confirmation in caso di successo, una ConfirmationDenied altrimenti.
     *
     * @param onSelectTableCallback callback che implementa la logica da attuare quando un
     *                              cliente invia un messaggio di selezione tavolo
     */
    public void onSelectTable(
            @NonNull BiFunction<
                    Customer,
                    Table,
                    Confirmation<? extends TableException>>
            onSelectTableCallback) {

        Objects.requireNonNull(onSelectTableCallback);

        this.onSelectTableCallback = onSelectTableCallback;
    }


    /**
     * La callback `onRequestFreeTableListCallback` verrà chiamata nel caso un cliente
     * invii un messaggio di richiesta dei tavoli liberi.
     * Essa dovrà restitire una SuccessResponse con la lista dei tavoli libri in caso di successo,
     * una ErrorResponse con la relativa TableException altrimenti.
     *
     * @param onRequestFreeTableListCallback callback che implementa la logica da attuare
     *                                       per recuperare la lista di tavoli liberi
     */
    public void onRequestFreeTableList(@NonNull Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> onRequestFreeTableListCallback) {
        Objects.requireNonNull(onRequestFreeTableListCallback);

        this.onRequestFreeTableListCallback = onRequestFreeTableListCallback;
    }


    /**
     * La callback `onCustomerLeftRoomCallback` verrà chiamata nel caso un cliente
     * si disconnetta dalla stanza, con parametro l'oggetto della classe Customer
     * che lo rappresenta.
     *
     * @param onCustomerLeftRoomCallback callback che implementa la logica da attuare quando un
     *                                   cliente si disconnette dalla stanza
     */
    public void onCustomerLeftRoom(@NonNull Consumer<Customer> onCustomerLeftRoomCallback) {
        Objects.requireNonNull(onCustomerLeftRoomCallback);

        this.onCustomerLeftRoomCallback = onCustomerLeftRoomCallback;
    }


    /**
     * Invia un messaggio al cliente avvisandolo del cambiamento del tavolo assegnato.
     *
     * @param customer il cliente da notificare
     * @param newTable il nuovo tavolo
     */
    public void notifyTableChanged(@NonNull Customer customer, @NonNull Table newTable) {
        Objects.requireNonNull(customer);
        Objects.requireNonNull(newTable);

        if (!isRoomStarted()) {
            Log.w(TAG, "trying to notify a customer without starting the room");
            return;
        }

        if(!customerHandler.containsCustomer(customer)){
            Log.w(TAG, "trying to notify a not connected customer");
            return;
        }

        sendMessage(customer.getId(), new Message(RequestType.TABLE_CHANGED, newTable));
    }


    /**
     * Invia un messaggio al cliente avvisandolo della rimozione dell'assegnamento del tavolo.
     *
     * @param customer il cliente da notificare
     */
    public void notifyTableRemoved(@NonNull Customer customer) {
        Objects.requireNonNull(customer);

        if (!isRoomStarted()) {
            Log.w(TAG, "trying to notify a customer without starting the room");
            return;
        }

        if(!customerHandler.containsCustomer(customer)){
            Log.w(TAG, "trying to notify a not connected customer");
            return;
        }

        sendMessage(customer.getId(), new Message(RequestType.TABLE_REMOVED, null));
    }


    /**
     * Chiude la stanza virtuale e disconnette tutti i clienti ad essa collegati.
     * Il dispositivo del gestore non sarà più rilevabile da altri dispositivi
     * nelle vicinanze.
     */
    public synchronized void closeRoom() {
        if (!isRoomStarted()) {
            throw new IllegalStateException("The room has not been started");
        }

        assert activity != null;
        Nearby.getConnectionsClient(activity).stopAdvertising();
        Nearby.getConnectionsClient(activity).stopAllEndpoints();

        activity = null;
    }


    /**
     * initialized: false
     * becomes true after: startRoom()
     * becomes false after: closeRoom()
     * @return true: quando è stato chiamato il metodo startRoom() ma non closeRoom(),
     *         false: altrimenti
     */
    public boolean isRoomStarted() {
        return activity != null;
    }
}
