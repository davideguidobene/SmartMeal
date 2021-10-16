package it.unive.quadcore.smartmeal.communication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.CustomerHandler;

/**
 * Sottoclasse di CustomerHandler, serve alla classe ManagerCommunication per gestire i
 * RemoteCustomer, ovvero i Customer che si connettono da CustomerCommunication tramite Nearby.
 * Dal momento che RemoteCustomerHandler è l'unica classe a poter instanziare un RemoteCustomer,
 * garantisce un mantenimento dell'insieme dei RemoteCustomer senza rischio di duplicati.
 */
class RemoteCustomerHandler extends CustomerHandler<RemoteCustomerHandler.RemoteCustomer> {
    public static class RemoteCustomer extends Customer {
        private RemoteCustomer(String id, String name) {
            super(id, name);
        }
    }

    @Nullable
    private static RemoteCustomerHandler instance;

    synchronized static RemoteCustomerHandler getInstance() {
        if (instance == null) {
            instance = new RemoteCustomerHandler();
        }
        return instance;
    }

    private RemoteCustomerHandler() {}

    synchronized void addCustomer(@NonNull String customerId, @NonNull String customerName) {
        addCustomer(new RemoteCustomer(customerId, customerName));
    }

    @Override
    public synchronized boolean containsCustomer(@Nullable Customer customer) {
        return customer instanceof RemoteCustomer && super.containsCustomerHelper((RemoteCustomer) customer);
    }
}
