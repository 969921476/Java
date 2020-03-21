// 生成树形结构的函数
function generateTree() {
    // 准备数据
    $.ajax({
        "url": "menu/get/whole/tree.json",
        "type": "post",
        "dataType":"json",
        "success":function (response) {
            var result = response.result;
            if (result == "SUCCESS") {
                // 创建JSON用于存储对zTree所做的设置
                var setting = {
                    "view": {
                        // 设置自定义图标
                        "addDiyDom": myAddDiyDom,
                        // 移入添加按钮
                        "addHoverDom":myAddHoverDom,
                        // 移出删除按钮
                        "removeHoverDom":myRemoveHoverDom,
                    },
                    // 点击不跳转 这是url为任意值
                    "data": {
                        "key": {
                            "url":"mao"
                        }
                    }
                };
                // 获取数据
                var zNodes = response.data;
                // 初始化树形结构
                $.fn.zTree.init($("#treeDemo"), setting, zNodes);
            }
            if (result == "FAILED") {
                layer.msg(response.message);
            }
        },
        "error":function (response) {

        }
    });
}


// 鼠标移入到节点范围是添加按钮组
function myAddHoverDom(treeId, treeNode) {

    // 为了移除按钮组需要精确的定位的span
    var btnGroup = treeNode.tId + "_btnGrp";
    // 找到附着的按钮组
    var anchorId = treeNode.tId + "_a";

    // 判断是否添加过
    if ($("#"+btnGroup).length > 0) {
        return ;
    }

    // 准备各个按钮的html
    var addBtn = "<a id='"+treeNode.id+"' class='addBtn btn btn-info dropdown-toggle btn-xs' style='margin-left:10px;padding-top:0px;' href='#' title='添加子节点'>&nbsp;&nbsp;<i class='fa fa-fw fa-plus rbg '></i></a>";
    var removeBtn = "<a id='"+treeNode.id+"' class='removeBtn btn btn-info dropdown-toggle btn-xs' style='margin-left:10px;padding-top:0px;' href='#' title='删除节点'>&nbsp;&nbsp;<i class='fa fa-fw fa-times rbg '></i></a>";
    var editBtn = "<a id='"+treeNode.id+"' class='editBtn btn btn-info dropdown-toggle btn-xs' style='margin-left:10px;padding-top:0px;' href='#' title='修改节点'>&nbsp;&nbsp;<i class='fa fa-fw fa-edit rbg '></i></a>";

    // 获取当前节点的级别数据
    var level = treeNode.level;

    // 声明变量存储拼装好的按钮代码
    var btnHTML = "";

    // 判断当前节点的级别
    if(level == 0) {
        // 级别为0时是根节点，只能添加子节点
        btnHTML = addBtn;
    }

    if(level == 1) {
        // 级别为1时是分支节点，可以添加子节点、修改
        btnHTML = addBtn + " " + editBtn;

        // 获取当前节点的子节点数量
        var length = treeNode.children.length;

        // 如果没有子节点，可以删除
        if(length == 0) {
            btnHTML = btnHTML + " " + removeBtn;
        }

    }

    if(level == 2) {
        // 级别为2时是叶子节点，可以修改、删除
        btnHTML = editBtn + " " + removeBtn;
    }


    // 执行附加元素
    $("#"+anchorId).after("<span id='"+ btnGroup +"'>" + btnHTML + "</span>");

}
// 鼠标离开节点范围是添加按钮组
function myRemoveHoverDom(treeId, treeNode) {

    // 找到附着的按钮组
    var btnGroup = treeNode.tId + "_btnGrp";

    // 移除对应的元素
    $("#"+btnGroup).remove();
}

// 修改默认图标
function myAddDiyDom(treeId, treeNode) {
    // treeId是整个树形结构附着的ul标签的ID

    // console.log("treeID" + treeId);
    //
    // // treeNode当前树形节点的数据
    // console.log(treeNode);

    // 根据treeNode的tid属性得到当前节点的序号
    // 根据id的生成规则找到span的id
    var spanId = treeNode.tId + "_ico"
    // 根据控制图标的span的id找到span标签 删除旧的class
    $("#"+spanId).removeClass().addClass(treeNode.icon);

}