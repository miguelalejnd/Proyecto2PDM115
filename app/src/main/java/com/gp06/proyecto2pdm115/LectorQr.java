package com.gp06.proyecto2pdm115;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class LectorQr extends AppCompatActivity {

    TextView txtVisor;
    Button limpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qr);
        txtVisor=findViewById(R.id.txtVisor);
        limpiar=findViewById(R.id.btnLimpiar);

        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        String recuperado = result.getContents();

        txtVisor.setText(recuperado);
    }

    public void limpiar(View view){
        txtVisor.setText("");
        recreate();
    }
}