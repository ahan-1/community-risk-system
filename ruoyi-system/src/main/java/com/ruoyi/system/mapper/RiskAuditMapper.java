package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.RiskAudit;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@Mapper
public interface RiskAuditMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public RiskAudit selectRiskAuditById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param riskAudit 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<RiskAudit> selectRiskAuditList(RiskAudit riskAudit);

    /**
     * 新增【请填写功能名称】
     * 
     * @param riskAudit 【请填写功能名称】
     * @return 结果
     */
    public int insertRiskAudit(RiskAudit riskAudit);

    /**
     * 修改【请填写功能名称】
     * 
     * @param riskAudit 【请填写功能名称】
     * @return 结果
     */
    public int updateRiskAudit(RiskAudit riskAudit);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteRiskAuditById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRiskAuditByIds(Long[] ids);
}
