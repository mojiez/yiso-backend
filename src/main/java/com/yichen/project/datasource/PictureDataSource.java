package com.yichen.project.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.exception.BusinessException;
import com.yichen.project.model.entity.Picture;
import com.yichen.project.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 */
// 要加一个 Service注解 不然Spring不会识别
@Service
public class PictureDataSource implements DataSource<Picture> {
    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        long current = (pageNum-1)*pageSize;
//        String url = "https://www.bing.com/images/search?q=蜡笔小新&first=1";
        List<Picture>pictureList = new ArrayList<>();
        String url = "https://www.bing.com/images/search?q="+searchText+"&first=" + current;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据获取异常");
        }

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
            if (pictureList.size() > pageSize) break;
        }
//        log.info(doc.title());
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (Element headline : newsHeadlines) {
////            log("%s\n\t%s",
////                    headline.attr("title"), headline.absUrl("href"));
//            log.info(headline.attr("title"), headline.absUrl("href"));
//        }

        /**
         * 多包含了 当前也页面号 和 一页有多少条数据
         */
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(pictureList);
        return picturePage;
    }

}
