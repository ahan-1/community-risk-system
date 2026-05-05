package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 风险研判规则对象 risk_rule（字段：id, keyword, type_id, weight, status）
 *
 * @author ruoyi
 */
public class RiskRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关键词 */
    @Excel(name = "关键词")
    private String keyword;

    /** 关联风险类型ID */
    @Excel(name = "风险类型ID")
    private Long typeId;

    /** 权重/分值 */
    @Excel(name = "权重/分值")
    private Long weight;

    /** 状态(0启用 1停用) */
    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private Long status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public Long getTypeId()
    {
        return typeId;
    }

    public void setWeight(Long weight)
    {
        this.weight = weight;
    }

    public Long getWeight()
    {
        return weight;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("keyword", getKeyword())
            .append("typeId", getTypeId())
            .append("weight", getWeight())
            .append("status", getStatus())
            .toString();
    }
}
