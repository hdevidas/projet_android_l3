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
import java.util.Random;

import static android.graphics.Bitmap.createBitmap;
import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.colorToHSV;
import static android.graphics.Color.rgb;
import static android.graphics.Color.valueOf;


public class MainActivity extends AppCompatActivity {

    static Bitmap img;
    ImageView imv;
    //noyau utilisé pour le filtre de convolution (!! DOIT ETRE DE TAILLE 2N+1 * 2N+1 !!)

    /*int noyau[] = new int[]{
            1, 2, 3, 2, 1,
            2, 6, 8, 6, 2,
            3, 8, 10, 8, 3,
            2, 6, 8, 6, 2,
            1, 2, 3, 2, 1};*/

    int noyau[] = new int[]{
            1, 2, 3, 5, 3, 2, 1,
            2, 6, 8, 12, 8, 6, 2,
            3, 8, 10, 15, 10, 8, 3,
            5, 12, 15, 20, 15, 12, 5,
            3, 8, 10, 15, 10, 8, 3,
            2, 6, 8, 12, 8, 6, 2,
            1, 2, 3, 5, 3, 2, 1};


    //------- TD1 --------

    //CONVERTIE L'IMAGE EN NIVEAU DE GRIS AVEC GETPIXEL (NON OPTIMAL)
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

    //CONVERTIE L'IMAGE EN NIVEAU DE GRIS AVEC GETPIXELS (OPTIMAL)
    void toGrayV2(Bitmap img){
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        int red;
        int green;
        int blue;
        int grey;
        int alpha = 0xFF << 24;
        img.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                grey = pixels[w * x + y];
                red = ((grey & 0x00FF0000) >> 16);
                green = ((grey & 0x0000FF00) >> 8);
                blue = (grey & 0x000000FF);
                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[w * x + y] = grey;
            }
        }
        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    //------- TD2 --------

    //DESSINE UNE CROIX (FONCTION D'ENTRAINEMENT)
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

    //COLORISE L'IMAGE EN UNE COULEUR ALEATOIRE
    void colorize(Bitmap img) {
        Random random = new Random();
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        int red;
        int green;
        int blue;
        int color;
        float hsv[] = new float[3];
        int random_col = random.nextInt(254);

        img.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                color = pixels[w * x + y];
                red = ((color & 0x00FF0000) >> 16);
                green = ((color & 0x0000FF00) >> 8);
                blue = (color & 0x000000FF);

                RGBToHSV(red, green, blue, hsv);
                hsv[0] = random_col;
                color = HSVToColor(hsv);
                pixels[w * x + y] = color;
            }
        }

        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    //A FAIRE : fonction de conversion personnelle "RGB/HSV"

    //------- TD3 --------

    //RESET IMAGE PAR DEFAUT
    void defaultImg(){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        opts.inScaled = false;
        img = BitmapFactory.decodeResource(getResources(), R.drawable.synth, opts);
        imv.setImageBitmap(img);
    }

    //AUGMENTER LE CONTRASTE PAR EXTENSION DE DYNAMIQUE (!!A APPLIQUER SUR IMAGE EN NIVEAU DE GRIS!!)
    void contrast_up(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);

        int max_value = ((pixels[0] & 0x00FF0000) >> 16);
        int min_value = ((pixels[0] & 0x00FF0000) >> 16);

        for (int x = 0; x < w; x++) {
            for (int y = 1; y < h; y++) {
                if(((pixels[w*x+y] & 0x00FF0000) >> 16) > max_value){
                    max_value = ((pixels[w*x+y] & 0x00FF0000) >> 16);
                }
                if(((pixels[w*x+y] & 0x00FF0000) >> 16) < min_value){
                    min_value = ((pixels[w*x+y] & 0x00FF0000) >> 16);
                }
            }
        }
        double red;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                red = (double)255 / (max_value - min_value) * (((pixels[w*x+y] & 0x00FF0000) >> 16) - min_value);
                pixels[w * x + y] = ((int)red & 0xff) << 16 | ((int)red & 0xff) << 8 | ((int)red & 0xff);
            }
        }
        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    void contrast_down(Bitmap img,int min_value, int max_value) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);
        double red;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                red = ((pixels[w*x+y] & 0x00FF0000) >> 16) / (double)255 * (max_value - min_value) + min_value;
                pixels[w * x + y] = ((int)red & 0xff) << 16 | ((int)red & 0xff) << 8 | ((int)red & 0xff);
            }
        }
        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    void contrast_color(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);

        int max_value_r = ((pixels[0] & 0x00FF0000) >> 16);
        int min_value_r = ((pixels[0] & 0x00FF0000) >> 16);

        int max_value_g = ((pixels[0] & 0x0000FF00) >> 8);
        int min_value_g = ((pixels[0] & 0x0000FF00) >> 8);

        int max_value_b = (pixels[0] & 0x000000FF);
        int min_value_b = (pixels[0] & 0x000000FF);

        for (int x = 0; x < w; x++) {
            for (int y = 1; y < h; y++) {
                if(((pixels[w*x+y] & 0x00FF0000) >> 16) > max_value_r){
                    max_value_r = ((pixels[w*x+y] & 0x00FF0000) >> 16);
                }
                if(((pixels[w*x+y] & 0x00FF0000) >> 16) < min_value_r){
                    min_value_r = ((pixels[w*x+y] & 0x00FF0000) >> 16);
                }
                if(((pixels[w*x+y] & 0x0000FF00) >> 8) > max_value_g){
                    max_value_g = ((pixels[w*x+y] & 0x0000FF00) >> 8);
                }
                if(((pixels[w*x+y] & 0x0000FF00) >> 8) < min_value_g){
                    min_value_g = ((pixels[w*x+y] & 0x0000FF00) >> 8);
                }
                if((pixels[w*x+y] & 0x000000FF) > max_value_b){
                    max_value_b = (pixels[w*x+y] & 0x000000FF);
                }
                if((pixels[w*x+y] & 0x000000FF) < min_value_b){
                    min_value_b = (pixels[w*x+y] & 0x000000FF);
                }
            }
        }

        double red;
        double green;
        double blue;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                red = (double)255 / (max_value_r - min_value_r) * (((pixels[w*x+y] & 0x00FF0000) >> 16) - min_value_r);
                green = (double)255 / (max_value_g - min_value_g) * (((pixels[w*x+y] & 0x0000FF00) >> 8) - min_value_g);
                blue = (double)255 / (max_value_b - min_value_b) * ((pixels[w*x+y] & 0x000000FF) - min_value_b);
                pixels[w * x + y] = ((int)red & 0xff) << 16 | ((int)green & 0xff) << 8 | ((int)blue & 0xff);
            }
        }
        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    //AUGMENTER LE CONTRASTE PAR EGALISATION D'HISTOGRAMME
    void histo_equal(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] histo = new int[256];
        int nb_px = w*h;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
               histo[((pixels[w*x+y] & 0x00FF0000) >> 16)] = histo[((pixels[w*x+y] & 0x00FF0000) >> 16)] +1;
            }
        }
        for (int i = 1; i < 256; i++) {
            histo[i] = histo[i] + histo[i-1];
        }

        int rouge; //compris entre 0 et 255
        double histo_rouge;
        double red;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                rouge = (pixels[w*x+y] & 0x00FF0000) >> 16;
                histo_rouge = (double)(histo[rouge] * 255);
                red = Math.round(histo_rouge / nb_px);
                pixels[w * x + y] = ((int)red & 0xff) << 16 | ((int)red & 0xff) << 8 | ((int)red & 0xff);
            }
        }

        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }


    //AUGMENTER LE CONTRASTE PAR EGALISATION D'HISTOGRAMME EN COULEUR
    void histo_color(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] histo_r = new int[256];
        int[] histo_g = new int[256];
        int[] histo_b = new int[256];
        int nb_px = w*h;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                histo_r[((pixels[w*x+y] & 0x00FF0000) >> 16)] = histo_r[((pixels[w*x+y] & 0x00FF0000) >> 16)] +1;
                histo_g[((pixels[w*x+y] & 0x0000FF00) >> 8)] = histo_g[((pixels[w*x+y] & 0x0000FF00) >> 8)] +1;
                histo_b[((pixels[w*x+y] & 0x000000FF))] = histo_b[((pixels[w*x+y] & 0x000000FF))] +1;
            }
        }
        for (int i = 1; i < 256; i++) {
            histo_r[i] = histo_r[i] + histo_r[i-1];
            histo_g[i] = histo_g[i] + histo_g[i-1];
            histo_b[i] = histo_b[i] + histo_b[i-1];
        }

        double histo_red;
        double r;
        double histo_green;
        double g;
        double histo_blue;
        double b;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                histo_red = (double)(histo_r[(pixels[w*x+y] & 0x00FF0000) >> 16] * 255);
                r = Math.round(histo_red / nb_px);
                histo_green = (double)(histo_g[(pixels[w*x+y] & 0x0000FF00) >> 8] * 255);
                g = Math.round(histo_green / nb_px);
                histo_blue = (double)(histo_b[(pixels[w*x+y] & 0x000000FF)] * 255);
                b = Math.round(histo_blue / nb_px);
                pixels[w * x + y] = ((int)r & 0xff) << 16 | ((int)g & 0xff) << 8 | ((int)b & 0xff);
            }
        }

        img.setPixels(pixels, 0, w, 0, 0, w, h);
    }


    //------- TD4 --------
    //RENDERSCRIPT




    //------- TD5 --------

    //FONCTION FILTRE MOYENNEUR
    void filtre_moyenneur(Bitmap img, int size){
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        int[] pixels_new = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);

        int offset = (size-1) /2;

        int coefficient = offset * 2 +1 ;
        double r;
        double g;
        double b;
        int counter;

        for (int x = offset; x < img.getWidth()-offset; x++) {
            for (int y = offset; y < img.getHeight()-offset; y++){

                r = 0;
                g = 0;
                b = 0;
                counter = 0;

                for (int x2 = (x - offset); x2 <= (x + offset); x2++ ){
                    for (int y2 = (y - offset); y2 <= (y + offset); y2++){
                        r = r + ((pixels[w*x2+y2] & 0x00FF0000) >> 16) ;
                        g = g + ((pixels[w*x2+y2] & 0x0000FF00) >> 8) ;
                        b = b + ((pixels[w*x2+y2] & 0x000000FF));

                        counter++;
                    }
                }

                r = r/coefficient;
                g = g/coefficient;
                b = b/coefficient;

                pixels_new[w*x+y] = ((int)r & 0xff) << 16 | ((int)g & 0xff) << 8 | ((int)b & 0xff);



            }
        }

        img.setPixels(pixels_new, 0, w, 0, 0, w, h);

    }

    //FONCTION FILTRE GAUSSIEN
    void filtre_gaussien(Bitmap img, int[] noyau){
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        int[] pixels_new = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);

        double line_size = Math.sqrt((noyau.length));
        int offset = (int) ((line_size -1) /2);

        int coefficient = 0;
        for (int value : noyau) {
            coefficient += value;
        }

        double r;
        double g;
        double b;
        int counter;

        for (int x = offset; x < img.getWidth()-offset; x++) {
            for (int y = offset; y < img.getHeight()-offset; y++){

                r = 0;
                g = 0;
                b = 0;
                counter = 0;

                for (int x2 = (x - offset); x2 <= (x + offset); x2++ ){
                    for (int y2 = (y - offset); y2 <= (y + offset); y2++){
                        r = r + ((pixels[w*x2+y2] & 0x00FF0000) >> 16) * noyau[counter];
                        g = g + ((pixels[w*x2+y2] & 0x0000FF00) >> 8) * noyau[counter];
                        b = b + ((pixels[w*x2+y2] & 0x000000FF)) * noyau[counter];

                        counter++;
                    }
                }

                r = r/coefficient;
                g = g/coefficient;
                b = b/coefficient;

                pixels_new[w*x+y] = ((int)r & 0xff) << 16 | ((int)g & 0xff) << 8 | ((int)b & 0xff);



            }
        }

        img.setPixels(pixels_new, 0, w, 0, 0, w, h);

    }


    //FONCTION DE DETECTION DE CONTOURS
    void detect_contours(Bitmap img, int coefficient){
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        int[] new_pixels = new int[w * h];
        img.getPixels(pixels, 0, w, 0, 0, w, h);

        int offset = (coefficient -1) /2;

        double gx;
        double gy;
        double g_xy;

        for (int x = offset; x < img.getWidth()-offset; x++) {
            for (int y = offset; y < img.getHeight()-offset; y++){
                gx = 0;
                for (int y2 = (y - offset); y2 <= (y + offset); y2++ ){
                    gx = gx + ((pixels[y2 + (x+1)*w] & 0x00FF0000) >> 16) - ((pixels[y2 + (x-1)*w] & 0x00FF0000) >> 16);
                }
                gx = gx/coefficient;

                gy = 0;
                for (int x2 = (x - offset); x2 <= (x + offset); x2++ ){
                    gy = gy + ((pixels[w*x2 +y+1] & 0x00FF0000) >> 16) - ((pixels[w*x2 +y-1] & 0x00FF0000) >> 16);
                }
                gy = gy/coefficient;

                g_xy=(gy+gx)/2;
                new_pixels[w*x+y] = ((int)gx & 0xff) << 16 | ((int)gx & 0xff) << 8 | ((int)gx & 0xff);
                new_pixels[w*x+y] = ((int)g_xy & 0xff) << 16 | ((int)g_xy & 0xff) << 8 | ((int)g_xy & 0xff);
            }
        }

        img.setPixels(new_pixels, 0, w, 0, 0, w, h);

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

        //Button toGrayV2
        Button bt_toGrayV2 = findViewById(R.id.bt_toGrayV2);
        bt_toGrayV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
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

        //Button Augmenter Contraste
        Button bt_contrast_up = findViewById(R.id.bt_contrast_up);
        bt_contrast_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
                contrast_up(img);
            }
        });

        //Button Diminuer Contraste
        Button bt_contrast_down = findViewById(R.id.bt_contrast_down);
        bt_contrast_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
                contrast_down(img, 100, 156);
            }
        });

        //Button Augmenter Contraste EN COULEURS
        Button bt_contrast_color = findViewById(R.id.bt_contrast_color);
        bt_contrast_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contrast_color(img);
            }
        });

        //Button Augmenter Contraste
        Button bt_histo_equal = findViewById(R.id.bt_histo_equal);
        bt_histo_equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
                histo_equal(img);
            }
        });

        //Button Augmenter Contraste en couleur
        Button bt_histo_color = findViewById(R.id.bt_histo_color);
        bt_histo_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                histo_color(img);
            }
        });

        //Button FiltreMoyenneur
        Button bt_filtre_moy = findViewById(R.id.bt_filtre_moy);
        bt_filtre_moy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtre_moyenneur(img, 5); // impair
            }
        });

        //Button Filtre Gaussien
        Button bt_filtre_gaussien = findViewById(R.id.bt_filtre_gaussien);
        bt_filtre_gaussien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtre_gaussien(img,noyau); //définis plus haut
            }
        });

        //Button Detect Contours
        Button bt_detect_cont = findViewById(R.id.bt_detect_cont);
        bt_detect_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect_contours(img,17);
            }
        });





        //Affichage de l'image
        defaultImg();
        imv.setImageBitmap(img);

        //Affichage taille de l'image
        tv.setText("Taille de l'image: " + img.getWidth() + "px * " + img.getHeight() +"px");
    }
}


