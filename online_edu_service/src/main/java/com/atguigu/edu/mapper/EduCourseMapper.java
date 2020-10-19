package com.atguigu.edu.mapper;

import com.atguigu.edu.entity.EduCourse;
import com.atguigu.response.CourseConfirmVo;
import com.atguigu.response.CourseDetailInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2020-06-26
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    CourseConfirmVo queryCourseConfirmInfo(String courseId);

    CourseDetailInfoVo queryCourseDetailById(String courseId);
}
