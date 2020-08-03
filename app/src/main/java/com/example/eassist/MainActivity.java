package com.example.eassist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    private int FIND_VIEWS_with_CONTENT_DESCRIPTION;
    public String glbtoken = "2713bdd887de360040fa210a1261653f4935a123";

    //public funcionesGnl fnc= new funcionesGnl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }
    public void scanContinuous(View view) {
        String[] Configuracion = VerificaConfiguracion("");
        //SetConfig(Configuracion[3].toString()+"salida","miarray");
        if(Configuracion[3].equals("")){
            Toast.makeText(this, "Favor de Cargar una Configuracion", Toast.LENGTH_LONG).show();
        }else {
            Intent intent = new Intent(this, ContinuousCaptureActivity.class);
            startActivity(intent);
        }
    }
    public void crearbotones(View view){
        Intent intent = new Intent(this, mi_linear_layout.class);
        startActivity(intent);
    }

    public void contruyeFormulario(View view) {

        ArrayList<int[]> listagpo=new ArrayList<int[]>();
        int[] listradio = new int[2];
        listradio[0]=1;
        listradio[1]=10;
        listagpo.add(listradio);
        for(int i=0; i<listagpo.size();i++){
            int[] radios= listagpo.get(i);
            SetConfig(radios[0]+"valor","salida"+i);
            SetConfig(radios[1]+"valor","salida"+i+1);
        }



    }

    public void crearbotones2(View view){
        String form=getFormulario("1");
        SetConfig(form, "miformulario");
        if(!form.equals("")) {
            setContentView(R.layout.mi_linear_layout);
            LinearLayout layout = (LinearLayout) findViewById(R.id.mi_linear_layout);

            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedor.setOrientation(LinearLayout.VERTICAL);
            //contenedor.setId(getTaskId());
            try {
                JSONObject mJsonObject = new JSONObject(form);
                //JSONArray mJsonArray = new ;
                //JSONArray mJsonArray = new JSONArray(mJson.toString());
                //JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                String pk = mJsonObject.getString("pk");
                String Nombre = mJsonObject.getString("Nombre");

                int inicial=1;
                TextView Titulo = new TextView(this);
                Titulo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Titulo.setText(Nombre);
                Titulo.setId(inicial);
                contenedor.addView(Titulo);

                JSONArray mJsonArraycontenidoformulario = mJsonObject.getJSONArray("contenidoformulario");
                for (int i = 0; i < mJsonArraycontenidoformulario.length(); i++) {
                    JSONObject mJsonObjectcontenidoformulario = mJsonArraycontenidoformulario.getJSONObject(i);
                    String Elemento = mJsonObjectcontenidoformulario.getString("Elemento");
                    String TipoElemento = mJsonObjectcontenidoformulario.getString("TipoElemento");

                    TextView pregunta = new TextView(this);
                    pregunta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pregunta.setText(Elemento);
                    pregunta.setId(i);
                    contenedor.addView(pregunta);
                    int idgpo=i+1;
                    RadioGroup gpo = new RadioGroup(this);
                    gpo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    gpo.setId(idgpo);
                    SetConfig("id "+idgpo, "debug"+i);
                    JSONArray mJsonArraycontenidoopcion = mJsonObjectcontenidoformulario.getJSONArray("contenidoopcion");
                    for (int j = 0; j < mJsonArraycontenidoopcion.length(); j++) {
                        JSONObject mJsonObjectcontenidoopcion = mJsonArraycontenidoopcion.getJSONObject(j);
                        //String NumeroOpcion = mJsonObjectcontenidoopcion.getString("NumeroOpcion");
                        String Opcion = mJsonObjectcontenidoopcion.getString("Opcion");
                        if(TipoElemento.equals("2")){
                            RadioButton op = new RadioButton(this);
                            op.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                            op.setText(Opcion);
                            op.setId(j+1);
                            gpo.addView(op);

                        }
                    }
                    contenedor.addView(gpo);
                }

                layout.addView(contenedor);


            } catch (Throwable t) {
                Log.e("", "Could not parse malformed JSON: \"" + form + "\"");
            }
        }


        //for (int j = 0; j < 3; j++){
            /*
            Button boton = new Button(this);
            boton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            boton.setText("mi Botón " + j);
            boton.setId(j);
            contenedor.addView(boton);
            */

/*
            TextView text =new TextView(this);
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            text.setText("mi text"+j);
            text.setId(j);
            contenedor.addView(text);

            EditText textedit = new EditText(this);
            textedit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            textedit.setText("mi text"+j);
            textedit.setId(j);
            contenedor.addView(textedit);

                 opcion1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                            opcion1.setText(Opcion);
                            opcion1.setId(j);
                            opcion1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mensaje(v);
                                }
                            });

            */
        //}

    }
    public void EnviarFormulario(View view){


         /*
        ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();
        SetConfig(radioButtons.toString(),"radios");
        boolean flag=false;
        CharSequence valor="";
        for (int i=0;i<radioButtons.size();i++){
            RadioButton radio = radioButtons.get(i);
            flag=radio.isChecked();
            if(flag){
                valor=radio.getText();
            }
        }
        if(!flag){
            Toast.makeText(this, "Debe seleccionar una opcion", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, valor, Toast.LENGTH_LONG).show();
        }

         */
    }


     public String getFormulario(String idformulario){
        String result="";
        String url = "http://192.168.1.72:8000/api/getformulario/"+idformulario;
        //String uuid = "47bf9dbb-b415-4347-9289-8d06ab5eba38";
        //String token = "2713bdd887de360040fa210a1261653f4935a123";
        //SetConfig(url, "urlbotones");
        //url+uuid+"/"
        try {
            requestApi servicioTask = new requestApi(this, url, glbtoken,"GET","");
            String valor = servicioTask.execute().get();
            result = valor;
            //SetConfig(result, "urlbotones2");
        }
        catch (Exception e){
            Log.e("Error ApiFormulario",e.toString());
        }
        return result;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {
            case CUSTOMIZED_REQUEST_CODE: {
                Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

        if(result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                Toast.makeText(this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("MainActivity", "Scanned");
            SetConfig(result.getContents().toString(),"scannertest");
            //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            String uuid = DameUUID(result.getContents());
            String[] Configuracion = VerificaConfiguracion(uuid);
            SetConfig(Configuracion[3].toString()+"salida","miarray");
            if(Configuracion[3]==null || Configuracion[3].equals("")){
                Toast.makeText(this, "Favor de Cargar una URL valida", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "Configuracion Cargada Correctamente", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String DameUUID(String url){
        String salida="";
        String[] urldividida= url.split("/");
        for(int i=0; i<urldividida.length; i++){
            //SetConfig(urldividida[i].toString(),"ciclo" + i);
            String[] uuid=urldividida[i].split("-");
            if(uuid.length>4){
                //SetConfig(urldividida[i].toString(),"ciclo" + i);
                salida=urldividida[i];
            }
        }
        return salida;
    }

    public String[] VerificaConfiguracion(String uuid){
        //String result="";
        //SetConfig("toy dentro","dentro1");
        String[] result=new String[4];
        String file = "ConfigAssist";
        String Config=GetConfig(file);
        //SetConfig("sigo dentro","dentro2");

        if (Config.equals("") && !uuid.equals("")) {
            try {
                String url = "http://eassist360.sytes.net:8000/api/getconfig/";
                //String uuid = "47bf9dbb-b415-4347-9289-8d06ab5eba38";
                //String token = "28eb012ed0b8644d482c62bf1b2cb65a6494a216";
                SetConfig("token", "dentro3");
                //url+uuid+"/"
                requestApi servicioTask = new requestApi(this, url+uuid+"/", glbtoken,"GET","");
                String valor = servicioTask.execute().get();
                String nConfig = valor;
                SetConfig(nConfig, "dentro4");
                result = procesaConfig(nConfig);
                if (result[0].equals("0")) {
                    if (!result[3].isEmpty()) {
                        SetConfig(nConfig, file);
                    }
                }
            } catch (Exception e) {
                result[1] = e.toString();
            }


        } else {
            result = procesaConfig(Config);
        }

        return result;
    }

    public String SetFormulario(String json){
        String result="";
        try{
            String url = "http://192.168.1.72:8000/api/setformulario";
            requestApi enviarFormulario = new requestApi(this, url, glbtoken,"POST",json);
            result = enviarFormulario.execute().get();
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }

    public String[] procesaConfig(String config){
        String[] result=new String[4];
        result[0]="";
        result[1]="";
        result[2]="";
        result[3]="";
        if(!config.contains("error")){
            try {

                JSONObject obj = new JSONObject(config);
                result[0]=obj.getString("accion");
                result[1]=obj.getString("idscaner");
                result[2]=obj.getString("esvalido");
                result[3]=obj.getString("configuracion");

            } catch (Throwable t) {
                Log.e("", "Could not parse malformed JSON: \"" + config + "\"");
            }
        }
        return result;
    }

    public String GetConfig(String archivo){
        String texto="";
        try
        {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput(archivo+".txt")));

            texto= fin.readLine();

            fin.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
        return texto;
    }
    public void SetConfig(String texto,String archivo){
        try
        {
            OutputStreamWriter fout=
                    new OutputStreamWriter(
                            openFileOutput(archivo+".txt", Context.MODE_PRIVATE));

            fout.write(texto);
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }


}



class requestApi extends AsyncTask<Void, Void, String> {
    private Context httpContext;
    ProgressDialog progressDialog;
    public String resultadoapi="";
    public String result="";
    public  String url;
    public String uuid="";
    public String token="";
    public String metodo="";
    public String bodyjson="";

    public requestApi(Context httpContext,String url, String token,String metodo,String bodyjson){
        //this.result=result;
        this.httpContext=httpContext;
        this.url=url;
        //this.uuid=uuid;
        this.token=token;
        this.metodo=metodo;
        this.bodyjson=bodyjson;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(httpContext, "Procesando Solicitud", "por favor, espere");
    }

    @Override
    protected String doInBackground(Void... voids) {


        String res="";
        Response response=null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            switch (metodo){
                case "GET":
                    Request requestGet = new Request.Builder()
                            .url(url+"/")
                            .method(metodo, null)
                            .addHeader("Authorization", "Token "+token)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();
                    response = client.newCall(requestGet).execute();
                    break;
                case "POST":
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType,bodyjson);
                    Request requestPost = new Request.Builder()
                            .url(url+"/")
                            .method(metodo, body)
                            .addHeader("Authorization", "Token "+token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    response = client.newCall(requestPost).execute();
                    break;
            }


            //Response response = client.newCall(request).execute();
            res=response.body().string();
        }
        catch (Exception e){
            res=e.toString();
        }
        return  res;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        result=s;
        //Toast.makeText(httpContext,result,Toast.LENGTH_LONG).show();
    }
}

