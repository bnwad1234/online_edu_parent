package com.atguigu.edu.service;

import com.atguigu.edu.entity.EduSubject;
import com.atguigu.response.SubjectVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2020-06-24
 */
public interface EduSubjectService extends IService<EduSubject> {

    void uploadSubject(MultipartFile file) throws Exception;

    // (3).编写分类在数据库中是否存在的方法
    // 条件是 parentId结合title进行判断
    EduSubject existSubject(String parentId, String title);

    List<SubjectVo> getAllSubject();

    boolean deleteSubjectById(String id);

    boolean saveParentSubject(EduSubject eduSubject);

    boolean saveChildSubject(EduSubject eduSubject);
}
