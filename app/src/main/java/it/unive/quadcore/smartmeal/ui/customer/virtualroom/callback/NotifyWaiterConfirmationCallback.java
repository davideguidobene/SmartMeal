package it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;

public class NotifyWaiterConfirmationCallback implements Consumer<Confirmation<? extends WaiterNotificationException>> {
    private static final String TAG = "NotifyWaiterConfCb";

    @Nullable
    private final Activity activity;

    public NotifyWaiterConfirmationCallback(@Nullable Activity activity) {
        this.activity = activity;
    }

    @Override
    public void accept(Confirmation<? extends WaiterNotificationException> confirmation) {
        if (activity == null) {
            return;
        }

        int snackbarMessageId;

        try {
            confirmation.obtain();
            snackbarMessageId = R.string.waiter_notification_confirmed;
        } catch (WaiterNotificationException e) {
            Log.i(TAG, "Waiter notification rejected: " + e.getMessage());
            snackbarMessageId = R.string.waiter_notification_rejected;
        }

        final int snackbarMessageIdFinal = snackbarMessageId;
        activity.runOnUiThread(() -> {
            Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    snackbarMessageIdFinal,
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();
        });
    }
}
