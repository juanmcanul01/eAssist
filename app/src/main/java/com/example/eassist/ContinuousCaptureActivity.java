package com.example.eassist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;


/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ContinuousCaptureActivity extends Activity {
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    WebView miVisorWeb;


    private BarcodeCallback callback = new BarcodeCallback() {
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();

            //MC
            //http://eassist360.sytes.net:8000/reservix/checkinoutapp/c93ae12c-1675-48f6-88c1-4d4f6ef35ffd/2020-07-14/22:05/6

            //MainActivity mi= new MainActivity();
            String[] Configuracion = VerificaConfiguracion("");
            //SetConfig(Configuracion[3].toString()+"salida","miarray");
            //if(Configuracion[3].isEmpty()){
            //    barcodeView.setStatusText("Favor de Cargar una Configuracion");
            //    beepManager.playBeepSoundAndVibrate();
            //   return;
            //}
            String uuid = DameColaborador(lastText);
            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();
            if(!uuid.equals("")) {

                String recurso=DameParametro(Configuracion[3],"recurso");
                String server=DameParametro(Configuracion[3],"server");
                //Definir de donde se obtien la fecha y hora hay dos caminos
                //String url="http://eassist360.sytes.net:8000/reservix/checkinoutappnew/"+uuid+"/"+recurso+"/";
                String fragmento=getFecha()+"/"+getHora();
                String url=server+uuid+"/"+fragmento+"/"+recurso+"/";
                SetConfig(url,"testurl");
                //url = result.getText();
                if (URLUtil.isValidUrl(url)) {
                    onPause();
                    //lanzarNavegador(lastText);
                    miVisorWeb = (WebView) findViewById(R.id.visorweb);
                    miVisorWeb.setVisibility(View.VISIBLE);
                    miVisorWeb.setWebViewClient(new WebViewClient());    //the lines of code added
                    miVisorWeb.setWebChromeClient(new WebChromeClient()); //same as above
                    final WebSettings ajustesVisorWeb = miVisorWeb.getSettings();
                    ajustesVisorWeb.setJavaScriptEnabled(true);
                    miVisorWeb.loadUrl(url);

                    esperarYCerrar(8000);
                }
            }

            //Added preview of scanned barcode
            ImageView imageView = findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

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

        public String[] VerificaConfiguracion(String uuid){
            //String result="";
            //SetConfig("toy dentro","dentro1");
            String[] result=new String[4];
            String file = "ConfigAssist";
            String Config=GetConfig(file);
            //SetConfig("sigo dentro","dentro2");

            if (!Config.equals("") && uuid.equals("")) {

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

        public String DameParametro(String Config,String parametro){
            String result="";
            String[] parametros=Config.split(";");
            for(int i=0; i<parametros.length; i++) {
                if(parametros[i].contains(parametro)){
                    String[] valores =parametros[i].split("=");
                    result =valores[1];
                }
            }
            return result;
        }
        /*
        public String GetConfigAPI(String url,String uuid, String token,String file) throws ExecutionException, InterruptedException {
            //MainActivity mi= new MainActivity();
            //mi.getBaseContext();

            requestApi servicioTask= new requestApi(url,uuid,token);
            String valor=servicioTask.execute().get();
            //SetConfig(valor,file);
            return valor;
        }
        */





        public String getHora(){
            SimpleDateFormat formato = new SimpleDateFormat("HH:mm");
            formato.setTimeZone(TimeZone.getTimeZone("America/cancun"));
            Date fechaActual = Calendar.getInstance().getTime();

            String s = formato.format(fechaActual);
            return String.format("%s", s);
        }
        public String getFecha(){
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            formato.setTimeZone(TimeZone.getTimeZone("America/cancun"));
            Date fechaActual = Calendar.getInstance().getTime();

            String s = formato.format(fechaActual);
            return String.format("%s", s);
        }

        public String DameColaborador(String url){
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

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }

        public void esperarYCerrar(int milisegundos) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // acciones que se ejecutan tras los milisegundos
                    cerrarNavegador();
                }
            }, milisegundos);
        }



        public void cerrarNavegador(){
            miVisorWeb.setVisibility(View.INVISIBLE);
            onResume();
        }


    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.continuous_scan);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}

