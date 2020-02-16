package com.mimose.component.db.mysql.service;

import com.mimose.component.db.mysql.dao.BaseDao;
import com.mimose.component.db.mysql.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @Description 基础service
 * @Author ccy
 * @Date 2020/2/16
 */
public abstract class BaseServiceImpl<T extends BaseEntity, D extends BaseDao<T>> {

    @Autowired
    protected D dao;

    protected int insert(T entity, String by){
        if(ObjectUtils.isEmpty(entity)){
            throw new IllegalArgumentException("Insert failed, entity is empty");
        }
        entity.setCreateBy(by);
        entity.setCreateTime(new Date());
        return this.dao.insert(entity);
    }

    protected int insertSelective(T entity, String by){
        if(ObjectUtils.isEmpty(entity)){
            throw new IllegalArgumentException("Insert failed, entity is empty");
        }
        entity.setCreateBy(by);
        entity.setCreateTime(new Date());
        return this.dao.insertSelective(entity);
    }

    protected int update(T entity, String by){
        if(ObjectUtils.isEmpty(entity)){
            throw new IllegalArgumentException("Update failed, entity is empty");
        }
        entity.setUpdateBy(by);
        entity.setUpdateTime(new Date());
        return this.dao.updateByPrimaryKey(entity);
    }

}
