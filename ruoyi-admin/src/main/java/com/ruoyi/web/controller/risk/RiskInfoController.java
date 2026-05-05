package com.ruoyi.web.controller.risk;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.RiskInfo;
import com.ruoyi.system.domain.dto.RiskAuditDTO;
import com.ruoyi.system.service.IRiskInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风险信息主Controller
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@RestController
@RequestMapping("/system/info")
public class RiskInfoController extends BaseController
{
    @Autowired
    private IRiskInfoService riskInfoService;

    /**
     * 按类型统计（管理员）
     */
    @PreAuthorize("@ss.hasPermi('system:info:list')")
    @GetMapping("/stat/type")
    public AjaxResult statByType()
    {
        return success(riskInfoService.countRiskInfoGroupByTypeId());
    }

    /**
     * 按风险等级统计（管理员）
     */
    @PreAuthorize("@ss.hasPermi('system:info:list')")
    @GetMapping("/stat/level")
    public AjaxResult statByLevel()
    {
        return success(riskInfoService.countRiskInfoGroupByRiskLevel());
    }

    /**
     * 按月份统计审核记录（管理员，基于 risk_audit.audit_time）
     */
    @PreAuthorize("@ss.hasPermi('system:info:list')")
    @GetMapping("/stat/month")
    public AjaxResult statByMonth()
    {
        return success(riskInfoService.countRiskAuditByMonth());
    }

    /**
     * 审核风险信息
     */
    @PreAuthorize("@ss.hasPermi('system:info:edit')")
    @Log(title = "风险信息审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody RiskAuditDTO dto)
    {
        riskInfoService.auditRisk(dto);
        return success();
    }

    /**
     * 查询风险信息主列表
     */
    @PreAuthorize("@ss.hasPermi('system:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(RiskInfo riskInfo)
    {
        startPage();
        List<RiskInfo> list = riskInfoService.selectRiskInfoList(riskInfo);
        return getDataTable(list);
    }

    /**
     * 导出风险信息主列表
     */
    @PreAuthorize("@ss.hasPermi('system:info:export')")
    @Log(title = "风险信息主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RiskInfo riskInfo)
    {
        List<RiskInfo> list = riskInfoService.selectRiskInfoList(riskInfo);
        ExcelUtil<RiskInfo> util = new ExcelUtil<RiskInfo>(RiskInfo.class);
        util.exportExcel(response, list, "风险信息主数据");
    }

    /**
     * 获取风险信息主详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(riskInfoService.selectRiskInfoById(id));
    }

    /**
     * 新增风险信息主
     */
    @PreAuthorize("@ss.hasPermi('system:info:add')")
    @Log(title = "风险信息主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RiskInfo riskInfo)
    {
        return toAjax(riskInfoService.insertRiskInfo(riskInfo));
    }

    /**
     * 修改风险信息主
     */
    @PreAuthorize("@ss.hasPermi('system:info:edit')")
    @Log(title = "风险信息主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RiskInfo riskInfo)
    {
        return toAjax(riskInfoService.updateRiskInfo(riskInfo));
    }

    /**
     * 删除风险信息主
     */
    @PreAuthorize("@ss.hasPermi('system:info:remove')")
    @Log(title = "风险信息主", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(riskInfoService.deleteRiskInfoByIds(ids));
    }
}
