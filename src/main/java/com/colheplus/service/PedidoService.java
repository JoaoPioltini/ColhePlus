package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.model.Pedido;
import com.colheplus.model.Usuario;
import com.colheplus.repository.LoteRepository;
import com.colheplus.repository.PedidoRepository;
import com.colheplus.repository.UsuarioRepository;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final GeolocalizacaoService geolocalizacaoService;
    private final SecureRandom secureRandom = new SecureRandom();

    public PedidoService(PedidoRepository pedidoRepository, LoteRepository loteRepository,
            UsuarioRepository usuarioRepository, AuthService authService,
            GeolocalizacaoService geolocalizacaoService) {
        this.pedidoRepository = pedidoRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
        this.geolocalizacaoService = geolocalizacaoService;
    }

    public Pedido criarPedido(Pedido pedido, String authorizationHeader) {
        Usuario comprador = authService.autenticarComTermo(authorizationHeader, "COMPRADOR");
        if (pedido.getLoteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LoteId não pode ser null");
        }
        if (pedido.getQuantidadeKg() == null || pedido.getQuantidadeKg() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }

        Lote lote = loteRepository.findById(pedido.getLoteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));

        if (!"ABERTO".equals(lote.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este lote não está aberto para pedidos");
        }

        double soma = calcularVolumeAgrupado(lote.getId());
        double somaFutura = soma + pedido.getQuantidadeKg();

        if (somaFutura > lote.getVolumeDisponivelKg()) {
            double disponivel = lote.getVolumeDisponivelKg() - soma;
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido ultrapassa o volume disponível do lote. Disponível para compra: " + disponivel + " kg");
        }

        validarEntrega(pedido, lote);
        preencherDadosPedido(pedido, lote, comprador);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        lote.setVolumeAgrupado(somaFutura);
        pedidoSalvo = ativarLoteSeViavel(lote, somaFutura, pedidoSalvo);
        loteRepository.save(lote);
        return pedidoSalvo;
    }

    public List<Pedido> listarPedidos(String authorizationHeader) {
        Usuario usuario = authService.autenticarComTermo(authorizationHeader, "PRODUTOR", "COMPRADOR", "ADMIN");
        List<Pedido> pedidos = pedidoRepository.findAll();
        if ("PRODUTOR".equals(usuario.getPapel())) {
            return pedidos.stream()
                    .filter(pedido -> pedidoPertenceAoProdutor(pedido, usuario.getId()))
                    .toList();
        }
        if ("COMPRADOR".equals(usuario.getPapel())) {
            return pedidos.stream()
                    .filter(pedido -> usuario.getId().equals(pedido.getCompradorId()) || pedido.getCompradorId() == null)
                    .toList();
        }
        return pedidos;
    }

    public Pedido cancelarPedido(Long id, String authorizationHeader) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        Usuario usuario = authService.autenticarComTermo(authorizationHeader, "COMPRADOR");
        if (!usuario.getId().equals(pedido.getCompradorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pedido pertence a outro comprador");
        }
        Lote lote = loteRepository.findById(pedido.getLoteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        if (!"ABERTO".equals(lote.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedidos de lote ativado não podem ser alterados");
        }
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos pendentes podem ser cancelados");
        }
        pedido.setStatus("CANCELADO");
        Pedido salvo = pedidoRepository.save(pedido);
        recalcularLote(pedido.getLoteId());
        return salvo;
    }

    public Pedido confirmarRetirada(Long id, String codigo, String authorizationHeader) {
        Usuario produtor = authService.autenticarComTermo(authorizationHeader, "PRODUTOR");
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        Lote lote = loteRepository.findById(pedido.getLoteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        if (!produtor.getId().equals(lote.getProdutorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pedido pertence a outro produtor");
        }
        if (!"RETIRADA".equals(pedido.getTipoEntrega())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não é para retirada");
        }
        if (!"ACEITO".equals(pedido.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos aceitos podem ser retirados");
        }
        if (codigo == null || !codigo.trim().equals(pedido.getCodigoRetirada())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de retirada inválido");
        }
        pedido.setStatus("RETIRADO");
        return pedidoRepository.save(pedido);
    }

    private double calcularVolumeAgrupado(Long loteId) {
        double soma = 0;
        for (Pedido pedido : pedidoRepository.findByLoteId(loteId)) {
            if (!"CANCELADO".equals(pedido.getStatus())) {
                soma += pedido.getQuantidadeKg();
            }
        }
        return soma;
    }

    private Pedido ativarLoteSeViavel(Lote lote, double somaFutura, Pedido pedido) {
        if (somaFutura < lote.getVolumeMinimoViavelKg()) {
            return pedido;
        }
        lote.setStatus("ATIVADO");
        confirmarPedidosDoLote(lote.getId());
        pedido.setStatus("ACEITO");
        preencherRetirada(pedido);
        return pedidoRepository.save(pedido);
    }

    private void preencherDadosPedido(Pedido pedido, Lote lote, Usuario comprador) {
        pedido.setStatus("PENDENTE");
        pedido.setLoteProduto(lote.getProduto());
        pedido.setCompradorId(comprador.getId());
        pedido.setCompradorNome(comprador.getNome());
    }

    private void confirmarPedidosDoLote(Long loteId) {
        for (Pedido pedido : pedidoRepository.findByLoteId(loteId)) {
            if (!"CANCELADO".equals(pedido.getStatus())) {
                pedido.setStatus("ACEITO");
                preencherRetirada(pedido);
                pedidoRepository.save(pedido);
            }
        }
    }

    private void preencherRetirada(Pedido pedido) {
        if (!"RETIRADA".equals(pedido.getTipoEntrega())) {
            return;
        }
        Lote lote = loteRepository.findById(pedido.getLoteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        Usuario produtor = usuarioRepository.findById(lote.getProdutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produtor do lote não encontrado"));
        pedido.setLatitudeRetirada(produtor.getLatitude());
        pedido.setLongitudeRetirada(produtor.getLongitude());
        pedido.setHorarioRetirada(lote.getHorarioRetirada());
        pedido.setInstrucoesRetirada(lote.getInstrucoesRetirada());
        if (pedido.getCodigoRetirada() == null) {
            pedido.setCodigoRetirada("%06d".formatted(secureRandom.nextInt(1_000_000)));
        }
    }

    private boolean pedidoPertenceAoProdutor(Pedido pedido, Long produtorId) {
        return loteRepository.findById(pedido.getLoteId())
                .map(lote -> produtorId.equals(lote.getProdutorId()))
                .orElse(false);
    }

    private void validarEntrega(Pedido pedido, Lote lote) {
        if (pedido.getTipoEntrega() == null || "RETIRADA".equals(pedido.getTipoEntrega())) {
            if (!aceitaRetirada(lote)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este lote exige entrega");
            }
            pedido.setTipoEntrega("RETIRADA");
            pedido.setLatitudeEntrega(null);
            pedido.setLongitudeEntrega(null);
            pedido.setDistanciaKm(null);
            return;
        }
        if (!"ENTREGA".equals(pedido.getTipoEntrega())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de entrega inválido");
        }
        if (!aceitaEntrega(lote)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este lote não oferece entrega");
        }
        if (pedido.getLatitudeEntrega() == null || pedido.getLongitudeEntrega() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Localização de entrega é obrigatória");
        }
        Usuario produtor = usuarioRepository.findById(lote.getProdutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produtor do lote não encontrado"));
        if (produtor.getLatitude() == null || produtor.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produtor do lote não possui localização");
        }
        double distancia = geolocalizacaoService.calcularDistanciaKm(
                produtor.getLatitude(), produtor.getLongitude(),
                pedido.getLatitudeEntrega(), pedido.getLongitudeEntrega());
        if (distancia > lote.getRaioMaximoEntregaKm()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Local de entrega fora do raio do lote");
        }
        pedido.setDistanciaKm(distancia);
    }

    private boolean aceitaEntrega(Lote lote) {
        return "ENTREGA".equals(lote.getModalidadeEntrega())
                || "RETIRADA_E_ENTREGA".equals(lote.getModalidadeEntrega());
    }

    private boolean aceitaRetirada(Lote lote) {
        return !"ENTREGA".equals(lote.getModalidadeEntrega());
    }

    private void recalcularLote(Long loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        double soma = calcularVolumeAgrupado(loteId);
        lote.setVolumeAgrupado(soma);
        if ("ATIVADO".equals(lote.getStatus()) && soma < lote.getVolumeMinimoViavelKg()) {
            lote.setStatus("ABERTO");
        }
        loteRepository.save(lote);
    }
}
