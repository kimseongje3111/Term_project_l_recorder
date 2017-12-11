package com.example.seongje.l_recorder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class TranslateActivity extends AppCompatActivity {

    private static final String TAG = TranslateActivity.class.getSimpleName();
    private static final String CLIENT_ID = "RzL7EzfiAHchBtDIjrA2";

    private NaverRecognizer naverRecognizer;
    private RecognitionHandler handler;
    private AudioWriterPCM writer;

    TextView txtResult;
    Button btnStart;
    Button btnNext;

    String mResult;

    String title = "recorded";
    String filename = "recorded";
    String inputValue;
    String rename = "";

    MediaPlayer player;
    MediaRecorder recorder;

    int playbackPosition = 0;
    //ArrayList<TranslateItems> item = new ArrayList<TranslateItems>();
    ArrayList<TranslateItems> divideItems = new ArrayList<TranslateItems>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity);

        Intent intent = getIntent();
        title = intent.getExtras().getString("title");

        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnNext = (Button) findViewById(R.id.btn_next);

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_listening);
                    naverRecognizer.recognize();

                } else {

                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);
                    naverRecognizer.getSpeechRecognizer().stop();

                }

            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = divideItem(txtResult.getText().toString());
                if(result){
                    Intent intent = new Intent(TranslateActivity.this, SearchActivity.class);
                    intent.putExtra("text_content", txtResult.getText().toString());
                    intent.putExtra("title", title);
                    intent.putParcelableArrayListExtra("devideItems", divideItems);
                    startActivity(intent);
                    //finish();
                }

            }
        });

    }

    // Handle speech recognition Messages.

    private void handleMessage(Message msg) {

        switch (msg.what) {

            case R.id.clientReady:
                // Now an user can speak.
                Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();

                PlayRecord();
                txtResult.setText("connected");

                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/l_recorder");
                writer.open("Test_naverspeech");

                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);

                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);

                break;

            case R.id.finalResult:
                /*
                String[] results = (String[]) msg.obj;
                mResult = results[0];
                txtResult.setText(mResult);
                item.add(new TranslateItems(mResult,null));
                */
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();

                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                    //divideItems.add(new TranslateItems(result,null));
                }

                mResult = strBuf.toString();
                txtResult.setText(mResult);

                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);

                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);

                break;
        }

    }

    @Override
    protected  void onStart(){
        super.onStart();

        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected  void onStop(){
        super.onStop();

        naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    protected void onPause(){
        super.onPause();

        naverRecognizer.getSpeechRecognizer().cancel();
        naverRecognizer.getSpeechRecognizer().release();

    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer() {
        if (player != null) {
            try {
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void PlayRecord(){
        File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath() + "/l_recorder/";

        try {
            playAudio(path + title + ".mp4");
            Toast.makeText(getApplicationContext(),"녹음 파일 재생중",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.

    static class RecognitionHandler extends Handler{
        private final WeakReference<TranslateActivity> mActivity;

        RecognitionHandler(TranslateActivity activity) {
            mActivity = new WeakReference<TranslateActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TranslateActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    public ArrayList<TranslateItems> divideItem(ArrayList<TranslateItems> item){

        String[] divide = item.get(0).getItem().split(" ");
        for(int i = 0; i<divide.length;i++){
            divideItems.add(new TranslateItems(divide[i],null));
        }
        return divideItems;
    }
    */

    public boolean divideItem(String s){

        if(s == null){
            return false;
        }
        /*
        StringTokenizer tokenizer = new StringTokenizer(s,"' '\n");

        while (tokenizer.hasMoreTokens()){
            divideItems.add(new TranslateItems(tokenizer.nextToken(),null));
        }
        */

        String[] divide_1 = s.split("\n");

        for(int i = 0; i < divide_1.length;i++){
            String[] divide_2 = divide_1[i].split(" ");
            for(int j = 0; j < divide_2.length; j++){
                divideItems.add(new TranslateItems(divide_2[j],null));
            }
        }

        return true;

    }


}
