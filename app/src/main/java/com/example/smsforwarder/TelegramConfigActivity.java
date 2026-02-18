package com.example.smsforwarder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TelegramConfigActivity extends AppCompatActivity {

    private EditText botTokenInput;
    private EditText chatIdInput;
    private Button saveButton;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telegram_config);

        prefsHelper = new SharedPreferencesHelper(this);

        botTokenInput = findViewById(R.id.botTokenInput);
        chatIdInput = findViewById(R.id.chatIdInput);
        saveButton = findViewById(R.id.saveButton);

        // Load saved config
        botTokenInput.setText(prefsHelper.getTelegramBotToken());
        chatIdInput.setText(prefsHelper.getTelegramChatId());

        saveButton.setOnClickListener(v -> saveConfig());
    }

    private void saveConfig() {
        String token = botTokenInput.getText().toString().trim();
        String chatId = chatIdInput.getText().toString().trim();

        if (token.isEmpty() || chatId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        prefsHelper.saveTelegramBotToken(token);
        prefsHelper.saveTelegramChatId(chatId);

        Toast.makeText(this, "✅ Đã lưu cấu hình!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
