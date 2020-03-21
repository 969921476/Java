<%--
  Created by IntelliJ IDEA.
  User: cjn
  Date: 2020/3/14
  Time: 14:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp"%>

<link rel="stylesheet" href="css/pagination.css">
<link rel="stylesheet" href="ztree/zTreeStyle.css">
<script type="text/javascript" src="ztree/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="jquery/jquery.pagination.js"></script>

<script type="text/javascript" src="crowd/my-role.js" charset="UTF-8"></script>

<script type="text/javascript">

    $(function () {

        // 为分页操作准备初始化数据
        window.pageNum = 1;
        window.pageSize = 10;
        window.keyword = "";

        // 调用分页函数
        generatePage();

        $("#searchBtn").click(function () {
            // 获取关键词
            window.keyword = $("#keywordInput").val();

            // 刷新页面
            generatePage();
        });

        // 打开模态框
        $("#showAddModelBtn").click(function () {
            $("#addModel").modal();
        });

        // 给新增模态框添加响应函数
        $("#saveRoleBtn").click(function () {
            // 获取输入的数据
            var roleName = $.trim($("#addModel [name=roleName]").val());

            // 发送ajax
            $.ajax({
                "url": "role/save.json",
                "type": "post",
                "data": {
                    "name": roleName
                },
                "dataType":"json",
                "success":function (response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功");
                        // 重新加载分页
                        window.pageNum = 999999;
                        generatePage();
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败！", response.message);
                    }
                },
                "error":function (response) {
                    layer.msg(response.status + "" + response.statusText)
                }
            });

            // 关闭模态框
            $("#addModel").modal("hide");

            // 清理模态框
            $("#addModel [name=roleName]").val("");

        });

        // 给页面上的铅笔绑定单击响应函数
        $("#rolePageBody").on("click", ".pencilBtn", function () {
            // 打开模态框
            $("#editModel").modal("show");

            // 获取当前行中的角色名称
            var roleName = $(this).parent().prev().text();

            // 获取当前角色的id
            window.roleId = this.id;

            $("#editModel [name=roleName]").val(roleName);
        });

        // 更新绑定按钮
        $("#updateRoleBtn").click(function () {

            // 获取文本框中新的角色名称
            var  roleName = $("#editModel [name=roleName]").val();
            console.log(roleName);

            $.ajax({
               "url":"role/update.json",
                "data": {
                   "id": window.roleId,
                   "name": roleName
                },
                "dataType": "json",
                "success": function (response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功");
                        // 重新加载分页
                        generatePage();
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败！", response.message);
                    }
                },
                "error": function (response) {
                    layer.msg(response.status + "" + response.statusText)
                }
            });

            // 关闭模态框
            $("#editModel").modal("hide");
        });

        // 确认模态框的删除
        $("#removeRoleBtn").click(function () {
            // 获取角色Id的数组转换成JSON
            var requestBody = JSON.stringify(window.roleIdArray);
            $.ajax({
                "url": "role/remove/by/role/id/array.json",
                "type": "post",
                "data":requestBody,
                "contentType":"application/json;charset=UTF-8",
                "dataType":"json",
                "success": function (response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功");
                        // 重新加载分页
                        generatePage();
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败！", response.message);
                    }
                },
                "error": function (response) {
                    layer.msg(response.status + "" + response.statusText)
                }
            });

            // 关闭模态框
            $("#confirmModel").modal("hide");
            $("#summaryBox").prop("checked", false);

        });

        // 单条删除帮点点击事件
        $("#rolePageBody").on("click", ".removeBtn", function () {

            // 获取当前按钮的角色名称
            var roleName = $(this).parent().prev().text();

            // 创建 role对象数组
            var roleArray = [{
               roleId:this.id,
               roleName: roleName
            }];

            // 打开模态框
            showConfirmModel(roleArray);
        });

        // 给总的checkBox 绑定点击函数
        $("#summaryBox").click(function () {
            // 获取当前多选框自身的转态
            var currentStatus = this.checked;

            // 用当前多选框设置其他多选框
            $(".itemBox").prop("checked", currentStatus);
        });

        // 全选和全部选的反向操作
        $("#rolePageBody").on("click", ".itemBox", function () {

            // 获取当前已经选中的 .itemBox的数量
            var checkedBoxCount = $(".itemBox:checked").length;

            // 获取全部 itemBox的数量
            var totalBoxCount = $(".itemBox").length;

            // 使用比较结果来设置check的选择
            $("#summaryBox").prop("checked", checkedBoxCount == totalBoxCount);
        });

        // 给全选的删除按钮绑定单击函数
        $("#batchRemoveVtn").click(function () {

            //创建数组对象
            var  roleArray = [];

            // 遍历当前选择的多选框
            $(".itemBox:checked").each(function () {
                // 使用this引用遍历的多选框
                var roleId = this.id;

                // 获取角色名称
                var roleName = $(this).parent().next().text();

                roleArray.push({
                   "roleId": roleId,
                    "roleName":roleName
                });
            });

            if (roleArray.length == 0) {
                layer.msg("至少选择一个删除");
                return ;
            }

            showConfirmModel(roleArray);


        });

        // 给分配权限按钮绑定函数
        $("#rolePageBody").on("click", ".checkBtn", function () {
            $("#assignModal").modal("show");
            window.roleId = this.id,
            // 在模态框装配树形结构
            fillAuthTree();
        });

        $("#assignBtn").click(function () {

            // ①收集树形结构的各个节点中被勾选的节点
            // [1]声明一个专门的数组存放id
            var authIdArray = [];

            // [2]获取zTreeObj对象
            var zTreeObj = $.fn.zTree.getZTreeObj("authTreeDemo");

            // [3]获取全部被勾选的节点
            var checkedNodes = zTreeObj.getCheckedNodes();

            // [4]遍历checkedNodes
            for(var i = 0; i < checkedNodes.length; i++) {
                var checkedNode = checkedNodes[i];

                var authId = checkedNode.id;

                authIdArray.push(authId);
            }

            // ②发送请求执行分配
            var requestBody = {
                "authIdArray":authIdArray,

                // 为了服务器端handler方法能够统一使用List<Integer>方式接收数据，roleId也存入数组
                "roleId":[window.roleId]
            };

            requestBody = JSON.stringify(requestBody);

            $.ajax({
                "url":"assign/do/role/assign/auth.json",
                "type":"post",
                "data":requestBody,
                "contentType":"application/json;charset=UTF-8",
                "dataType":"json",
                "success":function(response){
                    var result = response.result;
                    if(result == "SUCCESS") {
                        layer.msg("操作成功！");
                    }
                    if(result == "FAILED") {
                        layer.msg("操作失败！"+response.message);
                    }
                },
                "error":function(response) {
                    layer.msg(response.status+" "+response.statusText);
                }
            });

            $("#assignModal").modal("hide");

        });

    });
</script>

<body>
<%@include file="/WEB-INF/include-nav.jsp"%>
<div class="container-fluid">
    <div class="row">
        <%@include file="/WEB-INF/include-sidebar.jsp"%>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><i class="glyphicon glyphicon-th"></i> 数据列表</h3>
                </div>
                <div class="panel-body">
                    <form class="form-inline" role="form" style="float:left;">
                        <div class="form-group has-feedback">
                            <div class="input-group">
                                <div class="input-group-addon">查询条件</div>
                                <input id="keywordInput" class="form-control has-success" type="text" placeholder="请输入查询条件">
                            </div>
                        </div>
                        <button type="button"  id="searchBtn" class="btn btn-warning"><i class="glyphicon glyphicon-search"></i> 查询</button>
                    </form>
                    <button id="batchRemoveVtn" type="button" class="btn btn-danger" style="float:right;margin-left:10px;"><i class=" glyphicon glyphicon-remove"></i> 删除</button>
                    <button id="showAddModelBtn" type="button" class="btn btn-primary" style="float:right;"><i class="glyphicon glyphicon-plus"></i> 新增</button>
                    <br>
                    <hr style="clear:both;">
                    <div class="table-responsive">
                        <table class="table  table-bordered">
                            <thead>
                            <tr>
                                <th width="30">#</th>
                                <th width="30"><input id="summaryBox" type="checkbox"></th>
                                <th>名称</th>
                                <th width="100">操作</th>
                            </tr>
                            </thead>
                            <tbody id="rolePageBody">
                            </tbody>
                            <tfoot>
                            <tr>
                                <td colspan="6" align="center">
                                    <div id="Pagination" class="pagination">
                                        <!-- 这里显示分页 -->

                                    </div>
                                </td>
                            </tr>

                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/modal-role-add.jsp"%>
<%@include file="/WEB-INF/modal-role-edit.jsp"%>
<%@include file="/WEB-INF/modal-role-confirm.jsp"%>
<%@include file="/WEB-INF/modal-role-assign-auth.jsp"%>
</body>
</html>
