package com.yichen.project.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yichen.project.model.entity.Post;
import com.yichen.project.service.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// 取消注释后，每次启动springboot项目会执行一次run方法
//@Component
public class FetchInitPost implements CommandLineRunner {
    @Resource
    private PostService postService;

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
        JSONArray records = (JSONArray) data.get("records");
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
    }

    @Override
    public void run(String... args) throws Exception {
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
        JSONArray records = (JSONArray) data.get("records");
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
    }
}
