package com.crowd.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author cjn
 * @create 2020-03-21 17:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalTypeVO {
    private Integer id;
    private String name;
    private String remark;

    private List<PortalProjectVO> portalProjectVOList;
}
