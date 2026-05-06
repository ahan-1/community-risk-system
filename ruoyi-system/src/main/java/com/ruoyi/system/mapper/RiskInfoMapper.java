package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.RiskInfo;
import com.ruoyi.system.domain.vo.RiskStatLevelCountVO;
import com.ruoyi.system.domain.vo.RiskStatMonthCountVO;
import com.ruoyi.system.domain.vo.RiskStatTypeCountVO;

/**
 * 风险信息主Mapper接口
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@Mapper
public interface RiskInfoMapper 
{
    /**
     * 查询风险信息主
     * 
     * @param id 风险信息主主键
     * @return 风险信息主
     */
    public RiskInfo selectRiskInfoById(Long id);

    /**
     * 查询风险信息主列表
     * 
     * @param riskInfo 风险信息主
     * @return 风险信息主集合
     */
    public List<RiskInfo> selectRiskInfoList(RiskInfo riskInfo);

    /**
     * 新增风险信息主
     * 
     * @param riskInfo 风险信息主
     * @return 结果
     */
    public int insertRiskInfo(RiskInfo riskInfo);

    /**
     * 修改风险信息主
     * 
     * @param riskInfo 风险信息主
     * @return 结果
     */
    public int updateRiskInfo(RiskInfo riskInfo);

    /**
     * 删除风险信息主
     * 
     * @param id 风险信息主主键
     * @return 结果
     */
    public int deleteRiskInfoById(Long id);

    /**
     * 批量删除风险信息主
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRiskInfoByIds(Long[] ids);

    /**
     * 审核后更新风险信息状态与处理说明
     */
    public int updateRiskInfoAuditOutcome(@Param("id") Long id, @Param("status") Long status,
            @Param("handleResult") String handleResult, @Param("finalScore") Long finalScore,
            @Param("riskLevel") Long riskLevel);

    /**
     * 按类型统计风险信息数量
     */
    public List<RiskStatTypeCountVO> countRiskInfoGroupByTypeId();

    /**
     * 按风险等级统计数量
     */
    public List<RiskStatLevelCountVO> countRiskInfoGroupByRiskLevel();

    /**
     * 按审核时间所在月份统计审核记录数量（risk_info 无时间字段时的趋势参考）
     */
    public List<RiskStatMonthCountVO> countRiskAuditByMonth();
}
