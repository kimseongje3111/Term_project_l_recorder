package com.example.seongje.l_recorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by SEONGJE on 2017-12-05.
 */

public class NaverSearch extends AppCompatActivity {

    TextView text;
    Button search_result;

    XmlPullParser xxp;
    private final String C_ID = "hH4IJW7bsU3bsfEKKCvQ";
    private final String C_SECRET_ID = "bwvX2yrahh";
    String data;
    String seach_string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_activity);

        text = (TextView) findViewById(R.id.dictionary);
        search_result = (Button) findViewById(R.id.search_result);

        Intent intent = getIntent();
        seach_string = intent.getStringExtra("dic_search");

        /*
        search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText(getXmlData());
            }
        });
        */
    }

    public void mOnClick(View v){

        switch (v.getId()){
            case R.id.search_result:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = getXmlData();
                        final StringBuffer buffer = new StringBuffer();

                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray array = (JSONArray)jsonObject.get("items");

                            for(int i = 0; i < array.length(); i++){
                                JSONObject entity = (JSONObject)array.get(i);
                                String title = (String)entity.get("title");
                                String summary = (String)entity.get("description");

                                buffer.append((i+1) +". >>>>>>>> "+ title);
                                buffer.append("\n");
                                buffer.append("- " + summary);
                                buffer.append("\n\n");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(buffer.toString());
                            }

                        });
                    }
                }).start();

                break;
        }

    }

    private String getXmlData(){
        //StringBuffer buffer = new StringBuffer();
        StringBuffer response = new StringBuffer();

        try{

            String location = URLEncoder.encode(seach_string,"UTF-8");

            String queryUrl = "https://openapi.naver.com/v1/search"
                    + "/encyc?query="+ location
                    + "&display=8"
                    + "&start=1";

            URL url = new URL(queryUrl);


            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", C_ID);
            con.setRequestProperty("X-Naver-Client-Secret", C_SECRET_ID);


            int responseCode = con.getResponseCode();

            BufferedReader br;

            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            //JSONObject jsonObject = new JSONObject(br.toString());
            //String j_title = jsonObject.getString("title");
            //String j_summary = jsonObject.getString("description");

            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }

            br.close();

            /*
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is,"UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case  XmlPullParser.START_DOCUMENT:
                        buffer.append("start NAVER XML parsing... \n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if(tag.equals("title")){
                            buffer.append("사전정의 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag.equals("description")){
                            buffer.append("설명 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case  XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if(tag.equals("title")){
                            buffer.append("\n");
                        }

                        break;
                }

                eventType = xpp.next();
            }
            */
        }catch (Exception e){
            e.printStackTrace();
        }

        //buffer.append("end NAVER XML parsing... \n");
        //return buffer.toString();

        return response.toString();
    }

}
