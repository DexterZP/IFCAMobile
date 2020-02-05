package br.dexter.ifcamobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;

import br.dexter.ifcamobile.ADM.LoginLider;
import br.dexter.ifcamobile.Ajuda.Feedback;
import br.dexter.ifcamobile.Anexo.Anexos;
import br.dexter.ifcamobile.CalendarioACAD.calendarioACAD;
import br.dexter.ifcamobile.CalendarioACAD.noticias;
import br.dexter.ifcamobile.Gravacao.Gravacao;
import br.dexter.ifcamobile.Horario.Horario;
import br.dexter.ifcamobile.Semana.ResumoSemanal;
import br.dexter.ifcamobile.Service.NotificationAnexosADD;
import br.dexter.ifcamobile.Service.NotificationResumo;
import br.dexter.ifcamobile.Service.NotificationResumoChanged;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import br.dexter.ifcamobile.Sobre.Sobre;
import de.hdodenhof.circleimageview.CircleImageView;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private TextView TurmaConnect;

    private CircleImageView ProfileGerente;
    private LinearLayout mostrarAtividades, mostrarAnexos, mostrarNoticias;
    private TextView nameAnexo, nameAtividade, lastResumo, lastFile, lastNoticia;
    private CardView card1, card2, card3;
    private View view1, view2, view3;
    private LinearLayout adm, logout, sobre;
    private DatabaseReference databaseReference;
    private ShimmerRecyclerView shimmerRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        card1 = findViewById(R.id.Card1);
        card2 = findViewById(R.id.Card2);
        card3 = findViewById(R.id.Card3);

        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);

        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);

        ImageView handleMenu = findViewById(R.id.MenuHandle);
        ImageView handleSettings = findViewById(R.id.Settings);

        view1 = findViewById(R.id.View1);
        view2 = findViewById(R.id.View2);
        view3 = findViewById(R.id.View3);

        mostrarAtividades = findViewById(R.id.MostrarMaisAtividades);
        mostrarAnexos = findViewById(R.id.MostrarMaisAnexos);
        mostrarNoticias = findViewById(R.id.MostrarMaisNoticias);

        nameAnexo = findViewById(R.id.nameAnexo);
        nameAtividade = findViewById(R.id.nameAtividade);

        lastResumo = findViewById(R.id.LastResumo);
        lastFile = findViewById(R.id.LastPath);
        lastNoticia = findViewById(R.id.LastNoticia);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        handleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        handleSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final BottomSheetDialog mDialog = new BottomSheetDialog(MainActivity.this);

                mDialog.setContentView(R.layout.activity_sheet_dialog);
                mDialog.show();

                adm = mDialog.findViewById(R.id.sheet_adm);
                logout = mDialog.findViewById(R.id.sheet_logout);
                sobre = mDialog.findViewById(R.id.sheet_sobre);

                adm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LoginLider.class);
                        startActivity(intent);
                        mDialog.hide();
                    }
                });

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFile(SystemLogin.nameCodTxt);

                        getApplication().getSharedPreferences("AppStorage", Context.MODE_PRIVATE).edit().clear().apply();

                        Intent intent = new Intent(MainActivity.this, SystemLogin.class);
                        startActivity(intent);
                        finish();
                        mDialog.hide();
                    }
                });

                sobre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Sobre.class);
                        startActivity(intent);
                        mDialog.hide();
                    }
                });
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        TurmaConnect = headerView.findViewById(R.id.TurmaConnect);
        ProfileGerente = headerView.findViewById(R.id.ProfileGerente);

        ButtonsMenu();
        SyncTheMain();
        CarregarAtividades();
        CarregarRandom();
        CarregarNotify();
    }

    private void CarregarNotify()
    {
        NotificationResumo.startBaseAlarmManager(MainActivity.this);
        NotificationResumo.enableBootReceiver(this);

        NotificationResumoChanged.startBaseAlarmManager(MainActivity.this);
        NotificationResumoChanged.enableBootReceiver(this);

        NotificationAnexosADD.startBaseAlarmManager(MainActivity.this);
        NotificationAnexosADD.enableBootReceiver(this);
    }

    private void CarregarRandom() {
        Random rnd1 = new Random();
        Random rnd2 = new Random();
        Random rnd3 = new Random();

        int color1 = Color.argb(255, rnd1.nextInt(256), rnd1.nextInt(256), rnd1.nextInt(256));
        int color2 = Color.argb(255, rnd2.nextInt(256), rnd2.nextInt(256), rnd2.nextInt(256));
        int color3 = Color.argb(255, rnd3.nextInt(256), rnd3.nextInt(256), rnd3.nextInt(256));

        view1.setBackgroundColor(color1);
        view2.setBackgroundColor(color2);
        view3.setBackgroundColor(color3);
    }

    private void SyncTheMain()
    {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                handler.postDelayed(this, 1000);
                try
                {
                    CarregarAtividades();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void ButtonsMenu()
    {
        mostrarAtividades.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, ResumoSemanal.class);
                startActivity(intent);
            }
        });

        mostrarAnexos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, Anexos.class);
                startActivity(intent);
            }
        });

        mostrarNoticias.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, noticias.class);
                startActivity(intent);
            }
        });

        ProfileGerente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                final ViewGroup dialog = (ViewGroup) View.inflate(MainActivity.this, R.layout.choose_avatar, null);
                dialog.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left));

                CircleImageView heroi1 = dialog.findViewById(R.id.heroi1);
                CircleImageView heroi2 = dialog.findViewById(R.id.heroi2);
                CircleImageView heroi3 = dialog.findViewById(R.id.heroi3);
                CircleImageView heroi4 = dialog.findViewById(R.id.heroi4);
                CircleImageView heroi5 = dialog.findViewById(R.id.heroi5);
                CircleImageView heroi6 = dialog.findViewById(R.id.heroi6);
                CircleImageView heroi7 = dialog.findViewById(R.id.heroi7);
                CircleImageView heroi8 = dialog.findViewById(R.id.heroi8);
                CircleImageView heroi9 = dialog.findViewById(R.id.heroi9);

                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        dialog.dismiss();
                    }
                });

                heroi1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi1);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi1", true).apply();
                    }
                });
                heroi2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi2);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi2", true).apply();
                    }
                });
                heroi3.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi3);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi3", true).apply();
                    }
                });
                heroi4.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi4);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi4", true).apply();
                    }
                });
                heroi5.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi5);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi5", true).apply();
                    }
                });
                heroi6.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi6);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi6", true).apply();
                    }
                });
                heroi7.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi7);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi7", true).apply();
                    }
                });
                heroi8.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi8);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi8", true).apply();
                    }
                });
                heroi9.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProfileGerente.setImageResource(R.drawable.heroi9);
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().clear().apply();
                        getApplication().getSharedPreferences("Heroi", Context.MODE_PRIVATE).edit().putBoolean("heroi9", true).apply();
                    }
                });

                builder.setView(dialog);
                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setScaleY(0.9f); pbutton.setScaleX(0.9f);
                pbutton.setBackgroundColor(getResources().getColor(R.color.azure));
                pbutton.setTextColor(Color.WHITE);
            }
        });

        if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi1", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi1);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi2", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi2);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi3", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi3);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi4", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi4);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi5", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi5);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi6", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi6);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi7", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi7);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi8", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi8);
        }else if(getApplication().getSharedPreferences("Heroi", MODE_PRIVATE).getBoolean("heroi9", false)) {
            ProfileGerente.setImageResource(R.drawable.heroi9);
        }
    }

    public static class MyTask extends AsyncTask<Void, Void, String>
    {
        StringBuilder sp = new StringBuilder();

        private WeakReference<MainActivity> activityReference;

        MyTask(MainActivity context)
        {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                Document doc = Jsoup.connect("https://caxias.ifma.edu.br/categoria/noticias/").get();
                Elements title = doc.getElementsByClass("noticia-link");

                sp.append(title.text());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return "task finish";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            activityReference.get().databaseReference.child("IFMA - Notícias").setValue(sp.toString());
        }
    }

    @SuppressLint("SetTextI18n")
    private void CarregarAtividades()
    {
        SharedPreferences sp = getSharedPreferences("AppStorage", 0);
        String segundatxt = sp.getString("segundaFeira", "");
        String tercatxt = sp.getString("tercaFeira", "");
        String quartatxt = sp.getString("quartaFeira", "");
        String quintatxt = sp.getString("quintaFeira", "");
        String sextatxt = sp.getString("sextaFeira", "");

        if(segundatxt.contains("yep") && tercatxt.contains("yep") && quartatxt.contains("yep") && quintatxt.contains("yep") && sextatxt.contains("yep")) {
            nameAtividade.setText("Todas as atividades do resumo semanal foram concluídas");
        }
        else {
            nameAtividade.setText("Algumas atividades do resumo semanal, estão pendentes");
        }

        databaseReference.child("IFMA - Notícias").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String lastNote = dataSnapshot.getValue(String.class);

                    lastNoticia.setText("Última notícia: " + lastNote);

                    shimmerRecycler.setVisibility(View.GONE);

                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);

                    new MyTask(MainActivity.this).execute();
                }
                else
                {
                    shimmerRecycler.setVisibility(View.GONE);

                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);

                    new MyTask(MainActivity.this).execute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference.child(GetCodigo()).child("Resumo Semanal").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String lastUpdate = dataSnapshot.child("Last Update").getValue(String.class);

                    lastResumo.setText("Última alteração: " + lastUpdate);

                    shimmerRecycler.setVisibility(View.GONE);

                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);
                }
                else
                {
                    lastResumo.setText("Última alteração: 25 mar 1964");

                    shimmerRecycler.setVisibility(View.GONE);

                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference.child(GetCodigo()).child(getResources().getString(R.string.DatabaseName)).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String lastPath = dataSnapshot.child("Last Path").getValue(String.class);

                    nameAnexo.setText("Dê uma olhada nos arquivos que sua turma anexou");
                    lastFile.setText("Último anexo enviado: " + lastPath);

                    shimmerRecycler.setVisibility(View.GONE);

                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);
                }
                else
                {
                    nameAnexo.setText("Sua turma ainda não anexou nenhum arquivo");
                    lastFile.setText("Último anexo enviado: nenhum");

                    shimmerRecycler.setVisibility(View.GONE);

                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        TurmaConnect.setText(GetCodigo());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.nav_resumo)
        {
            Intent intent = new Intent(MainActivity.this, ResumoSemanal.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_horario)
        {
            Intent intent = new Intent(MainActivity.this, Horario.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_anexo)
        {
            Intent intent = new Intent(MainActivity.this, Anexos.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_calendarioACAD)
        {
            Intent intent = new Intent(MainActivity.this, calendarioACAD.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_gravar)
        {
            Intent intent = new Intent(MainActivity.this, Gravacao.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_ajuda)
        {
            Intent intent = new Intent(MainActivity.this, Feedback.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_noticia)
        {
            Intent intent = new Intent(MainActivity.this, noticias.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String GetCodigo()
    {
        try
        {
            FileInputStream fin = openFileInput(SystemLogin.nameCodTxt);
            StringBuilder sb = new StringBuilder();
            int size;

            while ((size = fin.read()) != -1) {
                sb.append((char) size);
            }

            if (sb.toString().contains(SystemLogin.cod_Informatica3)) {
                return SystemLogin.nameInformatica3;
            } else if (sb.toString().contains(SystemLogin.cod_Informatica2)) {
                return SystemLogin.nameInformatica2;
            } else if (sb.toString().contains(SystemLogin.cod_Administracao1)) {
                return SystemLogin.nameAdministracao1;
            } else if (sb.toString().contains(SystemLogin.cod_Administracao2)) {
                return SystemLogin.nameAdministracao2;
            } else if (sb.toString().contains(SystemLogin.cod_Administracao3)) {
                return SystemLogin.nameAdministracao3;
            } else if (sb.toString().contains(SystemLogin.cod_Agroindustria1)) {
                return SystemLogin.nameAgroindustria1;
            } else if (sb.toString().contains(SystemLogin.cod_Agroindustria2)) {
                return SystemLogin.nameAgroindustria2;
            } else if (sb.toString().contains(SystemLogin.cod_Agroindustria3)) {
                return SystemLogin.nameAgroindustria3;
            } else if (sb.toString().contains(SystemLogin.cod_Agropecuaria1)) {
                return SystemLogin.nameAgropecuaria1;
            } else if (sb.toString().contains(SystemLogin.cod_Agropecuaria2)) {
                return SystemLogin.nameAgropecuaria2;
            } else if (sb.toString().contains(SystemLogin.cod_Agropecuaria3)) {
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