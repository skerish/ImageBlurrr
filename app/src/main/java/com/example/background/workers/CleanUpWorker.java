package com.example.background.workers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.background.Constants;

import java.io.File;

/**
 *  Worker for cleaning temp files.
 */

public class CleanUpWorker extends Worker {

    private static final String TAG = "CleanUpWorker";

    public CleanUpWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();

        WorkerUtils.makeStatusNotification("Doing CleanUpWorker", context);
        WorkerUtils.sleep();

        try {
            File outputDirectory = new File(context.getFilesDir(), Constants.OUTPUT_PATH);
            if (outputDirectory.exists()){
                File[] entries = outputDirectory.listFiles();
                if (entries != null && entries.length>0){
                    for (File entry : entries){
                        String name = entry.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")){
                            boolean deleted = entry.delete();
                            Log.i(TAG, String.format("Deleted %s - %s", name, deleted));
                        }
                    }
                }
            }
            return Worker.Result.success();
        } catch(Exception e){
            Log.e(TAG, "Error Cleaning up!!!", e);
            return Worker.Result.failure();
        }

    }
}
