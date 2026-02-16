package com.example.smsforwarder;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat filterSwitch;
    private EditText phoneNumberInput;
    private Button addButton;
    private ListView phoneNumberList;

    private SharedPreferencesHelper prefsHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> phoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefsHelper = new SharedPreferencesHelper(this);

        // Initialize views
        filterSwitch = findViewById(R.id.filterSwitch);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        addButton = findViewById(R.id.addButton);
        phoneNumberList = findViewById(R.id.phoneNumberList);

        // Load saved data
        filterSwitch.setChecked(prefsHelper.isFilterEnabled());
        phoneNumbers = new ArrayList<>(prefsHelper.getPhoneNumbers());

        // Setup ListView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, phoneNumbers);
        phoneNumberList.setAdapter(adapter);

        // Switch listener
        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsHelper.setFilterEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Filter đã BẬT" : "Filter đã TẮT", Toast.LENGTH_SHORT).show();
        });

        // Add button listener
        addButton.setOnClickListener(v -> addPhoneNumber());

        // Long click to delete
        phoneNumberList.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    private void addPhoneNumber() {
        String number = phoneNumberInput.getText().toString().trim();

        if (number.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumbers.contains(number)) {
            Toast.makeText(this, "Số này đã có trong danh sách", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to list
        phoneNumbers.add(number);
        adapter.notifyDataSetChanged();

        // Save to SharedPreferences
        prefsHelper.addPhoneNumber(number);

        // Clear input
        phoneNumberInput.setText("");

        Toast.makeText(this, "Đã thêm: " + number, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog(int position) {
        String number = phoneNumbers.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Xóa số điện thoại")
                .setMessage("Bạn có muốn xóa số " + number + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    phoneNumbers.remove(position);
                    adapter.notifyDataSetChanged();
                    prefsHelper.removePhoneNumber(number);
                    Toast.makeText(this, "Đã xóa: " + number, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
