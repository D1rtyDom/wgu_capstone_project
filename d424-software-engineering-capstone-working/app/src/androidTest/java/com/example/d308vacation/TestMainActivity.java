package com.example.d308vacation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.d308vacation.UI.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestMainActivity {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void correctPasswordFormat() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertTrue(activity.validatePassword("Password1!"));
        });
    }
    @Test
    public void tooShort() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertFalse(activity.validatePassword("Pass"));
        });
    }
    @Test
    public void tooLong() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertFalse(activity.validatePassword("Password1!ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"));
        });
    }
    @Test
    public void missingNumber() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertFalse(activity.validatePassword("Password!"));
        });
    }
    @Test
    public void missingSpecialCharacter() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertFalse(activity.validatePassword("Password1"));
        });
    }
    @Test
    public void missingLowerCase() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertFalse(activity.validatePassword("PASSWORD1!"));
        });
    }
    @Test
    public void missingUpperCase() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertFalse(activity.validatePassword("password1!"));
        });
    }
    @Test
    public void testPasswordBlank() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            // Test with empty string
            assertFalse("Password should not be blank", activity.validatePassword(""));
            // Test with just whitespace
            assertFalse("Password should not be just spaces", activity.validatePassword("   "));
        });
    }

    @Test
    public void testCredentialMatching() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            // First, clear any existing data to ensure a clean state
            activity.onClickClearData();

            // Setup credentials via a mock "Create Account" flow
            // In MainActivity, if account doesn't exist, it uses these to save
            String testUser = "testUser";
            String testPass = "Password123!";

            // Manually save them into the encrypted prefs to simulate an existing account
            android.content.SharedPreferences.Editor editor = activity.getSharedPreferences("secure_prefs", android.content.Context.MODE_PRIVATE).edit();
            // Note: MainActivity uses EncryptedSharedPreferences. In a real test, 
            // we should ideally use the internal 'encryptedSharedPreferences' if accessible,
            // or perform the UI actions. Since we are inside the activity context:
            
            // We can call onClickGetStarted after setting the UI fields
            androidx.appcompat.widget.AppCompatEditText userEdit = activity.findViewById(R.id.username_input_edit);
            androidx.appcompat.widget.AppCompatEditText passEdit = activity.findViewById(R.id.password_input_edit);
            
            userEdit.setText(testUser);
            passEdit.setText(testPass);
            
            // This should trigger the 'Create Account' logic in MainActivity and save the prefs
            activity.onClickGetStarted();

            // Now, simulate a login attempt with the same credentials
            userEdit.setText(testUser);
            passEdit.setText(testPass);

            // validatePassword only checks format, but we want to check matching logic
            // In MainActivity: if (encryptedSharedPreferences.getString("username", "defaultUser").equals(username) && ...)
            
            // We check the logic used in onClickGetStarted
            boolean isMatch = activity.getSharedPreferences("secure_prefs", android.content.Context.MODE_PRIVATE)
                    .getString("username", "").equals(testUser) &&
                    activity.getSharedPreferences("secure_prefs", android.content.Context.MODE_PRIVATE)
                    .getString("password", "").equals(testPass);

            assertTrue("Stored credentials should match the input", isMatch);
        });
    }
}

