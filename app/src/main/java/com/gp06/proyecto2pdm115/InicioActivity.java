package com.gp06.proyecto2pdm115;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class InicioActivity extends AppCompatActivity {
    private final String url_servidor_web = "https://proyecto2pdm115.000webhostapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.menu is a reference to an xml file named menu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.buttonCerrarSesion) {
            // do something here
            SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.clear();
            editor.commit();

            FirebaseAuth.getInstance().signOut();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignIn.getClient(this, gso).signOut();

            Intent intent = new Intent(InicioActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void IrMenu(View view) {
        Intent i=new Intent(this, com.gp06.proyecto2pdm115.Menu.class);
        startActivity(i);
    }

    public void IrAsistencia(View view) {
        Intent i=new Intent(this, com.gp06.proyecto2pdm115.LectorQr.class);
        startActivity(i);
    }

    public void crearReporteAsistenciaExcel(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                                              new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                              PackageManager.PERMISSION_GRANTED);
        }

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                                                                   Context.MODE_PRIVATE);

        String correo = sharedpreferences.getString(MainActivity.CORREO,null);

        String url = MainActivity.url_servidor_web + "obtener_listado_asistencia.php" +
                     "?correo=" + correo;

        String respuestaString = ControladorServicio.obtenerRespuestaPeticion(url, this);
        ControladorServicio.crearArchivoExcel(respuestaString, this);
    }

    public void crearReporteAsistenciaTexto(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PackageManager.PERMISSION_GRANTED);
        }

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        String correo = sharedpreferences.getString(MainActivity.CORREO,null);

        String url = MainActivity.url_servidor_web + "obtener_listado_asistencia.php" +
                "?correo=" + correo;

        String respuestaString = ControladorServicio.obtenerRespuestaPeticion(url, this);
        ControladorServicio.crearArchivoDeTexto(respuestaString, this);
    }
}//https://proyecto2pdm115.000webhostapp.com/verificar_login.php?correo=lj16001@ues.edu.sv