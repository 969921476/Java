package com.crowd.service.impl;

import com.crowd.entity.po.MemberConfirmInfoPO;
import com.crowd.entity.po.MemberLaunchInfoPO;
import com.crowd.entity.po.ProjectPO;
import com.crowd.entity.po.ReturnPO;
import com.crowd.entity.vo.MemberConfirmInfoVO;
import com.crowd.entity.vo.MemberLauchInfoVO;
import com.crowd.entity.vo.ProjectVO;
import com.crowd.entity.vo.ReturnVO;
import com.crowd.mapper.*;
import com.crowd.service.api.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cjn
 * @create 2020-03-20 21:52
 */
@Transactional(readOnly = true)
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ReturnPOMapper returnPOMapper;

    @Autowired
    private ProjectPOMapper projectPOMapper;

    @Autowired
    private ProjectItemPicPOMapper projectItemPicPOMapper;

    @Autowired
    private MemberLaunchInfoPOMapper memberLaunchInfoPOMapper;

    @Autowired
    private MemberConfirmInfoPOMapper memberConfirmInfoPOMapper;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void saveProject(ProjectVO projectVO, Integer memberId) {
        // 保存 ProjectPo
        ProjectPO projectPO = new ProjectPO();

        BeanUtils.copyProperties(projectVO, projectPO);
        projectPO.setMemberid(memberId);

        String createDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        projectPO.setCreatedate(createDate);

        projectPO.setStatus(0);

        // 保存ProjectVO 并且需要获得自增主键
        projectPOMapper.insertSelective(projectPO);

        // 从PO获取组件
        Integer projectId = projectPO.getId();

        // 保存项目分类的关联信息
        List<Integer> typeIdList = projectVO.getTypeIdList();
        projectPOMapper.insertRelationship(typeIdList, projectId);

        // 标签信息
        List<Integer> tagIdList = projectVO.getTagIdList();
        projectPOMapper.insertTagRelationship(tagIdList, projectId);

        // 项目图片信息
        List<String> detailPicturePathList = projectVO.getDetailPicturePathList();
        projectItemPicPOMapper.insertPathList(projectId, detailPicturePathList);
        // 发起人信息
        MemberLauchInfoVO memberLauchInfoVO = projectVO.getMemberLauchInfoVO();
        MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
        BeanUtils.copyProperties(memberLauchInfoVO, memberLaunchInfoPO);
        memberLaunchInfoPO.setMemberid(memberId);
        memberLaunchInfoPOMapper.insert(memberLaunchInfoPO);
        // 回报信息
        List<ReturnVO> returnVOList = projectVO.getReturnVOList();

        List<ReturnPO> returnPOList = new ArrayList<>();

        for (ReturnVO returnVO : returnVOList) {
            ReturnPO returnPO = new ReturnPO();
            BeanUtils.copyProperties(returnVO, returnPO);
            returnPOList.add(returnPO);
        }


        returnPOMapper.insertReturnPOBatch(returnPOList, projectId);


        // 确认信息
        MemberConfirmInfoVO memberConfirmInfoVO = projectVO.getMemberConfirmInfoVO();
        MemberConfirmInfoPO memberConfirmInfoPO = new MemberConfirmInfoPO();
        BeanUtils.copyProperties(memberConfirmInfoVO, memberConfirmInfoPO);
        memberConfirmInfoPO.setMemberid(memberId);
        memberConfirmInfoPOMapper.insert(memberConfirmInfoPO);

    }
}
