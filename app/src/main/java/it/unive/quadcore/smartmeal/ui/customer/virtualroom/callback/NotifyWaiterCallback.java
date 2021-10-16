package it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;

public class NotifyWaiterCallback implements Runnable {
    @Nullable
    private final Activity activity;

    public NotifyWaiterCallback(@Nullable Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        if (activity == null) {
            return;
        }

        CustomerCommunication.getInstance().notifyWaiter(
                new NotifyWaiterConfirmationCallback(activity),
                new CustomerLeaveRoomAction(activity, activity.getString(R.string.timeout_error_snackbar))
        );
    }
}
