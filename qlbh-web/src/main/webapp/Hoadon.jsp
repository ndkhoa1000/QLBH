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
                <section class="card">
                <div class="card-header">
                    <div>
                        <h2>Tạo hóa đơn</h2>
                    </div>
                    <span class="card-badge">Hóa đơn mới</span>
                </div>
                    <form action="hoadon" method="post">
                        <div class="form-grid">
                            <div class="field">
                                <label for="mahd">Mã hóa đơn</label>
                                <input id="mahd" type="text" name="mahd" required>
                            </div>
                            <div class="field">
                                <label for="ngaylap">Ngày lập</label>
                                <input id="ngaylap" type="datetime-local" name="ngaylap" required>
                            </div>
                            <div class="field">
                                <label for="vat">VAT</label>
                                <input id="vat" type="number" name="vat" min="0" step="0.01" required>
                            </div>
                            <div class="field">
                                <label for="makh">Mã khách hàng</label>
                                <input id="makh" type="text" name="makh" required>
                            </div>
                        </div>

                        <div class="dynamic-actions">
                            <button class="btn btn-secondary" type="button" onclick="themCTHD()">Thêm chi tiết hóa đơn</button>
                        </div>
                        <div id="inputcthoadon" class="dynamic-list"></div>

                        <div class="actions">
                            <button class="btn btn-primary" type="submit" name="action" value="add">Tạo hóa đơn</button>
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

<script>
    function xemCT(mahd) {
        var hd = document.getElementById(mahd);
        var toggle = document.getElementById("toggle-" + mahd);
        if (hd.classList.contains("hide")) {
            hd.classList.remove("hide");
            if (toggle) {
                toggle.classList.add("is-open");
            }
        } else {
            hd.classList.add("hide");
            if (toggle) {
                toggle.classList.remove("is-open");
            }
        }
    }

    function moKhuyenMai(mahd) {
        document.getElementById("mahdkm").value = mahd;
        document.getElementById("khuyenMaiModal").classList.add("active");
    }

    function dongKhuyenMai(event) {
        if (event && event.target && event.target.id !== "khuyenMaiModal") {
            return;
        }
        document.getElementById("khuyenMaiModal").classList.remove("active");
    }

    function themCTHD() {
        var container = document.getElementById("inputcthoadon");

        const div = document.createElement("div");
        div.className = "rowct";

        const maspinput = document.createElement("input");
        maspinput.type = "text";
        maspinput.name = "masp[]";
        maspinput.required = true;
        maspinput.placeholder = "Mã sản phẩm";

        const slinput = document.createElement("input");
        slinput.type = "text";
        slinput.name = "soluong[]";
        slinput.placeholder = "Số lượng";
        slinput.required = true;
        slinput.inputMode = "numeric";
        slinput.pattern = "\\d*";

        const dginput = document.createElement("input");
        dginput.type = "number";
        dginput.name = "dongia[]";
        dginput.placeholder = "Đơn giá";
        dginput.required = true;
        dginput.min = "0";
        dginput.step = "0.01";

        const removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.className = "btn btn-secondary";
        removeBtn.innerText = "Bỏ";

        removeBtn.onclick = function () {
            container.removeChild(div);
        };

        div.appendChild(maspinput);
        div.appendChild(slinput);
        div.appendChild(dginput);
        div.appendChild(removeBtn);

        container.appendChild(div);
    }

    <% if (request.getAttribute("activePromotionInvoiceId") != null && !String.valueOf(request.getAttribute("activePromotionInvoiceId")).isBlank()) { %>
    moKhuyenMai("<%= String.valueOf(request.getAttribute("activePromotionInvoiceId")).replace("\\", "\\\\").replace("\"", "\\\"") %>");
    <% } %>
</script>
</body>
</html>
