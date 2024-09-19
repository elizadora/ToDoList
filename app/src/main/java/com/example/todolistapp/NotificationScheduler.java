package com.example.todolistapp;

import android.content.Context;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    public static void scheduleDailyNotification(Context context) {
        // Calcular o tempo restante até as 8 da manhã
        Calendar now = Calendar.getInstance();
        Calendar next8Am = Calendar.getInstance();
        next8Am.set(Calendar.HOUR_OF_DAY, 17);
        next8Am.set(Calendar.MINUTE, 58);
        next8Am.set(Calendar.SECOND, 0);

        if (now.after(next8Am)) {
            // Se a hora atual for depois das 8 da manhã, agende para o dia seguinte
            next8Am.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Calcular a duração até as 8 da manhã em milissegundos
        long initialDelay = next8Am.getTimeInMillis() - now.getTimeInMillis();

        // Criar uma solicitação de trabalho periódica
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        // Enviar a solicitação ao WorkManager
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
