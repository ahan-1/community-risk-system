package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Count of risk_info rows grouped by risk_level (for ECharts). */
public class RiskStatLevelCountVO
{
    @JsonProperty("risk_level")
    private Long riskLevel;

    private Long count;

    public Long getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(Long riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public Long getCount()
    {
        return count;
    }

    public void setCount(Long count)
    {
        this.count = count;
    }
}
