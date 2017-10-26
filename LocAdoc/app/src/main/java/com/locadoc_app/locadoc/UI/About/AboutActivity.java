package com.locadoc_app.locadoc.UI.About;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.locadoc_app.locadoc.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView description = (TextView) findViewById(R.id.About_Description);
        description.setText(Html.fromHtml(getString(R.string.About_description)));
        TextView version = (TextView) findViewById(R.id.About_Version);
        version.setText(getString(R.string.About_version));
        TextView website =(TextView) findViewById(R.id.Website);
        website.setText("Visit out website");
        website.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.locadoc-app.com/"));
                startActivity(web);
            }
        });

    }
}
