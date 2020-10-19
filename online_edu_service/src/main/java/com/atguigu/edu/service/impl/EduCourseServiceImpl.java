package com.atguigu.edu.service.impl;

import com.atguigu.edu.entity.EduCourse;
import com.atguigu.edu.entity.EduCourseDescription;
import com.atguigu.edu.mapper.EduCourseMapper;
import com.atguigu.edu.service.EduChapterService;
import com.atguigu.edu.service.EduCourseDescriptionService;
import com.atguigu.edu.service.EduCourseService;
import com.atguigu.edu.service.EduSectionService;
import com.atguigu.request.CourseCondition;
import com.atguigu.request.CourseInfoVO;
import com.atguigu.response.CourseConfirmVo;
import com.atguigu.response.CourseDetailInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2020-06-26
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {
    @Autowired
    private EduCourseDescriptionService descriptionService;
    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private EduSectionService sectionService;
    @Override
    public void saveCourseInfo(CourseInfoVO courseInfoVO) {
        //1.获取课程基本信息
        EduCourse course = new EduCourse();
        BeanUtils.copyProperties(courseInfoVO,course);
        baseMapper.insert(course);
        //2.获取课程描述信息 课程描述信息和课程基本信息id共用 type = IdType.INPUT
        EduCourseDescription description = new EduCourseDescription();
        String courseId = course.getId();
        description.setId(courseId);
        description.setDescription(courseInfoVO.getDescription());
        descriptionService.save(description);
    }

    @Override
    public CourseInfoVO getCourseById(String id) {
        CourseInfoVO courseInfoVO = new CourseInfoVO();
        //查询课程的基本信息
        EduCourse course = baseMapper.selectById(id);
        BeanUtils.copyProperties(course,courseInfoVO);
        //查询课程的描述信息
        EduCourseDescription description = descriptionService.getById(id);
        if(description!=null){
            courseInfoVO.setDescription(description.getDescription());
        }
        return courseInfoVO;
    }

    @Override
    public void queryCoursePageByCondition(Page<EduCourse> coursePage, CourseCondition courseCondition) {
        //获取每个查询参数
        String title = courseCondition.getTitle();
        String status = courseCondition.getStatus();
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //判断以上传递过来的参数是否为空
        if(StringUtils.isNotEmpty(title)){
            wrapper.like("title",title);
        }

        if(StringUtils.isNotEmpty(status)){
            wrapper.ge("status",status);
        }
        baseMapper.selectPage(coursePage,wrapper);
    }

    @Override
    public void updateCourseInfo(CourseInfoVO courseInfoVO) {
        //修改课程基本信息
        EduCourse course = new EduCourse();
        BeanUtils.copyProperties(courseInfoVO,course);
        baseMapper.updateById(course);
        //修改课程描述信息
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfoVO.getId());
        courseDescription.setDescription(courseInfoVO.getDescription());
        descriptionService.updateById(courseDescription);

    }

    @Override
    public CourseConfirmVo queryCourseConfirmInfo(String courseId) {
        return baseMapper.queryCourseConfirmInfo(courseId);
    }

    @Override
    public void deleteCourseById(String courseId) {
        //a.该课程所对应的章节
        chapterService.deleteChapterByCourseId(courseId);
        //b.该课程所对应的小节
        sectionService.deleteSectionByCourseId(courseId);
        //c.删除该课程所对应的描述信息
        descriptionService.removeById(courseId);
        //d.删除课程基本信息本身
        baseMapper.deleteById(courseId);
        //e.整个过程需要事务进行维护 作业
    }

    @Override
    public CourseDetailInfoVo queryCourseDetailById(String courseId) {
        return baseMapper.queryCourseDetailById(courseId);
    }


}
