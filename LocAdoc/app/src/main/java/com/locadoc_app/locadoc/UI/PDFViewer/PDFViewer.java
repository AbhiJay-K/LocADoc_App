package com.locadoc_app.locadoc.UI.PDFViewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.locadoc_app.locadoc.DynamoDB.PasswordDynamoHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.helper.Encryption;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

        FloatingActionButton fileExplorerfab = (FloatingActionButton) findViewById(R.id.pdfBackFloatingActionButton);
        fileExplorerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });

        pdfView = (PDFView)findViewById(R.id.pdfView);

        Intent extras = getIntent();
        int fileid = extras.getIntExtra("fileid", 0);
        new DecryptTask().execute(fileid);
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

    public void closeActivity(){
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();

        File dst = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/vault/~" + filename);
        if(dst.exists()) {
            dst.delete();
        }
        finish();
    }

    private class DecryptTask extends
            AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... objects) {
            int fileid = objects[0];
            com.locadoc_app.locadoc.Model.File file = FileSQLHelper.getFile(fileid, Credential.getPassword());
            int passwordid = file.getPasswordId();

            boolean differentPass = false;
            if(passwordid != Credential.getPassword().getPasswordid()){
                Password password = PasswordDynamoHelper.getInstance().getPasswordFromDB(passwordid);
                Encryption.getInstance(password.getPassword(), password.getSalt())
                        .setKey(password.getPassword(), password.getSalt());
                differentPass = true;
            }

            filename = file.getCurrentfilename();
            File src = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/vault/" + filename);
            File dst = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/vault/~" + filename);
            decryptFile(src, dst);
            PDFFILE = Uri.fromFile(dst);

            if(differentPass){
                Password password = Credential.getPassword();
                Encryption.getInstance(password.getPassword(), password.getSalt())
                        .setKey(password.getPassword(), password.getSalt());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void objects) {
            openPDF();
        }
    }

    public void decryptFile(File src, File dst){
        InputStream in = null;
        OutputStream out = null;

        try{
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);
            Password password = Credential.getPassword();
            Encryption.getInstance(password.getPassword(), password.getSalt()).decryptFile(in,out);
        } catch (Exception e){
            Log.e("LocAdoc", e.toString());
        }
        finally {
            try{
                in.close();
                out.close();
            } catch (Exception e){
                Log.e("LocAdoc", e.toString());
            }
        }
    }
}
