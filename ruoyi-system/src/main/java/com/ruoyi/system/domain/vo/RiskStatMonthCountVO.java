package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Count by calendar month (yyyy-MM), for ECharts.
 * Uses risk_audit.audit_time because risk_info has no time column.
 */
public class RiskStatMonthCountVO
{
    @JsonProperty("month")
    private String statMonth;

    private Long count;

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
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
