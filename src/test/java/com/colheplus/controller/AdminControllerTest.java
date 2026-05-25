package com.colheplus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.colheplus.model.Usuario;
import com.colheplus.repository.UsuarioRepository;
import com.colheplus.service.TermoService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TermoService termoService;

    @Test
    void deveListarEExcluirUsuarios() throws Exception {
        MvcResult usuario = mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Usuário Admin Teste",
                                  "email": "admin.usuario@example.com",
                                  "senhaHash": "123456",
                                  "papel": "COMPRADOR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Number usuarioId = JsonPath.read(usuario.getResponse().getContentAsString(), "$.id");
        String adminToken = criarAdminELogar("admin.lista@example.com");

        mockMvc.perform(get("/admin/usuarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(delete("/admin/usuarios/" + usuarioId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveDesativarLote() throws Exception {
        String produtorToken = criarUsuarioELogar("produtor.admin.lote@example.com", "PRODUTOR");
        String adminToken = criarAdminELogar("admin.lotes@example.com");

        MvcResult lote = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Milho",
                                  "volumeDisponivelKg": 1000,
                                  "volumeMinimoViavelKg": 500,
                                  "precoPorKg": 2.4
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Number loteId = JsonPath.read(lote.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/admin/lotes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %d)]".formatted(loteId)).exists());

        mockMvc.perform(patch("/admin/lotes/" + loteId + "/desativar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DESATIVADO"));
    }

    private String criarUsuarioELogar(String email, String papel) throws Exception {
        mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Usuário Teste",
                                  "email": "%s",
                                  "senhaHash": "123456",
                                  "papel": "%s",
                                  "latitude": -23.55,
                                  "longitude": -46.63
                                }
                                """.formatted(email, papel)))
                .andExpect(status().isCreated());

        String token = login(email);
        mockMvc.perform(post("/auth/aceite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        return token;
    }

    private String criarAdminELogar(String email) throws Exception {
        Usuario admin = new Usuario();
        admin.setNome("Admin Teste");
        admin.setEmail(email);
        admin.setSenhaHash("123456");
        admin.setPapel("ADMIN");
        admin.setAtivo(true);
        admin.setTermoAceito(true);
        admin.setVersaoTermoAceita(termoService.buscarTermoAtual().getVersao());
        admin.setDataHoraAceiteTermo(LocalDateTime.now());
        usuarioRepository.save(admin);
        return login(email);
    }

    private String login(String email) throws Exception {
        MvcResult login = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "senha": "123456"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(login.getResponse().getContentAsString(), "$.token");
    }
}
