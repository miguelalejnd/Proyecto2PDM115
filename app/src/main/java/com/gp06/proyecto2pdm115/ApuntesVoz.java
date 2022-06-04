package com.gp06.proyecto2pdm115;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ApuntesVoz extends AppCompatActivity {

    //lector de voz
    private static final int RED_CODE_SPEECH_INPUT=100;
    private TextView entradaVoz;
    private ImageButton botonHablar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apuntes_voz);

        entradaVoz=findViewById(R.id.textEntrada);
        botonHablar=findViewById(R.id.botonHablar);

        botonHablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarEntradaVoz();
            }
        });
    }

    private void iniciarEntradaVoz() {
        Intent intent=new Intent((RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Di algo para escribir");
        try{
            startActivityForResult(intent,RED_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "ALGO SALIO MAL :( ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RED_CODE_SPEECH_INPUT:{
                if(resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    entradaVoz.setText(result.get(0));//asignamos texto
                } break;
            }

        }
    }
}