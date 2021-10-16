package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.waiterNotifications;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.WaiterNotification;

// Adapter lista di notifiche, visibile dalla schermata delle notifiche cameriere
public class WaiterNotificationsAdapter extends RecyclerView.Adapter<WaiterNotificationsAdapter.NotificationViewHolder>{
    // View Holder di una riga della lista
    public static final class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView tableTextView;
        private TextView dateHourTextView;
        private Button deleteButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tableTextView = itemView.findViewById(R.id.table_notification_text_view);
            this.dateHourTextView = itemView.findViewById(R.id.date_hour_text_view);
            this.deleteButton = itemView.findViewById(R.id.waiter_notification_delete_button);

        }
    }

    @NonNull
    private final Activity activity;
    // Lista notifiche
    @NonNull
    private final List<WaiterNotification> waiterNotifications;

    public WaiterNotificationsAdapter(@NonNull Activity activity,@NonNull SortedSet<WaiterNotification> waiterNotifications) {
        this.activity = activity;
        this.waiterNotifications = new ArrayList<>(waiterNotifications);
    }

    @NonNull
    @Override
    public WaiterNotificationsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Mostro una riga della lista
        View view = LayoutInflater.from(activity).inflate(R.layout.waiter_notification_row, parent, false);
        return new WaiterNotificationsAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaiterNotificationsAdapter.NotificationViewHolder holder, int position) {
        // Creazione della riga della lista nella posizione "position"

        WaiterNotification notification =  waiterNotifications.get(position);

        try { // Mostro la notifica cameriere

            String prefix = activity.getString(R.string.table_prefix);
            holder.tableTextView.setText(String.format("%s %s", prefix, Local.getInstance().getTable(notification.getCustomer()).getId()));

            holder.dateHourTextView.setText(notification.getPrettyTime());

            holder.deleteButton.setOnClickListener(view->{ // Voglio eliminare la notifica cameriere
                try {
                    Local.getInstance().removeWaiterNotification(notification); // Rimuovo la notifica

                    int notificationToRemoveIndex = waiterNotifications.indexOf(notification); // Ricarico adapter
                    waiterNotifications.remove(notificationToRemoveIndex);
                    notifyItemRemoved(notificationToRemoveIndex);

                } catch (WaiterNotificationException e) { // Errore nel rimuovere la notifica
                    Snackbar.make(
                            activity.findViewById(android.R.id.content),
                            R.string.error_delete_notification_snackbar,
                            BaseTransientBottomBar.LENGTH_LONG
                    ).show();
                }
            });

            // Setto la riga visibile
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        } catch (TableException e) { // Cliente della notifica non ha un tavolo: notifica non valida. Non mostro la riga di tale notifica
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return waiterNotifications.size();
    }

    // Ricarico la lista notifiche
    public void reload() {
        waiterNotifications.clear();
        waiterNotifications.addAll(Local.getInstance().getWaiterNotificationList());
        notifyDataSetChanged();

    }
}
