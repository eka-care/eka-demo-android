package eka.dr.intl.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecuredSharedPref implements SharedPreferences {

    protected SharedPreferences sharedPreferences;
    private byte[] sharedPrefKey;


    public SecuredSharedPref(Context context, String sharedPrefName) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        }
        try {
            final String key = SecuredSharedPref.generateAesKeyName(context);
            String value = sharedPreferences.getString(key, null);
            if (value == null) {
                value = SecuredSharedPref.generateAesKeyValue();
                sharedPreferences.edit().putString(key, value).commit();
            }
            sharedPrefKey = decode(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String encode(byte[] input) {
        return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static byte[] decode(String input) {
        return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static String generateAesKeyName(Context context) throws InvalidKeySpecException,
            NoSuchAlgorithmException {
        final char[] password = (context.getPackageName() + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID)).toCharArray();
        final byte[] salt = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID).getBytes();

        final int iterations = 1000;

        // Generate a 256-bit key
        final int keyLength = 256;

        final KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        return SecuredSharedPref.encode(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).getEncoded());
    }

    private static String generateAesKeyValue() throws NoSuchAlgorithmException {
        final SecureRandom random = new SecureRandom();
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        try {
            generator.init(256, random);
        } catch (Exception e) {
            try {
                generator.init(192, random);
            } catch (Exception e1) {
                generator.init(128, random);
            }
        }
        return SecuredSharedPref.encode(generator.generateKey().getEncoded());
    }

    private  String encrypt(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sharedPrefKey, "AES"));
            return SecuredSharedPref.encode(cipher.doFinal(text.getBytes("UTF-8")));
        } catch (Exception e) {
            return null;
        }
    }

    private String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.length() == 0) {
            return encryptedText;
        }

        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sharedPrefKey, "AES"));
            return new String(cipher.doFinal(SecuredSharedPref.decode(encryptedText)), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, String> getAll() {
        final Map<String, ?> encryptedMap = sharedPreferences.getAll();
        final Map<String, String> decryptedMap = new HashMap<String, String>(encryptedMap.size());
        for (Map.Entry<String, ?> entry : encryptedMap.entrySet()) {
            try {
                decryptedMap.put(decrypt(entry.getKey()),
                        decrypt(entry.getValue().toString()));
            } catch (Exception e) {
            }
        }
        return decryptedMap;
    }

    @Override
    public String getString(String key, String defaultValue) {
        final String encryptedValue =
                sharedPreferences.getString(encrypt(key), null);
        if (encryptedValue != null) {
            return decrypt(encryptedValue);
        } else {
            return defaultValue;
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultValues) {
        final Set<String> encryptedSet =
                sharedPreferences.getStringSet(encrypt(key), null);
        if (encryptedSet == null) {
            return defaultValues;
        }
        final Set<String> decryptedSet = new HashSet<String>(encryptedSet.size());
        for (String encryptedValue : encryptedSet) {
            decryptedSet.add(decrypt(encryptedValue));
        }
        return decryptedSet;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        final String encryptedValue =
                sharedPreferences.getString(encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public long getLong(String key, long defaultValue) {
        final String encryptedValue =
                sharedPreferences.getString(encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        final String encryptedValue =
                sharedPreferences.getString(encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        final String encryptedValue =
                sharedPreferences.getString(encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public boolean contains(String key) {
        return sharedPreferences.contains(encrypt(key));
    }

    @Override
    public Editor edit() {
        return new Editor();
    }


    public  class Editor implements SharedPreferences.Editor {
        private SharedPreferences.Editor mEditor;

        private Editor() {
            mEditor = sharedPreferences.edit();
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            mEditor.putString(encrypt(key), encrypt(value));
            return this;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            final Set<String> encryptedValues = new HashSet<String>(values.size());
            for (String value : values) {
                encryptedValues.add(encrypt(value));
            }
            mEditor.putStringSet(encrypt(key), encryptedValues);
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            mEditor.putString(encrypt(key),
                    encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            mEditor.putString(encrypt(key),
                    encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            mEditor.putString(encrypt(key),
                    encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            mEditor.putString(encrypt(key),
                    encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            mEditor.remove(encrypt(key));
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            mEditor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public void apply() {
            mEditor.apply();
        }
    }

    ArrayList<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();


    OnSharedPreferenceChangeListener decryptListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            try {
                CopyOnWriteArrayList<OnSharedPreferenceChangeListener> copyListeners = new CopyOnWriteArrayList<>(listeners);
                for (OnSharedPreferenceChangeListener applistener : copyListeners) {
                    applistener.onSharedPreferenceChanged(sharedPreferences, decrypt(key));
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    };
    @Override
    public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener applistener) {
        listeners.add(applistener);

        sharedPreferences.registerOnSharedPreferenceChangeListener(decryptListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener applistener) {
        listeners.remove(applistener);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(applistener);
    }
}
