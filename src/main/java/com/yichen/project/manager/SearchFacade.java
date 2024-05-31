package com.yichen.project.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yichen.project.common.BaseResponse;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.common.ResultUtils;
import com.yichen.project.exception.BusinessException;
import com.yichen.project.model.dto.post.PostQueryRequest;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 */
@Component
@Slf4j
public class SearchFacade {
    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;
    @Resource
    private PostService postService;
    public SearchVO searchAllByType(@RequestBody SearchTypeRequest searchTypeRequest, HttpServletRequest request) {
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
        String type = searchTypeRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        if (StringUtils.isBlank(type)) {
            // 如果说 没有获取到type
            // todo null 和 isBlank是什么关系
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (searchTypeEnum == null) {
            // 说明要查询所有的数据
            /**
             * 并发实现
             * 提升并不明显 （甚至更慢了）
             */
            String searchText = searchTypeRequest.getSearchText();

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
                return searchVO;

            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        }
        // 如果不为空
        /**
         * 并发实现
         * 提升并不明显 （甚至更慢了）
         */
        String searchText = searchTypeRequest.getSearchText();
        SearchVO searchVO = new SearchVO();
        // todo 可以优化的点： type增多后，要把查询逻辑堆积在controller代码里 解决思路： 设计模式！
        switch (searchTypeEnum) {

            case POST:
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                searchVO.setPostVOList(postVOPage.getRecords());
                break;
            case PICTURE:
                Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                searchVO.setPictureList(picturePage.getRecords());
                break;
            case USER:
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                searchVO.setUserVOList(userVOPage.getRecords());
                break;
            default:
        }

        return searchVO;

    }
}
