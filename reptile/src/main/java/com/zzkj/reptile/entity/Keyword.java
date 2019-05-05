package com.zzkj.reptile.entity;

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Date;

public class Keyword implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;

    private String keywordName;

    private String createTime;

    private Date updateTime;

    private String lastTime;

    private int delType;

    private String parentId;

    private int serverNum ;

    private int remainderNum;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getDelType() {
        return delType;
    }

    public void setDelType(int delType) {
        this.delType = delType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getServerNum() {
        return serverNum;
    }

    public void setServerNum(int serverNum) {
        this.serverNum = serverNum;
    }

    public int getRemainderNum() {
        return remainderNum;
    }

    public void setRemainderNum(int remainderNum) {
        this.remainderNum = remainderNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeywordName() {
        return keywordName;
    }

    public void setKeywordName(String keywordName) {
        this.keywordName = keywordName == null ? null : keywordName.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", keywordName=").append(keywordName);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", lastTime=").append(lastTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public Keyword(int serverNum, int remainderNum) {
        this.serverNum = serverNum;
        this.remainderNum = remainderNum;
    }

    public Keyword() {
    }
}
