package br.dexter.ifcamobile.Horario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import br.dexter.ifcamobile.R;

public class Horario_1 extends Fragment
{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.horario_1, container, false);

        PDFView pdfView = view.findViewById(R.id.pdfView);

        pdfView.fromAsset("Horario.pdf").load();

        return view;
    }
}
