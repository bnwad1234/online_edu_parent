package com.atguigu.edu.controller;


import com.atguigu.edu.entity.EduTeacher;
import com.atguigu.edu.service.EduTeacherService;
import com.atguigu.exception.EduException;
import com.atguigu.request.TeacherCondition;
import com.atguigu.response.RetVal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2020-06-21
 */
@RestController
@RequestMapping("/edu/teacher")
@CrossOrigin
public class EduTeacherController {
    @Autowired
    private EduTeacherService teacherService;

//    //1.查询所有讲师
//    @GetMapping
//    public List<EduTeacher> getAllTeacher(){
//        List<EduTeacher> teacherList = teacherService.list(null);
//        for (EduTeacher eduTeacher : teacherList) {
//            System.out.println(eduTeacher.getGmtCreate());
//            System.out.println(eduTeacher.getGmtModified());
//        }
//        return teacherList;
//    }
//
//    //2.删除讲师
//    @DeleteMapping("{id}")
//    public boolean deleteTeacherById(@PathVariable String id){
//        boolean flag = teacherService.removeById(id);
//        return flag;
//    }

    //1.查询所有讲师
    @GetMapping
    public RetVal getAllTeacher() throws EduException {
//        try {
//            int a=10/0;
//        } catch (Exception e) {
//            throw new EduException();
//        }
        List<EduTeacher> teacherList = teacherService.list(null);
        return RetVal.success().data("teacherList",teacherList);
    }

    //2.删除讲师
    @DeleteMapping("{id}")
    public RetVal deleteTeacherById(@PathVariable String id){
        boolean flag = teacherService.removeById(id);
        if(flag){
            return RetVal.success();
        }else{
            return RetVal.error();
        }
    }
    //3.讲师分页查询
    @GetMapping("queryTeacherPage/{pageNum}/{pageSize}")
    public RetVal queryTeacherPage(
            @PathVariable("pageNum") long pageNum,
            @PathVariable("pageSize") long pageSize){
        Page<EduTeacher> teacherPage = new Page<>(pageNum,pageSize);
        teacherService.page(teacherPage, null);
        //返回结果集
        List<EduTeacher> teacherList = teacherPage.getRecords();
        //总记录数
        long total = teacherPage.getTotal();
        return RetVal.success().data("total",total).data("teacherList",teacherList);
    }
    //4.讲师分页查询带条件
    @GetMapping("queryTeacherPageByCondition/{pageNum}/{pageSize}")
    public RetVal queryTeacherPageByCondition(
            @PathVariable("pageNum") long pageNum,
            @PathVariable("pageSize") long pageSize,
            TeacherCondition teacherCondition){
        Page<EduTeacher> teacherPage = new Page<>(pageNum,pageSize);
        teacherService.queryTeacherPageByCondition(teacherPage, teacherCondition);
        //返回结果集
        List<EduTeacher> teacherList = teacherPage.getRecords();
        //总记录数
        long total = teacherPage.getTotal();
        return RetVal.success().data("total",total).data("teacherList",teacherList);
    }
    //5.添加讲师
    @PostMapping
    public RetVal saveTeacher(EduTeacher teacher){
        boolean flag = teacherService.save(teacher);
        if(flag){
            return RetVal.success();
        }else{
            return RetVal.error();
        }
    }
    //6.修改讲师
    @PutMapping
    public RetVal updateTeacher(EduTeacher teacher){
        boolean flag = teacherService.updateById(teacher);
        if(flag){
            return RetVal.success();
        }else{
            return RetVal.error();
        }
    }
    //7.根据id查询讲师
    @GetMapping("{id}")
    public RetVal queryTeacherById(@PathVariable String id){
        EduTeacher teacher = teacherService.getById(id);
        return RetVal.success().data("teacher",teacher);
    }

}





















