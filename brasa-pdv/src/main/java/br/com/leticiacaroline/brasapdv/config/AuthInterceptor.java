package br.com.leticiacaroline.brasapdv.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import br.com.leticiacaroline.brasapdv.usuario.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Barra qualquer chamada da API feita por quem não fez login. */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        HttpSession sessao = req.getSession(false);
        if (sessao != null && sessao.getAttribute(AuthController.SESSAO_USUARIO) != null) {
            return true;
        }
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"erro\":\"Faça login para continuar\"}");
        return false;
    }
}
