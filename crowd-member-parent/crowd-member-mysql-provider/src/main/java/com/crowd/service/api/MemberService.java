package com.crowd.service.api;

import com.crowd.entity.po.MemberPO;

/**
 * @author cjn
 * @create 2020-03-19 18:54
 */
public interface MemberService {

    MemberPO getMemberPOByLoginAcct(String loginacct);

    void saveMember(MemberPO memberPO);
}
