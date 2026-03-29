<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect("login");
        return;
    }
    if (request.getAttribute("dsSP") == null) {
        response.sendRedirect("sanpham");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sản phẩm</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">
        <div class="hero-panel">
            <div class="hero-copy">
                <span class="eyebrow">Điều phối sản phẩm</span>
                <h1>Quản lý danh mục sản phẩm và tồn kho tập trung.</h1>
            </div>
        </div>

        <div class="split-layout">
            <section class="card">
                <div class="card-header">
                    <div>
                        <h2>Danh sách sản phẩm</h2>
                    </div>
                    <span class="card-badge">Bảng dữ liệu</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã sản phẩm</th>
                            <th>Tên sản phẩm</th>
                            <th>Giá</th>
                            <th>Nhà cung cấp</th>
                            <th>Tồn kho</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        ${dsSP}
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="card">
                <div class="card-header">
                    <div>
                        <h2 id="sanpham-form-title">Thêm sản phẩm</h2>
                    </div>
                    <div class="card-header-actions">
                        <button class="btn btn-secondary btn-compact" type="button" onclick="lamMoiSanPham()">Tạo mới</button>
                        <span class="card-badge">Biểu mẫu</span>
                    </div>
                </div>
                <form id="sanpham-form" action="sanpham" method="get">
                    <input id="sanpham-action" type="hidden" name="action" value="add">
                    <div class="form-grid">
                        <div class="field">
                            <label for="masp">Mã sản phẩm</label>
                            <input id="masp" type="text" name="masp" required>
                        </div>
                        <div class="field">
                            <label for="tensp">Tên sản phẩm</label>
                            <input id="tensp" type="text" name="tensp" required>
                        </div>
                        <div class="field">
                            <label for="gia">Giá</label>
                            <input id="gia" type="text" name="gia" inputmode="numeric" pattern="\d*" required>
                        </div>
                        <div class="field">
                            <label for="nhacungcap">Mã nhà cung cấp</label>
                            <input id="nhacungcap" type="text" name="nhacungcap" required>
                        </div>
                        <div class="field">
                            <label for="slton">Số lượng tồn</label>
                            <input id="slton" type="text" name="slton" inputmode="numeric" pattern="\d*" required>
                        </div>
                    </div>
                    <div class="actions">
                        <button id="sanpham-submit" class="btn btn-primary" type="submit">Thêm sản phẩm</button>
                        <button id="sanpham-cancel" class="btn btn-secondary is-hidden" type="button" onclick="lamMoiSanPham()">Hủy chỉnh sửa</button>
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
    function datCheDoSanPham(dangSua) {
        document.getElementById("sanpham-form-title").textContent = dangSua ? "Cập nhật sản phẩm" : "Thêm sản phẩm";
        document.getElementById("sanpham-submit").textContent = dangSua ? "Lưu thay đổi" : "Thêm sản phẩm";
        document.getElementById("sanpham-action").value = dangSua ? "update" : "add";
        document.getElementById("sanpham-cancel").classList.toggle("is-hidden", !dangSua);
        document.getElementById("masp").readOnly = dangSua;
    }

    function lamMoiSanPham() {
        document.getElementById("sanpham-form").reset();
        document.getElementById("masp").value = "";
        document.getElementById("tensp").value = "";
        document.getElementById("gia").value = "";
        document.getElementById("nhacungcap").value = "";
        document.getElementById("slton").value = "";
        datCheDoSanPham(false);
    }

    function chinhSuaSanPham(masp, tensp, gia, nhacungcap, slton) {
        document.getElementById("masp").value = masp;
        document.getElementById("tensp").value = tensp;
        document.getElementById("gia").value = gia;
        document.getElementById("nhacungcap").value = nhacungcap;
        document.getElementById("slton").value = slton;
        datCheDoSanPham(true);
    }
</script>
</body>
</html>
