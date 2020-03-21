package com.crowd.service.api;

import com.crowd.entity.Menu;

import java.util.List;

/**
 * @author cjn
 * @create 2020-03-16 8:41
 */
public interface MenuService {
    List<Menu> getAll();

    void saveMenu(Menu menu);

    void updateMenu(Menu menu);

    void removeMenu(Menu menu);
}
