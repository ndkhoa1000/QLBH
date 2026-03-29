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
                <h1>Tạo hóa đơn và quản lý khuyến mãi.</h1>
            </div>
        </div>

        <div class="split-layout">
            <section class="card">
                <div class="card-header">
                    <div>
                        <h2>Danh sách hóa đơn</h2>
                    </div>
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
                            <th>Tổng tiền</th>
                            <th>Thao tác</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        ${dsHoaDon}
                        </tbody>
                    </table>
                </div>
            </section>

            <div class="stack">
                <section class="card" id="card-form-hoadon">
                <div class="card-header">
                    <div>
                        <h2 id="hoadon-form-title">Tạo hóa đơn</h2>
                    </div>
                    <div class="card-header-actions">
                        <button class="btn btn-secondary btn-compact" type="button" onclick="lamMoiHoaDon()">Tạo mới</button>
                        <span class="card-badge">Biểu mẫu</span>
                    </div>
                </div>
                    <form id="hoadon-form" action="hoadon" method="post">
                        <input type="hidden" id="hoadon-action" name="action" value="add">
                        <div class="form-grid">
                            <div class="field">
                                <label for="mahd">Mã hóa đơn <span class="auto-badge">Tự động</span></label>
                                <input id="mahd" type="text" name="mahd" required readonly>
                            </div>
                            <div class="field">
                                <label for="ngaylap">Ngày lập</label>
                                <input id="ngaylap" type="datetime-local" name="ngaylap" required>
                            </div>
                            <div class="field">
                                <label for="vat">VAT (%)</label>
                                <input id="vat" type="number" name="vat" min="0" max="100" step="0.01" required>
                            </div>
                            <div class="field">
                                <label for="makh">Khách hàng</label>
                                <select id="makh" name="makh" required>${dsKhachHangOpts}</select>
                            </div>
                        </div>

                        <div id="cthoadon-section">
                            <div class="dynamic-actions">
                                <button class="btn btn-secondary" type="button" onclick="themCTHD()">+ Thêm chi tiết</button>
                            </div>
                            <div id="inputcthoadon" class="dynamic-list"></div>
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

<div id="khuyenMaiModal" class="modal" onclick="dongKhuyenMai(event)">
    <div class="modal-card" onclick="event.stopPropagation()">
        <div class="modal-header">
            <div>
                <h2>Áp dụng khuyến mãi</h2>
            </div>
            <button class="modal-close" type="button" aria-label="Đóng" onclick="dongKhuyenMai()">&times;</button>
        </div>
        <form action="hoadon" method="post">
            <div class="form-grid">
                <div class="field">
                    <label for="mahdkm">Mã hóa đơn</label>
                    <input id="mahdkm" type="text" name="mahdkm" readonly>
                </div>
                <div class="field">
                    <label for="makmchohd">Mã khuyến mãi</label>
                    <input id="makmchohd" type="text" name="makmchohd">
                </div>
                <div class="field">
                    <label for="ngayapdungkm">Ngày áp dụng</label>
                    <input id="ngayapdungkm" type="date" name="ngayapdungkm">
                </div>
                <div class="field">
                    <label for="ngayketthuckm">Ngày kết thúc</label>
                    <input id="ngayketthuckm" type="date" name="ngayketthuckm">
                </div>
                <div class="field">
                    <label for="phantramgiamkm">Phần trăm giảm</label>
                    <input id="phantramgiamkm" type="number" name="phantramgiamkm" min="0" max="100" step="0.01" placeholder="0.2 hoặc 20">
                </div>
            </div>
            <% if (request.getAttribute("messagekm") != null && !String.valueOf(request.getAttribute("messagekm")).isBlank()) { %>
                <div class="message success">${messagekm}</div>
            <% } %>
            <div class="actions">
                <button class="btn btn-secondary" type="button" onclick="dongKhuyenMai()">Đóng</button>
                <button class="btn btn-accent" type="submit" name="action" value="aplkm">Áp dụng khuyến mãi</button>
            </div>
        </form>
    </div>
</div>

<div id="chiTietModal" class="modal" onclick="dongChiTiet(event)">
    <div class="modal-card" onclick="event.stopPropagation()">
        <div class="modal-header">
            <div>
                <h2>Chi tiết hóa đơn: <span id="ct-modal-title"></span></h2>
            </div>
            <button class="modal-close" type="button" aria-label="Đóng" onclick="dongChiTiet()">&times;</button>
        </div>
        <div id="ct-modal-table" style="overflow-x:auto;margin-bottom:1rem"></div>
        <% if (request.getAttribute("message") != null && !String.valueOf(request.getAttribute("message")).isBlank()
                && request.getAttribute("activeChiTietInvoiceId") != null
                && !String.valueOf(request.getAttribute("activeChiTietInvoiceId")).isBlank()) { %>
            <div class="message warn">${message}</div>
        <% } %>
        <form action="hoadon" method="post">
            <input type="hidden" name="action" value="addChiTiet">
            <input type="hidden" id="ct-mahd-input" name="mahd">
            <div class="form-grid">
                <div class="field">
                    <label for="ct-masp-select">Sản phẩm</label>
                    <select id="ct-masp-select" name="masp" required></select>
                </div>
                <div class="field">
                    <label for="ct-soluong-input">Số lượng</label>
                    <input id="ct-soluong-input" type="number" name="soluong" min="1" required placeholder="Nhập SL">
                </div>
                <div class="field">
                    <label for="ct-dongia-input">Đơn giá</label>
                    <input id="ct-dongia-input" type="number" name="dongia" min="0" step="0.01" placeholder="Tự động">
                </div>
            </div>
            <div class="actions">
                <button class="btn btn-secondary" type="button" onclick="dongChiTiet()">Đóng</button>
                <button class="btn btn-primary" type="submit">Thêm chi tiết</button>
            </div>
        </form>
    </div>
</div>

<script id="spData" type="application/json">${dsSPJson}</script>

<script>
    var dsSP = [];
    try { dsSP = JSON.parse(document.getElementById('spData').textContent || '[]'); } catch(e) { dsSP = []; }

    function genMa(prefix) {
        return prefix + Math.random().toString(36).slice(2, 8).toUpperCase();
    }

    function todayDatetime() {
        var d = new Date();
        d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
        return d.toISOString().slice(0, 16);
    }

    function datCheDoHoaDon(dangSua) {
        document.getElementById("hoadon-form-title").textContent = dangSua ? "Cập nhật hóa đơn" : "Tạo hóa đơn";
        document.getElementById("hoadon-submit").textContent = dangSua ? "Lưu thay đổi" : "Tạo hóa đơn";
        document.getElementById("hoadon-action").value = dangSua ? "update" : "add";
        document.getElementById("hoadon-cancel").classList.toggle("is-hidden", !dangSua);
        document.getElementById("cthoadon-section").style.display = dangSua ? "none" : "";
    }

    function lamMoiHoaDon() {
        document.getElementById("hoadon-form").reset();
        document.getElementById("mahd").value = genMa("HD");
        document.getElementById("ngaylap").value = todayDatetime();
        document.getElementById("vat").value = "10";
        document.getElementById("inputcthoadon").innerHTML = "";
        datCheDoHoaDon(false);
    }

    function chinhSuaHoaDon(mahd, ngaylap, vat, makh) {
        document.getElementById("mahd").value = mahd;
        document.getElementById("ngaylap").value = ngaylap + "T00:00";
        var displayVat = parseFloat(vat);
        if (displayVat > 0 && displayVat <= 1) displayVat = Math.round(displayVat * 100);
        document.getElementById("vat").value = displayVat;
        document.getElementById("makh").value = makh;
        datCheDoHoaDon(true);
        document.getElementById("card-form-hoadon").scrollIntoView({ behavior: 'smooth' });
    }

    function xemCT(mahd) {
        var hd = document.getElementById(mahd);
        var toggle = document.getElementById("toggle-" + mahd);
        if (hd.classList.contains("hide")) {
            hd.classList.remove("hide");
            if (toggle) toggle.classList.add("is-open");
        } else {
            hd.classList.add("hide");
            if (toggle) toggle.classList.remove("is-open");
        }
    }

    function moKhuyenMai(mahd) {
        document.getElementById("mahdkm").value = mahd;
        document.getElementById("khuyenMaiModal").classList.add("active");
    }

    function dongKhuyenMai(event) {
        if (event && event.target && event.target.id !== "khuyenMaiModal") return;
        document.getElementById("khuyenMaiModal").classList.remove("active");
    }

    function themCTHD() {
        var container = document.getElementById("inputcthoadon");
        var div = document.createElement("div");
        div.className = "rowct";

        // SP select field
        var spField = document.createElement("div");
        spField.className = "field";
        var spLabel = document.createElement("label");
        spLabel.textContent = "Sản phẩm";
        var spSelect = document.createElement("select");
        spSelect.name = "masp[]";
        spSelect.required = true;

        // SL field
        var slField = document.createElement("div");
        slField.className = "field";
        var slLabel = document.createElement("label");
        slLabel.textContent = "Số lượng";
        var slInput = document.createElement("input");
        slInput.type = "number";
        slInput.name = "soluong[]";
        slInput.required = true;
        slInput.min = "1";
        slInput.placeholder = "Nhập SL";

        // DG field (defined before SP listener so closure captures it)
        var dgField = document.createElement("div");
        dgField.className = "field";
        var dgLabel = document.createElement("label");
        dgLabel.textContent = "Đơn giá";
        var dgInput = document.createElement("input");
        dgInput.type = "number";
        dgInput.name = "dongia[]";
        dgInput.required = true;
        dgInput.min = "0";
        dgInput.step = "0.01";
        dgInput.placeholder = "Tự động";

        // Populate SP select
        var defaultOpt = document.createElement("option");
        defaultOpt.value = "";
        defaultOpt.textContent = "-- Chọn sản phẩm --";
        spSelect.appendChild(defaultOpt);
        dsSP.forEach(function(sp) {
            var opt = document.createElement("option");
            var ma = sp.maSP || sp.MaSP || "";
            var ten = sp.tenSP || sp.TenSP || "";
            var gia = sp.gia || sp.Gia || 0;
            var ton = sp.soLuongTon || sp.SoLuongTon || 0;
            opt.value = ma;
            opt.textContent = ma + " - " + ten + " (Giá: " + gia + ", Tồn: " + ton + ")";
            opt.dataset.gia = gia;
            spSelect.appendChild(opt);
        });

        // Auto-fill dongia when SP is selected
        spSelect.addEventListener("change", function() {
            var sel = this.options[this.selectedIndex];
            if (sel && sel.dataset.gia) dgInput.value = sel.dataset.gia;
        });

        // Assemble fields
        spField.appendChild(spLabel);
        spField.appendChild(spSelect);
        slField.appendChild(slLabel);
        slField.appendChild(slInput);
        dgField.appendChild(dgLabel);
        dgField.appendChild(dgInput);

        // Remove button
        var removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.className = "btn btn-secondary btn-compact";
        removeBtn.textContent = "Bỏ";
        removeBtn.onclick = function() { container.removeChild(div); };

        div.appendChild(spField);
        div.appendChild(slField);
        div.appendChild(dgField);
        div.appendChild(removeBtn);
        container.appendChild(div);
    }

    // Initialize form on page load
    (function() {
        document.getElementById("mahd").value = genMa("HD");
        document.getElementById("ngaylap").value = todayDatetime();
        document.getElementById("vat").value = "10";
    })();

    <% if (request.getAttribute("activePromotionInvoiceId") != null && !String.valueOf(request.getAttribute("activePromotionInvoiceId")).isBlank()) { %>
    moKhuyenMai("<%= String.valueOf(request.getAttribute("activePromotionInvoiceId")).replace("\\", "\\\\").replace("\"", "\\\"") %>");
    <% } %>

    <% if (request.getAttribute("activeChiTietInvoiceId") != null && !String.valueOf(request.getAttribute("activeChiTietInvoiceId")).isBlank()) { %>
    moSuaChiTiet("<%= String.valueOf(request.getAttribute("activeChiTietInvoiceId")).replace("\\", "\\\\").replace("\"", "\\\"") %>");
    <% } %>

    // --- Chi tiet modal functions ---
    var _ctModalMaHD = "";

    function escHtml(s) {
        return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
    }

    function moSuaChiTiet(mahd) {
        _ctModalMaHD = mahd;
        var el = document.getElementById("ct-json-" + mahd);
        var chiTiets = [];
        try { chiTiets = JSON.parse(el ? el.textContent : "[]"); } catch(e) {}

        document.getElementById("ct-modal-title").textContent = mahd;
        document.getElementById("ct-mahd-input").value = mahd;

        // Populate product select in add form
        var spSel = document.getElementById("ct-masp-select");
        spSel.innerHTML = "<option value=''>-- Chọn sản phẩm --</option>";
        dsSP.forEach(function(sp) {
            var ma = sp.maSP || sp.MaSP || "";
            var ten = sp.tenSP || sp.TenSP || "";
            var gia = sp.gia || sp.Gia || 0;
            var ton = sp.soLuongTon || sp.SoLuongTon || 0;
            var opt = document.createElement("option");
            opt.value = ma;
            opt.textContent = ma + " - " + ten + " (Tồn: " + ton + ")";
            opt.dataset.gia = gia;
            spSel.appendChild(opt);
        });
        spSel.onchange = function() {
            var sel = this.options[this.selectedIndex];
            if (sel && sel.dataset.gia) document.getElementById("ct-dongia-input").value = sel.dataset.gia;
        };

        renderChiTietModal(mahd, chiTiets);
        document.getElementById("chiTietModal").classList.add("active");
    }

    function renderChiTietModal(mahd, chiTiets) {
        var container = document.getElementById("ct-modal-table");
        if (!chiTiets || chiTiets.length === 0) {
            container.innerHTML = "<p style='margin:0.5rem 0;color:var(--text-muted,#888)'>Chưa có chi tiết hóa đơn.</p>";
            return;
        }
        var html = "<table><thead><tr><th>Mã SP</th><th>Sản phẩm</th><th>Số lượng</th><th>Đơn giá</th><th></th></tr></thead><tbody>";
        chiTiets.forEach(function(ct) {
            var maSP = ct.maSP || ct.MaSP || "";
            var sp = ct.sanPham || ct.SanPham || {};
            var tenSP = sp.tenSP || sp.TenSP || "";
            var sl = ct.soLuong || ct.SoLuong || 0;
            var dg = ct.donGia || ct.DonGia || 0;
            html += "<tr>";
            html += "<td>" + escHtml(maSP) + "</td>";
            html += "<td>" + escHtml(tenSP) + "</td>";
            html += "<td>" + sl + "</td>";
            html += "<td>" + dg + "</td>";
            html += "<td><form action='hoadon' method='post' style='display:inline'>" +
                    "<input type='hidden' name='action' value='deleteChiTiet'>" +
                    "<input type='hidden' name='mahd' value='" + escHtml(mahd) + "'>" +
                    "<input type='hidden' name='masp' value='" + escHtml(maSP) + "'>" +
                    "<button class='btn btn-secondary btn-compact' type='submit' onclick=\"return confirm('Xóa chi tiết này?')\">Xóa</button>" +
                    "</form></td>";
            html += "</tr>";
        });
        html += "</tbody></table>";
        container.innerHTML = html;
    }

    function dongChiTiet(event) {
        if (event && event.target.id !== "chiTietModal") return;
        document.getElementById("chiTietModal").classList.remove("active");
    }
</script>
</body>
</html>
