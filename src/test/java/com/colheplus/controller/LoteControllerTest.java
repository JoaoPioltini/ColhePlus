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
        mockMvc.perform(patch("/lotes/" + loteId + "/cancelar")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        mockMvc.perform(get("/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.produto == 'Milho Cancelado')]").isEmpty());
    }

    @Test
    void deveExigirAceiteEPermitirApenasProdutorCriarLote() throws Exception {
        String compradorToken = criarUsuarioELogar("comprador.lote.negado@example.com", "COMPRADOR");
        String produtorSemAceite = criarUsuarioELogarSemAceite("produtor.sem.aceite@example.com", "PRODUTOR");

        mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorSemAceite)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Tomate",
                                  "volumeDisponivelKg": 100,
                                  "volumeMinimoViavelKg": 50,
                                  "precoPorKg": 4.2
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Aceite o termo atual antes de continuar"));

        mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Tomate",
                                  "volumeDisponivelKg": 100,
                                  "volumeMinimoViavelKg": 50,
                                  "precoPorKg": 4.2
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Papel sem permissão para esta ação"));
    }

    @Test
    void naoDeveCriarLoteComProdutoNumerico() throws Exception {
        String token = criarUsuarioELogar("produtor.produto.numerico@example.com", "PRODUTOR");

        mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "12345",
                                  "volumeDisponivelKg": 100,
                                  "volumeMinimoViavelKg": 50,
                                  "precoPorKg": 4.2
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Produto deve conter letras"));
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDENTE"));

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEITO"));
    }

    @Test
    void deveValidarEntregaDentroDoRaio() throws Exception {
        String produtorToken = criarUsuarioELogar("produtor.entrega@example.com", "PRODUTOR");
        String compradorToken = criarUsuarioELogar("comprador.entrega@example.com", "COMPRADOR");

        MvcResult lote = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Alface",
                                  "volumeDisponivelKg": 1000,
                                  "volumeMinimoViavelKg": 800,
                                  "precoPorKg": 3.1,
                                  "modalidadeEntrega": "RETIRADA_E_ENTREGA",
                                  "taxaFixaEntrega": 12,
                                  "raioMaximoEntregaKm": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Number loteId = JsonPath.read(lote.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loteId": %d,
                                  "quantidadeKg": 10,
                                  "tipoEntrega": "ENTREGA",
                                  "latitudeEntrega": -23.55,
                                  "longitudeEntrega": -46.63
                                }
                                """.formatted(loteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanciaKm").isNumber());

        mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + compradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loteId": %d,
                                  "quantidadeKg": 10,
                                  "tipoEntrega": "ENTREGA",
                                  "latitudeEntrega": -22.90,
                                  "longitudeEntrega": -43.20
                                }
                                """.formatted(loteId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Local de entrega fora do raio do lote"));
    }

    @Test
    void naoDeveCancelarLoteAtivado() throws Exception {
        String produtorToken = criarUsuarioELogar("produtor.ativado@example.com", "PRODUTOR");
        String compradorToken = criarUsuarioELogar("comprador.ativado@example.com", "COMPRADOR");

        MvcResult lote = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Batata",
                                  "volumeDisponivelKg": 100,
                                  "volumeMinimoViavelKg": 10,
                                  "precoPorKg": 2.7
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
                .andReturn();

        Number pedidoId = JsonPath.read(pedido.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/lotes/" + loteId + "/cancelar")
                        .header("Authorization", "Bearer " + produtorToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Apenas lotes abertos podem ser alterados"));

        mockMvc.perform(patch("/pedidos/" + pedidoId + "/cancelar")
                        .header("Authorization", "Bearer " + compradorToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Pedidos de lote ativado não podem ser alterados"));
    }

    @Test
    void deveConfirmarRetiradaComCodigoDoPedidoAceito() throws Exception {
        String produtorToken = criarUsuarioELogar("produtor.retirada@example.com", "PRODUTOR");
        String compradorToken = criarUsuarioELogar("comprador.retirada@example.com", "COMPRADOR");

        MvcResult lote = mockMvc.perform(post("/lotes")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "produto": "Cenoura",
                                  "volumeDisponivelKg": 100,
                                  "volumeMinimoViavelKg": 10,
                                  "precoPorKg": 3.4,
                                  "horarioRetirada": "Segunda, 09h às 12h",
                                  "instrucoesRetirada": "Portão verde"
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
                .andExpect(jsonPath("$.status").value("ACEITO"))
                .andExpect(jsonPath("$.codigoRetirada").exists())
                .andExpect(jsonPath("$.latitudeRetirada").value(-23.55))
                .andExpect(jsonPath("$.longitudeRetirada").value(-46.63))
                .andExpect(jsonPath("$.horarioRetirada").value("Segunda, 09h às 12h"))
                .andReturn();

        Number pedidoId = JsonPath.read(pedido.getResponse().getContentAsString(), "$.id");
        String codigo = JsonPath.read(pedido.getResponse().getContentAsString(), "$.codigoRetirada");

        mockMvc.perform(patch("/pedidos/" + pedidoId + "/retirada")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "000000"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Código de retirada inválido"));

        mockMvc.perform(patch("/pedidos/" + pedidoId + "/retirada")
                        .header("Authorization", "Bearer " + produtorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "%s"
                                }
                                """.formatted(codigo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETIRADO"));
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
        String token = criarUsuarioELogarSemAceite(email, papel);

        mockMvc.perform(post("/auth/aceite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        return token;
    }

    private String criarUsuarioELogarSemAceite(String email, String papel) throws Exception {
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
