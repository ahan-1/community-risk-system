package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.RiskInfo;
import com.ruoyi.system.domain.dto.RiskAuditDTO;
import com.ruoyi.system.domain.vo.RiskStatLevelCountVO;
import com.ruoyi.system.domain.vo.RiskStatMonthCountVO;
import com.ruoyi.system.domain.vo.RiskStatTypeCountVO;

/**
 * 风险信息主Service接口
 *
 * @author ruoyi
 * @date 2026-05-04
 */
public interface IRiskInfoService
{
    public RiskInfo selectRiskInfoById(Long id);

    public List<RiskInfo> selectRiskInfoList(RiskInfo riskInfo);

    public int insertRiskInfo(RiskInfo riskInfo);

    public int updateRiskInfo(RiskInfo riskInfo);

    public int deleteRiskInfoByIds(Long[] ids);

    public int deleteRiskInfoById(Long id);

    /**
     * 审核风险信息并记录审核表
     *
     * @param dto 审核参数
     */
    public void auditRisk(RiskAuditDTO dto);

    /**
     * 按类型统计风险信息数量
     */
    public List<RiskStatTypeCountVO> countRiskInfoGroupByTypeId();

    /**
     * 按风险等级统计数量
     */
    public List<RiskStatLevelCountVO> countRiskInfoGroupByRiskLevel();

    /**
     * 按月份统计（基于 risk_audit.audit_time）
     */
    public List<RiskStatMonthCountVO> countRiskAuditByMonth();
}
