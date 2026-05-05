package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.RiskAudit;
import com.ruoyi.system.domain.RiskInfo;
import com.ruoyi.system.domain.RiskRule;
import com.ruoyi.system.domain.dto.RiskAuditDTO;
import com.ruoyi.system.domain.vo.RiskStatLevelCountVO;
import com.ruoyi.system.domain.vo.RiskStatMonthCountVO;
import com.ruoyi.system.domain.vo.RiskStatTypeCountVO;
import com.ruoyi.system.mapper.RiskAuditMapper;
import com.ruoyi.system.mapper.RiskInfoMapper;
import com.ruoyi.system.mapper.RiskRuleMapper;
import com.ruoyi.system.service.IRiskInfoService;

/**
 * 风险信息主Service业务层处理
 *
 * @author ruoyi
 * @date 2026-05-04
 */
@Service
public class RiskInfoServiceImpl implements IRiskInfoService
{
    @Autowired
    private RiskInfoMapper riskInfoMapper;

    @Autowired
    private RiskRuleMapper riskRuleMapper;

    @Autowired
    private RiskAuditMapper riskAuditMapper;

    @Override
    public RiskInfo selectRiskInfoById(Long id)
    {
        RiskInfo info = riskInfoMapper.selectRiskInfoById(id);
        if (info == null)
        {
            return null;
        }
        if (!SecurityUtils.isAdmin() && !SecurityUtils.getUserId().equals(info.getUserId()))
        {
            throw new ServiceException("无权限查看该数据", HttpStatus.FORBIDDEN);
        }
        return info;
    }

    @Override
    public List<RiskInfo> selectRiskInfoList(RiskInfo riskInfo)
    {
        if (riskInfo == null)
        {
            riskInfo = new RiskInfo();
        }
        if (!SecurityUtils.isAdmin())
        {
            riskInfo.setUserId(SecurityUtils.getUserId());
        }
        return riskInfoMapper.selectRiskInfoList(riskInfo);
    }

    /**
     * 新增风险信息：自动关键词研判、打分与定级
     */
    @Override
    public int insertRiskInfo(RiskInfo riskInfo)
    {
        if (StringUtils.isEmpty(riskInfo.getTitle()))
        {
            throw new ServiceException("风险标题不能为空");
        }
        String title = riskInfo.getTitle();
        String content = riskInfo.getContent() != null ? riskInfo.getContent() : "";
        String text = title + content;

        List<RiskRule> rules = riskRuleMapper.selectRiskRuleListActive();
        long keywordTotal = 0L;
        Map<Long, Long> typeScoreMap = new HashMap<>();
        if (rules != null)
        {
            for (RiskRule rule : rules)
            {
                if (rule == null || StringUtils.isEmpty(rule.getKeyword()))
                {
                    continue;
                }
                String kw = rule.getKeyword().trim();
                if (kw.isEmpty())
                {
                    continue;
                }
                if (!text.contains(kw))
                {
                    continue;
                }
                long w = rule.getWeight() != null ? rule.getWeight() : 0L;
                keywordTotal += w;
                if (rule.getTypeId() != null)
                {
                    typeScoreMap.merge(rule.getTypeId(), w, Long::sum);
                }
            }
        }

        Long userChosenTypeId = riskInfo.getTypeId();
        Long resolvedTypeId = userChosenTypeId;
        if (resolvedTypeId == null)
        {
            resolvedTypeId = pickBestTypeId(typeScoreMap);
        }

        long score = keywordTotal;
        Long riskLevel;
        if (score < 5L)
        {
            riskLevel = 1L;
        }
        else if (score <= 15L)
        {
            riskLevel = 2L;
        }
        else
        {
            riskLevel = 3L;
        }

        riskInfo.setKeywordScore(keywordTotal);
        riskInfo.setFinalScore(keywordTotal);
        riskInfo.setTypeId(resolvedTypeId);
        riskInfo.setRiskLevel(riskLevel);
        riskInfo.setStatus(0L);
        riskInfo.setUserId(SecurityUtils.getUserId());
        return riskInfoMapper.insertRiskInfo(riskInfo);
    }

    /**
     * 在类型得分中取最高者；同分取较小 type_id
     */
    private Long pickBestTypeId(Map<Long, Long> typeScoreMap)
    {
        if (typeScoreMap == null || typeScoreMap.isEmpty())
        {
            return null;
        }
        Long bestId = null;
        long bestScore = Long.MIN_VALUE;
        for (Map.Entry<Long, Long> e : typeScoreMap.entrySet())
        {
            Long tid = e.getKey();
            if (tid == null)
            {
                continue;
            }
            long s = e.getValue() != null ? e.getValue() : 0L;
            if (s > bestScore)
            {
                bestScore = s;
                bestId = tid;
            }
            else if (s == bestScore && bestId != null && tid < bestId)
            {
                bestId = tid;
            }
        }
        return bestId;
    }

    @Override
    public int updateRiskInfo(RiskInfo riskInfo)
    {
        return riskInfoMapper.updateRiskInfo(riskInfo);
    }

    @Override
    public int deleteRiskInfoByIds(Long[] ids)
    {
        return riskInfoMapper.deleteRiskInfoByIds(ids);
    }

    @Override
    public int deleteRiskInfoById(Long id)
    {
        return riskInfoMapper.deleteRiskInfoById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditRisk(RiskAuditDTO dto)
    {
        if (!SecurityUtils.isAdmin())
        {
            throw new ServiceException("仅管理员可审核风险信息", HttpStatus.FORBIDDEN);
        }
        if (dto == null || dto.getRiskId() == null)
        {
            throw new ServiceException("风险编号不能为空");
        }
        String result = StringUtils.trim(dto.getAuditResult());
        String comment = dto.getAuditComment() != null ? dto.getAuditComment() : "";
        if (StringUtils.isEmpty(result))
        {
            throw new ServiceException("审核结果不能为空");
        }
        RiskInfo existing = riskInfoMapper.selectRiskInfoById(dto.getRiskId());
        if (existing == null)
        {
            throw new ServiceException("风险信息不存在");
        }
        Long newStatus;
        if ("通过".equals(result))
        {
            newStatus = 1L;
        }
        else if ("驳回".equals(result))
        {
            newStatus = 2L;
        }
        else
        {
            throw new ServiceException("审核结果无效，请填写：通过 或 驳回");
        }
        riskInfoMapper.updateRiskInfoAuditOutcome(dto.getRiskId(), newStatus, comment);

        RiskAudit audit = new RiskAudit();
        audit.setRiskId(dto.getRiskId());
        audit.setAuditUserId(SecurityUtils.getUserId());
        audit.setAuditResult(result);
        audit.setAuditComment(comment);
        audit.setAuditTime(DateUtils.getNowDate());
        riskAuditMapper.insertRiskAudit(audit);
    }

    @Override
    public List<RiskStatTypeCountVO> countRiskInfoGroupByTypeId()
    {
        assertAdminForStatistics();
        return riskInfoMapper.countRiskInfoGroupByTypeId();
    }

    @Override
    public List<RiskStatLevelCountVO> countRiskInfoGroupByRiskLevel()
    {
        assertAdminForStatistics();
        return riskInfoMapper.countRiskInfoGroupByRiskLevel();
    }

    @Override
    public List<RiskStatMonthCountVO> countRiskAuditByMonth()
    {
        assertAdminForStatistics();
        return riskInfoMapper.countRiskAuditByMonth();
    }

    private void assertAdminForStatistics()
    {
        if (!SecurityUtils.isAdmin())
        {
            throw new ServiceException("仅管理员可查看统计数据", HttpStatus.FORBIDDEN);
        }
    }
}
