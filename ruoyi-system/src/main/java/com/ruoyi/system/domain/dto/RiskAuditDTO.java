package com.ruoyi.system.domain.dto;

import java.io.Serializable;

/** Request body for risk info audit API. */
public class RiskAuditDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** risk_info.id */
    private Long riskId;

    /** auditResult: pass or reject (see RiskInfoServiceImpl constants) */
    private String auditResult;

    /** audit comment text */
    private String auditComment;

    public Long getRiskId()
    {
        return riskId;
    }

    public void setRiskId(Long riskId)
    {
        this.riskId = riskId;
    }

    public String getAuditResult()
    {
        return auditResult;
    }

    public void setAuditResult(String auditResult)
    {
        this.auditResult = auditResult;
    }

    public String getAuditComment()
    {
        return auditComment;
    }

    public void setAuditComment(String auditComment)
    {
        this.auditComment = auditComment;
    }
}
