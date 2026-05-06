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

    /** final score adjusted by admin */
    private Long finalScore;

    /** risk level adjusted by admin */
    private Long riskLevel;

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

    public Long getFinalScore()
    {
        return finalScore;
    }

    public void setFinalScore(Long finalScore)
    {
        this.finalScore = finalScore;
    }

    public Long getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(Long riskLevel)
    {
        this.riskLevel = riskLevel;
    }
}
