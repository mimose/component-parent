package com.mimose.component.db.mysql.dao;

import com.mimose.component.db.mysql.entity.BaseEntity;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Description 基础dao
 * @Author ccy
 * @Date 2020/2/16
 */
public interface BaseDao<T extends BaseEntity> extends Mapper<T> {
}
