package com.example.repartidor;

import android.content.Context;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkScheduler {


    public static void scheduleDailyWork(Context context, Class<? extends Worker> workerClass, int hour, int minute) {//Metodo para mandar a ejecutar una clase a cierta hora
        // Obtener la hora actual
        Calendar current = Calendar.getInstance();

        // Establecer la hora objetivo (por ejemplo, 6:30 PM)
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);

        // Si la hora objetivo ya pasó hoy, programar para mañana
        if (current.after(target)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Calcular el tiempo de retraso
        long delay = target.getTimeInMillis() - System.currentTimeMillis();

        // Crear una solicitud de trabajo para ejecutar a la hora deseada
        OneTimeWorkRequest.Builder workRequestBuilder = new OneTimeWorkRequest.Builder(workerClass)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS);

        // Encolar el trabajo
        OneTimeWorkRequest workRequest = workRequestBuilder.build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
