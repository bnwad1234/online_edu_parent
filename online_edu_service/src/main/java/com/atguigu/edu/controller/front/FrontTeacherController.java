package com.atguigu.edu.controller.front;

import com.atguigu.edu.entity.EduCourse;
import com.atguigu.edu.entity.EduTeacher;
import com.atguigu.edu.service.EduCourseService;
import com.atguigu.edu.service.EduTeacherService;
import com.atguigu.response.RetVal;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/edu/front/teacher")
@CrossOrigin
public class FrontTeacherController {
    @Autowired
    private EduTeacherService teacherService;
    @Autowired
    private EduCourseService courseService;

    //1.分页查询讲师列表
    @GetMapping("queryTeacherPage/{pageNum}/{pageSize}")
    public RetVal queryTeacherPage(
            @PathVariable("pageNum") long pageNum,
            @PathVariable("pageSize") long pageSize){
        Page<EduTeacher> teacherPage = new Page<>(pageNum,pageSize);
        Map<String,Object> retMap=teacherService.queryTeacherPage(teacherPage);
        return RetVal.success().data(retMap);
    }

    //2.查询讲师的详情信息
    @GetMapping("queryTeacherDetailById/{teacherId}")
    public RetVal queryTeacherDetailById(@PathVariable String teacherId){
       //a.讲师基本信息
        EduTeacher teacher = teacherService.getById(teacherId);
        //b.该讲师所教授的课程
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id",teacherId);
        List<EduCourse> courseList = courseService.list(wrapper);
        return RetVal.success().data("teacher",teacher).data("courseList",courseList);
    }




}
