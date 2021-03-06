package com.example.hello;

import androidx.appcompat.app.AppCompatActivity;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

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

    //Utilisation sur fonction de convolution
    int noyau[] = new int[]{
            1, 2, 3, 5, 3, 2, 1,
            2, 6, 8, 12, 8, 6, 2,
            3, 8, 10, 15, 10, 8, 3,
            5, 12, 15, 20, 15, 12, 5,
            3, 8, 10, 15, 10, 8, 3,
            2, 6, 8, 12, 8, 6, 2,
            1, 2, 3, 5, 3, 2, 1};

    // ------------------- DEBUT ALGO. ------------------

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

    //------- TD3 --------

    //RESET IMAGE PAR DEFAUT
    void defaultImg(){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        opts.inScaled = false;
        img = BitmapFactory.decodeResource(getResources(), R.drawable.synth, opts);
        imv.setImageBitmap(img);
    }

    //AUGMENTER LE CONTRASTE PAR EXTENSION DE DYNAMIQUE (A APPLIQUER SUR IMAGE EN NIVEAU DE GRIS)
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

    //DIMINUER LE CONTRASTE (SUR IMAGE EN NIVEAU DE GRIS)
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

    //AUGMENTER LE CONTRASTE (VERSION POUR IMAGE EN COULEUR)
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

    //AUGMENTER LE CONTRASTE PAR EGALISATION D'HISTOGRAMME (GRIS)
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

    //AUGMENTER LE CONTRASTE PAR EGALISATION D'HISTOGRAMME (COULEUR)
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

    //INVERSION COULEUR RGB A L'AIDE DE RENDERSCRIPT
    private  void  toInvertRS(Bitmap  bmp) {
        RenderScript  rs = RenderScript.create(this);
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output= Allocation.createTyped(rs , input. getType ());
        ScriptC_invert  invertScript = new  ScriptC_invert(rs);
        invertScript.forEach_toInvert(input , output);
        output.copyTo(bmp);
        input.destroy (); output.destroy ();
        invertScript.destroy (); rs.destroy ();
    }

    //TO GRAY AVEC RENDERSCRIPT
    private  void  toGrayRS(Bitmap  bmp) {
        RenderScript  rs = RenderScript.create(this);
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output= Allocation.createTyped(rs , input. getType ());

        ScriptC_gray  grayScript = new  ScriptC_gray(rs);
        grayScript.forEach_toGray(input , output);

        output.copyTo(bmp);
        input.destroy (); output.destroy ();
        grayScript.destroy (); rs.destroy ();
    }

    //------- TD5 --------

    //FONCTION FILTRE MOYENNEUR (ENCORE DES ERREURS)
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

    // ------------------- FIN ALGO. ------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.textview);
        imv = (ImageView) findViewById(R.id.imgsynth);

        // ----------------------BOUTONS---------------------

        //Bouton default (Remettre l'image par défaut) #0
        Button bt_default = findViewById(R.id.bt_default);
        bt_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultImg();
            }
        });

        //Bouton Gray v1 (codé avec getpixel) #1
        Button bt_gray_1 = findViewById(R.id.bt_gray_1);
        bt_gray_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGray(img);
            }
        });

        //Bouton Gray v2 (codé avec getpixels) #2
        Button bt_gray_2 = findViewById(R.id.bt_gray_2);
        bt_gray_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
            }
        });

        //Bouton Gray v3 (codé avec renderscript) #3
        Button bt_gray_3 = findViewById(R.id.bt_gray_3);
        bt_gray_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayRS(img);
            }
        });

        //Bouton Diminuer Contraste (utilisation uniquement sur image en niveau de gris) #4
        Button bt_contrast_down = findViewById(R.id.bt_contrast_down);
        bt_contrast_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
                contrast_down(img, 100, 156);
            }
        });
        //Bouton Augmenter Contraste (utilisation uniquement sur image en niveau de gris) #5
        Button bt_contrast_up = findViewById(R.id.bt_contrast_up);
        bt_contrast_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
                contrast_up(img);
            }
        });

        //Bouton Augmenter Contraste (utilisation possible sur image en couleurs) #6
        Button bt_contrast_up_color = findViewById(R.id.bt_contrast_up_color);
        bt_contrast_up_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contrast_color(img);
            }
        });

        //Bouton Coloriser (change les couleurs de l'image aléatoirement) #7
        Button bt_colorize = findViewById(R.id.bt_colorize);
        bt_colorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorize(img);
            }
        });

        //Bouton Egalisation d'histogramme (utilisation sur image en niveau de gris) #8
        Button bt_histo_equal = findViewById(R.id.bt_histo_equal);
        bt_histo_equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGrayV2(img);
                histo_equal(img);
            }
        });

        //Bouton Egalisation d'histogramme (utilisation sur image en couleur) #9
        Button bt_histo_equal_color = findViewById(R.id.bt_histo_equal_color);
        bt_histo_equal_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                histo_color(img);
            }
        });

        //Bouton Inversion couleurs (codé avec Renderscript) #10
        Button bt_invert = findViewById(R.id.bt_invert);
        bt_invert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toInvertRS(img);
            }
        });

        //Button Filtre Gaussien (flous) #11
        Button bt_filtre_gaussien = findViewById(R.id.bt_filtre_gaussien);
        bt_filtre_gaussien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtre_gaussien(img,noyau); //définis plus haut
            }
        });

        //Button Detect Contours #12
        Button bt_detect_cont = findViewById(R.id.bt_detect_cont);
        bt_detect_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect_contours(img,15);
            }
        });

        // ------------------- FIN BOUTONS ------------------

        //Affichage de l'image
        defaultImg();
        imv.setImageBitmap(img);

        //Affichage dimensions image
        tv.setText("Taille de l'image: " + img.getWidth() + "px * " + img.getHeight() +"px");
    }
}


