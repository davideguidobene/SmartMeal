package it.unive.quadcore.smartmeal.communication;

/**
 * Enum che rappresenta la tipologia del messaggo trasmesso.
 */
enum RequestType {
    /**
     * Messaggio condivisione nome cliente
     * - Customer -> Manager: invio nome
     * - Customer <- Manager: conferma nome ricevuto
     */
    CUSTOMER_NAME,

    /**
     * Messaggio condivisione lista di tavoli
     * - Customer -> Manager: richiesta tavoli
     * - Customer <- Manager: risposta con lista tavoli
     */
    FREE_TABLE_LIST,

    /**
     * Messaggio condivisione id tavolo
     * - Customer -> Manager: invio id tavolo scelto
     * - Customer <- Manager: conferma tavolo selezionato
     */
    SELECT_TABLE,

    /**
     * Messaggio notifica cameriere
     * - Customer -> Manager: invio notifica cameriere
     * - Customer <- Manager: conferma notifica cameriere
     */
    NOTIFY_WAITER,

    /**
     * Messaggio notifica tavolo modificato
     * - Customer -> Manager: //
     * - Customer <- Manager: notifica tavolo modificato
     */
    TABLE_CHANGED,

    /**
     * Messaggio notifica tavolo rimosso
     * - Customer -> Manager: //
     * - Customer <- Manager: notifica tavolo rimosso
     */
    TABLE_REMOVED
}
