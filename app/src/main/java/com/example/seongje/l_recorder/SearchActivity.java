package com.example.seongje.l_recorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textContent;

    String title = "";
    String txt = "";
    String content;

    MediaPlayer player;
    MediaRecorder recorder;

    ArrayList<TranslateItems> divideItems = new ArrayList<TranslateItems>();
    ArrayList<TranslateItems> Items = new ArrayList<TranslateItems>();

    int playbackPosition = 0;
    int next = 0;


    @Override///
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        //     Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //     setSupportActionBar(toolbar);

        final Button StartBtn = (Button) findViewById(R.id.start_btn);
        final Button StopBtn = (Button) findViewById(R.id.stop_btn);

        Button saveBtn = (Button) findViewById(R.id.save_txt_btn);
        Button translasteBtn = (Button) findViewById(R.id.translate_btn);
        Button searchBtn = (Button) findViewById(R.id.search_btn);

        editText = (EditText) findViewById(R.id.messageText);
        editText.requestFocus();

        Intent intent = getIntent();
        content = intent.getStringExtra("text_content");
        title = intent.getExtras().getString("title");

        textContent = (TextView) findViewById(R.id.txt_next);
        textContent.setText(content);

        divideItems = intent.getParcelableArrayListExtra("divideItems");

        divideItem(content);

        //키보드 보이게 하는 부분
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        StartBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartBtn.setVisibility(View.GONE);
                StopBtn.setVisibility(View.VISIBLE);

                File file = Environment.getExternalStorageDirectory();
                String path = file.getAbsolutePath() + "/l_recorder/";

                try {
                    playAudio(path + title + ".mp4");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        StopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StartBtn.setVisibility(View.VISIBLE);
                StopBtn.setVisibility(View.GONE);

                Playstop();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                File file = Environment.getExternalStorageDirectory();
                String path = file.getAbsolutePath() + "/l_recorder/text";

                writetextfile(title,path,content);

            }
        });

        translasteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, NaverSearch.class);
                intent.putExtra("dic_search", txt);
                startActivity(intent);

            }

        });


        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                txt = editText.getText().toString();

                if (content.contains(txt)) {
                    // Toast.makeText(SearchActivity.this,txt,Toast.LENGTH_SHORT).show();
                    //setTextViewColorPartial(textContent, content, txt, 0xffffd700);
                    next = 0;
                    search(Items , txt);
                } else {
                    Toast.makeText(SearchActivity.this, "다시 검색해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void writetextfile(String filename, String path, String contents){
        try{
            FileOutputStream fos = new FileOutputStream(path + "/" + filename + ".txt",true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();
            fos.close();
            Toast.makeText(SearchActivity.this, "저장", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void setTextViewColorPartial(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new BackgroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void search(ArrayList<TranslateItems> t_item, String s) {
        int j = 0;
        for (j = next; j < t_item.size(); j++) {
            if (t_item.get(j).getItem().equals(s)) {
                Toast.makeText(SearchActivity.this, t_item.get(j).getItem(), Toast.LENGTH_SHORT).show();
                setTextViewColorPartial(textContent, content, s, 0xffffd700);
                next = j+1;
                break;
            }
        }
        if(j == t_item.size())
            Toast.makeText(SearchActivity.this, "더 이상 찾을 문자가 없습니다", Toast.LENGTH_SHORT).show();
    }

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
                Items.add(new TranslateItems(divide_2[j],null));
            }
        }

        return true;

    }

    private void Playstop() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            player.pause();
            Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.", Toast.LENGTH_LONG).show();
        }
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

    protected void onPause() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }

        super.onPause();

    }
}

