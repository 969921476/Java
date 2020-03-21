package com.crowd.controller;

import com.crowd.api.MySQLRemoteService;
import com.crowd.api.RedisRemoteService;
import com.crowd.config.ShortMessageProperties;
import com.crowd.entity.po.MemberPO;
import com.crowd.entity.vo.MemberLoginVO;
import com.crowd.entity.vo.MemberVO;
import com.crowd.util.CrowdConstant;
import com.crowd.util.CrowdUtil;
import com.crowd.util.ResultEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author cjn
 * @create 2020-03-20 10:10
 */
@Controller
public class MemberController {

    @Autowired
    private ShortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/member/my/crowd")
    public String toCrowd(){
        // TODO

        return "member-crowd";
    }


    @RequestMapping("/auth/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping("/auth/member/do/login")
    public String Login(@RequestParam("loginacct") String loginacct, @RequestParam("userpswd") String userpswd, ModelMap modelMap, HttpSession session) {
        ResultEntity<MemberPO> resultEntity = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
        if (ResultEntity.FAILED.equals(resultEntity)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());
            return "member-login";
        }
        MemberPO memberPO = resultEntity.getData();
        if (memberPO == null) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 比较密码
        String userpswdDataBase = memberPO.getUserpswd();


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean matches = passwordEncoder.matches(userpswd, userpswdDataBase);

        if (!matches) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 创建MemberLoginVO存入Session
        MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getId(), memberPO.getUsername(), memberPO.getEmail());
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberLoginVO);

        return "redirect:http://localhost:8080/auth/member/to/center/page";

    }



    @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, ModelMap modelMap) {
        // 获取手机号
        String phoneNum = memberVO.getPhoneNum();
        // 获取Redis存储的key
        String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
        // 读取Redis 的value
        ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKeyRemote(key);
        // 检查查询操作是否有效
        String result = resultEntity.getResult();

        if (ResultEntity.FAILED.equals(result)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());

            return "member-reg";
        }
        String redisCode = resultEntity.getData();
        if (redisCode == null) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
            return "member-reg";
        }

        // 可以比较验证码
        String formCode = memberVO.getCode();
        if (!Objects.equals(redisCode, formCode)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_INVALID);
            return "member-reg";
        }

        // 验证码一致 删除Redis
        redisRemoteService.removeRedisKeyRemote(key);

        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String useerPswd = memberVO.getUserpswd();
        String encode = passwordEncoder.encode(useerPswd);
        memberVO.setUserpswd(encode);
        // 执行保存
        // 创建PO对象
        MemberPO memberPO = new MemberPO();

        // 赋值属性
        BeanUtils.copyProperties(memberVO, memberPO);

        // 调用Mysql
        ResultEntity<String> saveMemberResultEntity = mySQLRemoteService.saveMember(memberPO);

        if (ResultEntity.FAILED.equals(saveMemberResultEntity)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, saveMemberResultEntity.getMessage());
            return "member-reg";
        }


        return "redirect:/auth/member/to/login/page";
    }

    @ResponseBody
    @RequestMapping("/auth/member/send/short/message.json")
    public ResultEntity<String > sendMessage(@RequestParam("phoneNum") String phoneNum) {

        // 发送验证码到手机
        ResultEntity<String> sendMessageResultEntity = CrowdUtil.sendCodeByShortMessage(
                shortMessageProperties.getHost(),
                shortMessageProperties.getPath(),
                shortMessageProperties.getMethod(),
                phoneNum,
                shortMessageProperties.getAppCode(),
                shortMessageProperties.getSkin()
        );
        // 判断结果
        if (ResultEntity.SUCCESS.equals(sendMessageResultEntity.getResult())) {
            // 发送成功存入Redis
            // 获取验证码
            String code = sendMessageResultEntity.getData();
            // redis中存储的key
            String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
            // 储存到Redis
            ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, code, 15, TimeUnit.MINUTES);

            if (ResultEntity.SUCCESS.equals(sendMessageResultEntity.getResult())) {
                return ResultEntity.successWithoutData();
            } else {
                return saveCodeResultEntity;
            }

        } else {
            return sendMessageResultEntity;
        }
    }

}
