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
    String khInfo  = hd.getKhachHang() != null
            ? hd.getKhachHang().getMaKH()
              + (hd.getKhachHang().getHoTen() != null ? " – " + hd.getKhachHang().getHoTen() : "")
            : "—";
    String kmInfo  = hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : "Không";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi tiết hóa đơn <%= maHDVal %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    <style>
        /* ── print ── */
        @media print {
            .no-print { display: none !important; }
            .topbar   { display: none !important; }
            body      { background: #fff; }
            .card     { box-shadow: none; border: 1px solid #ccc; page-break-inside: avoid; }
            .print-header { display: block !important; }
        }
        .print-header { display: none; }

        /* ── detail meta grid ── */
        .meta-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: .5rem 1.5rem;
            margin: .75rem 0 1rem;
        }
        .meta-grid dt {
            font-size: .72rem;
            text-transform: uppercase;
            letter-spacing: .05em;
            color: #9ca3af;
            margin: 0;
        }
        .meta-grid dd {
            font-weight: 600;
            font-size: .95rem;
            margin: .1rem 0 0;
        }

        /* ── totals ── */
        .totals {
            margin-left: auto;
            max-width: 340px;
            border-collapse: collapse;
            width: 100%;
        }
        .totals td { padding: .3rem .5rem; }
        .totals .lbl { text-align: right; color: #6b7280; font-size: .9rem; }
        .totals .val { text-align: right; font-weight: 600; }
        .totals .grand td {
            border-top: 2px solid #374151;
            font-size: 1.05rem;
            padding-top: .5rem;
        }

        /* ── right-align numeric cols ── */
        .num { text-align: right; }

        /* ── back link ── */
        .back-link {
            display: inline-flex;
            align-items: center;
            gap: .35rem;
            font-size: .88rem;
            color: #6b7280;
            text-decoration: none;
            margin-bottom: 1rem;
        }
        .back-link:hover { color: #111827; }
    </style>
</head>
<body class="dashboard-body">
<main class="shell">
    <%@ include file="/WEB-INF/jspf/dashboard-nav.jspf" %>

    <section class="content">

        <!-- print-only title block -->
        <div class="print-header" style="text-align:center;margin-bottom:1.5rem">
            <h2 style="margin:0">HÓA ĐƠN BÁN HÀNG</h2>
            <p style="margin:.3rem 0 0;color:#555">Mã: <%= maHDVal %> &nbsp;&bull;&nbsp; Ngày: <%= ngayLap %></p>
        </div>

        <a class="back-link no-print" href="<%= request.getContextPath() %>/hoadon">&#8592; Quay lại danh sách</a>

        <% if (request.getAttribute("message") != null && !String.valueOf(request.getAttribute("message")).isBlank()) { %>
            <div class="message warn no-print">${message}</div>
        <% } %>

        <div class="split-layout">

            <!-- ── LEFT: add-product form ── -->
            <div class="stack no-print">
                <!-- Invoice header info -->
                <section class="card">
                    <div class="card-header">
                        <div>
                            <h2>Hóa đơn</h2>
                            <p style="margin:.25rem 0 0;font-size:.82rem;color:#9ca3af"><%= maHDVal %></p>
                        </div>
                        <button class="btn btn-secondary btn-compact" onclick="window.print()">&#128438; In</button>
                    </div>
                    <dl class="meta-grid">
                        <div><dt>Ngày lập</dt><dd><%= ngayLap %></dd></div>
                        <div><dt>VAT</dt><dd><%= vatDisp %>%</dd></div>
                        <div><dt>Khách hàng</dt><dd><%= khInfo %></dd></div>
                        <div><dt>Khuyến mãi</dt><dd><%= kmInfo %></dd></div>
                    </dl>
                </section>

                <!-- Add product form -->
                <section class="card">
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
                                <select id="ct-masp" name="masp" required onchange="autoFillGia(this)">${spOpts}</select>
                            </div>
                            <div class="field">
                                <label for="ct-soluong">Số lượng</label>
                                <input id="ct-soluong" name="soluong" type="number" min="1" required value="1">
                            </div>
                            <div class="field">
                                <label for="ct-dongia">Đơn giá (tự động)</label>
                                <input id="ct-dongia" name="dongia" type="number" min="0" step="0.01" placeholder="Lấy từ danh mục">
                            </div>
                        </div>
                        <div class="actions">
                            <button class="btn btn-primary" type="submit">Thêm vào hóa đơn</button>
                        </div>
                    </form>
                </section>
            </div>

            <!-- ── RIGHT: chi tiet table ── -->
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
                            <th class="no-print" style="width:56px"></th>
                        </tr>
                        </thead>
                        <tbody>${chiTietRows}</tbody>
                    </table>
                </div>

                <!-- Totals block -->
                <div style="padding:.75rem 1rem 1rem">
                    <table class="totals">
                        <tr>
                            <td class="lbl">Tạm tính</td>
                            <td class="val">${subtotal}&thinsp;₫</td>
                        </tr>
                        <tr>
                            <td class="lbl">VAT (<%= vatDisp %>%)</td>
                            <td class="val">${vatAmt}&thinsp;₫</td>
                        </tr>
                        <tr>
                            <td class="lbl">Giảm KM</td>
                            <td class="val">−${discAmt}&thinsp;₫</td>
                        </tr>
                        <tr class="grand">
                            <td class="lbl"><strong>Tổng cộng</strong></td>
                            <td class="val"><strong>${total}&thinsp;₫</strong></td>
                        </tr>
                    </table>
                </div>
            </section>

        </div><!-- /split-layout -->
    </section>
</main>

<script>
function autoFillGia(sel) {
    var opt = sel.options[sel.selectedIndex];
    var gia = opt ? opt.getAttribute('data-gia') : '';
    document.getElementById('ct-dongia').value = gia || '';
}
</script>
</body>
</html>
