<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ctu.cit.model.HoaDon" %>
<%
    if (session == null || session.getAttribute("currentUser") == null) {
        response.sendRedirect("login");
        return;
    }
    HoaDon hd = (HoaDon) request.getAttribute("hd");
    if (hd == null) {
        response.sendRedirect("hoadon");
        return;
    }
    String maHDVal = hd.getMaHD() != null ? hd.getMaHD() : "";
    String ngayLap = hd.getNgayLap() != null ? hd.getNgayLap().toString() : "—";
    double vatDisp = hd.getVAT() > 0 && hd.getVAT() <= 1
            ? Math.round(hd.getVAT() * 100) : hd.getVAT();
    String khTen   = hd.getKhachHang() != null
            ? (hd.getKhachHang().getHoTen() != null ? hd.getKhachHang().getHoTen() : hd.getKhachHang().getMaKH())
            : "—";
    String khMa    = hd.getKhachHang() != null ? hd.getKhachHang().getMaKH() : "";
    String kmInfo  = hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : "Không áp dụng";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi tiết HĐ <%= maHDVal %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    <style>
        /* ── print ──────────────────────────────── */
        @media print {
            .no-print { display: none !important; }
            .topbar   { display: none !important; }
            body      { background: #fff; }
            .shell    { display: block; }
            .card     { box-shadow: none; border: 1px solid #ddd; page-break-inside: avoid; }
            .print-banner { display: block !important; }
            .split-layout { display: block !important; }
        }
        .print-banner { display: none; }

        /* ── meta rows ───────────────────────────── */
        .meta-rows { display: flex; flex-direction: column; gap: .45rem; margin: .8rem 0 .25rem; padding-left: 0.5rem;}
        .meta-row  { display: flex; align-items: baseline; gap: .5rem; font-size: .9rem; }
        .meta-row .lbl {
            flex: 0 0 110px;
            font-size: .72rem;
            text-transform: uppercase;
            letter-spacing: .05em;
            color: #9ca3af;
        }
        .meta-row .val { font-weight: 600; color: #111827; }

        /* ── totals table ────────────────────────── */
        .totals-wrap { padding: 1rem 1rem .5rem; border-top: 1px solid #f3f4f6; margin-top: .5rem; }
        .totals-tbl  { width: 100%; border-collapse: collapse; }
        .totals-tbl td { padding: .28rem .4rem; }
        .totals-tbl .lbl { color: #6b7280; font-size: .88rem; }
        .totals-tbl .val { text-align: right; font-weight: 600; font-size: .9rem; white-space: nowrap; }
        .totals-tbl .grand td {
            border-top: 2px solid #374151;
            padding-top: .5rem;
            font-size: 1rem;
        }

        /* ── stock badge ─────────────────────────── */
        .stock-hint {
            display: inline-block;
            font-size: .75rem;
            color: #6b7280;
            margin-top: .25rem;
            min-height: 1rem;
        }
        .stock-hint.low  { color: #dc2626; font-weight: 600; }
        .stock-hint.ok   { color: #16a34a; }

        /* ── table numerics ──────────────────────── */
        th.num, td.num { text-align: right; }

        /* ── back link ───────────────────────────── */
        .back-link {
            display: inline-flex; align-items: center; gap: .3rem;
            color: #6b7280; font-size: .88rem; text-decoration: none;
            margin-bottom: 1rem;
        }
        .back-link:hover { color: #111827; }
    </style>
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">

        <!-- ── printer-only title ── -->
        <div class="print-banner" style="text-align:center;margin-bottom:1.5rem">
            <h2 style="margin:0">HÓA ĐƠN BÁN HÀNG</h2>
            <p style="margin:.3rem 0 0;color:#555">Mã: <%= maHDVal %> &bull; Ngày: <%= ngayLap %></p>
        </div>

        <a class="back-link no-print" href="<%= request.getContextPath() %>/hoadon">&#8592; Quay lại danh sách</a>

        <% if (request.getAttribute("message") != null && !String.valueOf(request.getAttribute("message")).isBlank()) { %>
            <div class="message warn no-print">${message}</div>
        <% } %>

        <div class="split-layout">

            <!-- ════════════════ LEFT COLUMN ════════════════ -->
            <div class="stack">

                <!-- Invoice header card -->
                <section class="card">
                    <div class="card-header">
                        <div>
                            <h2 style="margin:0">Hóa đơn <%= maHDVal %></h2>
                        </div>
                        <button class="btn btn-secondary btn-compact no-print" onclick="window.print()">&#128438; In hóa đơn</button>
                    </div>
                    <div class="meta-rows">
                        <div class="meta-row"><span class="lbl">Ngày lập</span><span class="val"><%= ngayLap %></span></div>
                        <div class="meta-row"><span class="lbl">VAT</span><span class="val"><%= vatDisp %>%</span></div>
                        <div class="meta-row"><span class="lbl">Khách hàng</span><span class="val"><%= khTen %><% if (!khMa.isEmpty()) { %> <small style="color:#9ca3af;font-weight:400">(<%= khMa %>)</small><% } %></span></div>
                        <div class="meta-row"><span class="lbl">Khuyến mãi</span><span class="val"><%= kmInfo %></span></div>
                    </div>
                </section>

                        <!-- List products -->
                             <section class="card">
                <div class="card-header">
                    <div><h2>Danh sách sản phẩm</h2></div>
                    <span class="card-badge">Chi tiết hóa đơn</span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Mã SP</th>
                            <th>Tên sản phẩm</th>
                            <th class="num">SL</th>
                            <th class="num">Đơn giá</th>
                            <th class="num">Thành tiền</th>
                            <th class="no-print" style="width:60px"></th>
                        </tr>
                        </thead>
                        <tbody>${chiTietRows}</tbody>
                    </table>
                </div>
            </section>
                            <!-- Totals card -->
                <section class="card">
                    <div class="card-header">
                        <div><h2>Tổng tiền</h2></div>
                        <span class="card-badge">Thanh toán</span>
                    </div>
                    <div class="totals-wrap">
                        <table class="totals-tbl">
                            <tr>
                                <td class="lbl">Tạm tính</td>
                                <td class="val">${subtotal}&thinsp;₫</td>
                            </tr>
                            <tr>
                                <td class="lbl">VAT (<%= vatDisp %>%)</td>
                                <td class="val">+ ${vatAmt}&thinsp;₫</td>
                            </tr>
                            <tr>
                                <td class="lbl">Giảm khuyến mãi</td>
                                <td class="val">− ${discAmt}&thinsp;₫</td>
                            </tr>
                            <tr class="grand">
                                <td class="lbl"><strong>Tổng cộng</strong></td>
                                <td class="val"><strong>${total}&thinsp;₫</strong></td>
                            </tr>
                        </table>
                    </div>
                </section>
            </div><!-- /LEFT -->

            <!-- ════════════════ RIGHT COLUMN ════════════════ -->
                             <!-- Add product form -->
                             <section class="card no-print">
                    <div class="card-header">
                        <div><h2>Thêm sản phẩm</h2></div>
                        <span class="card-badge">Chi tiết</span>
                    </div>
                    <form action="hoadon-detail" method="post">
                        <input type="hidden" name="mahd" value="<%= maHDVal %>">
                        <input type="hidden" name="action" value="addChiTiet">
                        <div class="form-grid">
                            <div class="field">
                                <label for="ct-masp">Sản phẩm</label>
                                <select id="ct-masp" name="masp" required onchange="onSpChange(this)">
                                    ${spOpts}
                                </select>
                                <span id="stock-hint" class="stock-hint">Chọn sản phẩm để xem tồn kho</span>
                            </div>
                            <div class="field">
                                <label for="ct-soluong">Số lượng <span id="max-label" style="font-size:.78rem;color:#9ca3af"></span></label>
                                <input id="ct-soluong" name="soluong" type="number" min="1" required value="1">
                            </div>
                            <div class="field">
                                <label for="ct-dongia">Đơn giá</label>
                                <input id="ct-dongia" name="dongia" type="number" min="0" step="1" placeholder="Tự động theo danh mục">
                            </div>
                        </div>
                        <div class="actions">
                            <button class="btn btn-primary" type="submit">Thêm vào hóa đơn</button>
                        </div>
                    </form>
                </section>
<!-- /RIGHT -->

        </div><!-- /split-layout -->
    </section>
</main>

<script>
function onSpChange(sel) {
    var opt = sel.options[sel.selectedIndex];
    if (!opt || !opt.value) {
        document.getElementById('stock-hint').textContent = 'Chọn sản phẩm để xem tồn kho';
        document.getElementById('stock-hint').className = 'stock-hint';
        document.getElementById('max-label').textContent = '';
        document.getElementById('ct-dongia').value = '';
        return;
    }
    var gia = opt.getAttribute('data-gia') || '';
    var ton = parseInt(opt.getAttribute('data-ton') || '0', 10);
    document.getElementById('ct-dongia').value = gia;
    document.getElementById('ct-soluong').max = ton > 0 ? ton : '';
    document.getElementById('max-label').textContent = ton > 0 ? '(tối đa ' + ton + ')' : '(hết hàng)';
    var hint = document.getElementById('stock-hint');
    if (ton <= 0) {
        hint.textContent = '⚠ Hết hàng';
        hint.className = 'stock-hint low';
    } else if (ton <= 5) {
        hint.textContent = 'Tồn kho: ' + ton + ' — sắp hết';
        hint.className = 'stock-hint low';
    } else {
        hint.textContent = 'Tồn kho: ' + ton;
        hint.className = 'stock-hint ok';
    }
}
</script>
</body>
</html>
