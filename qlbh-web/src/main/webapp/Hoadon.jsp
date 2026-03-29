<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect("login");
        return;
    }
    if (request.getAttribute("dsHoaDon") == null) {
        response.sendRedirect("hoadon");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hóa đơn</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">
        <div class="hero-panel">
            <div class="hero-copy">
                <span class="eyebrow">Điều phối hóa đơn</span>
                <h1>Quản lý hóa đơn.</h1>
            </div>
        </div>

        <div class="split-layout">
            <section class="card">
                <div class="card-header">
                    <div><h2>Danh sách hóa đơn</h2></div>
                    <span class="card-badge">Sổ hóa đơn</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã hóa đơn</th>
                            <th>Ngày lập</th>
                            <th>VAT</th>
                            <th>Khách hàng</th>
                            <th>Khuyến mãi</th>
                            <th>Tổng tiền</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>${dsHoaDon}</tbody>
                    </table>
                </div>
            </section>

            <div class="stack">
                <section class="card" id="card-form-hoadon">
                    <div class="card-header">
                        <div><h2 id="hoadon-form-title">Tạo hóa đơn</h2></div>
                        <div class="card-header-actions">
                            <button class="btn btn-secondary btn-compact" type="button" onclick="lamMoiHoaDon()">Tạo mới</button>
                            <span class="card-badge">Biểu mẫu</span>
                        </div>
                    </div>
                    <form id="hoadon-form" action="hoadon" method="post">
                        <input type="hidden" id="hoadon-action" name="action" value="add">
                        <input type="hidden" id="hoadon-mahd" name="mahd" value="">
                        <div class="form-grid">
                            <div class="field span-2">
                                <label>Mã hóa đơn <span class="auto-badge">Tự động</span></label>
                                <input type="text" readonly placeholder="Do hệ thống tạo" id="hoadon-mahd-display">
                            </div>
                            <div class="field">
                                <label for="ngaylap">Ngày lập</label>
                                <input id="ngaylap" type="date" name="ngaylap" required>
                            </div>
                            <div class="field">
                                <label for="vat">VAT (%)</label>
                                <input id="vat" type="number" name="vat" min="0" max="100" step="0.01" required placeholder="10">
                            </div>
                            <div class="field">
                                <label for="makh">Khách hàng</label>
                                <select id="makh" name="makh" required>${dsKhachHangOpts}</select>
                            </div>
                            <div class="field">
                                <label for="makm">Khuyến mãi</label>
                                <select id="makm" name="makm">${dsKhuyenMaiOpts}</select>
                            </div>
                        </div>
                        <div class="actions">
                            <button id="hoadon-submit" class="btn btn-primary" type="submit">Tạo hóa đơn</button>
                            <button id="hoadon-cancel" class="btn btn-secondary is-hidden" type="button" onclick="lamMoiHoaDon()">Hủy chỉnh sửa</button>
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
function suaHoaDon(maHD, ngayLap, vat, maKH, maKM) {
    document.getElementById('hoadon-form-title').textContent = 'Sửa hóa đơn';
    document.getElementById('hoadon-action').value = 'update';
    document.getElementById('hoadon-mahd').value = maHD;
    document.getElementById('hoadon-mahd-display').value = maHD;
    document.getElementById('ngaylap').value = ngayLap;
    document.getElementById('vat').value = vat;
    var selKH = document.getElementById('makh');
    for (var i = 0; i < selKH.options.length; i++) {
        if (selKH.options[i].value === maKH) { selKH.selectedIndex = i; break; }
    }
    var selKM = document.getElementById('makm');
    for (var j = 0; j < selKM.options.length; j++) {
        if (selKM.options[j].value === maKM) { selKM.selectedIndex = j; break; }
    }
    document.getElementById('hoadon-submit').textContent = 'Cập nhật';
    document.getElementById('hoadon-cancel').classList.remove('is-hidden');
    document.getElementById('card-form-hoadon').scrollIntoView({behavior:'smooth'});
}
function lamMoiHoaDon() {
    document.getElementById('hoadon-form-title').textContent = 'Tạo hóa đơn';
    document.getElementById('hoadon-action').value = 'add';
    document.getElementById('hoadon-mahd').value = '';
    document.getElementById('hoadon-mahd-display').value = '';
    document.getElementById('hoadon-form').reset();
    document.getElementById('hoadon-submit').textContent = 'Tạo hóa đơn';
    document.getElementById('hoadon-cancel').classList.add('is-hidden');
}
</script>
</body>
</html>
