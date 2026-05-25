package com.colheplus.service;

import com.colheplus.dto.AuthResponse;
import com.colheplus.dto.LoginRequest;
import com.colheplus.model.Usuario;
import com.colheplus.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TermoService termoService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, TermoService termoService) {
        this.usuarioRepository = usuarioRepository;
        this.termoService = termoService;
    }

    public Usuario cadastrar(Usuario usuario) {
        validarCadastro(usuario);
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        usuario.setTermoAceito(false);
        usuario.setDataHoraAceiteTermo(null);
        usuario.setVersaoTermoAceita(null);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(request.getEmail()))
                .orElseThrow(() -> erroCredenciais());

        if (!senhaConfere(request.getSenha(), usuario.getSenhaHash())) {
            throw erroCredenciais();
        }
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário inativo");
        }

        usuario.setTokenSessao(UUID.randomUUID().toString());
        Usuario salvo = usuarioRepository.save(usuario);
        return new AuthResponse(salvo.getTokenSessao(), salvo);
    }

    public void aceitarTermo(String authorizationHeader) {
        Usuario usuario = buscarPorAuthorizationHeader(authorizationHeader);
        String versaoAtual = termoService.buscarTermoAtual().getVersao();
        usuario.setTermoAceito(true);
        usuario.setDataHoraAceiteTermo(LocalDateTime.now());
        usuario.setVersaoTermoAceita(versaoAtual);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPerfil(String authorizationHeader) {
        return buscarPorAuthorizationHeader(authorizationHeader);
    }

    public Usuario atualizarPerfil(String authorizationHeader, Usuario dados) {
        Usuario usuario = buscarPorAuthorizationHeader(authorizationHeader);
        if (campoVazio(dados.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        usuario.setNome(dados.getNome().trim());
        if ("PRODUTOR".equals(usuario.getPapel())) {
            if (dados.getLatitude() == null || dados.getLongitude() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produtor precisa informar localização");
            }
            usuario.setLatitude(dados.getLatitude());
            usuario.setLongitude(dados.getLongitude());
        }
        if (!campoVazio(dados.getSenhaHash())) {
            usuario.setSenhaHash(passwordEncoder.encode(dados.getSenhaHash()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
        }
        String token = authorizationHeader.substring("Bearer ".length());
        Usuario usuario = usuarioRepository.findByTokenSessao(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido"));
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário inativo");
        }
        return usuario;
    }

    public Usuario autenticarComTermo(String authorizationHeader, String... papeis) {
        Usuario usuario = buscarPorAuthorizationHeader(authorizationHeader);
        exigirTermoAceito(usuario);
        exigirPapel(usuario, papeis);
        return usuario;
    }

    public void exigirPapel(Usuario usuario, String... papeis) {
        if (papeis.length > 0 && Arrays.stream(papeis).noneMatch(papel -> papel.equals(usuario.getPapel()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Papel sem permissão para esta ação");
        }
    }

    public void exigirTermoAceito(Usuario usuario) {
        String versaoAtual = termoService.buscarTermoAtual().getVersao();
        if (!Boolean.TRUE.equals(usuario.getTermoAceito()) || !versaoAtual.equals(usuario.getVersaoTermoAceita())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aceite o termo atual antes de continuar");
        }
    }

    private void validarCadastro(Usuario usuario) {
        if (campoVazio(usuario.getNome()) || campoVazio(usuario.getEmail()) || campoVazio(usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, e-mail e senha são obrigatórios");
        }
        if (usuarioRepository.existsByEmail(normalizarEmail(usuario.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if (!"PRODUTOR".equals(usuario.getPapel()) && !"COMPRADOR".equals(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Papel de cadastro inválido");
        }
        if ("PRODUTOR".equals(usuario.getPapel())
                && (usuario.getLatitude() == null || usuario.getLongitude() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produtor precisa informar localização");
        }
    }

    private boolean campoVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private ResponseStatusException erroCredenciais() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    private boolean senhaConfere(String senha, String hashSalvo) {
        if (hashSalvo != null && hashSalvo.startsWith("$2")) {
            return passwordEncoder.matches(senha, hashSalvo);
        }
        return hashSalvo != null && hashSalvo.equals(senha);
    }
}
