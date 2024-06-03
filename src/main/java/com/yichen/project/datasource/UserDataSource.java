package com.yichen.project.datasource;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.constant.CommonConstant;
import com.yichen.project.constant.UserConstant;
import com.yichen.project.exception.BusinessException;
import com.yichen.project.mapper.UserMapper;
import com.yichen.project.model.dto.user.UserQueryRequest;
import com.yichen.project.model.entity.User;
import com.yichen.project.model.enums.UserRoleEnum;
import com.yichen.project.model.vo.LoginUserVO;
import com.yichen.project.model.vo.UserVO;
import com.yichen.project.service.UserService;
import com.yichen.project.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
@Service
public class UserDataSource implements DataSource<UserVO> {
    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent((int) pageNum);
        userQueryRequest.setPageSize((int) pageSize);
        return userService.listUserVOByPage(userQueryRequest);
    }
}
