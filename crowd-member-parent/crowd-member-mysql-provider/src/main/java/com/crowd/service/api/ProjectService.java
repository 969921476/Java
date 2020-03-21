package com.crowd.service.api;

import com.crowd.entity.vo.ProjectVO;

/**
 * @author cjn
 * @create 2020-03-20 21:52
 */
public interface ProjectService {
    void saveProject(ProjectVO projectVO, Integer memberId);
}
