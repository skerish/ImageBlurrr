package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.background.Constants;


public class BlurWorker extends Worker {

    private static final String TAG = BlurWorker.class.getSimpleName();

    public BlurWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();
        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);

        WorkerUtils.makeStatusNotification("Doing BlurWorker", context);
        WorkerUtils.sleep();

        try {
            if (TextUtils.isEmpty(resourceUri)){
                Log.e(TAG, "Invalid input Uri!");
                throw new IllegalArgumentException("Invalid input Uri");
            }

            ContentResolver contentResolver = context.getContentResolver();
            // Create the bitmap
            Bitmap picture = BitmapFactory.decodeStream(contentResolver
                    .openInputStream(Uri.parse(resourceUri)));

            // Blur the image
            Bitmap output = WorkerUtils.blurBitmap(picture, context);
            // Write bitmap to the temp file
            Uri outputUri = WorkerUtils.writeBitmapToFile(context, output);
            // Show notification
            WorkerUtils.makeStatusNotification("Output is " + outputUri.toString(), context);
            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, outputUri.toString())
                    .build();
            return Result.success(outputData);
        }
        catch (Throwable throwable){
            Log.e(TAG, "Error applying blur!", throwable);
            return Result.failure();
        }
    }
}
