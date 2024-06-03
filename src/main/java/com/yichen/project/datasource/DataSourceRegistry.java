package com.yichen.project.datasource;

import com.yichen.project.model.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册器模式
 */
@Component
public class DataSourceRegistry {
    @Resource
    private PostDataSource postDataSource;
    @Resource
            private UserDataSource userDataSource;
    @Resource
            private PictureDataSource pictureDataSource;
    // 在依赖注入完以后才调用
    @PostConstruct
    public void doInit() {
        typeDataSourceMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
        }};
    }
    private Map<String, DataSource> typeDataSourceMap;
    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap == null) return null;
        return typeDataSourceMap.get(type);
    }
}
