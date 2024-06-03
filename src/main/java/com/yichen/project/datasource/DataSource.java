package com.yichen.project.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DataSource<T> {
    /**
     * 搜索
     * 通过声明接口来定义规范
     * 任何一个想要接入系统的数据源 都要实现这个接口
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
