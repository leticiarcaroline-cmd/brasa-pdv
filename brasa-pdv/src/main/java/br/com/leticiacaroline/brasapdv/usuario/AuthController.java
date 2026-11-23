package br.com.leticiacaroline.brasapdv.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Nome do atributo guardado na sessão quando o login dá certo. */
    public static final String SESSAO_USUARIO = "usuarioId";

    public record RegistroRequest(
            @NotBlank(message = "Informe o nome") @Size(max = 80, message = "Nome muito longo") String nome,
            @NotBlank(message = "Informe o usuário")
            @Size(min = 3, max = 30, message = "O usuário deve ter de 3 a 30 caracteres") String username,
            @NotBlank(message = "Informe a senha")
            @Size(min = 6, max = 72, message = "A senha deve ter de 6 a 72 caracteres") String senha) {
    }

    public record LoginRequest(
            @NotBlank(message = "Informe o usuário") String username,
            @NotBlank(message = "Informe a senha") String senha) {
    }

    public record UsuarioResponse(Long id, String nome, String username) {
        static UsuarioResponse de(Usuario u) {
            return new UsuarioResponse(u.getId(), u.getNome(), u.getUsername());
        }
    }

    private final UsuarioRepository usuarios;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest req, HttpServletRequest http) {
        String username = req.username().trim().toLowerCase();
        if (usuarios.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esse usuário já existe");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setUsername(username);
        u.setSenhaHash(encoder.encode(req.senha()));
        usuarios.save(u);
        iniciarSessao(http, u);
        return UsuarioResponse.de(u);
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        Usuario u = usuarios.findByUsername(req.username().trim().toLowerCase())
                .filter(x -> encoder.matches(req.senha(), x.getSenhaHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário ou senha inválidos"));
        iniciarSessao(http, u);
        return UsuarioResponse.de(u);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest http) {
        HttpSession sessao = http.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
    }

    /** Diz quem está logado (a tela usa isso ao abrir a página). */
    @GetMapping("/eu")
    public UsuarioResponse eu(HttpServletRequest http) {
        HttpSession sessao = http.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute(SESSAO_USUARIO);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Você não está logado");
        }
        return usuarios.findById((Long) id)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida"));
    }

    private void iniciarSessao(HttpServletRequest http, Usuario u) {
        http.getSession(true);
        http.changeSessionId(); // evita fixação de sessão
        http.getSession().setAttribute(SESSAO_USUARIO, u.getId());
    }
}
