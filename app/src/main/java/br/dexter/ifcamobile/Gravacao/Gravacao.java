package br.dexter.ifcamobile.Gravacao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

import br.dexter.ifcamobile.R;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class Gravacao extends AppCompatActivity implements GravacaoAdapter.ItemClickListener
{
    private MediaRecorder mediaRecorder;
    private TextView Tempo;

    private CardView notAudioCard;

    private long startHTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private String pathSave = "";
    private ArrayList<String> name = new ArrayList<>();
    private com.rey.material.widget.FloatingActionButton gravar, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_gravacao);

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gravar = findViewById(R.id.Gravar);
        stop = findViewById(R.id.Stop);
        Tempo = findViewById(R.id.Tempo);
        notAudioCard = findViewById(R.id.GravacoesNotCard);

        LoadData();
        VerifyListData();
        Button();
    }

    @Override
    public void onItemClick(View view, final int position)
    {
        if(Build.VERSION.SDK_INT >= 24)
        {
            try
            {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name.get(position) + ".mp3");
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(intent);
    }

    @Override
    public void onItemRename(View view, int position)
    {
        SaveData();
        VerifyListData();
    }

    @Override
    public void onItemRemove(View view, int position)
    {
        SaveData();
        VerifyListData();
    }

    public void Button()
    {
        gravar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pathSave = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + GravacaoAdapter.RandomID + ".mp3";

                setupMediaRecorder();
                try
                {
                   mediaRecorder.prepare();
                   mediaRecorder.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                startHTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                Tempo.setVisibility(View.VISIBLE);

                gravar.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
            }
        });

        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(timeInMilliseconds >= 2000)
                {
                    if(null != mediaRecorder)
                    {
                        try
                        {
                            mediaRecorder.stop();
                        }
                        catch (RuntimeException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    name.add(GravacaoAdapter.RandomID);

                    SaveData();
                    VerifyListData();

                    Tempo.setVisibility(View.GONE);
                    gravar.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(Gravacao.this, "Grave pelo menos 2 segundos", Toast.LENGTH_SHORT).show();

                    if(null != mediaRecorder)
                    {
                        try
                        {
                            mediaRecorder.stop();
                        }
                        catch (RuntimeException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    Tempo.setVisibility(View.GONE);
                    gravar.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.GONE);

                    VerifyListData();
                }
            }
        });
    }

    private Runnable updateTimerThread = new Runnable()
    {
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void run()
        {
            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            if (Tempo != null)
                Tempo.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    private void SaveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(name);
        editor.putString("task list", json);
        editor.apply();
    }

    private void LoadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        name = gson.fromJson(json, type);

        if(name == null)
        {
            name = new ArrayList<>();
        }
    }

    private void VerifyListData()
    {
        if(name.isEmpty()) {
            notAudioCard.setVisibility(View.VISIBLE);
        }
        else {
            notAudioCard.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(Gravacao.this));
        GravacaoAdapter adapter = new GravacaoAdapter(Gravacao.this, name);
        adapter.setClickListener(Gravacao.this, Gravacao.this, Gravacao.this);
        recyclerView.setAdapter(adapter);
    }

    private void setupMediaRecorder()
    {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(pathSave);
    }
}