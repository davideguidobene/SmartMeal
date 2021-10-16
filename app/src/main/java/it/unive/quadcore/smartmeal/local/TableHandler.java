package it.unive.quadcore.smartmeal.local;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;

/**
 * Classe che gestisce i tavoli.
 * (visibile solo a Local)
 */
class TableHandler {

    // Mappa clienti-tavoli
    @NonNull
    private final Map<Customer,ManagerTable> customerTableMap ;

    // Lista di tavoli liberi (usata come cache)
    @NonNull
    private final SortedSet<ManagerTable> freeTableList ;

    /* Mappa tavoli-tavoli gestore.
       Serve a 2 cose:
            - Tenere i soli tavoli significativi per il locale
            - Evitare casting a ManagerTable
     */
    @NonNull
    private final Map<Table,ManagerTable> tablesMap;

    // Costruttore con visibilità package
    TableHandler(){
        customerTableMap = new HashMap<>();

        // Prendo tutti i tavoli del locale
        Set<ManagerTable> tables = ManagerStorage.getTables();

        // Tutti i tavoli sono inizialmente liberi
        freeTableList = new TreeSet<>(tables);

        tablesMap = new HashMap<>();
        //tables.forEach(managerTable -> tableMap.put(managerTable,managerTable));
        for(ManagerTable managerTable : tables)
            tablesMap.put(managerTable, managerTable);

    }


    // Ritorna la lista di tavoli liberi
    @NonNull
    synchronized TreeSet<ManagerTable> getFreeTableList() {
        // Ritorna lista tavoli liberi (una copia)
        return new TreeSet<>(freeTableList) ;
    }

    // Ritorna la lista di tavoli occupati
    @NonNull
    synchronized SortedSet<ManagerTable> getAssignedTableList() {

        // Tutti i tavoli nella mappa customer-tables

        // Non c'è nessun tavolo occupato

        return new TreeSet<>(customerTableMap.values());
    }

    // Cambia il tavolo associato ad un dato cliente
    synchronized void changeCustomerTable(@NonNull Customer customer,@NonNull Table newTable) throws TableException {

        // Siccome metodo è syncrhonized, posso anche fare più semplicemente così :
        // QUESTI 2 CONTROLLI SONO GIA' FATTI IN ASSIGNTABLE FATTO IN FONDO. PERO' DEVO FARLI ORA, SE NO LIBERO UN TAVOLO CHE NON
        // AVREI DOVUTO LIBERARE
        if(!tablesMap.containsKey(newTable)) // Tavolo specificato non esiste
            throw new NoSuchTableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(newTable) ;

        if(!freeTableList.contains(managerTable)) // Tavolo già occupato
            throw new AlreadyOccupiedTableException("This table is already occupied");

        ManagerTable oldTable = getTable(customer);
        freeTable(oldTable);
        assignTable(customer, newTable);

    }

    // Assegna un tavolo ad un cliente senza tavolo
    synchronized void assignTable(@NonNull Customer customer,@NonNull Table table) throws TableException {

        // Il tavolo specificato non è un tavolo corretto per il locale
        if(!tablesMap.containsKey(table))
            throw new NoSuchTableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(table) ;

        // Cliente ha già un tavolo
        if(customerTableMap.containsKey(customer))
            throw new AlreadyAssignedTableException("This customer already has a table");

        // Tavolo già occupato
        if(!freeTableList.contains(managerTable))
            throw new AlreadyOccupiedTableException("This table is already occupied");

        // Assegno tavolo al cliente
        customerTableMap.put(customer, managerTable);

        // Tavolo specificato non è più libero
        freeTableList.remove(managerTable);
    }

    // Libera un tavolo
    synchronized void freeTable(@NonNull Table table) throws TableException {

        Customer customer = getCustomer(table); // Controllo se tavolo esiste e se è occupato
        ManagerTable managerTable = tablesMap.get(table) ;

        // Rimuovo questa associazione dalla mappa
        customerTableMap.remove(customer);

        // Il tavolo ora è libero
        freeTableList.add(managerTable);
    }

    // Ritorna il tavolo occupato da un certo cliente
    @NonNull
    synchronized ManagerTable getTable(@NonNull Customer customer) throws TableException {
        // Cliente non ha un tavolo
        if(!customerTableMap.containsKey(customer))
            throw new TableException("This customer doesn't have a table assigned");
        // Ritorno tavolo occupato (posso ritornarlo direttamente senza copiarlo perchè è immutable)
        return Objects.requireNonNull(customerTableMap.get(customer));
    }

    // Ritorna il cliente che occupa un certo tavolo
    @NonNull
    synchronized Customer getCustomer(@NonNull Table table) throws TableException {
        // Il tavolo specificato non è un tavolo corretto per il locale
        if(!tablesMap.containsKey(table))
            throw new NoSuchTableException("The selected table doesn't exist");
        ManagerTable managerTable = tablesMap.get(table) ;

        // Tavolo non è occupato da nessun cliente
        if(freeTableList.contains(managerTable))
            throw new TableException("This table isn't assigned");

        // Itero la mappa per trovare cliente con quel tavolo associato
        // Uso Iterator e non smart for perchè così posso fermarmi anticipatamente (più efficente)
        Iterator<Customer> it = customerTableMap.keySet().iterator();
        boolean found = false;
        Customer customer = null;
        while(it.hasNext() && !found){
            customer = it.next();
            if(getTable(customer).equals(managerTable))
                found = true;
        }

        return Objects.requireNonNull(customer);
    }
}
