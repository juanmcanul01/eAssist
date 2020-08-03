package com.example.eassist;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class mi_linear_layout extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mi_linear_layout);
        String form=getFormulario("4");
        if(!form.equals("")) {
            int cont=10;
            LinearLayout layout = (LinearLayout) findViewById(R.id.mi_linear_layout);
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setId(cont);

            final ArrayList<TextView> listaTextView =new ArrayList<TextView>();
            final ArrayList<RadioGroup> listaradiogpo=new ArrayList<RadioGroup>();
            final ArrayList<Integer> listarespuestapk=new ArrayList<Integer>();
            final ArrayList<int[]> listagpo=new ArrayList<int[]>();
            int[] listradio;



            try {
                JSONObject mJsonObject = new JSONObject(form);
                final int pk = mJsonObject.getInt("pk");
                String Nombre = mJsonObject.getString("Nombre");

                int inicial=20;
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
                    int respuestapk = mJsonObjectcontenidoformulario.getInt("pk");
                    TextView pregunta = new TextView(this);
                    int idtext=i+100;
                    pregunta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pregunta.setText(Elemento);
                    pregunta.setId(idtext);
                    listaTextView.add(pregunta);
                    listarespuestapk.add(respuestapk);
                    contenedor.addView(pregunta);

                    RadioGroup gpo = new RadioGroup(this);
                    gpo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


                    JSONArray mJsonArraycontenidoopcion = mJsonObjectcontenidoformulario.getJSONArray("contenidoopcion");
                    listradio = new int[mJsonArraycontenidoopcion.length()];
                    for (int j = 0; j < mJsonArraycontenidoopcion.length(); j++) {
                        JSONObject mJsonObjectcontenidoopcion = mJsonArraycontenidoopcion.getJSONObject(j);
                        //String NumeroOpcion = mJsonObjectcontenidoopcion.getString("NumeroOpcion");
                        String Opcion = mJsonObjectcontenidoopcion.getString("Opcion");
                        listradio[j]=mJsonObjectcontenidoopcion.getInt("pk");
                        if(TipoElemento.equals("2")){
                            RadioButton op = new RadioButton(this);
                            op.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                            op.setText(Opcion);
                            op.setId(j);
                            gpo.addView(op);

                        }
                    }
                    listagpo.add(listradio);
                    listaradiogpo.add(gpo);
                    contenedor.addView(gpo);
                }

                final Button boton = new Button(this);
                //boton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                boton.setGravity(Gravity.CENTER);
                boton.setText("Enviar");
                //boton.setId(view.getId());

                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton radioButton;
                        TextView respuesta;

                        //creamos respuesta
                        JSONObject principal = new JSONObject(); //principal
                        try {
                            principal.put("idCliente", 1);
                            principal.put("Creacion", "2020-07-28T23:34:46");
                            principal.put("idFormulario", pk);
                            principal.put("UltimaActualizacion", "2020-07-28T23:34:46");
                            principal.put("Completo", true);

                            JSONObject[] respuestacontenidoformulario = new JSONObject[listaTextView.size()];
                            JSONArray respuestacontenidoformularioArray = new JSONArray();
                            for (int i=0; i<listaTextView.size(); i++) {
                                respuesta= listaTextView.get(i);
                                respuestacontenidoformulario[i]=new JSONObject();
                                respuestacontenidoformulario[i].put("idElemento",listarespuestapk.get(i));
                                RadioGroup gporadio= listaradiogpo.get(i);
                                int[] listapkradio=listagpo.get(i);

                                //int select= gporadio.getCheckedRadioButtonId();
                                JSONObject[] respuestaopcioncontenidoformulario = new JSONObject[gporadio.getChildCount()];
                                JSONArray respuestaopcioncontenidoformularioArray = new JSONArray();
                                int numradio=gporadio.getChildCount();
                                int radiocheck=gporadio.getCheckedRadioButtonId();
                                String ValorRespuesta="";
                                for(int j=0;j<numradio;j++){
                                    //SetConfig("hola4","entre_principal");
                                    radioButton = (RadioButton) findViewById(j);

                                    respuestaopcioncontenidoformulario[j]=new JSONObject();
                                    respuestaopcioncontenidoformulario[j].put("idOpcion",listapkradio[j]);


                                    if(radioButton.getId()==radiocheck){
                                        respuestaopcioncontenidoformulario[j].put("Seleccion",1);
                                        ValorRespuesta=radioButton.getText().toString();
                                    }
                                    else{
                                        respuestaopcioncontenidoformulario[j].put("Seleccion",0);
                                    }
                                    respuestaopcioncontenidoformularioArray.put(respuestaopcioncontenidoformulario[j]);
                                }
                                JSONObject respuestaopcioncontenidoformularioObj = new JSONObject();
                                respuestaopcioncontenidoformularioObj.put("respuestaopcioncontenidoformulario", respuestaopcioncontenidoformularioArray);
                                respuestacontenidoformulario[i].put("Respuesta",ValorRespuesta); //aqui modifico
                                respuestacontenidoformulario[i].put("respuestaopcioncontenidoformulario", respuestaopcioncontenidoformularioArray);
                                respuestacontenidoformularioArray.put(respuestacontenidoformulario[i]);
                            }
                            principal.put("respuestacontenidoformulario",respuestacontenidoformularioArray);
                            String jsonStr = principal.toString();

                            //SetConfig(jsonStr,"mijsonreturn_final");
                            String res=SetFormulario(jsonStr);
                            //SetConfig(res,"res_final");
                            finish();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            SetConfig(e.toString(),"mijsonreturn_error");
                        }

                        /*
                        for (int i=0; i<listaradiogpo.size(); i++){
                            try {
                                RadioGroup gporadio = listaradiogpo.get(i);
                                //int idgrupo=gporadio.getId();
                                //int uno = gporadio.getChildCount();
                                int idradio= gporadio.getCheckedRadioButtonId();
                                radioButton = (RadioButton) findViewById(idradio);
                                //TextView text = listaTextView.get(i);
                                //int idtext=text.getId();

                                //RadioButton rd= gporadio.;
                                SetConfig(radioButton.getText().toString(),"valorradio");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                SetConfig(e.toString(),"mifor_error");
                            }
                        }
                        */

                        //enviar(listaTextView);
                        //enviar(v);
                        //Toast.makeText(this,"Salida", Toast.LENGTH_LONG).show();
                    }
                });


                contenedor.addView(boton);
                layout.addView(contenedor);

            } catch (Throwable t) {
                Log.e("", "Could not parse malformed JSON: \"" + form + "\"");
            }
        }
    }

    public void crearbotones_old(final View view){

        String form=getFormulario("1");
        SetConfig(form, "miformulario");
        if(!form.equals("")) {
            int cont=1;

            LinearLayout layout = (LinearLayout) findViewById(R.id.mi_linear_layout);

            final LinearLayout contenedor = new LinearLayout(this);
            contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setId(cont);

            final ArrayList<TextView> listaTextView =new ArrayList<TextView>();
            ArrayList<RadioGroup> listaradiogpo=new ArrayList<RadioGroup>();
            try {
                JSONObject mJsonObject = new JSONObject(form);
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
                    listaTextView.add(pregunta);
                    contenedor.addView(pregunta);

                    RadioGroup gpo = new RadioGroup(this);
                    gpo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    listaradiogpo.add(gpo);

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

                Button boton = new Button(this);
                boton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                boton.setText("Enviar auto");
                boton.setId(view.getId());
                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //enviame(view);
                        //enviar(listaTextView);
                        //enviar(v);
                        //Toast.makeText(this,, Toast.LENGTH_LONG).show();
                    }
                });
                contenedor.addView(boton);



                layout.addView(contenedor);

            } catch (Throwable t) {
                Log.e("", "Could not parse malformed JSON: \"" + form + "\"");
            }
        }

        /*
        setContentView(R.layout.mi_linear_layout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.mi_linear_layout);
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contenedor.setOrientation(LinearLayout.VERTICAL);


        RadioGroup gpo = new RadioGroup(this);
        gpo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));



        for (int j = 0; j < 6; j++){

            Button boton = new Button(this);
            boton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            boton.setText("mi Botón " + j);
            boton.setId(j);
            contenedor.addView(boton);



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

            RadioButton op = new RadioButton(this);
            //opcion1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            op.setText("radio "+j);
            //opcion1.setId(view.getId());
            gpo.addView(op);



        }
        //gpo.clearCheck();
        contenedor.addView(gpo);
        layout.addView(contenedor);
        */


    }
}
