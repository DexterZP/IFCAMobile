package br.dexter.ifcamobile.ADM;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rey.material.app.Dialog;

import br.dexter.ifcamobile.R;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class LoginLider extends AppCompatActivity
{
    private TextInputLayout textEmail, textPassword;
    private TextInputEditText email, senha;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_login_lider);

        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        Button login = findViewById(R.id.login);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        updateUI(user);

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert email.getText() != null;
                assert senha.getText() != null;

                loginUser(email.getText().toString(), senha.getText().toString());
            }
        });
    }

    public void loginUser(String Email, String Password)
    {
        if(Email.equals(""))
        {
            textEmail.setError("Não deixe o campo vazio");
        }
        else if(Password.equals(""))
        {
            textPassword.setError("Não deixe o campo vazio");
        }
        else
        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog);
            dialog.setCancelable(false);
            dialog.show();

            auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        finish();
                        Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                        startActivity(i);

                        Toast.makeText(LoginLider.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(LoginLider.this, "Falha ao realizar login", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            finish();
            Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
            startActivity(i);
        }
    }
}