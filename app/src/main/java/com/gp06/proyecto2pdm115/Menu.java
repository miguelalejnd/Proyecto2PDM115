package com.gp06.proyecto2pdm115;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void abrirCalendario(View view) {
        Intent i=new Intent(this,Calendario.class);
        startActivity(i);
    }

    /*public void abrirApuntes(View view) {
        Intent i=new Intent(this,Apuntes.class);
        startActivity(i);
    }*/
}