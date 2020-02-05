package br.dexter.ifcamobile.SistemaLogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rey.material.app.Dialog;

import java.io.FileOutputStream;
import java.util.Random;

import br.dexter.ifcamobile.MainActivity;
import br.dexter.ifcamobile.R;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class SystemLogin extends AppCompatActivity
{
    public Button entrar;

    private TextView randomTxt;

    private LinearLayout Linear;

    private TextInputLayout textInputLayout;
    private TextInputEditText matricula;

    public static String nameInformatica3 = "Informática III";
    public static String cod_Informatica3 = "20171CXIC";

    public static String nameInformatica2 = "Informática II";
    public static String cod_Informatica2 = "20181IC.CAX";

    public static String nameAgroindustria1 = "Agroindústria I";
    public static String cod_Agroindustria1 = "20191A.CAX";

    public static String nameAgroindustria2 = "Agroindústria II";
    public static String cod_Agroindustria2 = "20181A.CAX";

    public static String nameAgroindustria3 = "Agroindústria III";
    public static String cod_Agroindustria3 = "20171CXA";

    public static String nameAgropecuaria1 = "Agropecuária I";
    public static String cod_Agropecuaria1 = "20191AP.CAX";

    public static String nameAgropecuaria2 = "Agropecuária II";
    public static String cod_Agropecuaria2 = "20181AP.CAX";

    public static String nameAgropecuaria3 = "Agropecuária III";
    public static String cod_Agropecuaria3 = "20171CXAP";

    public static String nameAdministracao1 = "Administração I";
    public static String cod_Administracao1 = "20191AD.CAX";

    public static String nameAdministracao2 = "Administração II";
    public static String cod_Administracao2 = "20181AD.CAX";

    public static String nameAdministracao3 = "Administração III";
    public static String cod_Administracao3 = "2017CXAD";

    public static String nameCodTxt = "codMatricula.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_system_login);

        Linear = findViewById(R.id.Linear);
        matricula = findViewById(R.id.Matricula);
        entrar = findViewById(R.id.Entrar);
        textInputLayout = findViewById(R.id.textInputLayout);
        randomTxt = findViewById(R.id.youSabia);

        configLayout();

        entrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert matricula.getText() != null;

                String cod = matricula.getText().toString().toUpperCase();

                if(cod.equals(""))
                {
                    textInputLayout.setError("Não deixe os campos vazios");
                }
                else if(cod.contains(cod_Informatica3) || cod.contains(cod_Informatica2) ||
                        cod.contains(cod_Administracao1) || cod.contains(cod_Administracao2) || cod.contains(cod_Administracao3) ||
                        cod.contains(cod_Agroindustria1) || cod.contains(cod_Agroindustria2) || cod.contains(cod_Agroindustria3) ||
                        cod.contains(cod_Agropecuaria1) || cod.contains(cod_Agropecuaria2) || cod.contains(cod_Agropecuaria3)) {
                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            dialog.dismiss();

                            Intent intent = new Intent(SystemLogin.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            SavedCodigo();
                        }
                    },1000);
                }
                else
                {
                    textInputLayout.setError("Matrícula não encontrada");
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void configLayout()
    {
        String[] array = getResources().getStringArray(R.array.RandomTXT);
        String randomStr = array[new Random().nextInt(array.length)];

        randomTxt.setText("''" + randomStr + "''");

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
        Linear.setLayoutAnimation(controller);
    }

    public void SavedCodigo()
    {
        FileOutputStream outputStream;
        assert matricula.getText() != null;

        try
        {
            outputStream = openFileOutput(nameCodTxt, Context.MODE_PRIVATE);
            outputStream.write(matricula.getText().toString().toUpperCase().getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}