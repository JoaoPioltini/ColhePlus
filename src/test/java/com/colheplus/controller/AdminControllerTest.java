package com.colheplus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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

        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(delete("/admin/usuarios/" + usuarioId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveDesativarLote() throws Exception {
        MvcResult lote = mockMvc.perform(post("/lotes")
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

        mockMvc.perform(patch("/admin/lotes/" + loteId + "/desativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DESATIVADO"));
    }
}
