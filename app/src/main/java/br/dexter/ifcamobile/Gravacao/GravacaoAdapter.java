package br.dexter.ifcamobile.Gravacao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.UUID;

import br.dexter.ifcamobile.R;

public class GravacaoAdapter extends RecyclerView.Adapter<GravacaoAdapter.ViewHolder>
{
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener, mClickRemover, mClickRename;

    private LinearLayout edition;

    static String RandomID = "";

    GravacaoAdapter(Context context, List<String> data)
    {
        RandomID = UUID.randomUUID().toString();
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.activity_gravacao_adapter, parent, false);

        edition = view.findViewById(R.id.Opcao);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        final String name = mData.get(position);
        holder.myTextView.setText(name);
        holder.sizeFile.setText(getFolderSizeLabel(position));

        edition.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(name)
                .setCancelable(true)
                .setPositiveButton("Renomear", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        File searchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp3");

                        if(!searchFile.exists())
                        {
                            Toast.makeText(mInflater.getContext(), "Arquivo inexistente...\nRemoção automática", Toast.LENGTH_SHORT).show();

                            mData.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (mClickRemover != null) mClickRemover.onItemRemove(v, holder.getAdapterPosition());
                            dialog.cancel();
                            return;
                        }

                        AlertDialog.Builder alert = new AlertDialog.Builder(mInflater.getContext());

                        final EditText edittext = new EditText(mInflater.getContext());
                        alert.setTitle("Renomear gravação");

                        alert.setView(edittext);

                        alert.setPositiveButton("Renomear", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                File searchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/");

                                String YouEditTextValue = edittext.getText().toString();
                                mData.set(holder.getAdapterPosition(), YouEditTextValue);
                                notifyItemChanged(holder.getAdapterPosition());

                                File from = new File(searchFile,name + ".mp3");
                                File to = new File(searchFile,YouEditTextValue + ".mp3");
                                boolean rename = from.renameTo(to);

                                if(rename) {
                                    Log.d("LOG", "File renamed!");
                                }
                                else {
                                    Log.d("LOG", "File not renamed!");
                                }

                                if (mClickRename != null) mClickRename.onItemRename(v, holder.getAdapterPosition());
                            }
                        });

                        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                dialog.cancel();
                            }
                        });

                        alert.show();
                    }
                })
                .setNeutralButton("Excluir", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        final File searchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp3");

                        if(!searchFile.exists())
                        {
                            Toast.makeText(mInflater.getContext(), "Arquivo inexistente...\nRemoção automática", Toast.LENGTH_SHORT).show();

                            mData.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (mClickRemover != null) mClickRemover.onItemRemove(v, holder.getAdapterPosition());
                            dialog.cancel();
                            return;
                        }

                        mData.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());

                        boolean deleted = searchFile.delete();

                        if(deleted) {
                            Log.d("LOG", "File deleted!");
                        }
                        else {
                            Log.d("LOG", "File not deleted!");
                        }

                        if (mClickRemover != null) mClickRemover.onItemRemove(v, holder.getAdapterPosition());
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                pbutton.setScaleY(0.9f); pbutton.setScaleX(0.9f);
                nbutton.setScaleY(0.9f); nbutton.setScaleX(0.9f);
                pbutton.setBackgroundColor(v.getResources().getColor(R.color.azure));
                nbutton.setBackgroundColor(v.getResources().getColor(R.color.azure));
                pbutton.setTextColor(Color.WHITE);
                nbutton.setTextColor(Color.WHITE);
            }
        });
    }

    @NonNull
    private String getFolderSizeLabel(int position)
    {
        File searchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + mData.get(position) + ".mp3");
        long file = searchFile.length();

        long size = file / 1024;

        if (size >= 1024)
        {
            return (size / 1024) + " Mb";
        }
        else
        {
            return size + " Kb";
        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView myTextView;
        TextView sizeFile;

        ViewHolder(View itemView)
        {
            super(itemView);
            myTextView = itemView.findViewById(R.id.nameOfFile);
            sizeFile = itemView.findViewById(R.id.sizeFile);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            final File searchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + mData.get(getAdapterPosition()) + ".mp3");

            if(!searchFile.exists())
            {
                Toast.makeText(mInflater.getContext(), "Arquivo inexistente...\nRemoção automática", Toast.LENGTH_SHORT).show();

                mData.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                if (mClickRemover != null) mClickRemover.onItemRemove(view, getAdapterPosition());
                return;
            }

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /*String getItem(int id)
    {
        return mData.get(id);
    }*/

    void setClickListener(ItemClickListener itemClickListener, ItemClickListener itemRemoverListener, ItemClickListener itemRenameListener)
    {
        this.mClickListener = itemClickListener;
        this.mClickRemover = itemRemoverListener;
        this.mClickRename = itemRenameListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemRemove(View view, int position);
        void onItemRename(View view, int position);
    }
}