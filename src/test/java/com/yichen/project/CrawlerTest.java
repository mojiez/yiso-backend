package com.yichen.project;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yichen.project.model.entity.Picture;
import com.yichen.project.model.entity.Post;
import com.yichen.project.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class CrawlerTest {
    @Resource
    private PostService postService;
    @Test
    public void testFetchPicture() throws IOException {
        int current = 1;
//        String url = "https://www.bing.com/images/search?q=蜡笔小新&first=1";
        List<Picture>pictureList = new ArrayList<>();
        String url = "https://www.bing.com/images/search?q=芙莉莲&first=" + current;
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select(".iuscp.isv");
        for (Element element : elements) {
            // 取图片地址
            String m = element.select(".iusc").get(0).attr("m");
            // {"sid":"","cturl":"","cid":"tR0J57Ng","purl":"http://www.nipic.com/show/1496155.html","murl":"http://pic1.nipic.com/2009-02-25/200922520173452_2.jpg","turl":"https://tse2.mm.bing.net/th?id=OIP.tR0J57NgkNwE5U3l-n2YngHaIS&pid=15.1","md5":"b51d09e7b36090dc04e54de5fa7d989e","shkey":"dh2i62D351mUNY+htc7Y4VJOTWQzWJFbkkpi1GckUkE=","t":"蜡笔小新1矢量图__其他_人物图库_矢量图库_昵图网nipic.com","mid":"F6EFBD0D2F7540174A502A74A697576841092842","desc":""}
            // m是一个JSON 将m转为map
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            System.out.println(title);
            Picture picture = new Picture();
            picture.setUrl(murl);
            picture.setTitle(title);
            pictureList.add(picture);
//            System.out.println(element);
        }
//        log.info(doc.title());
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (Element headline : newsHeadlines) {
////            log("%s\n\t%s",
////                    headline.attr("title"), headline.absUrl("href"));
//            log.info(headline.attr("title"), headline.absUrl("href"));
//        }
    }
    @Test
    public void testFetchPassage() {
        // 使用Hutool的HttpCient来爬

        // 1. 获取数据
        String url = "https://api.code-nav.cn/api/post/search/page/vo";
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"tags\":[],\"reviewStatus\":1}";
        String result2 = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
        System.out.println(result2);

        // 2. JSON转对象
        Map<String, Object> map = JSONUtil.toBean(result2, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        // data
        JSONArray records = (JSONArray)data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            // 如果不强转，不能调用子类的方法
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            postList.add(post);
        }
        System.out.println(postList);

        // 3. 数据入库
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }
}
