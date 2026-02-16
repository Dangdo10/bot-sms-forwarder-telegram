package com.example.smsforwarder;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "SmsForwarderPrefs";
    private static final String KEY_PHONE_NUMBERS = "phone_numbers_json"; // Changed to avoid StringSet cache bug
    private static final String KEY_FILTER_ENABLED = "filter_enabled";

    private SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Lưu danh sách số điện thoại (JSON format to avoid StringSet cache)
    public void savePhoneNumbers(Set<String> numbers) {
        JSONArray jsonArray = new JSONArray(numbers);
        prefs.edit().putString(KEY_PHONE_NUMBERS, jsonArray.toString()).apply();
    }

    // Đọc danh sách số điện thoại (JSON format - always fresh read)
    public Set<String> getPhoneNumbers() {
        String json = prefs.getString(KEY_PHONE_NUMBERS, "[]");
        Set<String> result = new HashSet<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                result.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Thêm 1 số điện thoại
    public void addPhoneNumber(String number) {
        Set<String> numbers = getPhoneNumbers();
        numbers.add(number.trim());
        savePhoneNumbers(numbers);
    }

    // Xóa 1 số điện thoại
    public void removePhoneNumber(String number) {
        Set<String> numbers = getPhoneNumbers();
        numbers.remove(number.trim());
        savePhoneNumbers(numbers);
    }

    // Bật/tắt filter
    public void setFilterEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_FILTER_ENABLED, enabled).apply();
    }

    // Check filter có bật không
    public boolean isFilterEnabled() {
        return prefs.getBoolean(KEY_FILTER_ENABLED, false);
    }

    // Check số có trong danh sách không (hỗ trợ nhiều format)
    public boolean isPhoneNumberAllowed(String phoneNumber) {
        if (!isFilterEnabled()) {
            return true; // Filter tắt = cho tất cả qua
        }

        Set<String> allowedNumbers = getPhoneNumbers();
        if (allowedNumbers.isEmpty()) {
            return false; // Không có số nào trong list = chặn tất cả
        }

        // Normalize số điện thoại (xóa khoảng trắng, dấu +, -)
        String normalized = phoneNumber.replaceAll("[\\s+\\-()]", "");

        for (String allowed : allowedNumbers) {
            String normalizedAllowed = allowed.replaceAll("[\\s+\\-()]", "");

            // Check exact match hoặc contains
            if (normalized.equals(normalizedAllowed) ||
                    normalized.contains(normalizedAllowed) ||
                    normalizedAllowed.contains(normalized)) {
                return true;
            }
        }

        return false;
    }
}
