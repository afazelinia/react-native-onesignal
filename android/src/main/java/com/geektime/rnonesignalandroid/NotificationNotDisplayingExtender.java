package com.geektime.rnonesignalandroid;

import android.content.Intent;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrey Beletsky on 6/5/17.
 */
public class NotificationNotDisplayingExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        String code,taskId;

        JSONObject additionalData = receivedResult.payload.additionalData;

        // This part of code is added to send notification to main app.
        // Main app is responsible to call ReactServiceDispatcher and pass notification data.
        //As a result. notification data can be handled at react side when the app is closed.

        try {
            code = additionalData.getString("code");
        } catch (JSONException e) {
            code ="";
        }
        try {
            taskId = additionalData.get("task_id").toString();
        } catch (JSONException e) {
            taskId="";
        }
        Intent intent = new Intent(RNOneSignal.ACTION_NOTIFICATION_RECEIVED);
        intent.putExtra("title",receivedResult.payload.title);
        intent.putExtra("body",receivedResult.payload.body);
        intent.putExtra("code",code);
        intent.putExtra("task_id",taskId);
        sendBroadcast(intent);

        boolean hidden = false;
        try {
            if (additionalData.has(RNOneSignal.HIDDEN_MESSAGE_KEY)) {
                hidden = additionalData.getBoolean(RNOneSignal.HIDDEN_MESSAGE_KEY);
            }
        } catch (JSONException e) {
            Log.e("OneSignal", "onNotificationProcessing Failure: " + e.getMessage());
        }

        // Return true to stop the notification from displaying.
        return hidden;
    }
}
