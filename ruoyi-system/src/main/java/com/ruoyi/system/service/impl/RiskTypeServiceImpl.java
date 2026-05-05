package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.RiskTypeMapper;
import com.ruoyi.system.domain.RiskType;
import com.ruoyi.system.service.IRiskTypeService;

/**
 * 风险类型Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@Service
public class RiskTypeServiceImpl implements IRiskTypeService 
{
    @Autowired
    private RiskTypeMapper riskTypeMapper;

    /**
     * 查询风险类型
     * 
     * @param id 风险类型主键
     * @return 风险类型
     */
    @Override
    public RiskType selectRiskTypeById(Long id)
    {
        return riskTypeMapper.selectRiskTypeById(id);
    }

    /**
     * 查询风险类型列表
     * 
     * @param riskType 风险类型
     * @return 风险类型
     */
    @Override
    public List<RiskType> selectRiskTypeList(RiskType riskType)
    {
        return riskTypeMapper.selectRiskTypeList(riskType);
    }

    /**
     * 新增风险类型
     * 
     * @param riskType 风险类型
     * @return 结果
     */
    @Override
    public int insertRiskType(RiskType riskType)
    {
        return riskTypeMapper.insertRiskType(riskType);
    }

    /**
     * 修改风险类型
     * 
     * @param riskType 风险类型
     * @return 结果
     */
    @Override
    public int updateRiskType(RiskType riskType)
    {
        return riskTypeMapper.updateRiskType(riskType);
    }

    /**
     * 批量删除风险类型
     * 
     * @param ids 需要删除的风险类型主键
     * @return 结果
     */
    @Override
    public int deleteRiskTypeByIds(Long[] ids)
    {
        return riskTypeMapper.deleteRiskTypeByIds(ids);
    }

    /**
     * 删除风险类型信息
     * 
     * @param id 风险类型主键
     * @return 结果
     */
    @Override
    public int deleteRiskTypeById(Long id)
    {
        return riskTypeMapper.deleteRiskTypeById(id);
    }
}
