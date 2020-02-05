package br.dexter.ifcamobile.Ajuda;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.rey.material.app.Dialog;
import com.rey.material.widget.FloatingActionButton;

import java.io.FileInputStream;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class Feedback extends AppCompatActivity
{
    private TextInputLayout textInputLayoutFeed, textInputLayoutName;
    private TextInputEditText descricao, name;
    private FloatingActionButton enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_ajuda_feedback);

        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);

        descricao = findViewById(R.id.descricao);
        name = findViewById(R.id.Title);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutFeed = findViewById(R.id.textInputLayout);
        enviar = findViewById(R.id.enviarFeedback);

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expTv1.setText(getHardwareAndSoftwareInfo());

        Button();
    }

    private void Button()
    {
        enviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if(descricao.getText() != null && descricao.getText().toString().isEmpty() && name.getText() != null && name.getText().toString().isEmpty())
                {
                    textInputLayoutName.setError("Não deixe o campo vazio");
                    textInputLayoutFeed.setError("Não deixe o campo vazio");
                }
                else if(descricao.getText() != null && descricao.getText().toString().isEmpty())
                {
                    textInputLayoutFeed.setError("Não deixe o campo vazio");
                }
                else if(name.getText() != null && name.getText().toString().isEmpty())
                {
                    textInputLayoutName.setError("Não deixe o campo vazio");
                }
                else
                {
                    assert name.getText() != null;
                    final String Name = name.getText().toString();
                    final String Feedback = descricao.getText().toString();

                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(GetCodigo());

                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    databaseReference.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.getChildrenCount() > 0)
                            {
                                dialog.dismiss();
                            }
                            else
                            {
                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        dialog.dismiss();
                                    }
                                },3000);
                            }

                            databaseReference.child("Feedback").child(Name).child("Descrição").setValue(Feedback).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    databaseReference.child("Feedback").child(Name).child("Hardware").setValue(getHardwareAndSoftwareInfo());
                                    Toast.makeText(br.dexter.ifcamobile.Ajuda.Feedback.this,"Feedback enviado com sucesso", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();

                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(br.dexter.ifcamobile.Ajuda.Feedback.this,"Erro ao enviar feedback", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                    });
                }
            }
        });
    }

    @NonNull
    private String getHardwareAndSoftwareInfo() {

        return getString(R.string.hardware) + " " + Build.HARDWARE + "\n" +
                getString(R.string.model) + " " + Build.MODEL + "\n" +
                getString(R.string.id) + " " + Build.ID + "\n" +
                getString(R.string.manufacturer) + " " + Build.MANUFACTURER + "\n" +
                getString(R.string.brand) + " " + Build.BRAND + "\n" +
                getString(R.string.type) + " " + Build.TYPE + "\n" +
                getString(R.string.user) + " " + Build.USER + "\n" +
                getString(R.string.base) + " " + Build.VERSION_CODES.BASE + "\n" +
                getString(R.string.incremental) + " " + Build.VERSION.INCREMENTAL + "\n" +
                getString(R.string.board) + " " + Build.BOARD + "\n" +
                getString(R.string.host) + " " + Build.HOST + "\n" +
                getString(R.string.fingerprint) + " " + Build.FINGERPRINT + "\n" +
                getString(R.string.versioncode) + " " + Build.VERSION.RELEASE;
    }

    public String GetCodigo()
    {
        try
        {
            FileInputStream fin = openFileInput(SystemLogin.nameCodTxt);
            StringBuilder sb = new StringBuilder();
            int size;

            while ((size = fin.read()) != -1)
            {
                sb.append((char) size);
            }

            if(sb.toString().contains(SystemLogin.cod_Informatica3)) {
                return SystemLogin.nameInformatica3;
            }
            else if(sb.toString().contains(SystemLogin.cod_Informatica2)) {
                return SystemLogin.nameInformatica2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Administracao1)) {
                return SystemLogin.nameAdministracao1;
            }
            else if(sb.toString().contains(SystemLogin.cod_Administracao2)) {
                return SystemLogin.nameAdministracao2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Administracao3)) {
                return SystemLogin.nameAdministracao3;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agroindustria1)) {
                return SystemLogin.nameAgroindustria1;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agroindustria2)) {
                return SystemLogin.nameAgroindustria2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agroindustria3)) {
                return SystemLogin.nameAgroindustria3;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agropecuaria1)) {
                return SystemLogin.nameAgropecuaria1;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agropecuaria2)) {
                return SystemLogin.nameAgropecuaria2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agropecuaria3)) {
                return SystemLogin.nameAgropecuaria3;
            }
        }
        catch (Exception error)
        {
            error.printStackTrace();
        }

        return null;
    }
}