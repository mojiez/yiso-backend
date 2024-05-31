package com.yichen.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yichen.project.common.BaseResponse;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.common.ResultUtils;
import com.yichen.project.exception.BusinessException;
import com.yichen.project.exception.ThrowUtils;
import com.yichen.project.manager.SearchFacade;
import com.yichen.project.model.dto.picture.PictureQueryRequest;
import com.yichen.project.model.dto.post.PostQueryRequest;
import com.yichen.project.model.dto.search.SearchRequest;
import com.yichen.project.model.dto.search.SearchTypeRequest;
import com.yichen.project.model.dto.user.UserQueryRequest;
import com.yichen.project.model.entity.Picture;
import com.yichen.project.model.enums.SearchTypeEnum;
import com.yichen.project.model.vo.PostVO;
import com.yichen.project.model.vo.SearchVO;
import com.yichen.project.model.vo.UserVO;
import com.yichen.project.service.PictureService;
import com.yichen.project.service.PostService;
import com.yichen.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    //引入 门面
    @Resource
    private SearchFacade searchFacade;
    /**
     * 一次查询 查询所有的接口
     * 前端不用管具体哪个接口 要 传递什么参数
     * 前端只需要按规定统一传递参数
     * 后端对参数进行转换从而适配接口
     *
     * @return
     */
    // 对RequestBody的理解 如果前端传的数据是json格式，后端用一个对象来接受 就要用RequestBody
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        /**
         * 同步实现
         */
//        String searchText = searchRequest.getSearchText();
//        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
//        UserQueryRequest userQueryRequest = new UserQueryRequest();
//        userQueryRequest.setUserName(searchText);
//        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
//        PostQueryRequest postQueryRequest = new PostQueryRequest();
//        postQueryRequest.setSearchText(searchText);
//        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
//        SearchVO searchVO = new SearchVO();
//        searchVO.setUserVOList(userVOPage.getRecords());
//        searchVO.setPostVOList(postVOPage.getRecords());
//        searchVO.setPictureList(picturePage.getRecords());
//        return ResultUtils.success(searchVO);

        // todo 可以并发查这几个接口 更快

        /**
         * 并发实现
         * 提升并不明显 （甚至更慢了）
         */
        String searchText = searchRequest.getSearchText();

        CompletableFuture<Page<Picture>> pCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
            return picturePage;
        });

        CompletableFuture<Page<UserVO>> uCompletableFuture = CompletableFuture.supplyAsync(() -> {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
            return userVOPage;
        });

        CompletableFuture<Page<PostVO>> poCompletableFuture = CompletableFuture.supplyAsync(() -> {
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
            return postVOPage;
        });

        // 这里相当于打了一个断点 只有上面的三条异步全部执行完 才会往下走
        CompletableFuture.allOf(poCompletableFuture, pCompletableFuture, uCompletableFuture);
        try {
            Page<PostVO> postVOPage = poCompletableFuture.get();
            Page<Picture> picturePage = pCompletableFuture.get();
            Page<UserVO> userVOPage = uCompletableFuture.get();
            SearchVO searchVO = new SearchVO();
            searchVO.setUserVOList(userVOPage.getRecords());
            searchVO.setPostVOList(postVOPage.getRecords());
            searchVO.setPictureList(picturePage.getRecords());
            return ResultUtils.success(searchVO);

        } catch (Exception e) {
            log.error("查询异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
        }


    }

    /**
     * 使用了门面模式
     * todo 什么是门面模式
     * @param searchTypeRequest
     * @param request
     * @return
     */
    @PostMapping("/type")
    public BaseResponse<SearchVO> searchAllByType(@RequestBody SearchTypeRequest searchTypeRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAllByType(searchTypeRequest, request));
    }

}


