package com.example.smsforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = sms.getDisplayOriginatingAddress();
                            String message = sms.getDisplayMessageBody();

                            Log.d(TAG, "=== SMS RECEIVED DEBUG ===");
                            Log.d(TAG, "Sender: " + sender);
                            Log.d(TAG, "Message: " + message);

                            // Check filter
                            SharedPreferencesHelper prefsHelper = new SharedPreferencesHelper(context);
                            Log.d(TAG, "Filter enabled: " + prefsHelper.isFilterEnabled());
                            Log.d(TAG, "Phone numbers in whitelist: " + prefsHelper.getPhoneNumbers());

                            if (prefsHelper.isPhoneNumberAllowed(sender)) {
                                // Get Telegram config
                                String botToken = prefsHelper.getTelegramBotToken();
                                String chatId = prefsHelper.getTelegramChatId();

                                Log.d(TAG, "Bot Token (length): " + botToken.length());
                                Log.d(TAG, "Chat ID: " + chatId);

                                // Validate config
                                if (botToken.isEmpty() || chatId.isEmpty()) {
                                    Log.e(TAG, "❌ Telegram config EMPTY! Please configure in Settings.");
                                } else {
                                    Log.d(TAG, "✅ Sending to Telegram...");
                                    sendToTelegram("From: " + sender + "\n" + message, botToken, chatId);
                                    Log.d(TAG, "SMS forwarded from: " + sender);
                                }
                            } else {
                                Log.d(TAG, "❌ SMS blocked by filter from: " + sender);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error receiving SMS", e);
                }
            }
        }
    }

    private void sendToTelegram(String text, String botToken, String chatId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        FormBody body = new FormBody.Builder()
                .add("chat_id", chatId)
                .add("text", text)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "❌ Failed to send to Telegram");
                Log.e(TAG, "Exception type: " + e.getClass().getName());
                Log.e(TAG, "Exception message: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Message sent to Telegram successfully");
                } else {
                    Log.e(TAG, "❌ Telegram API error: " + response.code());
                    String responseBody = response.body() != null ? response.body().string() : "No body";
                    Log.e(TAG, "Response: " + responseBody);
                }
                response.close();
            }
        });
    }
}
