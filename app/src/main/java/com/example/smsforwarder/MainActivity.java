package com.example.smsforwarder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView statusText = findViewById(R.id.statusText);
        statusText.setText("SMS Forwarder đang chạy...\n\nỨng dụng sẽ tự động chuyển tiếp SMS lên Telegram.");

        // Button mở Settings
        findViewById(R.id.settingsButton).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, SettingsActivity.class));
        });

        // Xin quyền SMS và INTERNET
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        TextView statusText = findViewById(R.id.statusText);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                statusText.setText(
                        "✓ Quyền đã được cấp!\n\nSMS Forwarder đang hoạt động.\nMọi SMS nhận được sẽ được chuyển lên Telegram.");
            } else {
                statusText.setText("✗ Cần cấp quyền SMS để ứng dụng hoạt động!");
            }
        }
    }
}