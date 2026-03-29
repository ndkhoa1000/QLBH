package ctu.cit.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/auth", "/login", "/signup", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final Map<String, String> USERS = new ConcurrentHashMap<>();

    static {
        USERS.put("admin", "admin123");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("/logout".equals(request.getServletPath())) {
            handleLogout(request, response);
            return;
        }
        routeToPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if ("/logout".equals(request.getServletPath())) {
            handleLogout(request, response);
            return;
        }

        String mode = resolveMode(request);
        if ("signup".equals(mode)) {
            handleSignup(request, response);
            return;
        }

        handleLogin(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = trim(request.getParameter("username"));
        String password = trim(request.getParameter("password"));

        request.setAttribute("username", username);

        if (username.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String storedPassword = USERS.get(username);
        if (storedPassword == null || !storedPassword.equals(password)) {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", username);
        response.sendRedirect(request.getContextPath() + "/hoadon");
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = trim(request.getParameter("username"));
        String password = trim(request.getParameter("password"));
        String confirmPassword = trim(request.getParameter("confirmPassword"));

        request.setAttribute("username", username);

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }

        if (USERS.putIfAbsent(username, password) != null) {
            request.setAttribute("error", "Tên đăng nhập đã tồn tại.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }

        request.getSession(true).setAttribute("flashMessage", "Tạo tài khoản thành công. Vui lòng đăng nhập.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("flashMessage", "Bạn đã đăng xuất.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void routeToPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/hoadon");
            return;
        }

        String mode = resolveMode(request);
        if ("signup".equals(mode)) {
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }

        if (session != null) {
            Object flashMessage = session.getAttribute("flashMessage");
            if (flashMessage != null) {
                request.setAttribute("message", flashMessage.toString());
                session.removeAttribute("flashMessage");
            }
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    private String resolveMode(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if ("/signup".equals(servletPath)) {
            return "signup";
        }
        if ("/login".equals(servletPath)) {
            return "login";
        }

        String mode = trim(request.getParameter("mode"));
        return "signup".equalsIgnoreCase(mode) ? "signup" : "login";
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
