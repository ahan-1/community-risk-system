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
import com.ruoyi.system.domain.RiskType;
import com.ruoyi.system.service.IRiskTypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风险类型Controller
 * 
 * @author ruoyi
 * @date 2026-05-04
 */
@RestController
@RequestMapping("/system/type")
public class RiskTypeController extends BaseController
{
    @Autowired
    private IRiskTypeService riskTypeService;

    /**
     * 查询风险类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:type:list')")
    @GetMapping("/list")
    public TableDataInfo list(RiskType riskType)
    {
        startPage();
        List<RiskType> list = riskTypeService.selectRiskTypeList(riskType);
        return getDataTable(list);
    }

    /**
     * 导出风险类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:type:export')")
    @Log(title = "风险类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RiskType riskType)
    {
        List<RiskType> list = riskTypeService.selectRiskTypeList(riskType);
        ExcelUtil<RiskType> util = new ExcelUtil<RiskType>(RiskType.class);
        util.exportExcel(response, list, "风险类型数据");
    }

    /**
     * 获取风险类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:type:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(riskTypeService.selectRiskTypeById(id));
    }

    /**
     * 新增风险类型
     */
    @PreAuthorize("@ss.hasPermi('system:type:add')")
    @Log(title = "风险类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RiskType riskType)
    {
        return toAjax(riskTypeService.insertRiskType(riskType));
    }

    /**
     * 修改风险类型
     */
    @PreAuthorize("@ss.hasPermi('system:type:edit')")
    @Log(title = "风险类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RiskType riskType)
    {
        return toAjax(riskTypeService.updateRiskType(riskType));
    }

    /**
     * 删除风险类型
     */
    @PreAuthorize("@ss.hasPermi('system:type:remove')")
    @Log(title = "风险类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(riskTypeService.deleteRiskTypeByIds(ids));
    }
}
