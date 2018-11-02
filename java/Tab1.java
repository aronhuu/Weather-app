package com.example.ao.tabapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by pc on 27/10/2017.
 */

public class Tab1 extends Fragment implements  ListView.OnItemClickListener{
    //Global Constant Values
    final static String XML_TEMPERATURE_SPAIN = "http://www.aemet.es/xml/ccaa/20181026_t_prev_esp.xml";
    final static int NUMBER_CITIES = 200;
    final static int DAYS = 7;

    private boolean flagPrediction;
    String previousCity, actualCity;
    int previousCheckedIndex, lastCheckedIndex;
    boolean isFirst=true;
//    LineDataSet previousDataSet1, previousDataSet2;


    //Elements from the layout
    ListView lv;
    LineChart lChart;
    TextView predictionText;
    WeatherArrayAdapter weatherArrayAdapter;

    //Global arrays to save the values for the lv
    String[] cities = null;
    String[] tMax = new String[NUMBER_CITIES];
    String[] tMin = new String[NUMBER_CITIES];
    Boolean[] checks = new Boolean[NUMBER_CITIES];


    //Global arrays for prediction data
    String[] postalCodes = new String[NUMBER_CITIES];
    String[] predictionDate = new String[DAYS];
    String[] predictionTmax = new String[DAYS];
    List<Entry> previousTmax= new ArrayList<>();
    List<Entry> previousTmin = new ArrayList<>();
    List<Entry> actualTmax = new ArrayList<>();
    List<Entry> actualTmin = new ArrayList<>();
    String[] predictionTmin = new String[DAYS];
    String[] skyStates = new String[DAYS];

    Communicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1, container, false);

        lv = (ListView)view.findViewById(R.id.listView);
        lChart = (LineChart) view.findViewById(R.id.chart);
        lChart.setNoDataText("Please, select a city and wait");
        lChart.setNoDataTextColor(Color.BLACK);
        predictionText = (TextView)view.findViewById(R.id.prediction);
        predictionText.setText("Please wait, downloading city information");

        Arrays.fill(checks, Boolean.FALSE);

//        if(cities==null) {
//            DownloadXML taskDownloadXML = new DownloadXML();
//            taskDownloadXML.execute(XML_TEMPERATURE_SPAIN);
//        }

        communicator = (Communicator) getActivity();

        return view;
    }

    //Class that download the xml with the cities and the temperatures, AsyncTask

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Weather w=((Weather)lv.getItemAtPosition(position));
        w.setCheck();

        if(w.getCheck()) {
            if(!isFirst){
                previousCity=actualCity;
                previousCheckedIndex=lastCheckedIndex;
                previousTmax=actualTmax;
                previousTmin=actualTmin;
            }
            lastCheckedIndex= Arrays.asList(cities).indexOf(w.getLocation());
            checks[lastCheckedIndex]=!checks[lastCheckedIndex];
            flagPrediction = true;
            //Download the prediction xml of the selected city
            actualCity=cities[lastCheckedIndex];
            predictionText.setText("Please wait" );

            DownloadXML taskDownloadXML = new DownloadXML();
            taskDownloadXML.execute("https://www.aemet.es/xml/municipios/localidad_" + postalCodes[lastCheckedIndex] + ".xml");
        }

		weatherArrayAdapter.notifyDataSetChanged();
    }

    //-------------------------Asynctask--------------------------
    private class DownloadXML extends AsyncTask<String, Void, Void> {
        private String contentType = "";
        @Override
        protected Void doInBackground(String... urls) {
            HttpURLConnection urlConnection;
            InputStream is;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                contentType = urlConnection.getContentType();
                if (contentType.toString().contains("xml")) {
                    is = urlConnection.getInputStream();
                    xmlParser(is);
                }
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

            if(!flagPrediction) {
                predictionText.setText("Select one item to see the prediction of 7 days");
                WeatherData weatherData = new WeatherData(checks, cities, tMax, tMin);
                weatherArrayAdapter = new WeatherArrayAdapter(getContext(), weatherData.getWeatherList());
                lv.setAdapter(weatherArrayAdapter);
                lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                lv.setOnItemClickListener(Tab1.this);
            }
            else{
                setPrediction();
                communicator.answer(postalCodes[lastCheckedIndex], cities[lastCheckedIndex], skyStates[0]);
            }
        }
    }

    public void setPrediction(){
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        final List<String> dias = new ArrayList<>();
        actualTmax =  new ArrayList<>();
        actualTmin = new ArrayList<>();
        for (int i=0; i<DAYS; i++){
            try {
                dias.add(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[i])).substring(0, 2).toUpperCase());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            actualTmax.add(new Entry(i,(Integer.parseInt(predictionTmax[i]))));
            actualTmin.add(new Entry(i,(Integer.parseInt(predictionTmin[i]))));
            System.out.println(predictionDate[i]+":"+predictionTmax[i]+":"+predictionTmin[i]);
        }

        lChart.getDescription().setEnabled(false);

        setChart();

        previousCheckedIndex=lastCheckedIndex;

        XAxis xAxis = lChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(0){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dias.get((int)value);
            }
        });

        lChart.setScaleX(1);
        lChart.setVisibleXRange(-1,6);
        lChart.getAxisLeft().setEnabled(false);
        lChart.getAxisLeft().setDrawGridLines(false);
        lChart.getAxisRight().setEnabled(false);
        lChart.setScaleEnabled(false);
        lChart.getLegend().setWordWrapEnabled(true);
        lChart.invalidate();
    }

    public void setChart() {
        if(isFirst){//If it's the first time to plot a prediction
            predictionText.setText("7 days prediction of " + actualCity );
            LineDataSet dataSet1=getDataSet(actualTmax, "Max temp "+actualCity);
            dataSet1.setColor(Color.BLUE);
            dataSet1.setValueTextColor(Color.BLUE);
            LineDataSet dataSet2=getDataSet(actualTmin, "Min temp "+actualCity);
            dataSet2.setColor(Color.MAGENTA);
            dataSet2.setValueTextColor(Color.MAGENTA);
            lChart.setData(new LineData(dataSet1,dataSet2));
            isFirst=false;
        }else {
            LineDataSet dataSet1 = getDataSet(previousTmax, "Max temp " + previousCity);
            dataSet1.setColor(Color.BLUE);
            dataSet1.setValueTextColor(Color.BLUE);
            LineDataSet dataSet2 = getDataSet(previousTmin, "Max temp " + previousCity);
            dataSet2.setColor(Color.MAGENTA);
            dataSet2.setValueTextColor(Color.MAGENTA);

            if (previousCity == actualCity ) {
                predictionText.setText("7 days prediction of " + actualCity);
                lChart.setData(new LineData(dataSet1,dataSet2));
            }
            else{
                LineDataSet dataSet3 = getDataSet(actualTmax, "Max temp " + actualCity);
                dataSet3.setColor(Color.RED);
                dataSet3.setValueTextColor(Color.RED);
                LineDataSet dataSet4 = getDataSet(actualTmin, "Min temp " + actualCity);
                dataSet4.setColor(Color.GRAY);
                dataSet4.setValueTextColor(Color.GRAY);
                predictionText.setText("7 days prediction of " + previousCity + " and " + actualCity);
                lChart.setData(new LineData(dataSet1,dataSet2,dataSet3,dataSet4));
            }

        }
    }

    public LineDataSet getDataSet( List<Entry> data, String label){
        LineDataSet dataSet1;

        dataSet1 = new LineDataSet(data,label);
        dataSet1.setDrawValues(true);
        dataSet1.setValueTextSize(10);
        dataSet1.setDrawCircles(false);
//        if (first) dataSet1.setColor(R.color.TempMaxBlue);
        dataSet1.setColor(R.color.TempMaxRed);
        dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet1.setHighlightEnabled(false);

        return dataSet1;
    }

    //-------------------------xmlParser--------------------------
    //Method that modifies the global arrays with the information of the xml
    public void xmlParser(InputStream is) {
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setExpandEntityReferences(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(is);
            int counter = 0;
            //DOWNLOAD THE XML WITH THE TEMPERATURES OF SPAIN
            if (!flagPrediction) {
                // Process XML document and extract names of cities and the temperatures
                NodeList ccaaList = document.getElementsByTagName("ccaa");
                for (int i = 0; i < ccaaList.getLength(); ++i) {
                    //Each Community
                    Element ccaa = (Element) ccaaList.item(i);
                    //Recover the Provinces
                    NodeList provinicasList = ccaa.getElementsByTagName("provincia");
                    for (int j = 0; j < provinicasList.getLength(); j++) {
                        //Each Province
                        Element provincia = (Element) provinicasList.item(j);
                        //Recover all cities
                        NodeList ciudadesList = provincia.getElementsByTagName("ciudad");
                        for (int k = 0; k < ciudadesList.getLength(); k++) {
                            //Each city
                            Element ciudad = (Element) ciudadesList.item(k);
                            //Recover the city name
                            cities[counter] = ciudad.getAttribute("nombre");
                            //Recover the postal code
                            postalCodes[counter] = ciudad.getAttribute("cod_ine");
                            //Recover tmax
                            NodeList tmaxList = ciudad.getElementsByTagName("tmax");
                            Element tmax = (Element) tmaxList.item(0); // There is just one description item
                            tMax[counter] = tmax.getTextContent().trim();
                            //Recover tmin
                            NodeList tminList = ciudad.getElementsByTagName("tmin");
                            Element tmin = (Element) tminList.item(0); // There is just one description item
                            tMin[counter] = tmin.getTextContent().trim();
                            counter++;
                        }
                    }
                }
            }
            //DOWNLOAD THE PREDICTION DATA
            else{
                //There is just one element
                NodeList prediccionList = document.getElementsByTagName("prediccion");
                Element prediccion = (Element) prediccionList.item(0);
                NodeList diaList = prediccion.getElementsByTagName("dia");
                for (int i = 0; i < diaList.getLength(); ++i) {
                    //Each day
                    Element dia = (Element) diaList.item(i);
                    predictionDate[i] = dia.getAttribute("fecha");
                    //Recover the sky state
                    NodeList skyStateList = dia.getElementsByTagName("estado_cielo");
                    for(int j = 0; (j < skyStateList.getLength()) ;j++) {
                        Element skyState = (Element) skyStateList.item(j);
                        if ((!skyState.getAttribute("descripcion").equals(""))){
                            skyStates[i] = skyState.getAttribute("descripcion");
                            break;
                        }
                        else{
                            if( i == (skyStateList.getLength() -1 )){
                                skyStates[i] ="";
                            }
                        }
                    }
                    //Recover the Provinces
                    NodeList temperaturaList = dia.getElementsByTagName("temperatura");
                    Element temperatura = (Element) temperaturaList.item(0);
                    //Recover the maximun temperature
                    NodeList maximaList = temperatura.getElementsByTagName("maxima");
                    Element maxima = (Element) maximaList.item(0);
                    predictionTmax[i] = maxima.getTextContent().trim();
                    //Recover the minimun temperature
                    NodeList minimaList = temperatura.getElementsByTagName("minima");
                    Element minima = (Element) minimaList.item(0);
                    predictionTmin[i] = minima.getTextContent().trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //Get the cities, tMax, tMin, postalCodes, checks
        String s =sharedPref.getString("cities", null);
        if(s!=null)
            cities=s.replace("[", "").replace("]", "").split(", ");

        s =sharedPref.getString("tMax", null);
        if(s!=null)
            tMax=s.replace("[", "").replace("]", "").split(", ");

        s =sharedPref.getString("tMin", null);
        if(s!=null)
            tMin=s.replace("[", "").replace("]", "").split(", ");

        s =sharedPref.getString("postalCodes", null);
        if(s!=null)
            postalCodes=s.replace("[", "").replace("]", "").split(", ");

        s =sharedPref.getString("checks", null);
        if(s!=null) {
            String [] sa=s.replace("[", "").replace("]", "").split(", ");
            for (int i =0; i<sa.length; i++){
                checks[i]=Boolean.parseBoolean(sa[i]);
            }
        }

        if(cities==null) {
            cities=new String[NUMBER_CITIES];
            DownloadXML taskDownloadXML = new DownloadXML();
            taskDownloadXML.execute(XML_TEMPERATURE_SPAIN);
        }else {
            predictionText.setText("Select one item to see the prediction of 7 days");
            weatherArrayAdapter = new WeatherArrayAdapter(getContext(), new WeatherData(checks, cities, tMax, tMin).getWeatherList());
            lv.setAdapter(weatherArrayAdapter);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setOnItemClickListener(Tab1.this);

            flagPrediction = sharedPref.getBoolean("flagPrediction", false);
//        isFirst=sharedPref.getBoolean("isFirst",false);
            if (flagPrediction) {
                //Get the prediction data
                s = sharedPref.getString("predictionDate", null);
                if (s != null)
                    predictionDate = s.replace("[", "").replace("]", "").split(", ");
                s = sharedPref.getString("predictionTmax", null);
                if (s != null)
                    predictionTmax = s.replace("[", "").replace("]", "").split(", ");
                s = sharedPref.getString("predictionTmin", null);
                if (s != null)
                    predictionTmin = s.replace("[", "").replace("]", "").split(", ");

                String sM = sharedPref.getString("previousTmax", null);
                String sm = sharedPref.getString("previousTmin", null);

                String[] dataMax = sM.replace("[", "").replace("]", "").replace(",", "").split("Entry");
                String[] dataMin = sm.replace("[", "").replace("]", "").replace(",", "").split("Entry");

                for (int i = 0; i < DAYS; i++) {
                    String sy = dataMax[i + 1].split("y: ")[1];
                    String sx = dataMin[i + 1].split("y: ")[1];
                    int y = (int) Float.parseFloat(sy);
                    int x = (int) Float.parseFloat(sx);

                    previousTmax.add(new Entry(i, (y)));
                    previousTmin.add(new Entry(i, (x)));
                    System.out.println(predictionDate[i] + ":" + predictionTmax[i] + ":" + predictionTmin[i]);
                }

                previousCity = sharedPref.getString("previousCity", null);
                actualCity = sharedPref.getString("actualCity", null);
                previousCheckedIndex = sharedPref.getInt("previousCheckedIndex", -1);
                lastCheckedIndex = sharedPref.getInt("lastCheckedIndex", -1);

                s = sharedPref.getString("skyStates", null);
                if (s != null)
                    skyStates = s.replace("[", "").replace("]", "").split(", ");

                if (previousCity == null) isFirst = true;
                else isFirst = false;

                setPrediction();
                communicator.answer(postalCodes[lastCheckedIndex], cities[lastCheckedIndex], skyStates[0]);

            }
        }



    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

//        System.out.println(tab1.weatherArrayAdapter.toString());
        editor.putString("cities", Arrays.toString(cities));
        editor.putString("tMax", Arrays.toString(tMax));
        editor.putString("tMin", Arrays.toString(tMin));
        editor.putString("postalCodes", Arrays.toString(postalCodes));
        editor.putString("checks", Arrays.toString(checks));
        editor.putString("skyStates", Arrays.toString(skyStates));


        editor.putBoolean("flagPrediction",flagPrediction);
        editor.putBoolean("isFirst",isFirst);
        if(flagPrediction){
            editor.putString("predictionDate",Arrays.toString(predictionDate));
            editor.putString("predictionTmax",Arrays.toString(predictionTmax));
            editor.putString("predictionTmin",Arrays.toString(predictionTmin));
            editor.putString("previousCity",previousCity);
            editor.putString("actualCity",actualCity);
            editor.putInt("previousCheckedIndex",previousCheckedIndex);
            editor.putInt("lastCheckedIndex",lastCheckedIndex);
            editor.putString("previousTmax",previousTmax.toString());
            System.out.println(previousTmax.toString());
            editor.putString("previousTmin",previousTmin.toString());
        }
        editor.commit();

    }
}
