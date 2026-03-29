<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect("login");
        return;
    }
    if (request.getAttribute("dsKhuyenMai") == null) {
        response.sendRedirect("khuyenmai");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Khuyến mãi</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">
        <div class="hero-panel">
            <div class="hero-copy">
                <span class="eyebrow">Quản lý ưu đãi</span>
                <h1>Chương trình khuyến mãi.</h1>
            </div>
        </div>

        <div class="split-layout">
            <section class="card">
                <div class="card-header">
                    <div><h2>Danh sách khuyến mãi</h2></div>
                    <span class="card-badge">Ưu đãi</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã KM</th>
                            <th>Ngày áp dụng</th>
                            <th>Ngày kết thúc</th>
                            <th>Giảm (%)</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>${dsKhuyenMai}</tbody>
                    </table>
                </div>
            </section>

            <div class="stack">
                <section class="card">
                    <div class="card-header">
                        <div><h2 id="km-form-title">Thêm khuyến mãi</h2></div>
                        <div class="card-header-actions">
                            <button class="btn btn-secondary btn-compact" type="button" onclick="lamMoiKM()">Tạo mới</button>
                            <span class="card-badge">Biểu mẫu</span>
                        </div>
                    </div>
                    <form id="km-form" action="khuyenmai" method="post">
                        <input type="hidden" id="km-action" name="action" value="add">
                        <div class="form-grid">
                            <div class="field">
                                <label for="km-makm-input">Mã khuyến mãi</label>
                                <input id="km-makm-input" type="text" name="makm"
                                       placeholder="Để trống → tự động (KM-YYYYMM-XXXX)">
                            </div>
                            <div class="field">
                                <label for="km-ngayapdung">Ngày áp dụng</label>
                                <input id="km-ngayapdung" type="date" name="ngayapdung" required>
                            </div>
                            <div class="field">
                                <label for="km-ngayketthuc">Ngày kết thúc</label>
                                <input id="km-ngayketthuc" type="date" name="ngayketthuc" required>
                            </div>
                            <div class="field">
                                <label for="km-phantramgiam">Phần trăm giảm (%)</label>
                                <input id="km-phantramgiam" type="number" name="phantramgiam" min="0" max="100" step="0.01" required placeholder="Vd: 10">
                            </div>
                        </div>
                        <div class="actions">
                            <button id="km-submit" class="btn btn-primary" type="submit">Thêm khuyến mãi</button>
                            <button id="km-cancel" class="btn btn-secondary is-hidden" type="button" onclick="lamMoiKM()">Hủy chỉnh sửa</button>
                        </div>
                    </form>
                    <% if (request.getAttribute("message") != null && !String.valueOf(request.getAttribute("message")).isBlank()) { %>
                        <div class="message warn">${message}</div>
                    <% } %>
                </section>
            </div>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/jspf/delete-confirm.jspf" %>

<script>
function suaKM(maKM, ngayAp, ngayKT, pct) {
    document.getElementById('km-form-title').textContent = 'Sửa khuyến mãi';
    document.getElementById('km-action').value = 'update';
    var inp = document.getElementById('km-makm-input');
    inp.value = maKM;
    inp.readOnly = true;
    inp.style.opacity = '.7';
    document.getElementById('km-ngayapdung').value = ngayAp;
    document.getElementById('km-ngayketthuc').value = ngayKT;
    document.getElementById('km-phantramgiam').value = pct;
    document.getElementById('km-submit').textContent = 'Cập nhật';
    document.getElementById('km-cancel').classList.remove('is-hidden');
    document.getElementById('km-form').scrollIntoView({behavior:'smooth'});
}
function lamMoiKM() {
    document.getElementById('km-form-title').textContent = 'Thêm khuyến mãi';
    document.getElementById('km-action').value = 'add';
    var inp = document.getElementById('km-makm-input');
    inp.value = '';
    inp.readOnly = false;
    inp.style.opacity = '';
    document.getElementById('km-ngayapdung').value = '';
    document.getElementById('km-ngayketthuc').value = '';
    document.getElementById('km-phantramgiam').value = '';
    document.getElementById('km-submit').textContent = 'Thêm khuyến mãi';
    document.getElementById('km-cancel').classList.add('is-hidden');
}
</script>
</body>
</html>
