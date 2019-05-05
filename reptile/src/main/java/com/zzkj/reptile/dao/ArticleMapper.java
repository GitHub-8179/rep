package com.zzkj.reptile.dao;

import com.zzkj.reptile.entity.Article;
import com.zzkj.reptile.entity.ArticleWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {

    List<Article> selectByExample(Article article);

    int setGetStartAdd(@Param("articleId") String articleId);

    int updateByDetails(@Param("record") ArticleWithBLOBs record);


}
