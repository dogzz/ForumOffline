/*
* @Author: dogzz
* @Created: 9/1/2016
*/

package com.dogzz.forumoffline.worker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageExtractor {
    public static String extractArticle(String result, boolean showVideo, int videoWidth) {
        String resultHtml;
        Document doc = Jsoup.parse(result);
        Elements heading = doc.select("li[class=page active]");
//        heading.select("div[class*=Breadcrumb]").first().text(""); //remove breadcrumbs
//        heading.select("h1").attr("style", "font-size: 120%; text-align: center");
//        heading.select("h3").attr("style", "font-size: 90%");
//        heading.select("img").attr("width", "99%"); //resize images
        Elements mainContent = doc.select("div[class=ipsBox]");
        mainContent.select("div[class=author_info]").remove();
        mainContent.select("ul[class*=post_controls]").remove();
//        mainContent.select("img").attr("width", "99%").removeAttr("height"); //resize images

//        mainContent.select("iframe[src*=facebook]").attr("width", "100%");
//        mainContent.select("iframe").attr("width", "99%").removeAttr("height");
//        mainContent.select("iframe").addClass("video").removeAttr("width").removeAttr("height").wrap("<figure></figure>");

//        "iframe:not([src*=disqus])"
        String styling = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "<meta charset=\"utf-8\">" +
                "<style>" +
                "figure {height: 0; padding-bottom: 80%; position: relative; width: 100%; margin: 0;}" +
                "iframe.video {height: 100%; left: 0; position: absolute; top: 0; width: 100%;}" +
                "</style>" +
                "</head>" +
                "<body style='text-align: justify'>";
        Elements comments = doc.select("div[class=widget-social]");
        resultHtml = styling
                .concat(heading.html().concat(mainContent.html())
                        .concat(comments.outerHtml().replace(".src = '//'", ".src = 'http://'"))).concat("</body>");
        return resultHtml;
    }

    private static String getPaddingRatio(String width, String height) {
        String paddingRatio = "80%";
        try {
            int intWidth = Integer.valueOf(width);
            int intHeight = Integer.valueOf(height);
            float ratio = (float) intHeight / intWidth;
            //for Facebook we need to increase height
            ratio = ratio * 100;
            paddingRatio = String.format("%.2f", ratio).concat("%").replace(',', '.');
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paddingRatio;
    }
}
