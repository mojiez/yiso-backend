package com.yichen.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.yichen.project.model.entity.Picture;
import com.yichen.project.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 聚合搜索
 * VO 一般就是我要返回给前端的内容
 * 对应的是一次性全部查完 然后直接返回给前端
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class SearchVO implements Serializable {

    List<UserVO> userVOList;
    List<PostVO> postVOList;
    List<Picture> pictureList;
    private static final long serialVersionUID = 1L;
}
