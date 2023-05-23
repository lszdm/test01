package com.yizhi.student.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yizhi.common.annotation.Log;
import com.yizhi.common.controller.BaseController;
import com.yizhi.common.utils.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.yizhi.student.domain.StudentInfoDO;
import com.yizhi.student.service.StudentInfoService;

/**
 * 生基础信息表
 */
 
@Controller
@RequestMapping("/student/studentInfo")
public class StudentInfoController {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private StudentInfoService studentInfoService;

	@Autowired
	private BaseController baseController;

	@Log("学生信息保存")
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("student:studentInfo:add")
	public R save(StudentInfoDO studentInfoDO){
		Date currentDate = new Date();
		String dateString = StudentInfoController.formatter.format(currentDate);
		int currentLoginId = baseController.getUserId().intValue();
		Date date = null;
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		studentInfoDO.setAddTime(date);
		studentInfoDO.setEditTime(date);
		studentInfoDO.setAddUserid(currentLoginId);
		studentInfoDO.setEditUserid(currentLoginId);
		if(studentInfoService.save(studentInfoDO) > 0){
			return R.ok("添加成功");
		}
		return R.error("添加失败");
	}

	/**
	 * 可分页 查询
	 */
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("student:studentInfo:studentInfo")
	public PageUtils list(@RequestParam Map<String, Object> params){

		if (params.get("sort")!=null) {
			params.put("sort",BeanHump.camelToUnderline(params.get("sort").toString()));
		}
		Query query = new Query(params);
		List<StudentInfoDO> studentList = studentInfoService.list(query);
		int total = studentInfoService.count(query);
		PageUtils pageUtils = new PageUtils(studentList, total, query.getCurrPage(), query.getPageSize());

		return pageUtils;

	}


	/**
	 * 修改
	 */
	@Log("学生基础信息表修改")
	@ResponseBody
	@PostMapping("/update")
	@RequiresPermissions("student:studentInfo:edit")
	public R update(StudentInfoDO studentInfo){
		Date currentDate = new Date();
		String dateString = StudentInfoController.formatter.format(currentDate);
		int currentLoginId = baseController.getUserId().intValue();
		Date date = null;
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		studentInfo.setEditTime(date);
		studentInfo.setEditUserid(currentLoginId);
		if(studentInfoService.update(studentInfo) > 0){
			return R.ok("修改成功");
		}
		return R.error("修改失败");
	}

	/**
	 * 删除
	 */
	@Log("学生基础信息表删除")
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("student:studentInfo:remove")
	public R remove( Integer id){
		if(studentInfoService.remove(id) > 0){
			return R.ok("删除成功");
		}
		return R.error("删除失败");
	}
	
	/**
	 * 批量删除
	 */
	@Log("学生基础信息表批量删除")
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("student:studentInfo:batchRemove")
	public R remove(@RequestParam("ids[]") Integer[] ids){
		if(studentInfoService.batchRemove(ids) > 0){
			return R.ok("删除成功");
		}
		return R.error("删除失败");
	}


	//前后端不分离 客户端 -> 控制器-> 定位视图
	/**
	 * 学生管理 点击Tab标签 forward页面
	 */
	@GetMapping()
	@RequiresPermissions("student:studentInfo:studentInfo")
	String StudentInfo(){
		return "student/studentInfo/studentInfo";
	}

	/**
	 * 更新功能 弹出View定位
	 */
	@GetMapping("/edit/{id}")
	@RequiresPermissions("student:studentInfo:edit")
	String edit(@PathVariable("id") Integer id,Model model){
		StudentInfoDO studentInfo = studentInfoService.get(id);
		model.addAttribute("studentInfo", studentInfo);
		return "student/studentInfo/edit";
	}

	/**
	 * 学生管理 添加学生弹出 View
	 */
	@GetMapping("/add")
	@RequiresPermissions("student:studentInfo:add")
	String add(){
	    return "student/studentInfo/add";
	}
	
}//end class
