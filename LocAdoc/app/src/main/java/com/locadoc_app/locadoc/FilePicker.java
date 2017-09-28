package com.locadoc_app.locadoc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.locadoc_app.locadoc.UI.PDFViewer.PDFViewer;

public class FilePicker extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = ">FILE PICKER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        final Button button = (Button)findViewById(R.id.Browse);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                String filePath = uri.toString();//getRealPathFromURI(uri);
                Log.i(TAG, "File: " + filePath);
                Intent PDFVIEWER = new Intent(this, PDFViewer.class);
                PDFVIEWER.putExtra("FILE", filePath);
                startActivity(PDFVIEWER);
            }
        }
    }
}
