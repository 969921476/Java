package com.crowd.mvc.controller;

import com.crowd.entity.Menu;
import com.crowd.service.api.MenuService;
import com.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cjn
 * @create 2020-03-16 8:42
 */
@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;
    @RequestMapping("/menu/update.json")
    public ResultEntity<String> updateMenu(Menu menu) {
        menuService.updateMenu(menu);
        return ResultEntity.successWithoutData();
    }
    @RequestMapping("/menu/delete.json")
    public ResultEntity<String> deleteMenu(Menu menu) {
        menuService.removeMenu(menu);
        return ResultEntity.successWithoutData();
    }

    @RequestMapping("/menu/save.json")
    public ResultEntity<String> saveMenu(Menu menu) {
        menuService.saveMenu(menu);

        return ResultEntity.successWithoutData();
    }



    @RequestMapping("/menu/get/whole/tree.json")
    public ResultEntity<Menu> getWholeTreeNew() {
        // 查询全部的Menu对象
        List<Menu> menuList = menuService.getAll();

        // 声明一个变量用来存储找到的根节点
        Menu root = null;

        // 创建一个Map存储id和menu便于查找父节点
        Map<Integer, Menu> menuMap = new HashMap<>();
        // 遍历List 填充map
        for (Menu menu : menuList) {
            Integer id = menu.getId();
            menuMap.put(id, menu);
        }

        // 再次遍历list查找根节点，组装父子节点
        for (Menu menu : menuList) {
            Integer pid = menu.getPid();

            if (pid == null) {
                root = menu;
                continue;
            }

            // 有父节点
            Menu father = menuMap.get(pid);

            // 存入父节点的menu集合
            father.getChildren().add(menu);
        }

        // 返回整个树
        return ResultEntity.successWithData(root);
    }
    public ResultEntity<Menu> getWholeTreeOld() {
        // 查询全部的Menu对象
        List<Menu> menuList = menuService.getAll();

        // 声明一个变量用来存储找到的根节点
        Menu root = null;

        // 遍历menuList
        for (Menu menu : menuList) {
            // 获取当前menu对象的pid属性值
            Integer pid = menu.getPid();

            // 检查Pid是否为 null
            if (pid == null) {
                // 当前正在遍历的menu对象赋值给root
                root = menu;

                // 本次循环停止， 继续执行下一个循环
                continue;
            }

            // 不为null 当前节点有父节点
            for (Menu maybeFather : menuList) {
                // 获取id
                Integer id = maybeFather.getId();

                // 子节点的pid和父节点的id进行比较
                if (Objects.equals(pid, id)) {
                    // 将子节点存入父节点的children节点
                    maybeFather.getChildren().add(menu);
                    // 找到即可停止循环
                    break;
                }
            }
        }
        // 将组装好的树形结构返回
        return ResultEntity.successWithData(root);
    }
}
