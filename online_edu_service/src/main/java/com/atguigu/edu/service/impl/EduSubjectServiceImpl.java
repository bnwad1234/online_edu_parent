package com.atguigu.edu.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.edu.entity.EduSubject;
import com.atguigu.edu.entity.ExcelSubject;
import com.atguigu.edu.listener.SubjectListener;
import com.atguigu.edu.mapper.EduSubjectMapper;
import com.atguigu.edu.service.EduSubjectService;
import com.atguigu.exception.EduException;
import com.atguigu.response.SubjectVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2020-06-24
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {
    @Autowired
    private SubjectListener subjectListener;

    @Override
    public void uploadSubject(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        EasyExcel.read(inputStream, ExcelSubject.class,subjectListener).doReadAll();
    }
    // (3).编写分类在数据库中是否存在的方法
    // 条件是 parentId结合title进行判断
    @Override
    public EduSubject existSubject(String parentId, String title){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",parentId);
        wrapper.eq("title",title);
        EduSubject existSubject = baseMapper.selectOne(wrapper);
        return existSubject;
    }

    @Override
    public List<SubjectVo> getAllSubject() {
        //a.先查询所有课程分类
        List<EduSubject> allSubject = baseMapper.selectList(null);
        ArrayList<SubjectVo> parentSubjectVos = new ArrayList<>();
        //b.查询所有的一级分类(找组长)
        for (EduSubject subject : allSubject) {
            //判断标准 parentId=0
            if(subject.getParentId().equals("0")){
                SubjectVo parentSubjectVo = new SubjectVo();
                //没有技术含量
                //parentSubjectVo.setId(subject.getId());
                //parentSubjectVo.setTitle(subject.getTitle());
                BeanUtils.copyProperties(subject,parentSubjectVo);
                parentSubjectVos.add(parentSubjectVo);
            }
        }
        //c.把一级分类放到一个角落(map) 为了去让二级分类找一级分类更简单
        HashMap<String, SubjectVo> parentSubjectMap = new HashMap<>();
        for (SubjectVo parentSubjectVo : parentSubjectVos) {
            //key 一级分类的id value一级分类对象本身
            parentSubjectMap.put(parentSubjectVo.getId(),parentSubjectVo);
        }
        //d.查询所有的二级分类(找组员)
        for (EduSubject subject : allSubject) {
            //判断标准 parentId!=0
            if(!subject.getParentId().equals("0")){
                //拿到二级分类的parentId 从map中找到该二级分类的一级分类
                SubjectVo parentSubjectVo = parentSubjectMap.get(subject.getParentId());
                SubjectVo childSubjectVo = new SubjectVo();
                BeanUtils.copyProperties(subject,childSubjectVo);
                //得到一级分类以后得到里的children 把自身放到children属性中 成为它的子节点
                parentSubjectVo.getChildren().add(childSubjectVo);
            }
        }
        // e.返回所有的一级分类
        return parentSubjectVos;
    }

    @Override
    public boolean deleteSubjectById(String id) {
        //a.先获取节点id
        //b.构建一个查询条件 parent_id该节点是否有子节点
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        //c.有子节点就不能删除
        if(count==0){
            int rows = baseMapper.deleteById(id);
            return rows>0;
        }else{
            throw new EduException(20001,"该节点存在子节点");
        }
    }

    @Override
    public boolean saveParentSubject(EduSubject eduSubject) {
        EduSubject subject = existSubject("0", eduSubject.getTitle());
        if(subject==null){
            subject=new EduSubject();
            subject.setParentId("0");
            subject.setTitle(eduSubject.getTitle());
            int rows = baseMapper.insert(subject);
            return rows>0;
        }
        return false;
    }

    @Override
    public boolean saveChildSubject(EduSubject eduSubject) {
        EduSubject existSubject = existSubject(eduSubject.getParentId(), eduSubject.getTitle());
        if(existSubject==null){
            int rows = baseMapper.insert(eduSubject);
            return rows>0;
        }
        return false;
    }
}
