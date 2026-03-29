<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect("login");
        return;
    }
    if (request.getAttribute("dsNCC") == null) {
        response.sendRedirect("nhacungcap");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nhà cung cấp</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">
        <div class="hero-panel">
            <div class="hero-copy">
                <span class="eyebrow">Danh mục nhà cung cấp</span>
                <h1>Tập trung quản lý thông tin nhà cung cấp.</h1>
            </div>
        </div>

        <div class="split-layout">
            <section class="card">
                <div class="card-header">
                    <div>
                        <h2>Danh sách nhà cung cấp</h2>
                    </div>
                    <span class="card-badge">Danh mục</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã nhà cung cấp</th>
                            <th>Tên nhà cung cấp</th>
                            <th>Địa chỉ</th>
                            <th>Số điện thoại</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        ${dsNCC}
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="card">
                <div class="card-header">
                    <div>
                        <h2 id="nhacungcap-form-title">Thêm nhà cung cấp</h2>
                    </div>
                    <div class="card-header-actions">
                        <button class="btn btn-secondary btn-compact" type="button" onclick="lamMoiNhaCungCap()">Tạo mới</button>
                        <span class="card-badge">Biểu mẫu</span>
                    </div>
                </div>
                <form id="nhacungcap-form" action="nhacungcap" method="get">
                    <input id="nhacungcap-action" type="hidden" name="action" value="add">
                    <div class="form-grid">
                        <div class="field">
                            <label for="mancc">Mã nhà cung cấp</label>
                            <input id="mancc" type="text" name="mancc" required>
                        </div>
                        <div class="field">
                            <label for="tenncc">Tên nhà cung cấp</label>
                            <input id="tenncc" type="text" name="tenncc" required>
                        </div>
                        <div class="field">
                            <label for="dcncc">Địa chỉ</label>
                            <input id="dcncc" type="text" name="dcncc" required>
                        </div>
                        <div class="field">
                            <label for="sdtncc">Số điện thoại</label>
                            <input id="sdtncc" type="text" name="sdtncc" required>
                        </div>
                    </div>
                    <div class="actions">
                        <button id="nhacungcap-submit" class="btn btn-primary" type="submit">Thêm nhà cung cấp</button>
                        <button id="nhacungcap-cancel" class="btn btn-secondary is-hidden" type="button" onclick="lamMoiNhaCungCap()">Hủy chỉnh sửa</button>
                    </div>
                </form>
                <% if (request.getAttribute("message") != null && !String.valueOf(request.getAttribute("message")).isBlank()) { %>
                    <div class="message warn">${message}</div>
                <% } %>
            </section>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/jspf/delete-confirm.jspf" %>

<script>
    function datCheDoNhaCungCap(dangSua) {
        document.getElementById("nhacungcap-form-title").textContent = dangSua ? "Cập nhật nhà cung cấp" : "Thêm nhà cung cấp";
        document.getElementById("nhacungcap-submit").textContent = dangSua ? "Lưu thay đổi" : "Thêm nhà cung cấp";
        document.getElementById("nhacungcap-action").value = dangSua ? "update" : "add";
        document.getElementById("nhacungcap-cancel").classList.toggle("is-hidden", !dangSua);
        document.getElementById("mancc").readOnly = dangSua;
    }

    function lamMoiNhaCungCap() {
        document.getElementById("nhacungcap-form").reset();
        document.getElementById("mancc").value = "";
        document.getElementById("tenncc").value = "";
        document.getElementById("dcncc").value = "";
        document.getElementById("sdtncc").value = "";
        datCheDoNhaCungCap(false);
    }

    function chinhSuaNhaCungCap(mancc, tenncc, dcncc, sdtncc) {
        document.getElementById("mancc").value = mancc;
        document.getElementById("tenncc").value = tenncc;
        document.getElementById("dcncc").value = dcncc;
        document.getElementById("sdtncc").value = sdtncc;
        datCheDoNhaCungCap(true);
    }
</script>
</body>
</html>
