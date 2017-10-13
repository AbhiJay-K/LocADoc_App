package com.locadoc_app.locadoc.UI.PDFViewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.locadoc_app.locadoc.R;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PDFViewer extends AppCompatActivity implements OnPageChangeListener,OnLoadCompleteListener{
    private static Uri PDFFILE;
    private static final String TAG = PDFViewer.class.getSimpleName();
    PDFView pdfView;
    Integer pageNumber = 0;
    String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        Bundle extras = getIntent().getExtras();
        PDFFILE = Uri.parse(extras.getString("FILE"));
        filename = extras.getString("filename");

        FloatingActionButton fileExplorerfab = (FloatingActionButton) findViewById(R.id.pdfBackFloatingActionButton);
        fileExplorerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });

        pdfView = (PDFView)findViewById(R.id.pdfView);
        openPDF();
    }


    private void openPDF()
    {
        pdfView.fromUri(PDFFILE).defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle("FileName.pdf");
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                exit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    public void exit(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("filename", filename);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed(){
        exit();
    }

    public void closeActivity(){
        exit();
    }

    @Override
    public void onStop(){
        super.onStop();
        exit();
        Log.d("LocAdoc", "STOP!!!!!!!!!");
    }
}
