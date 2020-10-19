package com.atguigu.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.edu.entity.EduSubject;
import com.atguigu.edu.entity.ExcelSubject;
import com.atguigu.edu.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubjectListener extends AnalysisEventListener<ExcelSubject> {
    @Autowired
    private EduSubjectService subjectService;

    //一行的读取数据
    @Override
    public void invoke(ExcelSubject excelSubject, AnalysisContext analysisContext) {
        //(1).读取到的数据分为两列 第一列是parent 第二列是child
        String parentSubjectName = excelSubject.getParentSubjectName();
        //(2).保存一级分类的时候需要判断数据库中是否存在
        EduSubject parentSubject = subjectService.existSubject("0", parentSubjectName);
        if(parentSubject==null){
            parentSubject = new EduSubject();
            parentSubject.setParentId("0");
            parentSubject.setTitle(parentSubjectName);
            subjectService.save(parentSubject);
        }
        //(3).保存二级分类也需要判断是否存在，还需拿到一级分类的id做为parentId
        String childSubjectName = excelSubject.getChildSubjectName();
        String parentId = parentSubject.getId();
        EduSubject childSubject = subjectService.existSubject(parentId, childSubjectName);
        if(childSubject==null){
            childSubject = new EduSubject();
            childSubject.setParentId(parentId);
            childSubject.setTitle(childSubjectName);
            subjectService.save(childSubject);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}
