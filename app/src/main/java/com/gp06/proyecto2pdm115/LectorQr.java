package com.gp06.proyecto2pdm115;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LectorQr extends AppCompatActivity {

    TextView txtVisor;
    Button limpiar;
    Spinner tipo;
    String tipoSeleccionado;
    EditText comentario;
    String fecha;
    String urlHost="https://proyecto2pdm115.000webhostapp.com/insertar_asistencia.php";
    Button marcar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qr);
        txtVisor=findViewById(R.id.txtVisor);
        limpiar=findViewById(R.id.btnLimpiar);
        tipo=findViewById(R.id.spinner_tipo);
        comentario=findViewById(R.id.txtComentario);
        marcar=findViewById(R.id.btnMarcar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo,
                android.R.layout.simple_spinner_item);

        tipo.setAdapter(adapter);

        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipoSeleccionado=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
        fecha = simpleDateFormat.format(calendar.getTime());

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

    public void insertarAsistencia(View view) {
        try {
            String usuarioCorreo = txtVisor.getText().toString();
            String tipoSel = tipoSeleccionado;
            String marcaTiempo = fecha;
            String coment = comentario.getText().toString();
            String codigoQrLeido = txtVisor.getText().toString();
            String url = null;
            JSONObject datosAsistencia = new JSONObject();
            JSONObject registro_de_asistencia = new JSONObject();

            url = urlHost + "?tipo=" + tipoSel + "&marca_de_tiempo=" + marcaTiempo +
                    "&usuario_correo=" + usuarioCorreo + "&comentario=" + coment +
                    "&codigo_qr_leido=" + codigoQrLeido;

            ControladorServicio.insertarAsistenciaExterno(url, this);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al insertar", Toast.LENGTH_LONG).show();
        }
    }
}