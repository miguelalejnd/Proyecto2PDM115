package com.gp06.proyecto2pdm115;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextPassword;
    private Button buttonIniciarSesion;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NOMBRE = "nameKey";
    public static final String PASSWORD = "passwordKey";

    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Take instance of Action Bar
        // using getSupportActionBar and
        // if it is not Null
        // then call hide function
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextNombre = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonIniciarSesion = (Button) findViewById(R.id.buttonIniciarSesion);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        buttonIniciarSesion.setOnClickListener(view -> {
            String nombre = editTextNombre.getText().toString();
            String password = editTextPassword.getText().toString();

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString(NOMBRE, nombre);
            editor.putString(PASSWORD, password);

            editor.commit();

            Intent intent = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String nombre = sharedpreferences.getString(NOMBRE,null);
        String password = sharedpreferences.getString(PASSWORD,null);
        if (nombre != null && password != null) {
            Intent i = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(i);
            finish();
        }
    }
}