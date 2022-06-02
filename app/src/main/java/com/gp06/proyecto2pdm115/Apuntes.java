package com.gp06.proyecto2pdm115;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Apuntes extends AppCompatActivity {

    EditText title,body;
    Button exportar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apuntes);

        title = findViewById(R.id.editTitulo);
        body = findViewById(R.id.editTexto);
        exportar = findViewById(R.id.buttonExport);

        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = title.getText().toString();
                String texto = body.getText().toString();
                String path = getExternalFilesDir(null).toString() + "/" + titulo + ".pdf";
                File file = new File(path);
                if (!file.exists()) {
                    try {
                        file.createNewFile(); //crear pdf
                        Intent intent = new Intent(getApplicationContext(), PDFVista.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                document.open();
                try {
                    document.add(new Paragraph(titulo));
                    document.add(new Paragraph("\n"));
                    document.add(new Paragraph(texto));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Descarga completada", Toast.LENGTH_LONG).show();
                document.close();
            }
        });
    }
}