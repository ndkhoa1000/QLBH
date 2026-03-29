<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body class="auth-body">
    <div class="auth-shell">
        <section class="auth-copy">
            <div>
                <span class="eyebrow">Thiết lập tài khoản</span>
                <h1>Tạo tài khoản quản trị mới.</h1>
                <p>Đăng ký người dùng cho cổng quản lý này và sử dụng ngay tại màn hình đăng nhập.</p>
            </div>
            <ul>
                <li>Sử dụng tên đăng nhập dễ nhận biết trong quá trình kiểm thử.</li>
                <li>Tài khoản được lưu trong bộ nhớ trong suốt thời gian ứng dụng đang chạy.</li>
                <li>Sau khi đăng ký thành công, bạn sẽ được chuyển lại trang đăng nhập với thông báo xác nhận.</li>
            </ul>
        </section>

        <section class="auth-card">
            <span class="eyebrow">Đăng ký</span>
            <h1>Tạo tài khoản</h1>
            <p>Nhập thông tin bên dưới để thêm người dùng mới.</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="notice error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form class="auth-form" action="signup" method="post">
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
                        autocomplete="new-password"
                        required>
                </div>

                <div class="field">
                    <label for="confirmPassword">Xác nhận mật khẩu</label>
                    <input
                        id="confirmPassword"
                        type="password"
                        name="confirmPassword"
                        autocomplete="new-password"
                        required>
                </div>

                <button class="btn btn-accent" type="submit">Tạo tài khoản</button>
            </form>

            <div class="auth-footer">
                Đã có tài khoản? <a href="login">Quay lại đăng nhập</a>
            </div>
        </section>
    </div>
</body>
</html>
