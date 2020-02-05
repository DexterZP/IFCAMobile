package br.dexter.ifcamobile.Anexo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class Anexos extends AppCompatActivity
{
    private StorageReference mStorabase;
    private DatabaseReference mDatabase;

    private ShimmerRecyclerView shimmerRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private CardView cardAnexos;
    private Spinner spinner;
    private FloatingActionButton EnviarFile;
    private Button EscolherArquivo, CancelarEnvio;
    private CircularProgressBar circularProgressBar;

    private ArrayList<String> path = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<Long> size = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_anexos);

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        shimmerRecyclerView = findViewById(R.id.shimmer_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        mStorabase = FirebaseStorage.getInstance().getReference().child(GetCodigo());
        mDatabase = FirebaseDatabase.getInstance().getReference().child(GetCodigo());

        recyclerView = findViewById(R.id.recyclerView);
        cardAnexos = findViewById(R.id.CardNotAnexos);
        EnviarFile = findViewById(R.id.EnviarFile);
        circularProgressBar = findViewById(R.id.ProgressBar);

        CarregarItens();

        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(getResources().getString(R.string.DatabaseName)).child("Enviados").exists())
                {
                    cardAnexos.setVisibility(View.GONE);
                    shimmerRecyclerView.setVisibility(View.GONE);
                }
                else
                {
                    cardAnexos.setVisibility(View.VISIBLE);
                    shimmerRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.child(getResources().getString(R.string.DatabaseName)).child("Enviados").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                String path1 = dataSnapshot.child("Path").getValue(String.class);
                String name1 = dataSnapshot.child("Nome").getValue(String.class);
                String url1 = dataSnapshot.child("Download").getValue(String.class);
                String data1 = dataSnapshot.child("Data").getValue(String.class);
                Long size1 = dataSnapshot.child("Tamanho").getValue(Long.class);

                if(path1 != null && url1 != null && name1 != null && size1 != null && data1 != null) {

                    path.add(path1);
                    url.add(url1);
                    name.add(name1);
                    size.add(size1);
                    data.add(data1);

                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(Anexos.this, 1);
                    recyclerView.setLayoutManager(mLayoutManager);

                    MyAdapter myAdapter = new MyAdapter(recyclerView, Anexos.this, path, name, url, size, data);
                    recyclerView.setAdapter(myAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void CarregarItens()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                finish();
                startActivity(getIntent());
            }
        });

        EnviarFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final BottomSheetDialog mDialog = new BottomSheetDialog(Anexos.this);

                mDialog.setContentView(R.layout.activity_anexos_sendfile);
                mDialog.show();

                spinner = mDialog.findViewById(R.id.SelectMateria);
                EscolherArquivo = mDialog.findViewById(R.id.EscolherArquivo);
                CancelarEnvio = mDialog.findViewById(R.id.Cancelar);

                if(GetCodigo().equals(SystemLogin.nameAdministracao1)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_ADM1, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameAdministracao2)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_ADM2, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameAgroindustria1)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_AGROINDUSTRIA1, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameAgroindustria2)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_AGROINDUSTRIA2, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameAgroindustria3)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_AGROINDUSTRIA3, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameAgropecuaria2)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_AGROPECUARIA2, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameAgropecuaria3)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_AGROPECUARIA3, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameInformatica2)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_Infor2, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                else if(GetCodigo().equals(SystemLogin.nameInformatica3)) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Anexos.this, R.array.Materias_Infor3, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }

                EscolherArquivo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent, "Escolha o seu Gerenciador de Arquivos"), 86);

                        mDialog.dismiss();
                    }
                });

                CancelarEnvio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    private void EnviarFile(final Uri fileUri, @NonNull String nameFileUri)
    {
        final String fileName = nameFileUri;
        final String fileName1 = nameFileUri.replaceFirst("[.][^.]+$", "");
        final String filePath = spinner.getSelectedItem().toString();

        final StorageReference ref = mStorabase.child(getResources().getString(R.string.DatabaseName)).child(filePath).child(fileName);
        final UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
            {
                circularProgressBar.setVisibility(View.VISIBLE);
                EnviarFile.setVisibility(View.GONE);

                float currentProgress = (float) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                circularProgressBar.setProgress((int) (currentProgress / 100));
            }
        });

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                if (!task.isSuccessful())
                {
                    assert task.getException() != null;
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    final Uri downloadUri = task.getResult();

                    if(downloadUri != null)
                    {
                        mStorabase.child(getResources().getString(R.string.DatabaseName)).child(filePath).child(fileName).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>()
                        {
                            @Override
                            public void onSuccess(final StorageMetadata storageMetadata)
                            {
                                Calendar calendar = Calendar.getInstance();
                                Locale myLocale = new Locale("pt", "PT");
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMM", myLocale);
                                String currentDate = sdf.format(calendar.getTime());

                                Post post = new Post(fileName1, filePath, downloadUri.toString(), currentDate, storageMetadata.getSizeBytes());
                                Map<String, Object> postValue = post.toMap();

                                mDatabase.child(getResources().getString(R.string.DatabaseName)).child("Enviados").child(fileName1).updateChildren(postValue);
                                mDatabase.child(getResources().getString(R.string.DatabaseName)).child("Last Path").setValue(fileName1);

                                Toast.makeText(Anexos.this, "Arquivo enviado", Toast.LENGTH_LONG).show();

                                circularProgressBar.setVisibility(View.GONE);
                                EnviarFile.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(Anexos.this, "Arquivo não carregado", Toast.LENGTH_LONG).show();

                circularProgressBar.setVisibility(View.GONE);
                EnviarFile.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null)
        {
            Uri fileUri = data.getData();

            if(fileUri != null)
            {
                String fileExtension = getMimeType(Anexos.this, fileUri);

                if (fileExtension.matches("docx|doc|pptx|ppt|xlsx|xls|pdf|jpg|jpeg|png|txt|zip|rar"))
                {
                    EnviarFile(fileUri, queryName(fileUri));
                }
                else
                {
                    Toast.makeText(Anexos.this, "Extensão de arquivo não suportada", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(Anexos.this, "Arquivo não encontrado", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(Anexos.this, "Por favor, selecione um arquivo", Toast.LENGTH_LONG).show();
        }
    }

    private String queryName(Uri uri)
    {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();

        return name;
    }

    private static String getMimeType(Context context, @NonNull Uri uri)
    {
        String extension;

        assert uri.getScheme() != null;
        assert uri.getPath() != null;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))
        {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        }
        else
        {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
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