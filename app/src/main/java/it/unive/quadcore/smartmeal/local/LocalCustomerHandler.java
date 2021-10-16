package it.unive.quadcore.smartmeal.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.CustomerHandler;

/**
 * Sottoclasse di CustomerHandler, serve alla classe Local per gestire i
 * LocalCustomer, ovvero i Customer che vengono inseriti manualmente dal gestore.
 * Dal momento che LocalCustomerHandler è l'unica classe a poter instanziare un LocalCustomer,
 * garantisce un mantenimento dell'insieme dei LocalCustomer senza rischio di duplicati.
 */
class LocalCustomerHandler extends CustomerHandler<LocalCustomerHandler.LocalCustomer> {
    public static class LocalCustomer extends Customer {
        private LocalCustomer(@NonNull String name) {
            super(LocalCustomerHandler.getNewId(), name);
        }
    }

    @NonNull
    private static Integer i=0;

    @NonNull
    private synchronized static String getNewId() {
        String newId = i.toString();
        i++;
        return newId;
    }

    @Nullable
    private static LocalCustomerHandler instance;

    @NonNull
    synchronized static LocalCustomerHandler getInstance() {
        if (instance == null) {
            instance = new LocalCustomerHandler();
        }
        return instance;
    }

    private LocalCustomerHandler() {}

    @NonNull
    synchronized LocalCustomer addCustomer(@NonNull String customerName) {
        LocalCustomer localCustomer = new LocalCustomerHandler.LocalCustomer(customerName);
        addCustomer(localCustomer);
        return localCustomer;
    }

    @Override
    public synchronized boolean containsCustomer(@Nullable Customer customer) {
        return customer instanceof LocalCustomer && super.containsCustomerHelper((LocalCustomer) customer);
    }
}

