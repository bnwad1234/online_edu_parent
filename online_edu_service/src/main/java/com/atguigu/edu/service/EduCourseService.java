package com.atguigu.edu.service;

import com.atguigu.edu.entity.EduCourse;
import com.atguigu.request.CourseCondition;
import com.atguigu.request.CourseInfoVO;
import com.atguigu.response.CourseConfirmVo;
import com.atguigu.response.CourseDetailInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2020-06-26
 */
public interface EduCourseService extends IService<EduCourse> {

    void saveCourseInfo(CourseInfoVO courseInfoVO);

    CourseInfoVO getCourseById(String id);

    void queryCoursePageByCondition(Page<EduCourse> coursePage, CourseCondition courseCondition);

    void updateCourseInfo(CourseInfoVO courseInfoVO);

    CourseConfirmVo queryCourseConfirmInfo(String courseId);

    void deleteCourseById(String courseId);

    CourseDetailInfoVo queryCourseDetailById(String courseId);
}
