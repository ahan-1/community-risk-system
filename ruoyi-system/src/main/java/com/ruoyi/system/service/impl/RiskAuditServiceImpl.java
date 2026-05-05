package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RiskAuditMapper;
import com.ruoyi.system.domain.RiskAudit;
import com.ruoyi.system.service.IRiskAuditService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@Service
public class RiskAuditServiceImpl implements IRiskAuditService 
{
    @Autowired
    private RiskAuditMapper riskAuditMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public RiskAudit selectRiskAuditById(Long id)
    {
        return riskAuditMapper.selectRiskAuditById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param riskAudit 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<RiskAudit> selectRiskAuditList(RiskAudit riskAudit)
    {
        return riskAuditMapper.selectRiskAuditList(riskAudit);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param riskAudit 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertRiskAudit(RiskAudit riskAudit)
    {
        return riskAuditMapper.insertRiskAudit(riskAudit);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param riskAudit 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateRiskAudit(RiskAudit riskAudit)
    {
        return riskAuditMapper.updateRiskAudit(riskAudit);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteRiskAuditByIds(Long[] ids)
    {
        return riskAuditMapper.deleteRiskAuditByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteRiskAuditById(Long id)
    {
        return riskAuditMapper.deleteRiskAuditById(id);
    }
}
