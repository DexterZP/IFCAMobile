package br.dexter.ifcamobile.Introdução;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class Introdution extends AppCompatActivity
{
    private Button iniciar, permissao, politica;
    private CheckBox checkBox;
    private LinearLayout linearLayout1, linearLayout2, LinearPolitica;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_introdution);

        iniciar = findViewById(R.id.Iniciar);
        permissao = findViewById(R.id.Permissao);
        linearLayout1 = findViewById(R.id.Linear1);
        linearLayout2 = findViewById(R.id.Linear2);

        Button();

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
            actionBar.hide();
    }

    private void Button()
    {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 3.0f);
        animation.setDuration(850);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        linearLayout1.setLayoutAnimation(controller);

        iniciar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(LinearLayout.VISIBLE);

                AnimationSet set = new AnimationSet(true);

                Animation animation = new AlphaAnimation(0.0f, 3.0f);
                animation.setDuration(850);
                set.addAnimation(animation);

                animation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
                );
                animation.setDuration(300);
                set.addAnimation(animation);

                LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
                linearLayout2.setLayoutAnimation(controller);
            }
        });

        permissao.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkAndRequestPermissions())
                {
                    setContentView(R.layout.fragment_politica);
                    politica = findViewById(R.id.politicaFloat);
                    checkBox = findViewById(R.id.cb_concordo);
                    Button2();

                    LinearPolitica = findViewById(R.id.LinearPolitica);
                    LinearPolitica.setVisibility(LinearLayout.VISIBLE);

                    AnimationSet set = new AnimationSet(true);

                    Animation animation = new AlphaAnimation(0.0f, 3.0f);
                    animation.setDuration(900);
                    set.addAnimation(animation);

                    animation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
                    );
                    animation.setDuration(300);
                    set.addAnimation(animation);

                    LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
                    LinearPolitica.setLayoutAnimation(controller);
                }
            }
        });
    }

    private void Button2()
    {
        politica.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkBox.isChecked()) {
                    getApplication().getSharedPreferences("newIntro", Context.MODE_PRIVATE).edit().putBoolean("status", true).apply();
                    finish();
                    Intent intent = new Intent(Introdution.this, SystemLogin.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Introdution.this, "Você precisa aceitar os termos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkAndRequestPermissions()
    {
        int readExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readExternal != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (writeExternal != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (recordExternal != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);

            return false;
        }
        return true;
    }
}