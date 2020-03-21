package com.crowd.controller;

import com.crowd.entity.po.MemberPO;
import com.crowd.service.api.MemberService;
import com.crowd.util.CrowdConstant;
import com.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cjn
 * @create 2020-03-19 18:51
 */
@RestController
public class MemberProviderHandler {

    @Autowired
    private MemberService memberService;

    @RequestMapping("/save/member/remote")
    public ResultEntity<String> saveMember(@RequestBody MemberPO memberPO) {

        try {
            memberService.saveMember(memberPO);
            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
               return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
            }
            return ResultEntity.failed(e.getMessage());
        }
    }


    @RequestMapping("/get/get/memberpo/by/login/acct/remote")
    public ResultEntity<MemberPO> getMemberPOByLoginAcctRemote(@RequestParam("loginacct") String loginacct){

        try {

            // 调用本地Service
            MemberPO memberPO = memberService.getMemberPOByLoginAcct(loginacct);
            // 如果没有抛异常返回 成功结果
            return ResultEntity.successWithData(memberPO);
        } catch (Exception e) {
            // 铺货异常返回错误信息
            return ResultEntity.failed(e.getMessage());
        }

    }

}
