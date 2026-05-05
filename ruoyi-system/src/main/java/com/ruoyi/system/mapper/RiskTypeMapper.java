package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.RiskType;

/**
 * 风险类型Mapper接口
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
public interface RiskTypeMapper 
{
    /**
     * 查询风险类型
     * 
     * @param id 风险类型主键
     * @return 风险类型
     */
    public RiskType selectRiskTypeById(Long id);

    /**
     * 查询风险类型列表
     * 
     * @param riskType 风险类型
     * @return 风险类型集合
     */
    public List<RiskType> selectRiskTypeList(RiskType riskType);

    /**
     * 新增风险类型
     * 
     * @param riskType 风险类型
     * @return 结果
     */
    public int insertRiskType(RiskType riskType);

    /**
     * 修改风险类型
     * 
     * @param riskType 风险类型
     * @return 结果
     */
    public int updateRiskType(RiskType riskType);

    /**
     * 删除风险类型
     * 
     * @param id 风险类型主键
     * @return 结果
     */
    public int deleteRiskTypeById(Long id);

    /**
     * 批量删除风险类型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRiskTypeByIds(Long[] ids);
}
