<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="auth-body">
    <div class="auth-shell">
        <section class="auth-copy">
            <div>
                <span class="eyebrow">Quản trị bán hàng</span>
                <h1>Truy cập không gian quản lý.</h1>
                <p>Đăng nhập để quản lý khách hàng, sản phẩm, nhà cung cấp và hóa đơn.</p>
            </div>
            <ul>
                <li>Tài khoản mặc định: <strong>admin</strong> / <strong>admin123</strong></li>
            </ul>
        </section>

        <section class="auth-card">
            <span class="eyebrow">Đăng nhập</span>
            <h1>Vào hệ thống</h1>
            <p>Nhập tên đăng nhập và mật khẩu để tiếp tục.</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="notice error"><%= request.getAttribute("error") %></div>
            <% } %>
            <% if (request.getAttribute("message") != null) { %>
                <div class="notice"><%= request.getAttribute("message") %></div>
            <% } %>

            <form class="auth-form" action="login" method="post">
                <div class="field">
                    <label for="username">Tên đăng nhập</label>
                    <input
                        id="username"
                        type="text"
                        name="username"
                        value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                        autocomplete="username"
                        required>
                </div>

                <div class="field">
                    <label for="password">Mật khẩu</label>
                    <input
                        id="password"
                        type="password"
                        name="password"
                        autocomplete="current-password"
                        required>
                </div>

                <button class="btn btn-primary" type="submit">Đăng nhập</button>
            </form>

            <div class="auth-footer">
                Chưa có tài khoản? <a href="signup">Tạo tài khoản</a>
            </div>
        </section>
    </div>
</body>
</html>
