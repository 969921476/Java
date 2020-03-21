package com.crowd.controller;

import com.crowd.api.MySQLRemoteService;
import com.crowd.config.OSSProperties;
import com.crowd.entity.vo.MemberConfirmInfoVO;
import com.crowd.entity.vo.MemberLoginVO;
import com.crowd.entity.vo.ProjectVO;
import com.crowd.entity.vo.ReturnVO;
import com.crowd.util.CrowdConstant;
import com.crowd.util.CrowdUtil;
import com.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.security.krb5.internal.crypto.Crc32CksumType;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cjn
 * @create 2020-03-21 11:08
 */
@Controller
public class ProjectConsumerController {
    @Autowired
    private OSSProperties ossProperties;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/create/confirm")
    public String saveConfirm(ModelMap modelMap, HttpSession session, MemberConfirmInfoVO memberConfirmInfoVO) {
        // 读取之前临时存储的对象
        ProjectVO projectVO = (ProjectVO) session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);

        // 为空
        if (projectVO == null) {
            throw new RuntimeException(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
        }

        // 设置属性进去
        projectVO.setMemberConfirmInfoVO(memberConfirmInfoVO);

        // 读取当前登录的用户
        MemberLoginVO memberLoginVO = (MemberLoginVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);

        System.out.println(memberLoginVO);

        Integer memberId = memberLoginVO.getId();
        // 去MySql保存
        ResultEntity<String> saveResultEntity = mySQLRemoteService.saveProjectVORemote(projectVO, memberId);

        String result = saveResultEntity.getResult();

        //  判断保存是否成功
        if (ResultEntity.FAILED.equals(result)) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,saveResultEntity.getMessage());
            return "project-confirm";
        }

        // 移除临时对象
        session.removeAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);


        return "redirect:http://localhost:8080/project/create/success";
    }

    @ResponseBody
    @RequestMapping("/create/save/return.json")
    public ResultEntity<String> saveReturn(ReturnVO returnVO, HttpSession session) {

        try {
            // 获取之前存入的信息
            ProjectVO projectVO = (ProjectVO) session.getAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT);

            if (projectVO == null) {
                return ResultEntity.failed(CrowdConstant.MESSAGE_TEMPLE_PROJECT_MISSING);
            }

            // 获取回报集合对象
            List<ReturnVO> returnVOList = projectVO.getReturnVOList();

            // 判断集合是否有效
            if (returnVOList == null || returnVOList.size() == 0) {
                returnVOList = new ArrayList<>();
                projectVO.setReturnVOList(returnVOList);
            }

            // 给returnVOList存入returnVo
            returnVOList.add(returnVO);

            // 放回Session
            session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);

            // 成功后返回
            return ResultEntity.successWithoutData();

        } catch (Exception e) {
            e.printStackTrace();
            return  ResultEntity.failed(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/create/upload/return/picture.json")
    public ResultEntity<String> uploadReturnPicture(
            // 接收上传的图片
            @RequestParam("returnPicture") MultipartFile returnPicture
    ) throws IOException {
        // 执行上传
        ResultEntity<String> uploadFileToOss = CrowdUtil.uploadFileToOss(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                returnPicture.getInputStream(),
                ossProperties.getBucketName(),
                ossProperties.getBucketDomain(),
                returnPicture.getOriginalFilename()
        );
        // 返回信息
        return uploadFileToOss;
    }

    @RequestMapping("/create/project/information")
    public String saveProjectBasicInfo(
            // 除了图片的数据
            ProjectVO projectVO, MultipartFile headerPicture,
            List<MultipartFile> detailPictureList,
            HttpSession session, ModelMap modelMap) throws IOException {


        // 完成图片上传
        boolean multipartFileEmpty = headerPicture.isEmpty();

        // 没有上传图片
        if (multipartFileEmpty) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_FILE_ENPTY);
            return "project-launch";
        }
        // 上传图片
        ResultEntity<String> uploadFileToOss = CrowdUtil.uploadFileToOss(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret(),
                headerPicture.getInputStream(),
                ossProperties.getBucketName(),
                ossProperties.getBucketDomain(),
                headerPicture.getOriginalFilename()
        );
        String result = uploadFileToOss.getResult();

        // 判断是否上传成功
        if (ResultEntity.SUCCESS.equals(result)) {
            // 从返回的数据中获取访问地址
            String Path = uploadFileToOss.getData();

            projectVO.setHeaderPicturePath(Path);
        } else {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_FILE_FILED);
            return "project-launch";
        }


        // 存放详情图片的集合
        List<String> list = new ArrayList<>();

        // 上传图片集
        for (MultipartFile multipart : detailPictureList) {
            if (multipart.isEmpty()) {
                modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_FILE_ENPTY);
                return "project-launch";
            }
            ResultEntity<String> resultEntity = CrowdUtil.uploadFileToOss(ossProperties.getEndPoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret(),
                    multipart.getInputStream(),
                    ossProperties.getBucketName(),
                    ossProperties.getBucketDomain(),
                    multipart.getOriginalFilename());

            String results = resultEntity.getResult();

            if (ResultEntity.SUCCESS.equals(results)) {
                // 收集图片途径
                String data = resultEntity.getData();
                list.add(data);

            } else {
                modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_FILE_FILED);
                return "project-launch";
            }
        }
        // 图片路径集合存入实体类中
        projectVO.setDetailPicturePathList(list);

        session.setAttribute(CrowdConstant.ATTR_NAME_TEMPLE_PROJECT, projectVO);

        return "redirect:http://localhost:8080/project/return/info/page";
    }
}
