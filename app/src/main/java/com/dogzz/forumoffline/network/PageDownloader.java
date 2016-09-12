/*
* @Author: dogzz
* @Created: 9/1/2016
*/

package com.dogzz.forumoffline.network;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.forumoffline.R;
import com.dogzz.forumoffline.dataprocessing.*;
import it.sephiroth.android.library.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class PageDownloader {
    private static final String LOG_TAG = "PageDownloader";
    public static final String FILE_EXT = ".html";
    protected Activity mainActivity;
    private String downloadResult;
    private SQLiteDatabase db;
    private TasksListener mListener;
    private boolean showVideo;
    private int videoWidth;
    private ViewItem currentHeader;

    public PageDownloader(Activity mainActivity, boolean showVideo, int videoWidth) {
        this.mainActivity = mainActivity;
        this.showVideo = showVideo;
        this.videoWidth = videoWidth;
        if (mainActivity instanceof TasksListener) {
            mListener = (TasksListener) mainActivity;
        }
    }

    public void saveArticleOffline(ViewItem currentHeader, int pageMarker) {
        this.currentHeader = currentHeader;
        String title = String.valueOf(pageMarker + 1);
        pageMarker *= 20;
        String url = currentHeader.getUrl() + "/page__st__" + String.valueOf(pageMarker);
//        String fileName = generateFileName(url);
        String path = generatePath(title, mainActivity.getFilesDir(), this.currentHeader);
        DownloadTask downloadTask = new DownloadArticleTask();
        downloadTask.execute(url, path, title);
    }

    public void removeArticle(String filename) {
        String path = PageDownloader.generatePath(filename, mainActivity.getFilesDir(), currentHeader);
        File file = new File(path); //Создаем файловую переменную
        if (file.exists()) { //Если файл или директория существует
            String deleteCmd = "rm -r " + path; //Создаем текстовую командную строку
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd); //Выполняем системные команды
            } catch (IOException e) {
                Log.e(LOG_TAG, "Can't delete file: " + e.getMessage());
            }
        }
        if (mListener != null) mListener.onSavedArticleTaskFinished();
    }


    public static String generateFileName(String url) {
        String fileName = url.replace("http://", "");
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        if (fileName.contains(".")) {
            fileName = fileName.substring(0,  fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String generateDirName(ViewItem header) {
        String fileName = header.getUrl().replace("http://", "");
        fileName = fileName.substring(fileName.lastIndexOf("topic/") + 6, fileName.length());
        if (fileName.contains(".")) {
            fileName = fileName.substring(0,  fileName.lastIndexOf("."));
        }
        if (fileName.contains("/")) {
            fileName = fileName.replace("/", "");
        }
        return fileName;
    }

    public static String generatePath(String fileName, File filesDir, ViewItem header) {
        String parentPath = filesDir.getAbsolutePath().concat("/")
                .concat(PageDownloader.generateDirName(header));
        String path = parentPath.concat("/").concat(fileName);
        File parentDir = new File(parentPath);
        boolean parentResult = parentDir.mkdir();
        File dir = new File(path);
        boolean result = dir.mkdir();
        if (!parentResult || !result) {
            Log.d(LOG_TAG, "Can't create directory");
        }
        return path;
    }

    public class DownloadArticleTask extends DownloadTask {

        @Override
        protected Integer doInBackground(String... urls) {
            Integer result;
            // params comes from the execute() call: params[0] is the BASE_URL.
            try {
                result = downloadUrl(urls[0]);
                result = saveArticleContent(result, resultMessage, urls[0], urls[1], urls[2]);

            } catch (IOException e) {
                downloadResult = "Error: Unable to retrieve source data. The source is inaccessible.";
                Log.e(LOG_TAG, downloadResult + e.getMessage());
                result = 0;
            }
            return result;
        }

        private int saveArticleContent(Integer result, String content, String url,
                                       String path, String fileName) {
            if (result == 1) {
                try {
                    String pureArticle = extractArticle(content);
                    String articleWithoutImg = makeImagesLocal(pureArticle, path);
                    //save
                    saveToFile(articleWithoutImg, path, fileName);
                    return 1;
                } catch (Exception e) {
                    resultMessage = "Something went wrong with loaded data. ".concat(e.getMessage());
                    Log.e(LOG_TAG, resultMessage);
                    return 0;
                }
            } else {
                return 0;
            }
        }



        private void saveToFile(String pureArticle, String path, String fileName) throws IOException{
            FileOutputStream fout = new FileOutputStream(path.concat("/")
                    .concat(fileName.concat(FILE_EXT)));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
//                        mainActivity.openFileOutput(fileName.concat(FILE_EXT), MODE_PRIVATE)));
            bw.write(pureArticle);
            bw.close();
            Log.d(LOG_TAG, "File written");
        }

        private String extractArticle(String downloadResult) {
            return PageExtractor.extractArticle(downloadResult, showVideo, videoWidth);
        }

        private String makeImagesLocal(String pureArticle, String path) {
            String resultHtml = pureArticle;
            Document doc = Jsoup.parse(pureArticle);
            Elements imgTags = doc.select("img");
            for (Element imgTag: imgTags) {
                String imageUrl = imgTag.attr("src");
                String imageFileName = downloadImage(imageUrl, path);
                makeImgLinksLocal(imageFileName, imgTag, path);
            }
            return resultHtml = doc.html();
        }

        private String downloadImage(String imageUrl, String path) {
            String url = imageUrl;
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.length());
            FileOutputStream out = null;
            try {
                Bitmap bmp = Picasso.with(mainActivity).load(url).get();
//                out = mainActivity.openFileOutput(filename, MODE_PRIVATE);
                out = new FileOutputStream(path.concat("/").concat(filename));
                String format = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toUpperCase();
                format = format.replace("JPG", "JPEG");
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.valueOf(format);
                bmp.compress(compressFormat, 90, out);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Image downloading failed. " + e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Image downloading failed. " + e.getMessage());
                }
            }
            return filename;
        }

        private void makeImgLinksLocal(String imageFileName, Element imgTag, String path) {
            imgTag.attr("src", "file://".concat(path)
                    .concat("/").concat(imageFileName));
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Integer result) {
            downloadResult = resultMessage;
            if (result != 1) {
                Toast.makeText(mainActivity, resultMessage,
                        Toast.LENGTH_LONG).show();
            }
            if (mListener != null) mListener.onSavedArticleTaskFinished();
        }
    }

}
