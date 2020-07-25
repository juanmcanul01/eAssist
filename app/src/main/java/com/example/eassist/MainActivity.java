package com.example.eassist;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

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
                String token = "28eb012ed0b8644d482c62bf1b2cb65a6494a216";
                SetConfig("token", "dentro3");

                requestApi servicioTask = new requestApi(this, url, uuid, token);
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

    public requestApi(Context httpContext,String url, String uuid, String token){
        //this.result=result;
        this.httpContext=httpContext;
        this.url=url;
        this.uuid=uuid;
        this.token=token;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(httpContext, "Procesando Solicitud", "por favor, espere");
    }

    @Override
    protected String doInBackground(Void... voids) {


        String res="";
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url+uuid+"/")
                    .method("GET", null)
                    .addHeader("Authorization", "Token "+token)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            Response response = client.newCall(request).execute();
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

