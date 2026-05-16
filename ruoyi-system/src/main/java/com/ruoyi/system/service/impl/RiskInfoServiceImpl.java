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
 * 风险信息业务层
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
        if (!isRiskAdmin() && !SecurityUtils.getUserId().equals(info.getUserId()))
        {
            throw new ServiceException("无权限查看该数据", HttpStatus.FORBIDDEN);
        }
        hideSensitiveFieldsForNonAdmin(info);
        return info;
    }

    @Override
    public List<RiskInfo> selectRiskInfoList(RiskInfo riskInfo)
    {
        if (riskInfo == null)
        {
            riskInfo = new RiskInfo();
        }
        if (!isRiskAdmin())
        {
            riskInfo.setUserId(SecurityUtils.getUserId());
        }
        List<RiskInfo> list = riskInfoMapper.selectRiskInfoList(riskInfo);
        if (list != null)
        {
            for (RiskInfo item : list)
            {
                hideSensitiveFieldsForNonAdmin(item);
            }
        }
        return list;
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
                int titleCount = countKeywordOccurrences(title, kw);
                int contentCount = countKeywordOccurrences(content, kw);
                int totalCount = titleCount + contentCount;
                if (totalCount <= 0)
                {
                    continue;
                }
                long weight = rule.getWeight() != null ? rule.getWeight() : 0L;
                long ruleScore = calculateRuleScore(weight, titleCount, totalCount);
                keywordTotal += ruleScore;
                if (rule.getTypeId() != null)
                {
                    typeScoreMap.merge(rule.getTypeId(), ruleScore, Long::sum);
                }
            }
        }

        Long resolvedTypeId = riskInfo.getTypeId();
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
    private long calculateRuleScore(long weight, int titleCount, int totalCount)
    {
        long countScore = weight * Math.min(totalCount, 3);
        long titleBonus = titleCount > 0 ? (weight + 1) / 2 : 0L;
        return countScore + titleBonus;
    }

    private int countKeywordOccurrences(String text, String keyword)
    {
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(keyword))
        {
            return 0;
        }
        int count = 0;
        int startIndex = 0;
        while (startIndex <= text.length() - keyword.length())
        {
            int foundIndex = text.indexOf(keyword, startIndex);
            if (foundIndex < 0)
            {
                break;
            }
            count++;
            startIndex = foundIndex + keyword.length();
        }
        return count;
    }

    private Long pickBestTypeId(Map<Long, Long> typeScoreMap)
    {
        if (typeScoreMap == null || typeScoreMap.isEmpty())
        {
            return null;
        }
        Long bestId = null;
        long bestScore = Long.MIN_VALUE;
        for (Map.Entry<Long, Long> entry : typeScoreMap.entrySet())
        {
            Long typeId = entry.getKey();
            if (typeId == null)
            {
                continue;
            }
            long score = entry.getValue() != null ? entry.getValue() : 0L;
            if (score > bestScore)
            {
                bestScore = score;
                bestId = typeId;
            }
            else if (score == bestScore && bestId != null && typeId < bestId)
            {
                bestId = typeId;
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
        if (!isRiskAdmin())
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

        Long finalScore = dto.getFinalScore() != null ? dto.getFinalScore() : existing.getFinalScore();
        if (finalScore == null || finalScore < 0L)
        {
            throw new ServiceException("综合评分不能为空且不能小于0");
        }

        Long riskLevel = dto.getRiskLevel() != null ? dto.getRiskLevel() : existing.getRiskLevel();
        if (riskLevel == null || (riskLevel != 1L && riskLevel != 2L && riskLevel != 3L))
        {
            throw new ServiceException("风险等级只能是1、2或3");
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

        riskInfoMapper.updateRiskInfoAuditOutcome(dto.getRiskId(), newStatus, comment, finalScore, riskLevel);

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
        if (!isRiskAdmin())
        {
            throw new ServiceException("仅管理员可查看统计数据", HttpStatus.FORBIDDEN);
        }
    }

    private void hideSensitiveFieldsForNonAdmin(RiskInfo riskInfo)
    {
        if (riskInfo == null || isRiskAdmin())
        {
            return;
        }
        riskInfo.setKeywordScore(null);
        riskInfo.setFinalScore(null);
        riskInfo.setRiskLevel(null);
    }

    private boolean isRiskAdmin()
    {
        return SecurityUtils.hasRole("admin");
    }
}
