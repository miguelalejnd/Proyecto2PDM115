package com.gp06.proyecto2pdm115;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class Calendario extends AppCompatActivity {

    CalendarView cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        cal=findViewById(R.id.calendarView);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth){
                String fecha=dayOfMonth+"/"+(month+1)+"/"+year;
                Toast.makeText(Calendario.this, fecha, Toast.LENGTH_LONG).show();
            }
        });
    }
}