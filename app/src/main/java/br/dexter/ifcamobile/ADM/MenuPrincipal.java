package br.dexter.ifcamobile.ADM;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.dexter.ifcamobile.R;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class MenuPrincipal extends AppCompatActivity
{
    TextView email;
    Button sFaltas, sSair;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_menu_principal);

        email = findViewById(R.id.bemVindo);
        sFaltas = findViewById(R.id.sistemaFaltas);
        sSair = findViewById(R.id.logout);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        updateUI(user);

        sSair.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(user != null)
                {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent i = new Intent(getApplicationContext(), LoginLider.class);
                    startActivity(i);
                }
            }
        });

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sFaltas.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), ProfessoresFalta.class);
                startActivity(i);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(FirebaseUser user)
    {
        if(user != null)
        {
            String getEmail = user.getEmail();

            email.setText("Você está conectado em: " + getEmail);
        }
    }
}