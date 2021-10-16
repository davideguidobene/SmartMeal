package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.PayloadCallback;

import java.io.Serializable;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

/**
 * CustomerCommunication ha lo scopo di fornire un'interfaccia user-friendly per interagire con il locale lato Manager
 */
public class CustomerCommunication extends Communication {

    /**
     * Enum che rappresenta i possibili stati della connessione
     */
    private enum ConnectionState {
        /**
         * Il cliente è disconnesso (dal gestore).
         * Impostato su `disconnect`.
         */
        DISCONNECTED,   // <==> managerEndPointID == null

        /**
         * Il cliente si sta connettendo al gestore.
         * Impostato su `onConnectionInitiated`.
         */
        CONNECTING,

        /**
         * Il cliente è connesso al gestore (ha ricevuto conferma dal gestore
         * dopo avergli inviato il nome).
         * Impostato su `handleCustomerNameConfirmation.
         */
        CONNECTED
    }

    @NonNull
    private static final String TAG = "CustomerCommunication";

    @Nullable
    private String managerEndpointId;

    @Nullable
    private Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback;

    /**
     * initialized: DISCONNECTED
     * set to CONNECTED: handleCustomerNameConfirmation()
     * set to DISCONNECTED: disconnect()
     * set to CONNECTING: onConnectionInitiated() in ConnectionListener settato connectionLifecycleCallback()
     * method wrapper: connectionState()
     */
    @NonNull
    private ConnectionState connectionState;

    /**
     * initialized: false
     * set to true: onSuccessListener di Nearby settato in joinRoom()
     * set to false: stopDiscovery()
     */
    private boolean isDiscovering;

    @Nullable
    private Runnable onConnectionSuccessCallback;

    @Nullable
    private Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback;

    @Nullable
    private Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback;

    @Nullable
    private Consumer<Table> onTableChangedCallback;

    @Nullable
    private Runnable onTableRemovedCallback;

    /**
     * Se isInsideTheRoom(), chiama sempre il metodo leaveRoom()
     */
    @Nullable
    private Runnable onCloseRoomCallback;


    @Nullable
    private static CustomerCommunication instance;

    public synchronized static CustomerCommunication getInstance() {
        if (instance == null) {
            instance = new CustomerCommunication();
        }

        return instance;
    }

    private CustomerCommunication() {
        this.connectionState = ConnectionState.DISCONNECTED;
        this.isDiscovering = false;
    }


    /**
     * Prova a connettersi alla stanza virtuale del gestore.
     *
     * @param activity contesto
     * @param onConnectionSuccessCallback callback che implementa la logica da attuare
     *                                    quando la connessione con il getsore ha successo
     * @param onConnectionFailureCallback callback che implementa la logica da attuare
     *                                    quando la connessione con il getsore fallisce
     */
    public synchronized void joinRoom(@NonNull Activity activity, @NonNull Runnable onConnectionSuccessCallback,
                                      @NonNull Runnable onConnectionFailureCallback) {

        // verifica di non essere già all'interno della stanza
        if (isInsideTheRoom()) {
            Log.w(TAG, "joinRoom called, but already inside the room");
            return;
        }
        // validazione parametri
        Objects.requireNonNull(onConnectionSuccessCallback);
        Objects.requireNonNull(onConnectionFailureCallback);
        Objects.requireNonNull(onCloseRoomCallback);

        this.activity = activity;
        this.onConnectionSuccessCallback = onConnectionSuccessCallback;

        final EndpointDiscoveryCallback endpointDiscoveryCallback = endpointDiscoveryCallback();

        // timer per fermare la ricerca del gestore
        nearbyTimer(() -> {
            synchronized (CustomerCommunication.this) {
                // se scade lascia la stanza e esegui segnala il fallimento con la callback
                if (isNotConnected() && isInsideTheRoom()) {
                    leaveRoom();
                    onConnectionFailureCallback.run();
                    Log.i(TAG, "joinRoom failed for timeout");
                }
            }
        });

        // avvia la ricerca del gestore
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(activity)
                .startDiscovery(
                        SERVICE_ID,
                        endpointDiscoveryCallback,
                        discoveryOptions
                )
                .addOnSuccessListener((Void unused) -> {
                    Log.i(TAG, "Successfully started discovery");
                    synchronized (CustomerCommunication.this) {
                        isDiscovering = true;
                    }
                })
                .addOnFailureListener((Exception e) -> Log.e(TAG, "Discovery failed"));
    }

    private EndpointDiscoveryCallback endpointDiscoveryCallback() {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
                // An endpoint was found. We request a connection to it.
                synchronized (CustomerCommunication.this) {
                    if (isInsideTheRoom()) {
                        assert activity != null; // già controllato dall'if
                        Nearby.getConnectionsClient(activity)
                                .requestConnection(CustomerStorage.getName(), endpointId, connectionLifecycleCallback())
                                .addOnSuccessListener(
                                        (Void unused) -> {
                                            // We successfully requested a connection. Now both sides
                                            // must accept before the connection is established.
                                        })
                                .addOnFailureListener(
                                        (Exception e) -> {
                                            // Nearby Connections failed to request the connection.
                                        });
                    }
                }
            }

            @Override
            public void onEndpointLost(@NonNull String endpointId) {
                // A previously discovered endpoint has gone away.
                synchronized (CustomerCommunication.this) {
                    if(isInsideTheRoom()) {
                        Objects.requireNonNull(onCloseRoomCallback);
                        onCloseRoomCallback.run();
                    }
                }
            }
        };
    }

    private synchronized ConnectionLifecycleCallback connectionLifecycleCallback() {
        if(!isInsideTheRoom()) {
            throw new IllegalStateException("connectionLifeCycleCallback should be called only when inside the room");
        }

        final PayloadCallback payloadCallback = new MessageListener() {
            @Override
            protected void onMessageReceived(@NonNull String endpointId, @NonNull Message message) {
                synchronized (CustomerCommunication.this) {

                    if (isInsideTheRoom()) {
                        Objects.requireNonNull(endpointId);
                        Objects.requireNonNull(message);

                        switch (message.getRequestType()) {
                            case CUSTOMER_NAME:
                                assert message.getContent() != null;
                                handleCustomerNameConfirmation(message.getContent());
                                break;
                            case FREE_TABLE_LIST:
                                assert message.getContent() != null;
                                handleFreeTableListResponse(message.getContent());
                                break;
                            case SELECT_TABLE:
                                assert message.getContent() != null;
                                handleSelectTableResponse(message.getContent());
                                break;
                            case NOTIFY_WAITER:
                                assert message.getContent() != null;
                                handleNotifyWaiterResponse(message.getContent());
                                break;
                            case TABLE_CHANGED:
                                assert message.getContent() != null;
                                handleChangedTableMessage(message.getContent());
                                break;
                            case TABLE_REMOVED:
                                handleRemovedTableMessage();
                                break;
                            default:
                                throw new UnsupportedOperationException("Not implemented yet " + message.getRequestType());
                        }
                    }
                }
            }
        };

        return new ConnectionListener(activity, payloadCallback) {

            @Override
            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                synchronized (CustomerCommunication.this) {
                    if (!isInsideTheRoom()) {
                        Log.w(TAG, "Initiating the connection while not in the room");
                        return;
                    }
                    if(connectionState() != ConnectionState.DISCONNECTED) {
                        Log.wtf(TAG, "Initiating the connection while not disconnected");
                        return;
                    }
                    super.onConnectionInitiated(endpointId, connectionInfo);
                    managerEndpointId = endpointId;
                    connectionState = ConnectionState.CONNECTING;
                }
            }

            @Override
            protected void onConnectionSuccess(@NonNull String endpointId) {
                synchronized (CustomerCommunication.this) {
                    if (connectionState() != ConnectionState.CONNECTING) {
                        Log.i(TAG, "Connection success but not connecting");
                        return;
                    }
                    stopDiscovery();
                    sendName();
                }
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                synchronized (CustomerCommunication.this) {
                    super.onDisconnected(endpointId);
                    Objects.requireNonNull(onCloseRoomCallback);
                    onCloseRoomCallback.run();
                }
            }
        };
    }


    /* Sezione di codice in cui vengono gestiti i messaggi in ingresso */


    private void handleRemovedTableMessage() {
        assert onTableRemovedCallback != null;
        onTableRemovedCallback.run();
    }

    private synchronized void handleChangedTableMessage(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onTableChangedCallback != null;

        Table table = (Table) content;
        onTableChangedCallback.accept(table);
    }

    private synchronized void handleNotifyWaiterResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onNotifyWaiterConfirmationCallback != null;

        @SuppressWarnings("unchecked")
        Confirmation<WaiterNotificationException> confirmation = (Confirmation<WaiterNotificationException>) content;
        onNotifyWaiterConfirmationCallback.accept(confirmation);
    }

    private synchronized void handleSelectTableResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onSelectTableConfirmationCallback != null;

        @SuppressWarnings("unchecked")
        Confirmation<? extends TableException> confirmation = (Confirmation<? extends TableException>) content;
        onSelectTableConfirmationCallback.accept(confirmation);
    }

    private synchronized void handleCustomerNameConfirmation(@NonNull Serializable content) {
        Objects.requireNonNull(content);

        if(isInsideTheRoom()) {
            assert onConnectionSuccessCallback != null;

            if(connectionState() != ConnectionState.CONNECTING) {
                Log.wtf(TAG, "connection confirmation arrived while not connecting");
                return;
            }

            @SuppressWarnings("unchecked")
            Confirmation<CustomerNotRecognizedException> confirmation = (Confirmation<CustomerNotRecognizedException>) content;
            try {
                confirmation.obtain();
                connectionState = ConnectionState.CONNECTED;
                onConnectionSuccessCallback.run();

                Log.i(TAG, "Connection confirmed");
            } catch (CustomerNotRecognizedException e) {
                Log.e(TAG, "Connection not confirmed");
                sendName();
            }
        }
    }

    private synchronized void handleFreeTableListResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert freeTableListCallback != null;

        @SuppressWarnings("unchecked")
        Response<TreeSet<Table>, TableException> response = (Response<TreeSet<Table>, TableException>) content;
        freeTableListCallback.accept(response);
    }


    /* Fine sezione di codice in cui vengono gestiti i messaggi in ingresso */


    /**
     * Invia un messaggio al dispositivo identificato da `toEndpointId`
     *
     * @param toEndpointId id che identifica il destinatario
     * @param message messaggio da inviare
     */
    @Override
    protected synchronized void sendMessage(@NonNull String toEndpointId, @NonNull Message message) {
        if (!isInsideTheRoom()) {
            Log.w(TAG, "trying to send a message while not in the room");
            return;
        }
        super.sendMessage(toEndpointId, message);
    }

    private synchronized void sendName() {
        if(connectionState() == ConnectionState.DISCONNECTED){
            Log.i(TAG, "trying to send name while disconnected");
            return;
        }
        assert managerEndpointId != null;
        sendMessage(managerEndpointId, new Message(RequestType.CUSTOMER_NAME, CustomerStorage.getName()));
    }


    /**
     * Invia una notifica al gestore, richiedendo un cameriere al tavolo.
     * Richiede due callback come parametri per gestire la conferma di ricezione
     * da parte del gestore e l'eventuale errore di timeout causato dalla connessione
     * Nearby.
     *
     * @param onNotifyWaiterConfirmationCallback callback che implementa la logica da attuare
     *                                           quando il gestore conferma la ricezione
     *                                           della notifica al cameriere
     * @param onTimeoutCallback callback che implementa la logica da attuare quando la conferma
     *                          della notifica cameriere dal gestore non arriva entro un tempo
     *                          limite
     */
    public synchronized void notifyWaiter(@NonNull Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback, @NonNull Runnable onTimeoutCallback) {
        if (connectionState() == ConnectionState.DISCONNECTED) {
            Log.w(TAG, "trying to notify the waiter while disconnected");
            return;
        }

        Objects.requireNonNull(onNotifyWaiterConfirmationCallback);
        Objects.requireNonNull(onTimeoutCallback);
        ensureConnection();

        Timer timer = nearbyTimer(onTimeoutCallback);
        this.onNotifyWaiterConfirmationCallback = response -> {
            timer.cancel();
            onNotifyWaiterConfirmationCallback.accept(response);
        };

        assert managerEndpointId != null;
        sendMessage(managerEndpointId, new Message(RequestType.NOTIFY_WAITER, null));
    }


    /**
     * Invia il tavolo scelto dal cliente al gestore.
     * Richiede due callback come parametri per gestire la conferma di ricezione
     * da parte del gestore e l'eventuale errore di timeout causato dalla connessione
     * Nearby.
     *
     * @param table il tavolo scelto
     * @param onSelectTableConfirmationCallback callback che implementa la logica da attuare
     *                                          quando il gestore conferma la ricezione
     *                                          del messaggio di selezione tavolo
     * @param onTimeoutCallback callback che implementa la logica da attuare quando la conferma
     *                          della ricezione del messaggio di selezione tavolo dal gestore
     *                          non arriva entro un tempo limite
     */
    public synchronized void selectTable(@NonNull Table table, @NonNull Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback, @NonNull Runnable onTimeoutCallback) {
        if (connectionState() == ConnectionState.DISCONNECTED) {
            Log.w(TAG, "trying to select table while disconnected");
            return;
        }

        Objects.requireNonNull(table);
        Objects.requireNonNull(onSelectTableConfirmationCallback);
        Objects.requireNonNull(onTimeoutCallback);
        Objects.requireNonNull(onTableChangedCallback);
        Objects.requireNonNull(onTableRemovedCallback);

        ensureConnection();

        Timer timer = nearbyTimer(onTimeoutCallback);
        this.onSelectTableConfirmationCallback = response -> {
            timer.cancel();
            onSelectTableConfirmationCallback.accept(response);
        };

        assert managerEndpointId != null;
        sendMessage(managerEndpointId, new Message(RequestType.SELECT_TABLE, table));
    }


    /**
     * Invia un messaggio al gestore, richiedendo la lista dei tavoli liberi.
     * Richiede due callback come parametri per gestire la risposta da parte del gestore
     * e l'eventuale errore di timeout causato dalla connessione Nearby.
     *
     * @param freeTableListCallback callback che implementa la logica da attuare quando il gestore
     *                              risponde con la lista dei tavoli liberi oppure con un'eventuale
     *                              eccezione
     * @param onTimeoutCallback callback che implementa la logica da attuare quando la risposta
     *                          del gestore non arriva entro un tempo limite
     */
    public synchronized void requestFreeTableList(
            @NonNull Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback,
            @NonNull Runnable onTimeoutCallback) {

        if(connectionState() == ConnectionState.DISCONNECTED){
            Log.i(TAG, "trying to request free table list while disconnected");
            return;
        }

        Objects.requireNonNull(freeTableListCallback);
        Objects.requireNonNull(onTimeoutCallback);
        ensureConnection();

        Timer timer = nearbyTimer(onTimeoutCallback);
        this.freeTableListCallback = response -> {
            timer.cancel();
            freeTableListCallback.accept(response);
        };

        assert managerEndpointId != null;
        sendMessage(managerEndpointId, new Message(RequestType.FREE_TABLE_LIST, null));
    }


    /**
     * La callback `onTableChangedCallback` verrà chiamata nel caso il gestore modifichi
     * il tavolo asseganto al cliente.
     *
     * @param onTableChangedCallback callback che implementa la logica da attuare quando il
     *                               gestore modifica il tavolo assegnato al cliente
     */
    public synchronized void onTableChanged(@NonNull Consumer<Table> onTableChangedCallback) {
        Objects.requireNonNull(onTableChangedCallback);
        this.onTableChangedCallback = onTableChangedCallback;
    }

    /**
     * La callback `onTableRemovedCallback` verrà chiamata nel caso il gestore rimuova
     * il tavolo asseganto al cliente.
     *
     * @param onTableRemovedCallback callback che implementa la logica da attuare quando il
     *                               gestore rimuove il tavolo assegnato al cliente
     */
    public synchronized void onTableRemoved(@NonNull Runnable onTableRemovedCallback) {
        Objects.requireNonNull(onTableRemovedCallback);
        this.onTableRemovedCallback = onTableRemovedCallback;
    }

    /**
     * Esegue `onTimeoutCallback` se scade il timer.
     * Il timer è pari a 60 secondi.
     *
     * @param onTimeoutCallback callback che implementa la logica da attuare se il timer scade
     *
     * @return restituisce un nuovo {@link Timer}
     */
    private Timer nearbyTimer(@NonNull Runnable onTimeoutCallback) {
        final long NEARBY_TIMEOUT = 60 * 1000;      // 60 secondi

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimeoutCallback.run();
            }
        }, NEARBY_TIMEOUT);

        return timer;
    }


    /**
     * La callback `onCloseRoomCallback` verrà chiamata nel caso il gestore chiuda la stanza
     * e il cliente sia ancora connesso ad essa.
     *
     * @param onCloseRoomCallback callback che implementa la logica da attuare quando il
     *                            gestore chiude la stanza
     */
    public synchronized void onCloseRoom(@NonNull Runnable onCloseRoomCallback) {
        Objects.requireNonNull(onCloseRoomCallback);

        this.onCloseRoomCallback = () -> {
            synchronized (this) {
                if (isInsideTheRoom()) {
                    leaveRoom();
                }
                onCloseRoomCallback.run();
            }
        };
    }


    /**
     * Disconnette il cliente dalla stanza del gestore.
     */
    public synchronized void leaveRoom() {
        if(!isInsideTheRoom()) {
            Log.w(TAG, "trying to leave the Room while not in the Room");
        }
        stopDiscovery();
        disconnect();
        activity = null;
    }


    private synchronized void disconnect() {
        if(connectionState() != ConnectionState.DISCONNECTED) {
            assert managerEndpointId != null;
            assert activity != null;
            Nearby.getConnectionsClient(activity).disconnectFromEndpoint(managerEndpointId);
            connectionState = ConnectionState.DISCONNECTED;
            managerEndpointId = null;
        }
    }

    private synchronized void stopDiscovery() {
        if(isDiscovering) {
            assert activity != null;
            Nearby.getConnectionsClient(activity).stopDiscovery();
            isDiscovering = false;
        }
    }

    /**
     * initialized: false
     * becomes true after: joinRoom()
     * becomes false after: leaveRoom()
     * dipendenze: insideTheRoom == false   ==>   isDiscovering == false && connectionState == DISCONNECTED
     * @return true: quando è stato chiamato il metodo joinRoom() ma non leaveRoom(),
     *         false: altrimenti
     */
    private synchronized boolean isInsideTheRoom() {
        return activity != null;
    }

    private synchronized ConnectionState connectionState() {
        return connectionState;
    }

    /**
     * Verifica se il cliente è connesso alla stanza del gestore.
     *
     * @return true se il cliente è connesso alla stanza del gestore, false altrimenti
     */
    public synchronized boolean isNotConnected() {
        return connectionState() != ConnectionState.CONNECTED;
    }


    /**
     * Metodo che permette di assicuarsi che la connessione tra cliente e gestore sia stabilita.
     *
     * @throws RuntimeException se la connessione non è stabilita
     */
    private synchronized void ensureConnection() {
        if (isNotConnected()) {
            throw new IllegalStateException("Not connected with local");
        }
    }
}
