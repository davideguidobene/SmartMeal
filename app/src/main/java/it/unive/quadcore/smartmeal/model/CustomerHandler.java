package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * La classe CustomerHandler ha lo scopo di mantenere il codice usato sia nella sottoclasse
 * LocalCustomerHandler che nella sottoclasse RemoteCustomer handler in modo da ottimizzare il
 * riutilizzo di codice.
 * @param <C> sottoclasse di Customer che dovrà essere implementata da ogni sottoclasse di
 *           CustomerHandler avendo cura di tenerne il costruttore privato in modo da poter gestire
 *           in modo esclusivo il proprio insieme di Customer C
 */
public abstract class CustomerHandler<C extends Customer> {

    @NonNull
    private final Map<String, C> customerMap;

    protected CustomerHandler() {
        this.customerMap = new HashMap<>();
    }

    protected synchronized void addCustomer(@NonNull C customer) {
        if (containsCustomer(customer.getId())) {
            throw new IllegalStateException("A customer with the given id already exists");
        }
        customerMap.put(customer.getId(), customer);
    }

    public synchronized void removeCustomer(@NonNull C customer) {
        Objects.requireNonNull(customer);
        removeCustomer(customer.getId());
    }

    public synchronized void removeCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        customerMap.remove(customerId);
    }

    public synchronized C getCustomer(String customerId) {
        if (!containsCustomer(customerId)) {
            throw new IllegalStateException("A customer with the given id doesn't exist");
        }
        return customerMap.get(customerId);
    }

    public synchronized boolean containsCustomer(String customerId) {
        return customerMap.containsKey(customerId);
    }

    protected synchronized boolean containsCustomerHelper(@Nullable C customer) {
        return customer!=null && customerMap.containsKey(customer.getId());
    }

    public abstract boolean containsCustomer(@Nullable Customer customer);

    public synchronized void removeAllCustomers() {
        customerMap.clear();
    }
}
