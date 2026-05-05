package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Count of risk_info rows grouped by type_id (for ECharts). */
public class RiskStatTypeCountVO
{
    @JsonProperty("type_id")
    private Long typeId;

    private Long count;

    public Long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
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
