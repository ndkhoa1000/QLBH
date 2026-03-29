package ctu.cit.servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/signup",
            "/auth",
            "/index.jsp",
            "/login.jsp",
            "/signup.jsp"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = getRequestPath(httpRequest);
        HttpSession session = httpRequest.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("currentUser") != null;
        boolean rootRequest = path.isEmpty() || "/".equals(path);
        boolean publicRequest = rootRequest
                || PUBLIC_PATHS.contains(path)
                || path.startsWith("/rest/")
                || path.startsWith("/assets/");

        if (!loggedIn && !publicRequest) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (loggedIn && (rootRequest || PUBLIC_PATHS.contains(path))) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/hoadon");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getRequestPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}
