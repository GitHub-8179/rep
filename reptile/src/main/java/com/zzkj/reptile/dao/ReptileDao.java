package com.zzkj.reptile.dao;

import java.util.List;

import com.zzkj.reptile.entity.IpPostEntity;
import com.zzkj.reptile.entity.ReptileEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ReptileDao {

    int insert(ReptileEntity record);

    int inserts(List<ReptileEntity> list);

    int insertsIpPost(IpPostEntity ipPostEntity);

    List<IpPostEntity>  selectIpPost(IpPostEntity ipPostEntity);

}
