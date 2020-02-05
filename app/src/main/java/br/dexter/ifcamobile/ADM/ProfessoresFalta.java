package br.dexter.ifcamobile.ADM;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Spinner;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class ProfessoresFalta extends AppCompatActivity
{
    private TextView professorTXT1, professorTXT2, professorTXT3, professorTXT4, professorTXT5, professorTXT6;
    private TextInputLayout inputLayoutTXT;
    private TextInputEditText anotacaoTXT;
    private Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6;
    private Button enviar;

    private LinearLayout professor1, professor2, professor3, professor4, professor5, professor6;

    private Calendar calendar = Calendar.getInstance();
    private int day = calendar.get(Calendar.DAY_OF_WEEK);

    private Locale myLocale = new Locale("pt", "PT");
    private SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", myLocale);
    private String currentDate = sdf.format(calendar.getTime());

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_professores_falta);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(GetCodigo()).child("Faltas");

        enviar = findViewById(R.id.Enviar);

        professor1 = findViewById(R.id.professor1);
        professor2 = findViewById(R.id.professor2);
        professor3 = findViewById(R.id.professor3);
        professor4 = findViewById(R.id.professor4);
        professor5 = findViewById(R.id.professor5);
        professor6 = findViewById(R.id.professor6);
        inputLayoutTXT = findViewById(R.id.textInputLayout);

        anotacaoTXT = findViewById(R.id.Anotacao);

        professorTXT1 = findViewById(R.id.professorFalta_1);
        professorTXT2 = findViewById(R.id.professorFalta_2);
        professorTXT3 = findViewById(R.id.professorFalta_3);
        professorTXT4 = findViewById(R.id.professorFalta_4);
        professorTXT5 = findViewById(R.id.professorFalta_5);
        professorTXT6 = findViewById(R.id.professorFalta_6);

        spinner1 = findViewById(R.id.faltasSpinner1);
        spinner2 = findViewById(R.id.faltasSpinner2);
        spinner3 = findViewById(R.id.faltasSpinner3);
        spinner4 = findViewById(R.id.faltasSpinner4);
        spinner5 = findViewById(R.id.faltasSpinner5);
        spinner6 = findViewById(R.id.faltasSpinner6);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.FaltasProfessores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
        spinner4.setAdapter(adapter);
        spinner5.setAdapter(adapter);
        spinner6.setAdapter(adapter);

        VerifyDay();
        VerifyImage();

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void VerifyImage()
    {
        PDFView pdfView = findViewById(R.id.pdfView);

        switch (GetCodigo())
        {
            case "Agropecuária I":
            {
                pdfView.fromAsset("Horario.pdf").pages(0).load();
            }
            case "Agropecuária II":
            {
                pdfView.fromAsset("Horario.pdf").pages(1).load();
            }
            case "Agropecuária III":
            {
                pdfView.fromAsset("Horario.pdf").pages(2).load();
            }
            case "Administração I":
            {
                pdfView.fromAsset("Horario.pdf").pages(3).load();
            }
            case "Administração II":
            {
                pdfView.fromAsset("Horario.pdf").pages(4).load();
            }
            case "Administração III":
            {
                pdfView.fromAsset("Horario.pdf").pages(5).load();
            }
            case "Agroindústria I":
            {
                pdfView.fromAsset("Horario.pdf").pages(6).load();
            }
            case "Agroindústria II":
            {
                pdfView.fromAsset("Horario.pdf").pages(7).load();
            }
            case "Agroindústria III":
            {
                pdfView.fromAsset("Horario.pdf").pages(8).load();
            }
            case "Informática II":
            {
                pdfView.fromAsset("Horario.pdf").pages(9).load();
            }
            case "Informática III":
            {
                pdfView.fromAsset("Horario.pdf").pages(10).load();
            }
        }
    }

    private void VerifyDay()
    {
        switch (day)
        {
            case Calendar.MONDAY:
                SegundaFeira();
                break;
            case Calendar.TUESDAY:
                TercaFeira();
                break;
            case Calendar.WEDNESDAY:
                QuartaFeira();
                break;
            case Calendar.THURSDAY:
                QuintaFeira();
                break;
            case Calendar.FRIDAY:
                SextaFeira();
                break;
            case Calendar.SATURDAY:
                Sabado();
                break;
            case Calendar.SUNDAY:
                Domingo();
                break;
        }
    }

    public void SegundaFeira()
    {
        if(GetCodigo().contains(SystemLogin.nameInformatica3)) {
            SetNameProf("Prof°: Fernando Amaro", "Prof°: Madson", "Prof°: Leudjane", "Prof°: Antônia", "Prof°: Wallonilson", null);
            CallButtonSend("Segunda-Feira", 5);
        }
        else if(GetCodigo().contains(SystemLogin.nameInformatica2)) {
            SetNameProf("Prof°: Breno", "Prof°: Francisca", "Prof°: Fernando Amaro", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao1)) {
            SetNameProf("Prof°: SUBS ADM", "Prof°: Fernando Amaro", "Prof°: Vanessa", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao2)) {
            SetNameProf("Prof°: Francisca", "Prof°: Régia", "Prof°: Hellen", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao3)) {
            SetNameProf("Prof°: Hellen", "Prof°: Antônia", "Prof°: Madson", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria1)) {
            SetNameProf("Prof°: Vanda", "Prof°: Valdone", "Prof°: Fernando Amaro", "Prof°: Francisco", "Prof°: Janmylla", "Prof°: Élcio");
            CallButtonSend("Segunda-Feira", 6);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria2)) {
            SetNameProf("Prof°: Élcio", "Prof°: Iramar", "Prof°: Antônia", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria3)) {
            SetNameProf("Prof°: Letícia", "Prof°: Leudjane", "Prof°: Antônia", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria1)) {
            SetNameProf("Prof°: Ricardo", "Prof°: Vanda", "Prof°: Valdone", "Prof°: Marcelo", "Prof°: Fernando Amaro", "Prof°: Francisco");
            CallButtonSend("Segunda-Feira", 6);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria2)) {
            SetNameProf("Prof°: Manoel", "Prof°: Hellen", "Prof°: Vanda", "Prof°: Diogo", "Prof°: Letícia", null);
            CallButtonSend("Segunda-Feira", 5);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria3)) {
            SetNameProf("Prof°: Daniel", "Prof°: Leudjane", "Prof°: Ribamar", null, null, null);
            CallButtonSend("Segunda-Feira", 3);
        }
    }

    public void TercaFeira()
    {
        if(GetCodigo().contains(SystemLogin.nameInformatica3)) {
            SetNameProf("Prof°: Fernando Amaro", "Prof°: Josenilson", "Prof°: Vanessa", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameInformatica2)) {
            SetNameProf("Prof°: Breno", "Prof°: Letícia", "Prof°: Jorge", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao1)) {
            SetNameProf("Prof°: Guilherme", "Prof°: SUBS ADM", "Prof°: Francisco", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao2)) {
            SetNameProf("Prof°: SUBS ADM", "Prof°: Antônia", "Prof°: Élcio Silva", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao3)) {
            SetNameProf("Prof°: Raimundo Filho", "Prof°: Guilherme", "Prof°: Leudjane", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria1)) {
            SetNameProf("Prof°: Janmylla", "Prof°: Carlos Jardel", "Prof°: Hellen", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria2)) {
            SetNameProf("Prof°: Thays", "Prof°: Michele", "Prof°: Francisca", "Prof°: Iramar", "Prof°: Lucillia", "Prof°: JackLady");
            CallButtonSend("Terca-Feira", 6);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria3)) {
            SetNameProf("Prof°: Nahelton", "Prof°: Pavão", "Prof°: Vanessa", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria1)) {
            SetNameProf("Prof°: Eliane", "Prof°: Nahelton", "Prof°: Aldivan", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria2)) {
            SetNameProf("Prof°: Élcio", "Prof°: Thays", "Prof°: Guilherme", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria3)) {
            SetNameProf("Prof°: Vanessa", "Prof°: Diogo", "Prof°: Antônia", null, null, null);
            CallButtonSend("Terca-Feira", 3);
        }
    }

    public void QuartaFeira()
    {
        if(GetCodigo().contains(SystemLogin.nameInformatica3)) {
            SetNameProf("Prof°: Josenilson", "Prof°: Guilherme", "Prof°: Aciel", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameInformatica2)) {
            SetNameProf("Prof°: Antônia", "Prof°: Fernando Amaro", "Prof°: Fernando Costa", "Prof°: Francisca", "Prof°: Letícia", "Prof°: Thays");
            CallButtonSend("Quarta-Feira", 6);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao1)) {
            SetNameProf("Prof°: Vanda", "Prof°: Carlos Eduardo", "Prof°: Carlos Jardel", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao2)) {
            SetNameProf("Prof°: SUBS ADM", "Prof°: Ronilson", "Prof°: Francisca", "Prof°: Leronardo", "Prof°: Fernando Costa", null);
            CallButtonSend("Quarta-Feira", 5);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao3)) {
            SetNameProf("Prof°: Vanessa", "Prof°: Aciel", "Prof°: Madson", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria1)) {
            SetNameProf("Prof°: Lucillia", "Prof°: Kedman", "Prof°: Thays", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria2)) {
            SetNameProf("Prof°: Francisca", "Prof°: Paulo Sergio", "Prof°: Vanda", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria3)) {
            SetNameProf("Prof°: Joyce", "Prof°: Arlene", "Prof°: Fabiano", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria1)) {
            SetNameProf("Prof°: Valdone", "Prof°: Carlos Eduardo", "Prof°: Diogo", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria2)) {
            SetNameProf("Prof°: Aciel", "Prof°: Antônia", "Prof°: Carlos Eduardo", null, null, null);
            CallButtonSend("Quarta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria3)) {
            SetNameProf("Prof°: Fernando Gomes", "Prof°: Carlos jardel", "Prof°: Manoel", "Prof°: Marcelo", "Prof°: Diogo", "Prof°: Letícia");
            CallButtonSend("Quarta-Feira", 4);
        }
    }

    public void QuintaFeira()
    {
        if(GetCodigo().contains(SystemLogin.nameInformatica3)) {
            SetNameProf("Prof°: Fernando Gomes", "Prof°: Madson", "Prof°: Kedman", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameInformatica2)) {
            SetNameProf("Prof°: Aciel", "Prof°: Vanda", "Prof°: Élcio", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao1)) {
            SetNameProf("Prof°: Kedman", "Prof°: Eliane", "Prof°: Guilherme", "Prof°: Hellen", "Prof°: Thays", "Prof°: SUBS ADM");
            CallButtonSend("Quinta-Feira", 6);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao2)) {
            SetNameProf("Prof°: Carlos Eduardo", "Prof°: Werton", "Prof°: Aciel", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao3)) {
            SetNameProf("Prof°: Arlene", "Prof°: Iramar", "Prof°: Madson", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria1)) {
            SetNameProf("Prof°: Eliane", "Prof°: Carlos Eduardo", "Prof°: Paulo Sergio", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria2)) {
            SetNameProf("Prof°: Aciel", "Prof°: Janmylla", "Prof°: Ronilson", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria3)) {
            SetNameProf("Prof°: Fabiano", "Prof°: Carlos Jardel", "Prof°: Aciel", "Prof°: Garcez", "Prof°: Guilherme", null);
            CallButtonSend("Quinta-Feira", 5);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria1)) {
            SetNameProf("Prof°: Èlcio", "Prof°: Marcelo", "Prof°: Carlos Jardel", "Prof°: Kedman", "Prof°: Edmilson", "Prof°: Hellen");
            CallButtonSend("Quinta-Feira", 6);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria2)) {
            SetNameProf("Prof°: Jacklady", "Prof°: Ronilson", "Prof°: Fernando Costa", "Prof°: Kedman", null, null);
            CallButtonSend("Quinta-Feira", 4);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria3)) {
            SetNameProf("Prof°: Garcez", "Prof°: Hellen", "Prof°: Janmylla", null, null, null);
            CallButtonSend("Quinta-Feira", 3);
        }
    }

    public void SextaFeira()
    {
        if(GetCodigo().contains(SystemLogin.nameInformatica3)) {
            SetNameProf("Prof°: Garcez", "Prof°: Arlene", "Prof°: Fernando Gomes", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameInformatica2)) {
            SetNameProf("Prof°: Jacklady", "Prof°: Fernando Costa", "Prof°: Carlos Eduardo", "Prof°: Paulo Sergio", "Prof°: Jorge", null);
            CallButtonSend("Sexta-Feira", 5);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao1)) {
            SetNameProf("Prof°: Fernando Gomes", "Prof°: Kedman", "Prof°: Subs ADM", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao2)) {
            SetNameProf("Prof°: Jacklady", "Prof°: SUBS ADM", "Prof°: Fernando Costa", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAdministracao3)) {
            SetNameProf("Prof°: Pavão", "Prof°: Ronilson", "Prof°: Iramar", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria1)) {
            SetNameProf("Prof°: Valdone", "Prof°: Kedman", "Prof°: Rodrigo", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria2)) {
            SetNameProf("Prof°: Lucillia", "Prof°: Carlos Eduardo", "Prof°: Ribamar", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgroindustria3)) {
            SetNameProf("Prof°: Nahelton", "Prof°: Pavão", "Prof°: Joyce", "Prof°: Leonardo", null, null);
            CallButtonSend("Sexta-Feira", 4);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria1)) {
            SetNameProf("Prof°: Kedman", "Prof°: Verônica", "Prof°: Paulo Sergio", null, null, null);
            CallButtonSend("Sexta-Feira", 3);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria2)) {
            SetNameProf("Prof°: Kedman", "Prof°: Ribamar", "Prof°: Fernando Costa", "Prof°: Marcelo", "Prof°: Nahelton", null);
            CallButtonSend("Sexta-Feira", 5);
        }
        else if(GetCodigo().contains(SystemLogin.nameAgropecuaria3)) {
            SetNameProf("Prof°: Marcelo", "Prof°: Pavão", "Prof°: Fernando Gomes", "Prof°: Nahelton", "Prof°: Arlene", null);
            CallButtonSend("Sexta-Feira", 5);
        }
    }

    public void Sabado()
    {
        SetNameProf(null, null, null, null, null, null);

        inputLayoutTXT.setVisibility(View.GONE);
        enviar.setEnabled(false);
        enviar.setText("");
        enviar.setHint("Somente dia de semana");
    }

    public void Domingo()
    {
        SetNameProf(null, null, null, null, null, null);

        inputLayoutTXT.setVisibility(View.GONE);
        enviar.setEnabled(false);
        enviar.setText("");
        enviar.setHint("Somente dia de semana");
    }

    public void CallButtonSend(final String dia, final int Value)
    {
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MensagemRelatorio();
                SendDataBase(dia, Value);
            }
        });
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

    public void SetNameProf(String prof1, String prof2, String prof3, String prof4, String prof5, String prof6)
    {
        if(prof1 != null) { professorTXT1.setText(prof1);professor1.setVisibility(View.VISIBLE);
            GetNameProf(true, false, false, false, false, false);GetSpinnerProf(true, false, false, false, false, false);}
        else { professor1.setVisibility(View.GONE); }

        if(prof2 != null) { professorTXT2.setText(prof2);professor2.setVisibility(View.VISIBLE);
            GetNameProf(true, true, false, false, false, false);GetSpinnerProf(true, true, false, false, false, false);}
        else { professor2.setVisibility(View.GONE); }

        if(prof3 != null) { professorTXT3.setText(prof3);professor3.setVisibility(View.VISIBLE);
            GetNameProf(true, true, true, false, false, false);GetSpinnerProf(true, true, true, false, false, false);}
        else { professor3.setVisibility(View.GONE); }

        if(prof4 != null) { professorTXT4.setText(prof4);professor4.setVisibility(View.VISIBLE);
            GetNameProf(true, true, true, true, false, false);GetSpinnerProf(true, true, true, true, false, false);}
        else { professor4.setVisibility(View.GONE); }

        if(prof5 != null) { professorTXT5.setText(prof5);professor5.setVisibility(View.VISIBLE);
            GetNameProf(true, true, true, true, true, false);GetSpinnerProf(true, true, true, true, true, false);}
        else { professor5.setVisibility(View.GONE); }

        if(prof6 != null) { professorTXT6.setText(prof6);professor6.setVisibility(View.VISIBLE);
            GetNameProf(true, true, true, true, true, true);GetSpinnerProf(true, true, true, true, true, true);}
        else { professor6.setVisibility(View.GONE); }
    }

    public String GetNameProf(boolean prof1, boolean prof2, boolean prof3, boolean prof4, boolean prof5, boolean prof6)
    {
        if(prof1) return professorTXT1.getText().toString();
        if(prof2) return professorTXT2.getText().toString();
        if(prof3) return professorTXT3.getText().toString();
        if(prof4) return professorTXT4.getText().toString();
        if(prof5) return professorTXT5.getText().toString();
        if(prof6) return professorTXT6.getText().toString();

        return null;
    }

    public String GetSpinnerProf(boolean prof1, boolean prof2, boolean prof3, boolean prof4, boolean prof5, boolean prof6)
    {
        if(prof1) return spinner1.getSelectedItem().toString();
        if(prof2) return spinner2.getSelectedItem().toString();
        if(prof3) return spinner3.getSelectedItem().toString();
        if(prof4) return spinner4.getSelectedItem().toString();
        if(prof5) return spinner5.getSelectedItem().toString();
        if(prof6) return spinner6.getSelectedItem().toString();

        return null;
    }

    public void SendDataBase(final String name, final int quantidade)
    {
        databaseReference.child(name).child(currentDate).child(GetNameProf(true, false, false, false, false, false)).setValue(GetSpinnerProf(true, false, false, false, false, false)).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                assert anotacaoTXT.getText() != null;

                if(quantidade == 3)
                {
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, true, false, false, false, false)).setValue(GetSpinnerProf(false, true, false, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, true, false, false, false)).setValue(GetSpinnerProf(false, false, true, false, false, false));
                    databaseReference.child(name).child(currentDate).child("Anotação").setValue(anotacaoTXT.getText().toString().trim());
                }
                else if(quantidade == 4)
                {
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, true, false, false, false, false)).setValue(GetSpinnerProf(false, true, false, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, true, false, false, false)).setValue(GetSpinnerProf(false, false, true, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, true, false, false)).setValue(GetSpinnerProf(false, false, false, true, false, false));
                    databaseReference.child(name).child(currentDate).child("Anotação").setValue(anotacaoTXT.getText().toString().trim());
                }
                else if(quantidade == 5)
                {
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, true, false, false, false, false)).setValue(GetSpinnerProf(false, true, false, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, true, false, false, false)).setValue(GetSpinnerProf(false, false, true, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, true, false, false)).setValue(GetSpinnerProf(false, false, false, true, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, false, true, false)).setValue(GetSpinnerProf(false, false, false, false, true, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, false, false, true)).setValue(GetSpinnerProf(false, false, false, false, false, true));
                    databaseReference.child(name).child(currentDate).child("Anotação").setValue(anotacaoTXT.getText().toString().trim());
                }
                else if(quantidade == 6)
                {
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, true, false, false, false, false)).setValue(GetSpinnerProf(false, true, false, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, true, false, false, false)).setValue(GetSpinnerProf(false, false, true, false, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, true, false, false)).setValue(GetSpinnerProf(false, false, false, true, false, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, false, true, false)).setValue(GetSpinnerProf(false, false, false, false, true, false));
                    databaseReference.child(name).child(currentDate).child(GetNameProf(false, false, false, false, false, true)).setValue(GetSpinnerProf(false, false, false, false, false, true));
                    databaseReference.child(name).child(currentDate).child("Anotação").setValue(anotacaoTXT.getText().toString().trim());
                }

                enviar.setEnabled(false);
                enviar.setText("");
                enviar.setHint("Relatório enviado");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(ProfessoresFalta.this, "Relatório não enviado!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void MensagemRelatorio()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(false);
        dialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot)
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}