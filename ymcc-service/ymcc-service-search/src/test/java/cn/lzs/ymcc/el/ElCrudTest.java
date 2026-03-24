package cn.lzs.ymcc.el;

import cn.lzs.ymcc.ElDoc.EmployeeDoc;
import cn.lzs.ymcc.SearchApplication;
import cn.lzs.ymcc.repository.EsCrudRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * @author 李振生
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class ElCrudTest {
    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    private EsCrudRepository esCrudRepository;

    @Test
    public void testCreateIndex() {
        //创建索引
        template.createIndex(EmployeeDoc.class);
        //做文档映射
        template.putMapping(EmployeeDoc.class);
    }

    @Test
    public void addDoc() {
        EmployeeDoc employeeDoc = new EmployeeDoc();
        employeeDoc.setId(1L);
        employeeDoc.setName("张三");
        employeeDoc.setSex("男");
        esCrudRepository.save(employeeDoc);
    }
    @Test
    public void addBatchDoc() {
        ArrayList<EmployeeDoc> employeeDocs = new ArrayList<>();
        for (int i = 2; i <=10; i++) {
            EmployeeDoc employeeDoc = new EmployeeDoc();
            employeeDoc.setId(Long.valueOf(i));
            employeeDoc.setName("李四" + i);
            employeeDoc.setSex("男");
            employeeDocs.add(employeeDoc);
        }
        esCrudRepository.saveAll(employeeDocs);
    }
    @Test
    public void delDoc() {
        esCrudRepository.deleteById(1L);
    }
    @Test
    public void updateDoc() {
        EmployeeDoc employeeDoc = new EmployeeDoc();
        employeeDoc.setId(2L);
        employeeDoc.setName("李四修改");
        employeeDoc.setSex("女");
        esCrudRepository.save(employeeDoc);
    }
}
