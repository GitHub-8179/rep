package com.zzkj.reptile.dao;

import com.zzkj.reptile.entity.ArticleType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTypeMapper {
    int deleteByPrimaryKey(Integer articleTypeId);

    int deleteById(String articleId);


    int insert(ArticleType record);

    int insertSelective(ArticleType record);

    List<ArticleType> selectAllKeyWork(@Param("rem") Integer rem);


    List<ArticleType> selectKeyWork(@Param("day") Integer day, @Param("threadNum") Integer threadNum, @Param("rem") Integer rem);

    int updateLastTime(ArticleType articleType);


    ArticleType selectByPrimaryKey(Integer articleTypeId);

    int updateByPrimaryKeySelective(ArticleType record);

    int updateByPrimaryKey(ArticleType record);
}
