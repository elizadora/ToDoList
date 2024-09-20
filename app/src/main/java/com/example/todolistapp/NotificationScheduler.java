package com.example.todolistapp;

import android.content.Context;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    public static void scheduleDailyNotification(Context context) {
        // get the current time
        Calendar now = Calendar.getInstance();

        // create a calendar instance for the next 8 AM
        Calendar next8Am = Calendar.getInstance();
        next8Am.set(Calendar.HOUR_OF_DAY, 17);
        next8Am.set(Calendar.MINUTE, 58);
        next8Am.set(Calendar.SECOND, 0);

        if (now.after(next8Am)) {
            next8Am.add(Calendar.DAY_OF_MONTH, 1);
        }

        // calculate the initial delay until the scheduled time in milliseconds
        long initialDelay = next8Am.getTimeInMillis() - now.getTimeInMillis();

        // create a periodic work request that will trigger every 24 hours
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        // send the request to WorkManager
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
