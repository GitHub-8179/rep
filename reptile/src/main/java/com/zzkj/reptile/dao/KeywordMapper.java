package com.zzkj.reptile.dao;

import com.zzkj.reptile.entity.Keyword;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeywordMapper {

    List<Keyword> selectKeyWork(Keyword keyword);

}
