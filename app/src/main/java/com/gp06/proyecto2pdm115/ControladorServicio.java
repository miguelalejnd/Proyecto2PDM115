package com.gp06.proyecto2pdm115;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class ControladorServicio {
    private static File filePatch;

    public static String obtenerRespuestaPeticion(String url, Context ctx) {

        String respuesta = " ";

        // Estableciendo tiempo de espera del servicio
        HttpParams parametros = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(parametros, 13000);
        HttpConnectionParams.setSoTimeout(parametros, 15000);

        // Creando objetos de conexion
        HttpClient cliente = new DefaultHttpClient(parametros);
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpRespuesta = cliente.execute(httpGet);

            StatusLine estado = httpRespuesta.getStatusLine();
            int codigoEstado = estado.getStatusCode();
            //String esta=String.valueOf(codigoEstado);
            //Toast.makeText(ctx,esta,Toast.LENGTH_LONG).show();
            if (codigoEstado == 200) {
                HttpEntity entidad = httpRespuesta.getEntity();
                respuesta = EntityUtils.toString(entidad);
            }
        } catch (Exception e) {
            Toast.makeText(ctx,url,Toast.LENGTH_LONG).show();
            Toast.makeText(ctx, "Error en la conexion", Toast.LENGTH_LONG)
                    .show();
            // Desplegando el error en el LogCat
            Log.v("Error de Conexion", e.toString());
        }
        return respuesta;
    }

    public static int verificarCorreoYClave(String json, Context ctx) {
        int valor = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.length() != 0)
                valor = Integer.parseInt(jsonObject.getString("resultado"));
            else {
                Toast.makeText(ctx, "Error respuesta vacia: este mensaje no devería poder verse",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(ctx, "Error con la respuesta JSON", Toast.LENGTH_LONG).show();
            Log.v("Peticion",e.toString());
        }
        return valor;
    }

    public static void crearArchivoExcel(String json, Context ctx) {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            Toast.makeText(ctx, "Error en parseo de JSON", Toast.LENGTH_LONG)
                    .show();
        }

        if (jsonArray.length() == 0)
            Toast.makeText(ctx, "Usted no es un profesor", Toast.LENGTH_LONG).show();
        else {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();

            // primero crear fila de cabecera
            HSSFRow rowHEading = sheet.createRow(0);
            rowHEading.createCell(0).setCellValue("Correo");
            rowHEading.createCell(1).setCellValue("Marca de tiempo");
            rowHEading.createCell(2).setCellValue("Tipo");
            rowHEading.createCell(3).setCellValue("Comentario");
            rowHEading.createCell(4).setCellValue("Código qr leido");

            // crear una fila por cada objeto json en el array json.
            JSONObject obj = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    obj = jsonArray.getJSONObject(i);

                    HSSFRow row = sheet.createRow(i + 1);

                    row.createCell(0).setCellValue(obj.getString("usuario_correo"));
                    row.createCell(1).setCellValue(obj.getString("marca_de_tiempo"));
                    row.createCell(2).setCellValue(obj.getString("tipo"));
                    row.createCell(3).setCellValue(obj.getString("comentario"));
                    row.createCell(4).setCellValue(obj.getString("codigo_qr_leido"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            filePatch = new File(Environment.getExternalStorageDirectory() +
                    "/reporte_de_asistencias.xls");

            try {
                if (!filePatch.exists())
                    filePatch.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(filePatch);

                workbook.write(fileOutputStream);

                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                Toast.makeText(ctx, "Reporte de asistencias guardado como excel", Toast.LENGTH_LONG)
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void crearArchivoDeTexto(String json, Context ctx) {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            Toast.makeText(ctx, "Error en parseo de JSON", Toast.LENGTH_LONG)
                    .show();
        }

        if (jsonArray.length() == 0)
            Toast.makeText(ctx, "Usted no es un profesor", Toast.LENGTH_LONG).show();
        else {
            filePatch = new File(Environment.getExternalStorageDirectory() +
                    "/reporte_de_asistencias.csv");

            try {
                if (!filePatch.exists())
                    filePatch.createNewFile();
                CSVWriter csvWriter = new CSVWriter(new FileWriter(filePatch));
                String[] heading = {"Correo", "Marca de tiempo", "Tipo", "Comentario", "Código qr leido"};
                csvWriter.writeNext(heading);

                JSONObject obj = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    obj = jsonArray.getJSONObject(i);

                    String[] datos = {obj.getString("usuario_correo"),
                                      obj.getString("marca_de_tiempo"),
                                      obj.getString("tipo"),
                                      obj.getString("comentario"),
                                      obj.getString("codigo_qr_leido")};

                    csvWriter.writeNext(datos);
                }

                csvWriter.close();

                Toast.makeText(ctx, "Reporte de asistencias guardado como texto", Toast.LENGTH_LONG)
                        .show();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String obtenerRespuestaPost(String url, JSONObject obj,
                                              Context ctx) {
        String respuesta = " ";
        try {
            HttpParams parametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(parametros, 3000);
            HttpConnectionParams.setSoTimeout(parametros, 5000);
            HttpClient cliente = new DefaultHttpClient(parametros);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("content-type", "application/json");
            StringEntity nuevaEntidad = new StringEntity(obj.toString());
            httpPost.setEntity(nuevaEntidad);
            Log.v("Peticion",url);
            Log.v("POST", httpPost.toString());
            HttpResponse httpRespuesta = cliente.execute(httpPost);
            StatusLine estado = httpRespuesta.getStatusLine();

            int codigoEstado = estado.getStatusCode();
            if (codigoEstado == 200) {
                respuesta = Integer.toString(codigoEstado);
                Log.v("respuesta",respuesta);
            }
            else{
                Log.v("respuesta",Integer.toString(codigoEstado));
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Error en la conexion", Toast.LENGTH_LONG)
                    .show();
            // Desplegando el error en el LogCat
            Log.v("Error de Conexion", e.toString());
        }
        return respuesta;
    }
    public static void insertarAsistenciaExterno(String peticion, Context ctx) {
        String json = obtenerRespuestaPeticion(peticion, ctx);
        try {
            JSONObject resultado = new JSONObject(json);
            Toast.makeText(ctx, "Registro ingresado"+
                    resultado.getJSONArray("resultado").toString(), Toast.LENGTH_LONG)
                    .show();
            int respuesta = resultado.getInt("resultado");
            if (respuesta == 1)
                Toast.makeText(ctx, "Registro ingresado", Toast.LENGTH_LONG)
                        .show();
            else
                Toast.makeText(ctx, "Error registro duplicado",
                        Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}