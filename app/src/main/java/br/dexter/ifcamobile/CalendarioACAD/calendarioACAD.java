package br.dexter.ifcamobile.CalendarioACAD;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import br.dexter.ifcamobile.R;
import in.myinnos.customfontlibrary.TypefaceUtil;

public class calendarioACAD extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/minhafonte.ttf");
        setContentView(R.layout.activity_calendario_acad);

        PDFView pdfView = findViewById(R.id.pdfView);

        pdfView.fromAsset("CalendarioACAD.pdf").load();

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
