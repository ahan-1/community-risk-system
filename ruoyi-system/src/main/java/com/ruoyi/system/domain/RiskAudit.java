package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 risk_audit
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
public class RiskAudit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 风险ID */
    @Excel(name = "风险ID")
    private Long riskId;

    /** 审核人 */
    @Excel(name = "审核人")
    private Long auditUserId;

    /** 结果 */
    @Excel(name = "结果")
    private String auditResult;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String auditComment;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setRiskId(Long riskId) 
    {
        this.riskId = riskId;
    }

    public Long getRiskId() 
    {
        return riskId;
    }

    public void setAuditUserId(Long auditUserId) 
    {
        this.auditUserId = auditUserId;
    }

    public Long getAuditUserId() 
    {
        return auditUserId;
    }

    public void setAuditResult(String auditResult) 
    {
        this.auditResult = auditResult;
    }

    public String getAuditResult() 
    {
        return auditResult;
    }

    public void setAuditComment(String auditComment) 
    {
        this.auditComment = auditComment;
    }

    public String getAuditComment() 
    {
        return auditComment;
    }

    public void setAuditTime(Date auditTime) 
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() 
    {
        return auditTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("riskId", getRiskId())
            .append("auditUserId", getAuditUserId())
            .append("auditResult", getAuditResult())
            .append("auditComment", getAuditComment())
            .append("auditTime", getAuditTime())
            .toString();
    }
}
