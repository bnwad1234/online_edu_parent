package com.atguigu.edu.controller.front;

import com.atguigu.edu.service.EduChapterService;
import com.atguigu.edu.service.EduCourseService;
import com.atguigu.response.ChapterVo;
import com.atguigu.response.CourseDetailInfoVo;
import com.atguigu.response.RetVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/edu/front/course")
@CrossOrigin
public class FrontCourseController {
    @Autowired
    private EduCourseService courseService;
    @Autowired
    private EduChapterService chapterService;
    //1.分页查询课程列表


    //2.查询课程的详情信息
    @GetMapping("queryCourseDetailById/{courseId}")
    public RetVal queryCourseDetailById(@PathVariable String courseId) {
        //a.章节和小节信息
        List<ChapterVo> chapterAndSection = chapterService.getChapterAndSection(courseId);
        //b.课程的详情信息
       CourseDetailInfoVo courseDetailInfoVo= courseService.queryCourseDetailById(courseId);
        return RetVal.success().data("courseDetailInfoVo",courseDetailInfoVo).data("chapterAndSection",chapterAndSection);
    }


}
