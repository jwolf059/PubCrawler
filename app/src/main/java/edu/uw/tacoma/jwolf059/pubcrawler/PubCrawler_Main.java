package edu.uw.tacoma.jwolf059.pubcrawler;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class PubCrawler_Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_crawler__main);
    }

    public void findAPub(View view) {
        Intent intent = new Intent(this, FindpubActivity.class);
        startActivity(intent);

    }

    public void crawlPage(View view) {
        Intent intent = new Intent(this, CrawlActivity.class);
        startActivity(intent);

    }
}
