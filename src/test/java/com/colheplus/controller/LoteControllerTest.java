package com.colheplus.controller;

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
class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarEListarLoteNasRotasDoFrontend() throws Exception {
        String token = criarUsuarioELogar("produtor.lotes@example.com", "PRODUTOR");

        MvcResult result = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Milho Cancelado",
                                  "volumeDisponivelKg": 1000,
                                  "volumeMinimoViavelKg": 500,
                                  "precoPorKg": 2.4,
                                  "taxaFixaEntrega": 15,
                                  "raioMaximoEntregaKm": 50
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.produto").value("Milho Cancelado"))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andReturn();

        mockMvc.perform(get("/lotes/meus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].produto").value("Milho Cancelado"));

        Number loteId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        mockMvc.perform(patch("/lotes/" + loteId + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        mockMvc.perform(get("/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.produto == 'Milho Cancelado')]").isEmpty());
    }

    @Test
    void naoDevePermitirPedidoMaiorQueSaldoDisponivel() throws Exception {
        String produtorToken = criarUsuarioELogar("produtor.saldo@example.com", "PRODUTOR");
        String compradorToken = criarUsuarioELogar("comprador.saldo@example.com", "COMPRADOR");

        MvcResult result = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Milho",
                                  "volumeDisponivelKg": 1000,
                                  "volumeMinimoViavelKg": 1000,
                                  "precoPorKg": 2.4
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Number loteId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loteId": %d,
                                  "quantidadeKg": 990,
                                  "tipoEntrega": "RETIRADA"
                                }
                                """.formatted(loteId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loteId": %d,
                                  "quantidadeKg": 100,
                                  "tipoEntrega": "RETIRADA"
                                }
                                """.formatted(loteId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(
                        "Pedido ultrapassa o volume disponível do lote. Disponível para compra: 10.0 kg"));

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loteId": %d,
                                  "quantidadeKg": 10,
                                  "tipoEntrega": "RETIRADA"
                                }
                                """.formatted(loteId)))
                .andExpect(status().isOk());
    }

    @Test
    void deveCancelarPedido() throws Exception {
        String produtorToken = criarUsuarioELogar("produtor.cancelamento@example.com", "PRODUTOR");
        String compradorToken = criarUsuarioELogar("comprador.cancelamento@example.com", "COMPRADOR");

        MvcResult lote = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Milho",
                                  "volumeDisponivelKg": 1000,
                                  "volumeMinimoViavelKg": 1000,
                                  "precoPorKg": 2.4
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Number loteId = JsonPath.read(lote.getResponse().getContentAsString(), "$.id");

        MvcResult pedido = mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loteId": %d,
                                  "quantidadeKg": 10,
                                  "tipoEntrega": "RETIRADA"
                                }
                                """.formatted(loteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn();

        Number pedidoId = JsonPath.read(pedido.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/pedidos/" + pedidoId + "/cancelar")
                        .header("Authorization", "Bearer " + compradorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));
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
