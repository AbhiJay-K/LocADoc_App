package com.locadoc_app.locadoc.UI.PDFViewer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
    String pdfFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        Bundle extras = getIntent().getExtras();
        PDFFILE = Uri.parse(extras.getString("FILE"));
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

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}
