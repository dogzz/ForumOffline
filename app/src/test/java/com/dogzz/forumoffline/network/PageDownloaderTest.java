package com.dogzz.forumoffline.network;

import com.dogzz.forumoffline.dataprocessing.ViewItem;
import com.dogzz.forumoffline.dataprocessing.ViewItemType;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/*
* @Author: dogzz
* @Created: 9/8/2016
*/
public class PageDownloaderTest {
    ViewItem header;
    String pageUrl;
    @Before
    public void setUp() throws Exception {
        String url = "http://www.babyplan.ru/forums/topic/72399-zhizn-posle-preryvaniya-beremennosti/";
        String text = "Это название темы";
        header = new ViewItem(text, url, ViewItemType.THREAD);
        pageUrl = "/page__st__20";
    }

    @Test
    public void generateFileName() throws Exception {
        String result = PageDownloader.generateFileName(pageUrl);
        assertThat(result, is("page__st__20"));
    }

    @Test
    public void generateDirName() throws Exception {
        String result = PageDownloader.generateDirName(header);
        assertThat(result, is("72399-zhizn-posle-preryvaniya-beremennosti"));
    }

    @Test
    public void generatePath() throws Exception {
        String fileName = PageDownloader.generateFileName(pageUrl);
        File f = new File("//data/data/com.dogzz.forumoffline/");
        String result = PageDownloader.generatePath(fileName, f, header);
        String actual = "\\\\data\\data\\com.dogzz.forumoffline/72399-zhizn-posle-preryvaniya-beremennosti/page__st__20";
        assertThat(result, is(actual));
    }

}