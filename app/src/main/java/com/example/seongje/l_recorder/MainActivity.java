package com.example.seongje.l_recorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button recordBtn;
    Button recordStopBtn;
    Button saveBtn;

    String filename = "recorded";
    String inputValue;

    MediaPlayer player;
    MediaRecorder recorder;

    ListView listView;
    ListViewItems rItem;

    ImageView img1;
    ImageView img2;
    Animation animation;
    Animation animation2;

    ArrayList<ListViewItems> items = new ArrayList<ListViewItems>();
    ListViewItems item = new ListViewItems();
    ArrayAdapter<String> adapterReal;
    ArrayList<ListViewItems> arrayList;
    NewAdapter adapter;

    int playbackPosition = 0;
    String rename = "";
    String mResult = null;

    //텍스트문서를 만들어야한다.
    //

    @Override///
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SplashActivity.class));
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText etEdit = new EditText(this);

        recordBtn = (Button) findViewById(R.id.recordBtn);
        recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
        saveBtn = (Button) findViewById(R.id.recordSaveBtn);

        adapter = new NewAdapter(this, android.R.layout.simple_list_item_1, items);

        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.stop);

        DBAdapter db = new DBAdapter(this);
        db.open();
        arrayList= db.selectAllPersonList();
        db.close();
        listView.setOnItemClickListener(mItemClickListener);

        recordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                img1.startAnimation(animation);
                img2.startAnimation(animation);
                recordingStart();
            }

        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                img1.setAnimation(animation2);
                img2.setAnimation(animation2);
                recordingStop();
            }

        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                try {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("title 입력해 주세요");
                    dialog.setView(etEdit);
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            inputValue = etEdit.getText().toString();

                            File file = Environment.getExternalStorageDirectory();
                            String path = file.getAbsolutePath() + "/l_recorder/";

                            File file1 = new File(path + filename + ".mp4");
                            File file2 = new File(path + inputValue + ".mp4");
                            file1.renameTo(file2);

                            String text = etEdit.getText().toString();
                            if (text.length() != 0) {
                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat CurDateFormat1 = new SimpleDateFormat("yy-mm-dd");
                                SimpleDateFormat CurDateFormat2 = new SimpleDateFormat("mm분ss초");
                                String strCurDate1 = CurDateFormat1.format(date);
                                String strCurDate2 = CurDateFormat2.format(date);
                                rItem = new ListViewItems(text, strCurDate1, strCurDate2);
                                items.add(rItem);
                                etEdit.setText("");
                                adapter.notifyDataSetChanged();//수정해야함

                                ListViewItems item = new ListViewItems(rItem.getTitle(), rItem.getDate(), rItem.getTime());

                                DBAdapter db = new DBAdapter(MainActivity.this);
                                db.open();
                                db.insertAddress(item);
                                db.close();
                            }
                        }
                    });

                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void playAudio(String url) throws Exception{
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
        if(player != null){
            try {
                player.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private void recordingStart() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }// TODO Auto-generated method stub

        File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath() + "/l_recorder/";

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(path + filename + ".mp4");

        try {
            Toast.makeText(getApplicationContext(),
                    "녹음을 시작합니다.", Toast.LENGTH_LONG).show();

            recorder.prepare();
            recorder.start();

        } catch (Exception ex) {
            Log.e("SampleAudioRecorder", "Exception : ", ex);
        }
    }

    private void recordingStop(){
        if (recorder == null)
            return;

        recorder.stop();
        recorder.release();
        recorder = null;

        Toast.makeText(getApplicationContext(),
                "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
        // TODO Auto-generated method stub
    }


    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        //여기다가 intent넣어서 다른화면으로 넘어가면된다.

        public void onItemClick(AdapterView parent, View view, int position, long id) {
            String mes = "Select item =" + items.get(position);
            Intent intent = new Intent(MainActivity.this, TranslateActivity.class);
            intent.putExtra("id", 1);
            intent.putExtra("title", rItem.getTitle());
            startActivity(intent);
            //finish();
        }
    };

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

}
