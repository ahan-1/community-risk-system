package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.RiskRule;

/**
 * 风险研判规则Mapper接口
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
public interface RiskRuleMapper 
{
    /**
     * 查询风险研判规则
     * 
     * @param id 风险研判规则主键
     * @return 风险研判规则
     */
    public RiskRule selectRiskRuleById(Long id);

    /**
     * 查询风险研判规则列表
     * 
     * @param riskRule 风险研判规则
     * @return 风险研判规则集合
     */
    public List<RiskRule> selectRiskRuleList(RiskRule riskRule);

    /**
     * 查询状态为启用（0）的规则，用于自动研判
     *
     * @return 规则集合
     */
    public List<RiskRule> selectRiskRuleListActive();

    /**
     * 新增风险研判规则
     * 
     * @param riskRule 风险研判规则
     * @return 结果
     */
    public int insertRiskRule(RiskRule riskRule);

    /**
     * 修改风险研判规则
     * 
     * @param riskRule 风险研判规则
     * @return 结果
     */
    public int updateRiskRule(RiskRule riskRule);

    /**
     * 删除风险研判规则
     * 
     * @param id 风险研判规则主键
     * @return 结果
     */
    public int deleteRiskRuleById(Long id);

    /**
     * 批量删除风险研判规则
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRiskRuleByIds(Long[] ids);
}
