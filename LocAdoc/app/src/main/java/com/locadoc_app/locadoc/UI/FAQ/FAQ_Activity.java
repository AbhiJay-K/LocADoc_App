package com.locadoc_app.locadoc.UI.FAQ;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.locadoc_app.locadoc.R;

public class FAQ_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_);
        Intent faqpage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://locadoc.github.io/LocAdoc/FAQ/FAQ.html"));
        startActivity(faqpage);
    }
}
