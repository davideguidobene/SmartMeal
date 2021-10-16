package it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;

/**
 * Classe che implementa la callback da eseguire quando una richiesta
 * Nearby va in timeout.
 */
public class CustomerLeaveRoomAction implements Runnable {
    @Nullable
    private final Activity activity;

    @NonNull
    private final String snackbarMessage;

    public CustomerLeaveRoomAction(@Nullable Activity activity, @NonNull String snackbarMessage) {
        this.activity = activity;
        this.snackbarMessage = snackbarMessage;
    }

    @Override
    public void run() {
        if (activity == null) {
            return;
        }

        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(CustomerBottomNavigationActivity.SHOW_SNACKBAR, snackbarMessage);
        returnIntent.putExtras(bundle);
        activity.setResult(Activity.RESULT_OK, returnIntent);
        activity.finish();
    }
}
