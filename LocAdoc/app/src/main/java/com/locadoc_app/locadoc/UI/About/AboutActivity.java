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
    private boolean logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        logout = true;
        TextView description = (TextView) findViewById(R.id.About_Description);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            description.setText(Html.fromHtml(getString(R.string.About_description), Html.FROM_HTML_MODE_LEGACY));
        } else {
            description.setText(Html.fromHtml(getString(R.string.About_description)));
        }

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

    public void exit(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("logout", logout);
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        exit();
    }

    @Override
    public void onBackPressed(){
        logout = false;
        exit();
    }
}
