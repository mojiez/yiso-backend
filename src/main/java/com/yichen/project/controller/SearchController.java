package com.yichen.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yichen.project.common.BaseResponse;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.common.ResultUtils;
import com.yichen.project.exception.ThrowUtils;
import com.yichen.project.model.dto.picture.PictureQueryRequest;
import com.yichen.project.model.dto.post.PostQueryRequest;
import com.yichen.project.model.dto.search.SearchRequest;
import com.yichen.project.model.dto.user.UserQueryRequest;
import com.yichen.project.model.entity.Picture;
import com.yichen.project.model.vo.PostVO;
import com.yichen.project.model.vo.SearchVO;
import com.yichen.project.model.vo.UserVO;
import com.yichen.project.service.PictureService;
import com.yichen.project.service.PostService;
import com.yichen.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;
    @Resource
    private PostService postService;

    /**
     * 一次查询 查询所有的接口
     * 前端不用管具体哪个接口 要 传递什么参数
     * 前端只需要按规定统一传递参数
     * 后端对参数进行转换从而适配接口
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
        SearchVO searchVO = new SearchVO();
        searchVO.setUserVOList(userVOPage.getRecords());
        searchVO.setPostVOList(postVOPage.getRecords());
        searchVO.setPictureList(picturePage.getRecords());
        return ResultUtils.success(searchVO);

        // todo 可以并发查这几个接口 更快
    }


}
