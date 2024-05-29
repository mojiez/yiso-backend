package com.yichen.project.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yichen.project.annotation.AuthCheck;
import com.yichen.project.common.BaseResponse;
import com.yichen.project.common.DeleteRequest;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.common.ResultUtils;
import com.yichen.project.constant.UserConstant;
import com.yichen.project.exception.BusinessException;
import com.yichen.project.exception.ThrowUtils;
import com.yichen.project.model.dto.picture.PictureQueryRequest;
import com.yichen.project.model.dto.post.PostAddRequest;
import com.yichen.project.model.dto.post.PostEditRequest;
import com.yichen.project.model.dto.post.PostQueryRequest;
import com.yichen.project.model.dto.post.PostUpdateRequest;
import com.yichen.project.model.entity.Picture;
import com.yichen.project.model.entity.Post;
import com.yichen.project.model.entity.User;
import com.yichen.project.model.vo.PostVO;
import com.yichen.project.service.PictureService;
import com.yichen.project.service.PostService;
import com.yichen.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;


    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        String searchText = pictureQueryRequest.getSearchText();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(picturePage);
    }

    // endregion


}
