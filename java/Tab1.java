package com.example.ao.tabapplication;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab1, container, false);

        lv = (ListView)view.findViewById(R.id.listView);
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
        String items = "";
        SparseBooleanArray checked= lv.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if ( checked.valueAt(i) ) {
                int pos = checked.keyAt(i);

//                View childView = getViewByPosition(pos,lv);
//                ImageView imageView = childView.findViewById(R.id.check);
//                imageView.setImageResource(R.drawable.selected);

                TextView tv = (TextView) lv.getChildAt(pos).findViewById(R.id.location);
                items = items + "\n " +tv.getText().toString();
                // Getting the Container Layout of the ListView
                ImageView imageView =lv.getChildAt(pos).findViewById(R.id.check);
                imageView.setImageResource(R.drawable.selected);

                //predictionText.setText( items );
                flagPrediction = true;
                //Download the xml
                predictionText.setText("7 days prediction of " + cities[position]);
                DownloadXML taskDownloadXML = new DownloadXML();
                taskDownloadXML.execute("https://www.aemet.es/xml/municipios/localidad_" + postalCodes[position] + ".xml");
            }else{
                ImageView imageView =lv.getChildAt(checked.keyAt(i)).findViewById(R.id.check);
                imageView.setImageResource(R.drawable.non_selected);
//                int pos = checked.keyAt(i);
//                View childView = getViewByPosition(pos,lv);
//                ImageView imageView = childView.findViewById(R.id.check);
//                imageView.setImageResource(R.drawable.non_selected);
            }
        }
    }

//    public View getViewByPosition(int pos, ListView listView) {
//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
//            return listView.getAdapter().getView(pos, null, listView);
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//            return listView.getChildAt(childIndex);
//        }
//    }

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

    public void setPrediction(){
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[0])).substring(0, 1).toUpperCase());
            min1.setText(predictionTmin[0]);
            max1.setText(predictionTmax[0]);
            date2.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[1])).substring(0, 1).toUpperCase());
            min2.setText(predictionTmin[1]);
            max2.setText(predictionTmax[1]);
            date3.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[2])).substring(0, 1).toUpperCase());
            min3.setText(predictionTmin[2]);
            max3.setText(predictionTmax[2]);
            date4.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[3])).substring(0, 1).toUpperCase());
            min4.setText(predictionTmin[3]);
            max4.setText(predictionTmax[3]);
            date5.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[4])).substring(0, 1).toUpperCase());
            min5.setText(predictionTmin[4]);
            max5.setText(predictionTmax[4]);
            date6.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[5])).substring(0, 1).toUpperCase());
            min6.setText(predictionTmin[5]);
            max6.setText(predictionTmax[5]);
            date7.setText(new SimpleDateFormat("EEEE").format(inFormat.parse(predictionDate[6])).substring(0, 1).toUpperCase());
            min7.setText(predictionTmin[6]);
            max7.setText(predictionTmax[6]);
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
        }
    }

}
