package com.crowd.service.impl;

import com.crowd.entity.po.MemberPO;
import com.crowd.entity.po.MemberPOExample;
import com.crowd.mapper.MemberPOMapper;
import com.crowd.service.api.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author cjn
 * @create 2020-03-19 18:54
 */
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberPOMapper memberPOMapper;

    @Override
    public MemberPO getMemberPOByLoginAcct(String loginacct) {

        // 创建Example对象
        MemberPOExample memberPOExample = new MemberPOExample();

        // 创建criteria对象
        MemberPOExample.Criteria criteria = memberPOExample.createCriteria();
        // 封装查询条件
        criteria.andLoginacctEqualTo(loginacct);

        // 执行查询
        List<MemberPO> list = memberPOMapper.selectByExample(memberPOExample);

        if (list == null || list.size() == 0) {
            return null;
        }

        // 获取结果
        return list.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class,readOnly = false)
    @Override
    public void saveMember(MemberPO memberPO) {

        memberPOMapper.insertSelective(memberPO);
    }
}
