// 模态框中显示树形结构转配数据
function fillAuthTree() {
    // 发送Ajax请求获取数据
    var ajaxReturn = $.ajax({
        "url": "assign/get/all/auth.json",
        "type": "post",
        "dataType": "json",
        "async" : false,
    });

    if (ajaxReturn.status != 200) {
        layer.msg("请求出错！响应状态码是：" + ajaxReturn.status + "说明是：" + ajaxReturn.statusText);
        return ;
    }

    // 获取数据
    var authList = ajaxReturn.responseJSON.data;

    // 准备对zTree进行设置Json
    var setting = {
        "data": {
            "simpleData":{
                // 开启简单json功能
                "enable": true,
                "pIdKey": "categoryId",
            },
            "key":{
                "name":"title"
            },

        },
        "check": {
            "enable": true,
        }
    };
    // 生成树形结构
    $.fn.zTree.init($("#authTreeDemo"), setting, authList);

    // 获取zTreeObj对象吧节点展开
    var zTreeObj =  $.fn.zTree.getZTreeObj("authTreeDemo");
    zTreeObj.expandAll(true)

    // 查询已分配的Auth的id组成的数组
    ajaxReturn = $.ajax({
        "url":"assign/get/assigned/auth/id/by/role/id.json",
        "type":"post",
        "data": {
            "roleId": window.roleId,
        },
        "dataType":"json",
        "async":false,
    });

    if (ajaxReturn.status != 200) {
        layer.msg("请求出错！响应状态码是：" + ajaxReturn.status + "说明是：" + ajaxReturn.statusText);
        return ;
    }
    // 获取需选择的数据
    var authIdList = ajaxReturn.responseJSON.data;
    // 根据数组把对应的节点勾选上
    for (var i = 0; i <authIdList.length; i ++) {
        var  authId = authIdList[i];

        // 根据Id去查询数据结构的节点
        var treeNode = zTreeObj.getNodeByParam("id", authId);
        // 设置为勾选
        // 节点勾选
        var checked = true;
        // 表示父子不“联动”
        var checkTypeFlag = false;
        zTreeObj.checkNode(treeNode, checked, checkTypeFlag);
    }
}


// 声明专门的函数显示确认模态框
function showConfirmModel(roleArray) {
    $("#confirmModel").modal("show");

    // 清除旧的数据
    $("#roleNameDiv").empty();

    window.roleIdArray = [];

    // 遍历roleArray
    for (var i = 0; i < roleArray.length; i++) {
        var role = roleArray[i];
        var roleName = role.roleName;
        $("#roleNameDiv").append(roleName + "<br/>");
        var roleId = role.roleId;

        window.roleIdArray.push(roleId);
    }
}


// 生成页面效果
function generatePage() {
    // 获取分页数据
    var pageInfo = getPageInfoRemote();

    // 填充表格
    fillTableBody(pageInfo)
}
// 远程访问服务器端程序获取pageInfo
function getPageInfoRemote() {
    // 调用$.ajax()函数发送请求并接受$.ajax()函数的返回值
    var ajaxResult = $.ajax({
        "url": "role/get/page/info.json",
        "type":"post",
        "data": {
            "pageNum": window.pageNum,
            "pageSize": window.pageSize,
            "keyword": window.keyword
        },
        "async":false,
        "dataType":"json"
    });

    console.log(ajaxResult);

    // 判断当前响应状态码是否为200
    var statusCode = ajaxResult.status;

    // 如果当前响应状态码不是200，说明发生了错误或其他意外情况，显示提示消息，让当前函数停止执行
    if(statusCode != 200) {
        layer.msg("失败！响应状态码="+statusCode+" 说明信息="+ajaxResult.statusText);
        return null;
    }

    // 如果响应状态码是200，说明请求处理成功，获取pageInfo
    var resultEntity = ajaxResult.responseJSON;

    // 从resultEntity中获取result属性
    var result = resultEntity.result;

    // 判断result是否成功
    if(result == "FAILED") {
        layer.msg(resultEntity.message);
        return null;
    }

    // 确认result为成功后获取pageInfo
    var pageInfo = resultEntity.data;

    // 返回pageInfo
    return pageInfo;
}

// 填充表格
function fillTableBody(pageInfo) {

    // 清除tbody中的旧的内容
    $("#rolePageBody").empty();

    // 这里清空是为了让没有搜索结果时不显示页码导航条
    $("#Pagination").empty();

    // 判断pageInfo是否有效
    if (pageInfo == null || pageInfo == undefined || pageInfo.list == null || pageInfo.list.length == 0) {
        $("#rolePageBody").append("<tr><td colspan='4' align='center'>没有查询到数据！！</td></tr>")
        
        return ;
    }
    
    // 填充
    for (var i = 0; i < pageInfo.list.length; i ++) {
        var role = pageInfo.list[i];

        var roleId = role.id;

        var roleName = role.name;

        var numberTd = "<td>" + (i + 1) + "</td>";
        var checkboxTd = "<td> <input class='itemBox' id='"+roleId+"' type='checkbox'> </td>";
        var roleNameTd = "<td>" + roleName + "</td>";
        var checkBtn = "<button id='"+roleId+"' type='button' class='btn btn-success btn-xs checkBtn'><i class=' glyphicon glyphicon-check'></i></button>";

        // 通过button标签的id属性（别的属性其实也可以）把roleId值传递到button按钮的单击响应函数中，在单击响应函数中使用this.id
        var pencilBtn = "<button id='"+roleId+"' type='button' class='btn btn-primary btn-xs pencilBtn'><i class=' glyphicon glyphicon-pencil'></i></button>";

        // 通过button标签的id属性（别的属性其实也可以）把roleId值传递到button按钮的单击响应函数中，在单击响应函数中使用this.id
        var removeBtn = "<button id='"+roleId+"' type='button' class='btn btn-danger btn-xs removeBtn'><i class=' glyphicon glyphicon-remove'></i></button>";

        var buttonTd = "<td>"+checkBtn+" "+pencilBtn+" "+removeBtn+"</td>";

        var tr = "<tr>"+numberTd+checkboxTd+roleNameTd+buttonTd+"</tr>";

        $("#rolePageBody").append(tr);

        generateNavigator(pageInfo);
    }
    
}

// 生成分页页码导航条
function generateNavigator(pageInfo) {
    // 获取总记录数
    var totalRecord = pageInfo.total;

    // 声明相关属性
    var properties = {
        num_edge_entries: 3,                                 // 边缘页数
        num_display_entries: 5,                              // 主体页数
        callback: pageInationCallBack,                       // 用户指定翻页时指定的代码
        items_per_page: pageInfo.pageSize,                   // 每页要显示数据的数量
        current_page: pageInfo.pageNum - 1,                  // pagination使用PageIndex管理代码 从0开始
        prev_text: "上一页",                                 // 上一页按钮显示的文字
        next_text: "下一页",                                 // 下一页按钮显示的文字
    };

    // 调用函数
    $("#Pagination").pagination(totalRecord, properties);
}

// 翻页的回调函数
function pageInationCallBack(pageIndex, jQuery) {
    // 修改window的pageNum属性
    window.pageNum = pageIndex + 1;

    generatePage();

    // 取消超链接的默认行为
    return false;
}