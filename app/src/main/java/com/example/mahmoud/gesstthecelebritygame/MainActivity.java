package com.example.mahmoud.gesstthecelebritygame;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private Random generator = new Random();
    private int locationOfTrue ;
    private int indexOftTrue;


    private Button btn0;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button[] buttons = new Button[4];
    private ImageView imageView;
    private TextView textView;
    private RelativeLayout subrelativeLayout;
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DataTask().execute("http://www.posh24.com/celebrities");

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.ScoreTextVew);
        btn0 = (Button) findViewById(R.id.btn0);
        buttons[0]=btn0;
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        buttons[1]=btn1;
        buttons[2]=btn2;
        buttons[3]=btn3;
        subrelativeLayout = (RelativeLayout) findViewById(R.id.sub);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);


    }

    public void start (View v){
        subrelativeLayout.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);

    }

    private void newGame (){
        indexOftTrue = generator.nextInt(names.size());
        locationOfTrue = generator.nextInt(4);
        for (int i = 0; i < 4 ; i++) {
            if (i == locationOfTrue)
                buttons[locationOfTrue].setText(names.get(indexOftTrue));
            else
                buttons[i].setText(names.get(generator.nextInt(names.size())));
        }
    }


    public void gameEnd(View V){
        if(Integer.valueOf(V.getTag().toString()) == locationOfTrue  ){
            Toast.makeText(this, "correct", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Wrong it was " + names.get(indexOftTrue), Toast.LENGTH_LONG).show();
        }
        newGame();
        new ImageDownloader().execute(links.get(indexOftTrue));

    }

    public class ImageDownloader extends AsyncTask <String, Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap ;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap  = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            imageView.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }

    private class DataTask extends AsyncTask <String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                int data = reader.read();
                String html = "";
                while (data!=-1){
                    html += (char) data;
                    data = reader.read();
                }



                String[] spleted = html.split("<div class=\"sidebarContainer\">");
                html = spleted[0];

                System.out.println(html);


                Pattern p = Pattern.compile("<img src=\"(.*?)\"");
                Matcher m = p.matcher(html);
                while (m.find()){
                    links.add(m.group(1));
                }
                p = Pattern.compile("alt=\"(.*?)\"");
                m = p.matcher(html);
                while (m.find()){
                    names.add(m.group(1));
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            newGame();
            new ImageDownloader().execute(links.get(indexOftTrue));
            super.onPostExecute(s);
        }
    }



}
