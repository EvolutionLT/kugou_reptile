package com.lt.spider;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * @author Evolution
 * @title: SpiderKugou
 * @projectName kugou_reptile
 * @description: TODO
 * @date 2019/5/622:52
 */
public class SpiderKugou {
      //本地存放音乐路径
      public static String filePath = "F:/music/";
      //mp3 链接路径
      public static String mp3 = "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&callback=jQuery191027067069941080546_1546235744250&"
              + "hash=HASH&album_id=0&_=TIME";
      //酷狗链接地址
      public static String LINK = "https://www.kugou.com/yy/rank/home/PAGE-8888.html?from=rank";


      public static void main(String[] args) throws IOException {

            for(int i = 1 ; i <2 ; i++){
                  String url = LINK.replace("PAGE", i + "");
                  getTitle(url);
                  //download("https://www.kugou.com/song/mfy6je5.html");
            }
      }

      public static String getTitle(String url) throws IOException{
            HttpGetConnect connect = new HttpGetConnect();
            String content = connect.connect(url, "utf-8");
            HtmlManage html = new HtmlManage();
            Document doc = html.manage(content);
            Element ele = doc.getElementsByClass("pc_temp_songlist").get(0);
            Elements eles = ele.getElementsByTag("li");
            for(int i = 0 ; i < eles.size() ; i++){
                  Element item = eles.get(i);
                  String title = item.attr("title").trim();
                  String link = item.getElementsByTag("a").first().attr("href");

                  download(link,title);
            }
            return null;
      }

      public static String download(String url,String name) throws IOException{
            String hash = "";
            HttpGetConnect connect = new HttpGetConnect();
            String content = connect.connect(url, "utf-8");
            HtmlManage html = new HtmlManage();

            String regEx = "\"hash\":\"[0-9A-Z]+\"";
            // 编译正则表达式
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                  hash = matcher.group();
                  hash = hash.replace("\"hash\":\"", "");
                  hash = hash.replace("\"", "");
            }

            String item = mp3.replace("HASH", hash);
            item = item.replace("TIME", System.currentTimeMillis() + "");

            System.out.println(item);
            String mp = connect.connect(item, "utf-8");

            mp = mp.substring(mp.indexOf("(") + 1, mp.length() - 3);

            JSONObject json = JSONObject.fromObject(mp);
            String playUrl = json.getJSONObject("data").getString("play_url");


            System.out.print(playUrl + "  ==  ");
            FileDownload down = new FileDownload();
            down.download(playUrl, filePath + name + ".mp3");

            System.out.println(name + "下载完成");
            return playUrl;
      }




}
