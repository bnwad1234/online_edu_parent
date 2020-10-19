package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import java.util.ArrayList;

public class EasyExcelTest {
    @Test
    public void testWriteExcel(){
        //a.往那个文件里面写数据
        String fileName="C:\\200211\\work\\one.xls";
        //b.写什么样的数据
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            Student student = new Student();
            student.setStuNo(i);
            student.setStuName("王五"+i);
            students.add(student);
        }
        //c.执行操作
        EasyExcel.write(fileName,Student.class).sheet("学生列表").doWrite(students);
    }

    @Test
    public void testReadExcel(){
        //a.往那个文件里面写数据
        String fileName="C:\\200211\\work\\one.xls";
        //b.执行操作
        EasyExcel.read(fileName,Student.class,new ReadStudentListener()).doReadAll();
        //c.编写读取数据的listener
    }
}
