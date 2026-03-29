<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect("login");
        return;
    }
    if (request.getAttribute("dsKhachHang") == null) {
        response.sendRedirect("khachhang");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Khách hàng</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">
        <div class="hero-panel">
            <div class="hero-copy">
                <span class="eyebrow">Hồ sơ khách hàng</span>
                <h1>Quản lý thông tin khách hàng.</h1>
            </div>
        </div>

        <div class="split-layout">
            <section class="card">
                <div class="card-header">
                    <div>
                        <h2>Danh sách khách hàng</h2>
                    </div>
                    <span class="card-badge">Dữ liệu chính</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã khách hàng</th>
                            <th>Họ tên</th>
                            <th>Địa chỉ</th>
                            <th>CCCD</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        ${dsKhachHang}
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="card">
                <div class="card-header">
                    <div>
                        <h2 id="khachhang-form-title">Thêm khách hàng</h2>
                    </div>
                    <div class="card-header-actions">
                        <button class="btn btn-secondary btn-compact" type="button" onclick="lamMoiKhachHang()">Tạo mới</button>
                        <span class="card-badge">Biểu mẫu</span>
                    </div>
                </div>
                <form id="khachhang-form" action="khachhang" method="get">
                    <input id="khachhang-action" type="hidden" name="action" value="add">
                    <div class="form-grid">
                        <div class="field">
                            <label for="makh">Mã khách hàng <span class="auto-badge">Tự động</span></label>
                            <input id="makh" type="text" name="makh" required readonly>
                        </div>
                        <div class="field">
                            <label for="tenkh">Họ tên</label>
                            <input id="tenkh" type="text" name="tenkh" required>
                        </div>
                        <div class="field">
                            <label for="dc">Địa chỉ</label>
                            <input id="dc" type="text" name="dc" required>
                        </div>
                        <div class="field">
                            <label for="cccd">CCCD</label>
                            <input id="cccd" type="text" name="cccd" pattern="\d{12}" maxlength="12" required>
                        </div>
                    </div>
                    <div class="actions">
                        <button id="khachhang-submit" class="btn btn-primary" type="submit">Thêm khách hàng</button>
                        <button id="khachhang-cancel" class="btn btn-secondary is-hidden" type="button" onclick="lamMoiKhachHang()">Hủy chỉnh sửa</button>
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
    function genMa(prefix) {
        return prefix + Math.random().toString(36).slice(2, 8).toUpperCase();
    }

    function datCheDoKhachHang(dangSua) {
        document.getElementById("khachhang-form-title").textContent = dangSua ? "Cập nhật khách hàng" : "Thêm khách hàng";
        document.getElementById("khachhang-submit").textContent = dangSua ? "Lưu thay đổi" : "Thêm khách hàng";
        document.getElementById("khachhang-action").value = dangSua ? "update" : "add";
        document.getElementById("khachhang-cancel").classList.toggle("is-hidden", !dangSua);
        document.getElementById("makh").readOnly = true;
    }

    function lamMoiKhachHang() {
        document.getElementById("khachhang-form").reset();
        document.getElementById("makh").value = genMa("KH");
        document.getElementById("tenkh").value = "";
        document.getElementById("dc").value = "";
        document.getElementById("cccd").value = "";
        datCheDoKhachHang(false);
    }

    function chinhSuaKhachHang(makh, tenkh, dc, cccd) {
        document.getElementById("makh").value = makh;
        document.getElementById("tenkh").value = tenkh;
        document.getElementById("dc").value = dc;
        document.getElementById("cccd").value = cccd;
        datCheDoKhachHang(true);
    }

    // Auto-fill code on page load
    document.getElementById("makh").value = genMa("KH");
</script>
</body>
</html>
