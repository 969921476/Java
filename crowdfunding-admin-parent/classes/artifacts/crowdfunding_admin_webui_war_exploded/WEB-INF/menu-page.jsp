<%--
  Created by IntelliJ IDEA.
  User: cjn
  Date: 2020/3/16
  Time: 9:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<link rel="stylesheet" href="ztree/zTreeStyle.css">
<script type="text/javascript" src="ztree/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="crowd/my-menu.js"></script>
<script>
    $(function () {

        // 调用函数初始化树形结构
        generateTree();

        // 给添加子节点绑定店单击响应函数
        $("#treeDemo").on("click",".addBtn", function () {
            // 当前节点的i，作为新节点的pid保存
            window.pid = this.id;
            $("#menuAddModal").modal("show");
            return false;
        });

        // 给添加子节点的单击函数绑定子节点
        $("#menuSaveBtn").click(function () {
            // 收集数据
            var name = $.trim($("#menuAddModal [name=name]").val());
            var url =  $.trim($("#menuAddModal [name=url]").val());
            //单选按钮要定位到被选中的哪一个
            var icon =  $("#menuAddModal [name=icon]:checked").val();

            // 发送ajax请求
            $.ajax({
                "url": "menu/save.json",
                "type": "post",
                "data": {
                    "pid" : window.pid,
                    "name": name,
                    "url" : url,
                    "icon": icon
                },
                "dataType":"json",
                "success": function(response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功!");
                        generateTree();
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败!" + response.message);
                    }
                },
                "error": function(response) {
                    layer.msg(response.status + " " + response.statusText);
                },
            });
            $("#menuAddModal").modal("hide");

            $("#menuResetBtn").click();
        });


        $("#treeDemo").on("click",".removeBtn", function () {
            window.id = this.id;
            $("#menuConfirmModal").modal("show");

            // 获取zTreeObj对象
            var zTreeObj = $.fn.zTree.getZTreeObj("treeDemo");
            // 根据节点查询节点对象
            var key = "id";

            var value = window.id;

            // 用来搜索节点的属性值
            var currentNode = zTreeObj.getNodeByParam(key, value);

            // 回显表单数据
            $("#removeNodeSpan").html("【<i class='" + currentNode.icon + "'></i>"+currentNode.name +"】");

            return false;
        });

        $("#confirmBtn").click(function () {
            $.ajax({
                "url": "menu/delete.json",
                "type": "post",
                "data": {
                    "id" : window.id,
                },
                "dataType":"json",
                "success": function(response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功!");
                        generateTree();
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败!" + response.message);
                    }
                },
                "error": function(response) {
                    layer.msg(response.status + " " + response.statusText);
                },
            });

            $("#menuConfirmModal").modal("hide");
        });

        $("#treeDemo").on("click",".editBtn", function () {
            window.id = this.id;
            $("#menuEditModal").modal("show");
            // 获取zTreeObj对象
            var zTreeObj = $.fn.zTree.getZTreeObj("treeDemo");
            // 根据节点查询节点对象
            var key = "id";

            var value = window.id;

            // 用来搜索节点的属性值
            var currentNode = zTreeObj.getNodeByParam(key, value);

            // 回显表单数据
            $("#menuEditModal [name=name]").val(currentNode.name);
            $("#menuEditModal [name=url]").val(currentNode.url);
            // 回显radio可以这样理解 被选中的可以组成一个数组，再用设个数组设置会radio就可以选中对应的值
            $("#menuEditModal [name=icon]").val([currentNode.icon]);


            return false;
        });

        $("#menuEditBtn").click(function () {
            var name = $("#menuEditModal [name=name]").val();
            var url = $("#menuEditModal [name=url]").val();
            // 回显radio可以这样理解 被选中的可以组成一个数组，再用设个数组设置会radio就可以选中对应的值
            var icon = $("#menuEditModal [name=icon]:checked").val();

            $.ajax({
                "url": "menu/update.json",
                "type": "post",
                "data": {
                    "id" : window.id,
                    "name": name,
                    "url" : url,
                    "icon": icon
                },
                "dataType":"json",
                "success": function(response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功!");
                        generateTree();
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败!" + response.message);
                    }
                },
                "error": function(response) {
                    layer.msg(response.status + " " + response.statusText);
                },
            });

            $("#menuEditModal").modal("hide");

        });

    });
</script>
<body>
<%@include file="/WEB-INF/include-nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <%@include file="/WEB-INF/include-sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">

            <div class="panel panel-default">
                <div class="panel-heading"><i class="glyphicon glyphicon-th-list"></i> 权限菜单列表
                    <div style="float:right;cursor:pointer;" data-toggle="modal" data-target="#myModal"><i
                            class="glyphicon glyphicon-question-sign"></i></div>
                </div>
                <div class="panel-body">
                    <ul id="treeDemo" class="ztree"></ul>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/modal-menu-add.jsp"%>
<%@include file="/WEB-INF/modal-menu-edit.jsp"%>
<%@include file="/WEB-INF/modal-menu-confirm.jsp"%>
</body>
</html>
