package com.mimose.component.db.mysql.service;

import com.mimose.component.db.mysql.dao.BaseDao;
import com.mimose.component.db.mysql.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

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
        if(StringUtils.isEmpty(by)){
            throw new IllegalArgumentException("Insert failed, createBy is empty");
        }
        entity.setCreateBy(by);
        entity.setCreateTime(new Date());
        return this.dao.insert(entity);
    }

    protected int insert(List<T> entitys, String by){
        if(CollectionUtils.isEmpty(entitys)){
            throw new IllegalArgumentException("Insert failed, entity is empty");
        }
        if(StringUtils.isEmpty(by)){
            throw new IllegalArgumentException("Insert failed, createBy is empty");
        }
        return entitys.stream().mapToInt(entity -> {
            entity.setCreateBy(by);
            entity.setCreateTime(new Date());
            return this.dao.insert(entity);
        }).sum();
    }

    protected int insertSelective(T entity, String by){
        if(ObjectUtils.isEmpty(entity)){
            throw new IllegalArgumentException("Insert failed, entity is empty");
        }
        if(StringUtils.isEmpty(by)){
            throw new IllegalArgumentException("Insert failed, createBy is empty");
        }
        entity.setCreateBy(by);
        entity.setCreateTime(new Date());
        return this.dao.insertSelective(entity);
    }

    protected int insertSelective(List<T> entitys, String by){
        if(CollectionUtils.isEmpty(entitys)){
            throw new IllegalArgumentException("Insert failed, entity is empty");
        }
        if(StringUtils.isEmpty(by)){
            throw new IllegalArgumentException("Insert failed, createBy is empty");
        }
        return entitys.stream().mapToInt(entity -> {
            entity.setCreateBy(by);
            entity.setCreateTime(new Date());
            return this.dao.insertSelective(entity);
        }).sum();
    }

    protected int update(T entity, String by){
        if(ObjectUtils.isEmpty(entity)){
            throw new IllegalArgumentException("Update failed, entity is empty");
        }
        if(StringUtils.isEmpty(by)){
            throw new IllegalArgumentException("Update failed, updateBy is empty");
        }
        entity.setUpdateBy(by);
        entity.setUpdateTime(new Date());
        return this.dao.updateByPrimaryKey(entity);
    }

    protected int update(List<T> entitys, String by){
        if(CollectionUtils.isEmpty(entitys)){
            throw new IllegalArgumentException("Update failed, entity is empty");
        }
        if(StringUtils.isEmpty(by)){
            throw new IllegalArgumentException("Update failed, updateBy is empty");
        }
        return entitys.stream().mapToInt(entity -> {
            entity.setUpdateBy(by);
            entity.setUpdateTime(new Date());
            return this.dao.updateByPrimaryKey(entity);
        }).sum();
    }

}
