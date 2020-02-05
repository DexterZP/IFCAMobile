package br.dexter.ifcamobile.SplashScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import br.dexter.ifcamobile.Introdução.Introdution;
import br.dexter.ifcamobile.MainActivity;
import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;

public class Screen extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(!VerifyIntro())
                {
                    Intent i = new Intent(Screen.this, Introdution.class);
                    startActivity(i);
                    finish();
                }
                else if(fileExists(getApplicationContext(), SystemLogin.nameCodTxt))
                {
                    Intent intent = new Intent(Screen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(Screen.this, SystemLogin.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 850);
    }

    public boolean VerifyIntro() {
        return getApplication().getSharedPreferences("newIntro", MODE_PRIVATE).getBoolean("status", false);
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file.exists();
    }
}
