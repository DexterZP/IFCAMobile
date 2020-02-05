package br.dexter.ifcamobile.Anexo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rey.material.app.Dialog;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<String> materias;
    private ArrayList<String> items;
    private ArrayList<String> urls;
    private ArrayList<Long> sizes;
    private ArrayList<String> datas;

    private ImageView ImgDialog;
    private TextView fileName, materiasName, sizeFile, verifyFile, dataFile;
    private SmoothProgressBar circularProgressBar;

    MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> path, ArrayList<String> items, ArrayList<String> urls, ArrayList<Long> sizes, ArrayList<String> data)
    {
        this.recyclerView = recyclerView;
        this.context = context;
        this.materias = path;
        this.items = items;
        this.urls = urls;
        this.sizes = sizes;
        this.datas = data;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_anexos_enviados_card, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n") @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        viewHolder.nameOfFile.setText(items.get(i));
        viewHolder.nameOfPath.setText(materias.get(i));
        viewHolder.nameOfData.setText("Data: " + datas.get(i));

        if (urls.get(i).contains(".docx") || urls.get(i).contains(".doc")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_doc);
        } else if (urls.get(i).contains(".pptx") || urls.get(i).contains(".ppt")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_slide);
        } else if (urls.get(i).contains(".xlsx") || urls.get(i).contains(".xls")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_xls);
        } else if (urls.get(i).contains(".pdf")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_pdf);
        } else if (urls.get(i).contains(".jpg") || urls.get(i).contains(".jpeg")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_jpg);
        } else if (urls.get(i).contains(".png")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_png);
        } else if (urls.get(i).contains(".txt")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_txt);
        } else if (urls.get(i).contains(".rar") || urls.get(i).contains(".zip")) {
            viewHolder.nameOfImage.setImageResource(R.drawable.ic_zip);
        }
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameOfFile;
        TextView nameOfPath;
        TextView nameOfData;
        ImageView nameOfImage;

        ViewHolder(final View itemView)
        {
            super(itemView);

            nameOfFile = itemView.findViewById(R.id.nameOfFile);
            nameOfPath = itemView.findViewById(R.id.nameOfPath);
            nameOfData = itemView.findViewById(R.id.nameOfData);
            nameOfImage = itemView.findViewById(R.id.nameOfImage);

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
            {
                @Override
                public void onCreateContextMenu(final ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo)
                {
                    if(FirebaseAuth.getInstance().getCurrentUser() == null)
                    {
                        Toast.makeText(v.getContext(), "Você não pode editar esse arquivo", Toast.LENGTH_LONG).show();
                        return;
                    }

                    menu.add("Deletar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            final Dialog dialog = new Dialog(v.getContext());
                            dialog.setContentView(R.layout.dialog);
                            dialog.setCancelable(false);
                            dialog.show();

                            final int position = recyclerView.getChildLayoutPosition(v);

                            String GetCodigo = GetCodigo(); assert GetCodigo != null;
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(GetCodigo);
                            final StorageReference storageReference = FirebaseStorage.getInstance().getReference(GetCodigo);

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

                                    databaseReference.child(v.getResources().getString(R.string.DatabaseName)).child("Enviados").child(items.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        String getExtension = getFileUrlExtension(urls.get(position));

                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            storageReference.child(v.getResources().getString(R.string.DatabaseName)).child(materias.get(position)).child(items.get(position) + getExtension).delete().addOnSuccessListener(new OnSuccessListener<Void>()
                                            {
                                                @Override
                                                public void onSuccess(Void aVoid)
                                                {
                                                    Toast.makeText(v.getContext(), "Removido com sucesso", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(v.getContext(), "Erro ao remover anexo", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                            return true;
                        }
                    });
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    final int position = recyclerView.getChildLayoutPosition(v);
                    final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), items.get(position) + getFileUrlExtension(urls.get(position)));

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    final ViewGroup dialog = (ViewGroup) View.inflate(v.getContext(), R.layout.activity_anexos_enviados_click, null);
                    dialog.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));

                    ImgDialog = dialog.findViewById(R.id.nameImage);
                    fileName = dialog.findViewById(R.id.nameFile);
                    materiasName = dialog.findViewById(R.id.nameMat);
                    sizeFile = dialog.findViewById(R.id.nameSize);
                    verifyFile = dialog.findViewById(R.id.Verify);
                    dataFile = dialog.findViewById(R.id.nameData);

                    if (urls.get(position).contains(".docx") || urls.get(position).contains(".doc")) {
                        ImgDialog.setImageResource(R.drawable.ic_doc);
                    } else if (urls.get(position).contains(".pptx") || urls.get(position).contains(".ppt")) {
                        ImgDialog.setImageResource(R.drawable.ic_slide);
                    } else if (urls.get(position).contains(".xlsx") || urls.get(position).contains(".xls")) {
                        ImgDialog.setImageResource(R.drawable.ic_xls);
                    } else if (urls.get(position).contains(".pdf")) {
                        ImgDialog.setImageResource(R.drawable.ic_pdf);
                    } else if (urls.get(position).contains(".jpg") || urls.get(position).contains(".jpeg")) {
                        ImgDialog.setImageResource(R.drawable.ic_jpg);
                    } else if (urls.get(position).contains(".png")) {
                        ImgDialog.setImageResource(R.drawable.ic_png);
                    } else if (urls.get(position).contains(".txt")) {
                        ImgDialog.setImageResource(R.drawable.ic_txt);
                    } else if (urls.get(position).contains(".rar") || urls.get(position).contains(".zip")) {
                        ImgDialog.setImageResource(R.drawable.ic_zip);
                    }

                    fileName.setText("Nome: " + items.get(position));
                    materiasName.setText("Disciplina: " + materias.get(position));
                    sizeFile.setText("Tamanho: " + getFolderSizeLabel(sizes.get(position)));
                    if(datas.get(position) != null)
                        dataFile.setText("Data: " + datas.get(position));
                    else
                        dataFile.setText("Data: não identificada");

                    if(file.exists()) verifyFile.setText("Arquivo: Baixado");
                    else verifyFile.setText("Arquivo: Não baixado");

                    builder.setPositiveButton("Abrir", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(!file.exists())
                            {
                                Toast.makeText(v.getContext(), "Esse arquivo não existe em seu dispositivo", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(checkStatus(v.getContext()))
                            {
                                Toast.makeText(v.getContext(), "Download em progresso", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Intent intent = new Intent();
                            intent.setDataAndType(Uri.fromFile(file), getFileExtensionOpen(urls.get(position)));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Intent chooserIntent = Intent.createChooser(intent, "Abrir arquivo");
                            context.startActivity(chooserIntent);
                        }
                    });

                    builder.setNeutralButton("Download", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            if(file.exists())
                            {
                                Toast.makeText(v.getContext(), "Esse arquivo já existe em seu dispositivo", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(checkStatus(v.getContext()))
                            {
                                Toast.makeText(v.getContext(), "Download em progresso", Toast.LENGTH_LONG).show();
                                return;
                            }

                            circularProgressBar = v.findViewById(R.id.CircularProgress);
                            circularProgressBar.setVisibility(View.VISIBLE);

                            DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(urls.get(position)));
                            dmr.setTitle(items.get(position) + getFileUrlExtension(urls.get(position)));
                            dmr.setDescription("IFCA Downloads");
                            dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, items.get(position) + getFileUrlExtension(urls.get(position)));
                            dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            DownloadManager manager = (DownloadManager) v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                            if(manager != null) {
                                manager.enqueue(dmr);
                                Toast.makeText(v.getContext(), "Iniciando Download", Toast.LENGTH_SHORT).show();

                                context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                                dialog.dismiss();
                            }
                            else {
                                dialog.dismiss();
                            }
                        }
                    });

                    builder.setView(dialog);
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                    pbutton.setScaleY(0.9f); pbutton.setScaleX(0.9f);
                    nbutton.setScaleY(0.9f); nbutton.setScaleX(0.9f);
                    pbutton.setBackgroundColor(context.getResources().getColor(R.color.azure));
                    pbutton.setTextColor(Color.WHITE);
                    nbutton.setBackgroundColor(context.getResources().getColor(R.color.azure));
                    nbutton.setTextColor(Color.WHITE);
                }
            });
        }
    }

    private BroadcastReceiver onComplete = new BroadcastReceiver()
    {
        public void onReceive(Context ctxt, Intent intent)
        {
            circularProgressBar.setVisibility(View.GONE);
        }
    };

    private String getFolderSizeLabel(long sizes)
    {
        long size = sizes / 1024;

        if (size >= 1024)
        {
            return (size / 1024) + " Mb";
        }
        else
        {
            return size + " Kb";
        }
    }

    private boolean checkStatus(@NonNull Context context)
    {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
        assert downloadManager != null;
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst())
        {
            c.close();
            return true;
        }
        return false;
    }

    @Nullable
    private String GetCodigo()
    {
        try
        {
            FileInputStream fin = context.openFileInput(SystemLogin.nameCodTxt);
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

    @Nullable
    private String getFileExtensionOpen(@NonNull String url)
    {
        if(url.contains(".docx")) { return "application/msword"; }
        else if(url.contains(".doc")) { return "application/msword"; }
        else if(url.contains(".pptx")) { return "application/vnd.ms-powerpoint"; }
        else if(url.contains(".ppt")) { return "application/vnd.ms-powerpoint"; }
        else if(url.contains(".xlsx")) { return "application/vnd.ms-excel"; }
        else if(url.contains(".xls")) { return "application/vnd.ms-excel"; }
        else if(url.contains(".pdf")) { return "application/pdf"; }
        else if(url.contains(".jpg")) { return "image/jpeg"; }
        else if(url.contains(".jpeg")) { return "image/jpeg"; }
        else if(url.contains(".png")) { return "image/jpeg"; }
        else if(url.contains(".txt")) { return "text/plain"; }
        else if(url.contains(".rar")) { return "application/x-wav"; }
        else if(url.contains(".zip")) { return "application/x-wav"; }

        return null;
    }

    @Nullable
    private String getFileUrlExtension(@NonNull String url)
    {
        if(url.contains(".docx")) { return ".docx"; }
        else if(url.contains(".doc")) { return ".doc"; }
        else if(url.contains(".pptx")) { return ".pptx"; }
        else if(url.contains(".ppt")) { return ".ppt"; }
        else if(url.contains(".xlsx")) { return ".xlsx"; }
        else if(url.contains(".xls")) { return ".xls"; }
        else if(url.contains(".pdf")) { return ".pdf"; }
        else if(url.contains(".jpg")) { return ".jpg"; }
        else if(url.contains(".jpeg")) { return ".jpge"; }
        else if(url.contains(".png")) { return ".png"; }
        else if(url.contains(".txt")) { return ".txt"; }
        else if(url.contains(".rar")) { return ".rar"; }
        else if(url.contains(".zip")) { return ".zip"; }

        return null;
    }
}