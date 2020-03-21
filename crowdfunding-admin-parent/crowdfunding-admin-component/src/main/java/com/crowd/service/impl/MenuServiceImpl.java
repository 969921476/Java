package com.crowd.service.impl;

import com.crowd.entity.Menu;
import com.crowd.entity.MenuExample;
import com.crowd.mapper.MenuMapper;
import com.crowd.service.api.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cjn
 * @create 2020-03-16 8:42
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getAll() {
        return menuMapper.selectByExample(new MenuExample());
    }

    @Override
    public void saveMenu(Menu menu) {
        menuMapper.insert(menu);
    }

    @Override
    public void updateMenu(Menu menu) {
        // 保证pid字段不会置空
        menuMapper.updateByPrimaryKeySelective(menu);
    }

    @Override
    public void removeMenu(Menu menu) {
        menuMapper.deleteByPrimaryKey(menu.getId());
    }
}
