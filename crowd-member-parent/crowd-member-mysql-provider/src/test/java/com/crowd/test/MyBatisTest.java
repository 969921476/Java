package com.crowd.test;

import com.crowd.entity.po.MemberPO;
import com.crowd.entity.po.ProjectPO;
import com.crowd.entity.vo.PortalTypeVO;
import com.crowd.mapper.MemberPOMapper;
import com.crowd.mapper.ProjectPOMapper;
import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author cjn
 * @create 2020-03-19 18:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisTest {
    @Autowired
    private DataSource dataSource;
    private  Logger logger = LoggerFactory.getLogger(MyBatisTest.class);


    @Autowired
    private MemberPOMapper memberPOMapper;

    @Autowired
    private ProjectPOMapper projectPOMapper;

    @Test
    public void testMemberPOMapper() throws SQLException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String source = "123";
        String encode = passwordEncoder.encode(source);
        memberPOMapper.insert(new MemberPO(null, "jack", encode, "陈俊娜","222",1,2,"ss","sd",2));

    }

    @Test
    public void test() throws SQLException {
        Connection connection = dataSource.getConnection();
        logger.debug("-------------------------------------->" + connection.toString());

    }
    @Test
    public void test2(){
        List<PortalTypeVO> portalTypeVOS = projectPOMapper.selectPortalTypeVOList();
        System.out.println("................" + portalTypeVOS);

    }
}
