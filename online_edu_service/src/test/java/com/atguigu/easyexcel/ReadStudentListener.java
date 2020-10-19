package com.atguigu.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ReadStudentListener extends AnalysisEventListener<Student> {
    //1.读取表头信息
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头信息"+headMap   );
    }
    //2.一行数据一行的读
    @Override
    public void invoke(Student student, AnalysisContext analysisContext) {
        System.out.println("**"+student);
    }
    //3.读取数据完成之后做的事情
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("over");
    }
}
