package com.example.ao.tabapplication;

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

    ListView lv;
    LineChart lChart1;
    TextView predictionText, date1, date2, date3, date4, date5, date6, date7, max1, max2, max3, max4, max5, max6, max7, min1, min2, min3, min4, min5, min6, min7;
    WeatherArrayAdapter weatherArrayAdapter;
    //Global arrays to save the values for the lv
    String[] cities = new String[NUMBER_CITIES];
    String[] tMax = new String[NUMBER_CITIES];
    String[] tMin = new String[NUMBER_CITIES];
    //Global arrays for prediction data
    String[] postalCodes = new String[NUMBER_CITIES];
    String[] predictionDate = new String[DAYS];
    String[] predictionTmax = new String[DAYS];
    String[] predictionTmin = new String[DAYS];
    String[] skyStates = new String[DAYS];

    Communicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1, container, false);

        lv = (ListView)view.findViewById(R.id.listView);
        lChart1 = (LineChart) view.findViewById(R.id.chart);
        predictionText = (TextView)view.findViewById(R.id.prediction);
        predictionText.setText("Select one item to see the prediction of 7 days");


        date1 = (TextView) view.findViewById(R.id.date1);
        date2 = (TextView) view.findViewById(R.id.date2);
        date3 = (TextView) view.findViewById(R.id.date3);
        date4 = (TextView) view.findViewById(R.id.date4);
        date5 = (TextView) view.findViewById(R.id.date5);
        date6 = (TextView) view.findViewById(R.id.date6);
        date7 = (TextView) view.findViewById(R.id.date7);

        min1 = (TextView) view.findViewById(R.id.min1);
        min2 = (TextView) view.findViewById(R.id.min2);
        min3 = (TextView) view.findViewById(R.id.min3);
        min4 = (TextView) view.findViewById(R.id.min4);
        min5 = (TextView) view.findViewById(R.id.min5);
        min6 = (TextView) view.findViewById(R.id.min6);
        min7 = (TextView) view.findViewById(R.id.min7);

        max1 = (TextView) view.findViewById(R.id.max1);
        max2 = (TextView) view.findViewById(R.id.max2);
        max3 = (TextView) view.findViewById(R.id.max3);
        max4 = (TextView) view.findViewById(R.id.max4);
        max5 = (TextView) view.findViewById(R.id.max5);
        max6 = (TextView) view.findViewById(R.id.max6);
        max7 = (TextView) view.findViewById(R.id.max7);

        DownloadXML taskDownloadXML = new DownloadXML();
        taskDownloadXML.execute(XML_TEMPERATURE_SPAIN);

        communicator = (Communicator) getActivity();

        return view;
    }

    //Class that download the xml with the cities and the temperatures, AsyncTask
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
                WeatherData weatherData = new WeatherData(cities, tMax, tMin);
                weatherArrayAdapter = new WeatherArrayAdapter(getContext(), weatherData.getWeatherList());
                lv.setAdapter(weatherArrayAdapter);
                lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                lv.setOnItemClickListener(Tab1.this);
            }
            else{
                setPrediction();
//                flagPrediction = false;
            }
        }
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Weather w=((Weather)lv.getItemAtPosition(position));
        w.setCheck();

        if(w.getCheck()) {
            int pos= Arrays.asList(cities).indexOf(w.getLocation());
            flagPrediction = true;
            //Download the xml
            predictionText.setText("7 days prediction of " + cities[pos]);
            DownloadXML taskDownloadXML = new DownloadXML();
            taskDownloadXML.execute("https://www.aemet.es/xml/municipios/localidad_" + postalCodes[pos] + ".xml");

            communicator.answer(postalCodes[pos], cities[pos], skyStates[0]);
        }


		weatherArrayAdapter.notifyDataSetChanged();
    }

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
                    Element skyState = (Element) skyStateList.item(0);
                    skyStates[i] = skyState.getAttribute("descripcion");
                    //Recover the temperature bounds
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

    public void setPrediction(){
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        final List<String> dias = new ArrayList<>();
        List<Entry> zone1 = new ArrayList<Entry>();
        List<Entry> zone2 = new ArrayList<Entry>();

        for (int i=0; i<DAYS; i++){
            try {
                dias.add(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[i])).substring(0, 2).toUpperCase());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            zone1.add(new Entry(i,(Integer.parseInt(predictionTmax[i]))));
            zone2.add(new Entry(i,(Integer.parseInt(predictionTmin[i]))));
            System.out.println(predictionDate[i]+":"+predictionTmax[i]+":"+predictionTmin[i]);
        }


//        ArrayList<DataSet> dataSets=new ArrayList<>();
//
//
        LineDataSet dataSet1, dataSet2;
        dataSet1 = new LineDataSet(zone1, "Weather Forecast in Zone1");
        dataSet1.setDrawValues(true);
        dataSet1.setValueTextSize(10);
//
        dataSet1.setColor(Color.RED);
        dataSet1.setCircleColor(Color.RED);
        dataSet1.setCircleHoleColor(Color.RED);
        dataSet1.setCircleRadius(2);
        dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

//
        dataSet2 = new LineDataSet(zone2, "Weather Forecast in Zone2");
        dataSet2.setDrawValues(true);
        dataSet2.setValueTextSize(10);
        //dataSet2.setValueTextColor();

        dataSet2.setColor(Color.BLUE);
        dataSet2.setDrawCircles(false);
//        dataSet2.setCircleColor(Color.BLUE);
//        dataSet2.setCircleHoleColor(Color.BLUE);
//        dataSet2.setCircleRadius(2);
        dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//
//
        lChart1.getDescription().setEnabled(false);
        lChart1.setData(new LineData(dataSet1,dataSet2));
        lChart1.setScaleX(1);

        lChart1.setVisibleXRange(-1,6);
        //lChart1.setXAxisRenderer(new XAxisRenderer());

        XAxis xAxis = lChart1.getXAxis();
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(0){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dias.get((int)value);
            }
        });

        lChart1.invalidate();
    }

//    public void setPrediction(){
//        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            date1.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[0])).substring(0, 1).toUpperCase());
//            min1.setText(predictionTmin[0]);
//            max1.setText(predictionTmax[0]);
//            date2.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[1])).substring(0, 1).toUpperCase());
//            min2.setText(predictionTmin[1]);
//            max2.setText(predictionTmax[1]);
//            date3.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[2])).substring(0, 1).toUpperCase());
//            min3.setText(predictionTmin[2]);
//            max3.setText(predictionTmax[2]);
//            date4.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[3])).substring(0, 1).toUpperCase());
//            min4.setText(predictionTmin[3]);
//            max4.setText(predictionTmax[3]);
//            date5.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[4])).substring(0, 1).toUpperCase());
//            min5.setText(predictionTmin[4]);
//            max5.setText(predictionTmax[4]);
//            date6.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[5])).substring(0, 1).toUpperCase());
//            min6.setText(predictionTmin[5]);
//            max6.setText(predictionTmax[5]);
//            date7.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[6])).substring(0, 1).toUpperCase());
//            min7.setText(predictionTmin[6]);
//            max7.setText(predictionTmax[6]);
//        }
//        catch (java.text.ParseException e){
//            e.printStackTrace();
//        }
//    }

}
