package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 风险信息主对象 risk_info
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
public class RiskInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 风险标题 */
    @Excel(name = "风险标题")
    private String title;

    /** 风险详细描述 */
    @Excel(name = "风险详细描述")
    private String content;

    /** 风险类型ID(关联risk_type.id) */
    @Excel(name = "风险类型ID(关联risk_type.id)")
    private Long typeId;

    /** 关键词匹配得分 */
    @Excel(name = "关键词匹配得分")
    private Long keywordScore;

    /** 综合风险评分 */
    @Excel(name = "综合风险评分")
    private Long finalScore;

    /** 风险等级(1低 2中 3高) */
    @Excel(name = "风险等级(1低 2中 3高)")
    private Long riskLevel;

    /** 状态(0待审核 1已通过 2已驳回) */
    @Excel(name = "状态(0待审核 1已通过 2已驳回)")
    private Long status;

    /** 上报人ID(关联sys_user.user_id) */
    @Excel(name = "上报人ID(关联sys_user.user_id)")
    private Long userId;

    /** 风险图片 */
    @Excel(name = "风险图片")
    private String imageUrl;

    /** 处理结果 */
    @Excel(name = "处理结果")
    private String handleResult;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setTypeId(Long typeId) 
    {
        this.typeId = typeId;
    }

    public Long getTypeId() 
    {
        return typeId;
    }

    public void setKeywordScore(Long keywordScore) 
    {
        this.keywordScore = keywordScore;
    }

    public Long getKeywordScore() 
    {
        return keywordScore;
    }

    public void setFinalScore(Long finalScore) 
    {
        this.finalScore = finalScore;
    }

    public Long getFinalScore() 
    {
        return finalScore;
    }

    public void setRiskLevel(Long riskLevel) 
    {
        this.riskLevel = riskLevel;
    }

    public Long getRiskLevel() 
    {
        return riskLevel;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    public void setHandleResult(String handleResult) 
    {
        this.handleResult = handleResult;
    }

    public String getHandleResult() 
    {
        return handleResult;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("typeId", getTypeId())
            .append("keywordScore", getKeywordScore())
            .append("finalScore", getFinalScore())
            .append("riskLevel", getRiskLevel())
            .append("status", getStatus())
            .append("userId", getUserId())
            .append("imageUrl", getImageUrl())
            .append("handleResult", getHandleResult())
            .toString();
    }
}
