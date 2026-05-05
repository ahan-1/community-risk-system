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
import com.ruoyi.system.domain.RiskRule;
import com.ruoyi.system.service.IRiskRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风险研判规则Controller
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@RestController
@RequestMapping("/system/rule")
public class RiskRuleController extends BaseController
{
    @Autowired
    private IRiskRuleService riskRuleService;

    /**
     * 查询风险研判规则列表
     */
    @PreAuthorize("@ss.hasPermi('system:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(RiskRule riskRule)
    {
        startPage();
        List<RiskRule> list = riskRuleService.selectRiskRuleList(riskRule);
        return getDataTable(list);
    }

    /**
     * 导出风险研判规则列表
     */
    @PreAuthorize("@ss.hasPermi('system:rule:export')")
    @Log(title = "风险研判规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RiskRule riskRule)
    {
        List<RiskRule> list = riskRuleService.selectRiskRuleList(riskRule);
        ExcelUtil<RiskRule> util = new ExcelUtil<RiskRule>(RiskRule.class);
        util.exportExcel(response, list, "风险研判规则数据");
    }

    /**
     * 获取风险研判规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:rule:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(riskRuleService.selectRiskRuleById(id));
    }

    /**
     * 新增风险研判规则
     */
    @PreAuthorize("@ss.hasPermi('system:rule:add')")
    @Log(title = "风险研判规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RiskRule riskRule)
    {
        return toAjax(riskRuleService.insertRiskRule(riskRule));
    }

    /**
     * 修改风险研判规则
     */
    @PreAuthorize("@ss.hasPermi('system:rule:edit')")
    @Log(title = "风险研判规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RiskRule riskRule)
    {
        return toAjax(riskRuleService.updateRiskRule(riskRule));
    }

    /**
     * 删除风险研判规则
     */
    @PreAuthorize("@ss.hasPermi('system:rule:remove')")
    @Log(title = "风险研判规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(riskRuleService.deleteRiskRuleByIds(ids));
    }
}
