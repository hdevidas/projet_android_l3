package com.example.hello;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Bitmap.createBitmap;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.colorToHSV;
import static android.graphics.Color.rgb;


public class MainActivity extends AppCompatActivity {

    static Bitmap img;
    ImageView imv;

    void defaultImg(){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        opts.inScaled = false;
        img = BitmapFactory.decodeResource(getResources(), R.drawable.synth, opts);
        imv.setImageBitmap(img);
    }

    static void crossImg(Bitmap img){
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = img.getHeight()/2 -25; y < img.getHeight()/2 +25; y++)
                img.setPixel(x, y, rgb(255, 255, 255));
        }
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = img.getWidth()/2 -25; x < img.getWidth()/2 +25; x++)
                img.setPixel(x, y, rgb(255, 255, 255));
        }
    }

    void toGray(Bitmap img){
        double lum;
        int red;
        int green;
        int blue;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y <img.getHeight(); y++) {
                red = Color.red(img.getPixel(x, y));
                green = Color.green(img.getPixel(x, y));
                blue = Color.blue(img.getPixel(x, y));
                lum = 0.3*red + 0.59*green + 0.11*blue;
                img.setPixel(x, y, rgb((int)lum,(int)lum,(int)lum));
            }
        }
    }


    void colorize(Bitmap img){/*
        int rgb;
        float hsv[] = new float[3];

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y <img.getHeight(); y++) {
                rgb = img.getPixel(x, y);
                RGBToHSV(Color.red(rgb), Color.green(rgb), Color.blue(rgb), hsv);
                hsv[0]=100;
                rgb =
                colorToHSV(col, hsv);

                img.setPixel(x, y, rgb);
            }
        }*/
    }



    void passe_bas(Bitmap img){

        //int noyau[] = {1,2,3,2,1,2,6,8,6,2,3,8,10,8,3,2,6,8,6,2,1,2,3,2,1};
        int noyau[] = {1,2,1,2,3,2,1,2,1};

        int line_size = 3; // sqrt (nb elem de noyau )
        int decade = 1;
        int coefficient = 15; // somme elems noyau
        //int actual_px;
        double sum;
        double r;
        double g;
        double b;
        int counter;



        for (int x = decade; x < img.getWidth()-decade; x++) {
            for (int y = decade; y < img.getHeight()-decade; y++){

                //actual_px = img.getPixel(x,y);
                sum = 0;
                r = 0;
                g = 0;
                b = 0;
                counter = 0;

                for (int y2 = (y - decade); y2 < (y + decade); y2++ ){
                    for (int x2 = (x - decade); x2 < (x + decade); x2++){
                        //System.out.println(x2 + " " + y2);
                        sum = sum + img.getPixel(x2,y2) * noyau[counter];


                        //r = r + (Color.red(img.getPixel(x2,y2)) * noyau[counter]);
                        //g = g + (Color.green(img.getPixel(x2,y2)) * noyau[counter]);
                        //b = b + (Color.blue(img.getPixel(x2,y2)) * noyau[counter]);

                        System.out.println("red: "+r+ ", green: "+g+", blue: "+b);

                        //r = Color.red(img.getPixel(x, y));
                        //g = Color.green(img.getPixel(x, y));
                        //b = Color.blue(img.getPixel(x, y));



                        //sum_r = sum_r + Color.red(img.getPixel(x2,y2)) * noyau[counter];
                        //sum_g = sum_g + Color.green(img.getPixel(x2,y2)) * noyau[counter];
                        //sum_b = sum_b + Color.blue(img.getPixel(x2,y2)) * noyau[counter];
                        counter++;
                    }
                }

                //sum_r = sum_r/coefficient;
                //sum_g = sum_g/coefficient;
                //sum_b = sum_b/coefficient;

                //img.setPixel(x, y, (int)sum);
                //img.setPixel(x, y, rgb((int)sum,(int)sum,(int)sum));

                //r=r/15;
                //g=g/15;
                //b=b/15;

                //sum = 0.3*r + 0.59*g + 0.11*b;
                //sum = sum/15;
                img.setPixel(x, y, rgb((int)sum, (int)sum,(int)sum));


            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.textview);
        imv = (ImageView) findViewById(R.id.imgsynth);

        //Button default
        Button bt_default = findViewById(R.id.bt_default);
        bt_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultImg();
            }
        });

        //Button cross
        Button bt_cross = findViewById(R.id.bt_cross);
        bt_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crossImg(img);
            }
        });

        //Button toGray
        Button bt_toGray = findViewById(R.id.bt_toGray);
        bt_toGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGray(img);
            }
        });

        //Button Colorize
        Button bt_colorize = findViewById(R.id.bt_colorize);
        bt_colorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorize(img);
            }
        });

        //Button PasseBas
        Button bt_passe_bas = findViewById(R.id.bt_passe_bas);
        bt_passe_bas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passe_bas(img);
            }
        });

        //Affichage de l'image
        defaultImg();
        imv.setImageBitmap(img);

        //Affichage taille de l'image
        tv.setText("Taille de l'image: " + img.getWidth() + "px * " + img.getHeight() +"px");
    }
}


