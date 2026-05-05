package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RiskRuleMapper;
import com.ruoyi.system.domain.RiskRule;
import com.ruoyi.system.service.IRiskRuleService;

/**
 * 风险研判规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@Service
public class RiskRuleServiceImpl implements IRiskRuleService 
{
    @Autowired
    private RiskRuleMapper riskRuleMapper;

    /**
     * 查询风险研判规则
     * 
     * @param id 风险研判规则主键
     * @return 风险研判规则
     */
    @Override
    public RiskRule selectRiskRuleById(Long id)
    {
        return riskRuleMapper.selectRiskRuleById(id);
    }

    /**
     * 查询风险研判规则列表
     * 
     * @param riskRule 风险研判规则
     * @return 风险研判规则
     */
    @Override
    public List<RiskRule> selectRiskRuleList(RiskRule riskRule)
    {
        return riskRuleMapper.selectRiskRuleList(riskRule);
    }

    /**
     * 新增风险研判规则
     * 
     * @param riskRule 风险研判规则
     * @return 结果
     */
    @Override
    public int insertRiskRule(RiskRule riskRule)
    {
        return riskRuleMapper.insertRiskRule(riskRule);
    }

    /**
     * 修改风险研判规则
     * 
     * @param riskRule 风险研判规则
     * @return 结果
     */
    @Override
    public int updateRiskRule(RiskRule riskRule)
    {
        return riskRuleMapper.updateRiskRule(riskRule);
    }

    /**
     * 批量删除风险研判规则
     * 
     * @param ids 需要删除的风险研判规则主键
     * @return 结果
     */
    @Override
    public int deleteRiskRuleByIds(Long[] ids)
    {
        return riskRuleMapper.deleteRiskRuleByIds(ids);
    }

    /**
     * 删除风险研判规则信息
     * 
     * @param id 风险研判规则主键
     * @return 结果
     */
    @Override
    public int deleteRiskRuleById(Long id)
    {
        return riskRuleMapper.deleteRiskRuleById(id);
    }
}
