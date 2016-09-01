package com.dogzz.forumoffline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.dogzz.forumoffline.worker.PageDownloader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ViewPage";
    WebView webView;
    private String articleUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        articleUrl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        loadDataFromFile(articleUrl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove) {
            if (articleUrl != null && !articleUrl.isEmpty()) {
                PageDownloader downloader = new PageDownloader(this, false, 0);
                downloader.removeArticle(articleUrl);
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataFromFile(String fileName) {
        try {
            String pureArticle = readFile(fileName);
            //webView.loadDataWithBaseURL(HeadersList.BASE_URL, pureArticle, "text/html", null, "");
            webView.loadDataWithBaseURL("www.babyplan.ru", pureArticle, "text/html", null, "");
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong with read data. ".concat(e.getMessage()),
                    Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Something went wrong with read data. ".concat(e.getMessage()));
        }
    }

    private String readFile(String fileName) throws IOException {
        String pureArticle = "";
        String path = getFilesDir().getAbsolutePath().concat("/").concat(fileName);
        FileInputStream fin = new FileInputStream(path.concat("/")
                .concat(fileName.concat(PageDownloader.FILE_EXT)));
        BufferedReader br = new BufferedReader(new InputStreamReader(fin));
        String str = "";
        while ((str = br.readLine()) != null) {
            pureArticle = pureArticle.concat(str);
        }
        return pureArticle;
    }
}
