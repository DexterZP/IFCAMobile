package br.dexter.ifcamobile.Semana;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.app.Dialog;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class ResumoSemanal extends AppCompatActivity
{
    private SmoothProgressBar mProgressBar;

    private CardView segunda, terca, quarta, quinta, sexta, semAtividade;
    private ImageView segundaFeita, tercaFeita, quartaFeita, quintaFeita, sextaFeita;
    private TextView LastUpdate;
    private View view1, view2, view3, view4, view5, view6;
    private ShimmerRecyclerView shimmerRecycler;
    private TextView segundaTXT, tercaTXT, quartaTXT, quintaTXT, sextaTXT, semAtividadeTXT;
    private EditText segundaEdit, tercaEdit, quartaEdit, quintaEdit, sextaEdit;
    public LinearLayout editar, excluir, salvar;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_resumo_semanal);

        ImageView handleBack = findViewById(R.id.BackHandle);
        ImageView handleSettings = findViewById(R.id.Settings);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        handleSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirDialogADM();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child(GetCodigo());

        segundaEdit = findViewById(R.id.EditSegunda);
        tercaEdit = findViewById(R.id.EditTerca);
        quartaEdit = findViewById(R.id.EditQuarta);
        quintaEdit = findViewById(R.id.EditQuinta);
        sextaEdit = findViewById(R.id.EditSexta);

        view1 = findViewById(R.id.View1);
        view2 = findViewById(R.id.View2);
        view3 = findViewById(R.id.View3);
        view4 = findViewById(R.id.View4);
        view5 = findViewById(R.id.View5);
        view6 = findViewById(R.id.View6);

        segunda = findViewById(R.id.SegundaFeira);
        terca = findViewById(R.id.TercaFeira);
        quarta = findViewById(R.id.QuartaFeira);
        quinta = findViewById(R.id.QuintaFeira);
        sexta = findViewById(R.id.SextaFeira);
        semAtividade = findViewById(R.id.SemAtividade);

        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);

        segundaTXT = findViewById(R.id.TextoSegunda);
        tercaTXT = findViewById(R.id.TextoTerca);
        quartaTXT = findViewById(R.id.TextoQuarta);
        quintaTXT = findViewById(R.id.TextoQuinta);
        sextaTXT = findViewById(R.id.TextoSexta);
        semAtividadeTXT = findViewById(R.id.TextoSemAtividade);

        segundaFeita = findViewById(R.id.VerifyAtividadeSegunda);
        tercaFeita = findViewById(R.id.VerifyAtividadeTerca);
        quartaFeita = findViewById(R.id.VerifyAtividadeQuarta);
        quintaFeita = findViewById(R.id.VerifyAtividadeQuinta);
        sextaFeita = findViewById(R.id.VerifyAtividadeSexta);

        LastUpdate = findViewById(R.id.LastUpdate);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setIndeterminate(true);
        segunda.setVisibility(View.GONE);
        terca.setVisibility(View.GONE);
        quarta.setVisibility(View.GONE);
        quinta.setVisibility(View.GONE);
        sexta.setVisibility(View.GONE);

        Button();
        CarregarAtividades();
        CarregarRandom();
        LoadingResumo();
    }

    private void CarregarRandom(){
        Random rnd1 = new Random();
        Random rnd2 = new Random();
        Random rnd3 = new Random();
        Random rnd4 = new Random();
        Random rnd5 = new Random();
        Random rnd6 = new Random();

        int color1 = Color.argb(255, rnd1.nextInt(256), rnd1.nextInt(256), rnd1.nextInt(256));
        int color2 = Color.argb(255, rnd2.nextInt(256), rnd2.nextInt(256), rnd2.nextInt(256));
        int color3 = Color.argb(255, rnd3.nextInt(256), rnd3.nextInt(256), rnd3.nextInt(256));
        int color4 = Color.argb(255, rnd4.nextInt(256), rnd4.nextInt(256), rnd4.nextInt(256));
        int color5 = Color.argb(255, rnd5.nextInt(256), rnd5.nextInt(256), rnd5.nextInt(256));
        int color6 = Color.argb(255, rnd6.nextInt(256), rnd6.nextInt(256), rnd6.nextInt(256));

        view1.setBackgroundColor(color1);
        view2.setBackgroundColor(color2);
        view3.setBackgroundColor(color3);
        view4.setBackgroundColor(color4);
        view5.setBackgroundColor(color5);
        view6.setBackgroundColor(color6);
    }

    private void AbrirDialogADM(){
        final BottomSheetDialog mDialog = new BottomSheetDialog(this);

        mDialog.setContentView(R.layout.activity_sheet_dialog2);
        mDialog.show();

        editar = mDialog.findViewById(R.id.action_editar);
        excluir = mDialog.findViewById(R.id.action_excluir);
        salvar = mDialog.findViewById(R.id.action_salvar);

        editar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                {
                    Toast.makeText(ResumoSemanal.this, "Você não pode editar este arquivo", Toast.LENGTH_LONG).show();
                }
                else
                {
                    semAtividade.setVisibility(View.GONE);
                    segunda.setVisibility(View.VISIBLE);
                    terca.setVisibility(View.VISIBLE);
                    quarta.setVisibility(View.VISIBLE);
                    quinta.setVisibility(View.VISIBLE);
                    sexta.setVisibility(View.VISIBLE);

                    final Dialog dialog = new Dialog(ResumoSemanal.this);
                    dialog.setContentView(R.layout.dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    databaseReference.child("Resumo Semanal").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.exists())
                            {
                                final String segunda = dataSnapshot.child("Segunda").getValue(String.class);
                                final String terca = dataSnapshot.child("Terça").getValue(String.class);
                                final String quarta = dataSnapshot.child("Quarta").getValue(String.class);
                                final String quinta = dataSnapshot.child("Quinta").getValue(String.class);
                                final String sexta = dataSnapshot.child("Sexta").getValue(String.class);

                                segundaEdit.setText(segunda);
                                tercaEdit.setText(terca);
                                quartaEdit.setText(quarta);
                                quintaEdit.setText(quinta);
                                sextaEdit.setText(sexta);

                                segundaEdit.setVisibility(View.VISIBLE);
                                tercaEdit.setVisibility(View.VISIBLE);
                                quartaEdit.setVisibility(View.VISIBLE);
                                quintaEdit.setVisibility(View.VISIBLE);
                                sextaEdit.setVisibility(View.VISIBLE);

                                segundaTXT.setVisibility(View.GONE);
                                tercaTXT.setVisibility(View.GONE);
                                quartaTXT.setVisibility(View.GONE);
                                quintaTXT.setVisibility(View.GONE);
                                sextaTXT.setVisibility(View.GONE);

                                dialog.dismiss();
                            }
                            else
                            {
                                segundaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                                tercaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                                quartaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                                quintaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                                sextaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");

                                segundaEdit.setVisibility(View.VISIBLE);
                                tercaEdit.setVisibility(View.VISIBLE);
                                quartaEdit.setVisibility(View.VISIBLE);
                                quintaEdit.setVisibility(View.VISIBLE);
                                sextaEdit.setVisibility(View.VISIBLE);

                                segundaTXT.setVisibility(View.GONE);
                                tercaTXT.setVisibility(View.GONE);
                                quartaTXT.setVisibility(View.GONE);
                                quintaTXT.setVisibility(View.GONE);
                                sextaTXT.setVisibility(View.GONE);

                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {
                            dialog.dismiss();
                        }
                    });
                }
                mDialog.hide();
            }
        });

        excluir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                {
                    Toast.makeText(ResumoSemanal.this, "Você não pode excluir este arquivo", Toast.LENGTH_LONG).show();
                }
                else
                {
                    segundaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                    tercaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                    quartaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                    quintaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");
                    sextaEdit.setText("Física (Exemplo):\n1 - Responder Atividade da página 255 e 1964\n2 - Prova!\n\nArte (Exemplo):\n1 - Responder Atividade da página 255 e 2064\n2 - Prova");

                    segundaEdit.setVisibility(View.VISIBLE);
                    tercaEdit.setVisibility(View.VISIBLE);
                    quartaEdit.setVisibility(View.VISIBLE);
                    quintaEdit.setVisibility(View.VISIBLE);
                    sextaEdit.setVisibility(View.VISIBLE);

                    segundaTXT.setVisibility(View.GONE);
                    tercaTXT.setVisibility(View.GONE);
                    quartaTXT.setVisibility(View.GONE);
                    quintaTXT.setVisibility(View.GONE);
                    sextaTXT.setVisibility(View.GONE);

                    mDialog.dismiss();
                }
            }
        });

        salvar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                {
                    Toast.makeText(ResumoSemanal.this, "Você não pode editar este arquivo", Toast.LENGTH_LONG).show();
                }
                else if(segundaTXT.getVisibility() == View.VISIBLE)
                {
                    Toast.makeText(ResumoSemanal.this, "Você não pode salvar sem editar", Toast.LENGTH_LONG).show();
                }
                else
                {
                    final Dialog dialog = new Dialog(ResumoSemanal.this);
                    dialog.setContentView(R.layout.dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    Calendar calendar = Calendar.getInstance();
                    Locale myLocale = new Locale("pt", "PT");
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", myLocale);
                    String currentDate = sdf.format(calendar.getTime());

                    databaseReference.child("Resumo Semanal").child("Segunda").setValue(segundaEdit.getText().toString());
                    databaseReference.child("Resumo Semanal").child("Terça").setValue(tercaEdit.getText().toString());
                    databaseReference.child("Resumo Semanal").child("Quarta").setValue(quartaEdit.getText().toString());
                    databaseReference.child("Resumo Semanal").child("Quinta").setValue(quintaEdit.getText().toString());
                    databaseReference.child("Resumo Semanal").child("Last Update").setValue(currentDate);
                    databaseReference.child("Resumo Semanal").child("Sexta").setValue(sextaEdit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(ResumoSemanal.this, "Resumo semanal salvo com sucesso", Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                            finish();
                            startActivity(getIntent());
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(ResumoSemanal.this, "Erro ao salvar resumo semanal", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
                mDialog.hide();
            }
        });
    }

    private void CarregarAtividades()
    {
        SharedPreferences sp = getSharedPreferences("AppStorage", 0);
        String segundatxt = sp.getString("segundaFeira", "");
        String tercatxt = sp.getString("tercaFeira", "");
        String quartatxt = sp.getString("quartaFeira", "");
        String quintatxt = sp.getString("quintaFeira", "");
        String sextatxt = sp.getString("sextaFeira", "");

        if(segundatxt.equals("yep")) {
            segundaFeita.setImageResource(R.drawable.ic_correct);
        } else {
            segundaFeita.setImageResource(R.drawable.ic_alert);
        }

        if(tercatxt.equals("yep")) {
            tercaFeita.setImageResource(R.drawable.ic_correct);
        } else {
            tercaFeita.setImageResource(R.drawable.ic_alert);
        }

        if(quartatxt.equals("yep")) {
            quartaFeita.setImageResource(R.drawable.ic_correct);
        } else {
            quartaFeita.setImageResource(R.drawable.ic_alert);
        }

        if(quintatxt.equals("yep")) {
            quintaFeita.setImageResource(R.drawable.ic_correct);
        } else {
            quintaFeita.setImageResource(R.drawable.ic_alert);
        }

        if(sextatxt.equals("yep")) {
            sextaFeita.setImageResource(R.drawable.ic_correct);
        } else {
            sextaFeita.setImageResource(R.drawable.ic_alert);
        }
    }

    private void Button()
    {
        segunda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog("Deseja concluir as atividades de segunda?", "segundaFeira");
            }
        });

        terca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog("Deseja concluir as atividades de terça?", "tercaFeira");
            }
        });

        quarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog("Deseja concluir as atividades de quarta?", "quartaFeira");
            }
        });

        quinta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog("Deseja concluir as atividades de quinta?", "quintaFeira");
            }
        });

        sexta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog("Deseja concluir as atividades de sexta?", "sextaFeira");
            }
        });
    }

    public void ShowAlertDialog(String title, final String daySemana)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
        .setCancelable(true)
        .setPositiveButton("Concluir", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                SharedPreferences settings = getSharedPreferences("AppStorage", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(daySemana, "yep");
                editor.apply();

                CarregarAtividades();
                dialog.dismiss();
            }
        })
        .setNeutralButton("Retirar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                SharedPreferences settings = getSharedPreferences("AppStorage", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(daySemana, "dont");
                editor.apply();

                CarregarAtividades();
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        pbutton.setScaleY(0.9f); pbutton.setScaleX(0.9f);
        nbutton.setScaleY(0.9f); nbutton.setScaleX(0.9f);
        pbutton.setBackgroundColor(getResources().getColor(R.color.azure));
        nbutton.setBackgroundColor(getResources().getColor(R.color.azure));
        pbutton.setTextColor(Color.WHITE);
        nbutton.setTextColor(Color.WHITE);
    }

    @SuppressLint("SetTextI18n")
    public void LoadingResumo()
    {
        ValueEventListener eventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    final String segunda1 = dataSnapshot.child("Segunda").getValue(String.class);
                    final String terca1 = dataSnapshot.child("Terça").getValue(String.class);
                    final String quarta1 = dataSnapshot.child("Quarta").getValue(String.class);
                    final String quinta1 = dataSnapshot.child("Quinta").getValue(String.class);
                    final String sexta1 = dataSnapshot.child("Sexta").getValue(String.class);
                    final String lastUpdate1 = dataSnapshot.child("Last Update").getValue(String.class);

                    segundaTXT.setText(segunda1);
                    tercaTXT.setText(terca1);
                    quartaTXT.setText(quarta1);
                    quintaTXT.setText(quinta1);
                    sextaTXT.setText(sexta1);
                    LastUpdate.setText("Última alteração: " + lastUpdate1);
                    mProgressBar.setIndeterminate(false);

                    segunda.setVisibility(View.VISIBLE);
                    terca.setVisibility(View.VISIBLE);
                    quarta.setVisibility(View.VISIBLE);
                    quinta.setVisibility(View.VISIBLE);
                    sexta.setVisibility(View.VISIBLE);

                    shimmerRecycler.setVisibility(View.GONE);
                }
                else
                {
                    semAtividadeTXT.setText("Sua turma ainda não tem resumo semanal.");
                    LastUpdate.setText("Última alteração: 03/02/1964");
                    mProgressBar.setIndeterminate(false);

                    shimmerRecycler.setVisibility(View.GONE);

                    semAtividade.setVisibility(View.VISIBLE);
                    segunda.setVisibility(View.GONE);
                    terca.setVisibility(View.GONE);
                    quarta.setVisibility(View.GONE);
                    quinta.setVisibility(View.GONE);
                    sexta.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        databaseReference.child("Resumo Semanal").addListenerForSingleValueEvent(eventListener);
        databaseReference.keepSynced(true);
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