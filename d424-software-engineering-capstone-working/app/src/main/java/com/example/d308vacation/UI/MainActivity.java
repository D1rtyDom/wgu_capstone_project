package com.example.d308vacation.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.textfield.TextInputEditText;

import com.example.d308vacation.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static int numAlert;
    SharedPreferences encryptedSharedPreferences;
    Button loginButton;
    Button createAccountButton;
    TextInputEditText usernameView;
    TextInputEditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // setup EncryptedSharedPreferences
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        // set views
        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.create_account_button);
        usernameView = findViewById(R.id.username_input_edit);
        passwordView = findViewById(R.id.password_input_edit);

        loginButton.setOnClickListener(view -> onClickLogin());
        createAccountButton.setOnClickListener(view -> onClickCreateAccount());
    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.login_error_text).setVisibility(View.GONE);
        usernameView.setText("");
        passwordView.setText("");
        usernameView.clearFocus();
        passwordView.clearFocus();
    }

    public void onClickLogin() {
        TextView loginError = findViewById(R.id.login_error_text);
        loginError.setVisibility(View.GONE);

        String usernameInput = usernameView.getText().toString().trim();
        String passwordInput = passwordView.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            loginError.setText("Please enter both username and password");
            loginError.setVisibility(View.VISIBLE);
            return;
        }

        String storedPass = encryptedSharedPreferences.getString("user_" + usernameInput, null);

        if (storedPass != null && storedPass.equals(passwordInput)) {
            // Save current username for global access
            SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
            editor.putString("current_user", usernameInput);
            editor.apply();

            startActivity(new Intent(this, VacationListActivity.class));
        } else {
            loginError.setText(R.string.username_password_incorrect);
            loginError.setVisibility(View.VISIBLE);
        }
    }

    public void onClickCreateAccount() {
        TextView loginError = findViewById(R.id.login_error_text);
        loginError.setVisibility(View.GONE);

        String usernameInput = usernameView.getText().toString().trim();
        String passwordInput = passwordView.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            loginError.setText("Please enter both username and password");
            loginError.setVisibility(View.VISIBLE);
            return;
        }

        if (encryptedSharedPreferences.contains("user_" + usernameInput)) {
            loginError.setText("Username already exists!");
            loginError.setVisibility(View.VISIBLE);
            return;
        }

        if (validatePassword(passwordInput)) {
            SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
            editor.putString("user_" + usernameInput, passwordInput);
            // Save current username for global access
            editor.putString("current_user", usernameInput);
            editor.apply();
            
            startActivity(new Intent(this, VacationListActivity.class));
        } else {
            loginError.setText(R.string.validate_password);
            loginError.setVisibility(View.VISIBLE);
        }
    }

    public boolean validatePassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
