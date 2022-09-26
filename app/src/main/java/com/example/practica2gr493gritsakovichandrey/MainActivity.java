package com.example.practica2gr493gritsakovichandrey;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;

public class MainActivity extends AppCompatActivity
{
    Spinner spinnerPotok;
    public int potok;
    public int blur=3;

    SurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        String[] countPotok = {"1","2","3","4","5","6","7","8"};
        spinnerPotok = findViewById(R.id.spinner);
        ArrayAdapter<Integer> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countPotok);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPotok.setAdapter(adapter);
        SeekBar seekBar = findViewById(R.id.seekBar);
        TextView textSeekbar = findViewById(R.id.textView2);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                textSeekbar.setText("Уровень размытость: " + String.valueOf(i));
                blur=i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });//Размытось
    }

    class Worker implements Runnable
    {
        public int y0;
        public  int y1;

        public  int w;
        public int h;

        public Bitmap bmp;
        public Bitmap res;

        public void run()
        {

            for(int y = 0; y < h; y++)
                for(int x = 0; x < w; x++)
                {
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for(int v=0; v<blur; v++)
                        for (int u=0;u<blur;u++)
                        {
                            int px = u + x - blur/2;
                            int py = v + y - blur/2;

                            if (px < 0) px = 0;
                            if (py < 0) py = 0;
                            if (px >= w) px = w-1;
                            if (py >= h) py = h-1;

                            int c=bmp.getPixel(px,py);

                            red += Color.red(c);
                            green += Color.green(c);
                            blue += Color.blue(c);

                        }

                    red /= blur * blur;
                    green /= blur * blur;
                    blue /= blur * blur;

                    res.setPixel(x,y,Color.rgb(red,green,blue));

                }
        }
    }


    public void myFunc(View v)
    {
        potok = spinnerPotok.getSelectedItemPosition()+1;//ПОТОКИ

        Bitmap bmp= BitmapFactory.decodeResource(getResources(),R.drawable.pony);
        bmp=Bitmap.createScaledBitmap(bmp,256,256,false);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap res = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);


        Thread[] t= new Thread[potok];
        Worker[] r =new Worker[potok];

        int s= h/t.length;

        for (int i = 0; i < potok ; i++)
        {
            r[i]=new Worker();
            r[i].bmp=bmp;
            r[i].res=res;
            r[i].w=w;
            r[i].h=h;
            r[i].y0 = s * i;
            r[i].y1 = r[i].y0 + s;
            t[i]=new Thread(r[i]);
            t[i].start();
        }

        for (int i=0; i<potok;i++)
        {
            try
            {
                t[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        surfaceView.setForeground(new BitmapDrawable(res));
    }
}


