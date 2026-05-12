/**
 * LotesDisponiveisPage - Painel do Comprador: navegar lotes e fazer pedidos
 *
 * Fluxo (Sequência - Pedido e Ativação de Lote):
 *  POST /pedidos → PedidoController → PedidoService → salva pedido
 *               → verifica se volume mínimo foi atingido
 *               → se sim, atualiza lote para ATIVADO
 *
 * Fluxo (Sequência - Validação de Entrega):
 *  O backend valida se o comprador está dentro do raio usando GeolocalizacaoService
 */

import { useState, useEffect, useCallback } from "react";
import { loteApi, pedidoApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Button, Input, Modal, Spinner, EmptyState, ErrorMessage, Toast, Badge, ProgressBar } from "../common";

// ─── Modal de fazer pedido ────────────────────────────────────────────────────
function FazerPedidoModal({ lote, isOpen, onClose, onSuccess }) {
  const { token } = useAuth();
  const [form, setForm] = useState({
    quantidadeKg: "",
    tipoEntrega: "RETIRADA", // RETIRADA | ENTREGA
    latitudeEntrega: "",
    longitudeEntrega: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function set(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleSubmit() {
    if (!form.quantidadeKg || parseFloat(form.quantidadeKg) <= 0) {
      setError("Informe uma quantidade válida.");
      return;
    }
    if (form.tipoEntrega === "ENTREGA" && (!form.latitudeEntrega || !form.longitudeEntrega)) {
      setError("Informe sua localização para entrega.");
      return;
    }

    setError("");
    setLoading(true);

    const payload = {
      loteId: lote.id,
      quantidadeKg: parseFloat(form.quantidadeKg),
      tipoEntrega: form.tipoEntrega,
      latitudeEntrega: form.tipoEntrega === "ENTREGA" ? parseFloat(form.latitudeEntrega) : null,
      longitudeEntrega: form.tipoEntrega === "ENTREGA" ? parseFloat(form.longitudeEntrega) : null,
    };

    try {
      const pedido = await pedidoApi.criar(payload, token);
      onSuccess(pedido);
      onClose();
    } catch (err) {
      setError(err.message || "Erro ao realizar pedido.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Pedido - ${lote?.produto}`}>
      <div className="flex flex-col gap-4">
        {/* Resumo do lote */}
        <div className="bg-green-50 rounded-xl p-4 text-sm">
          <p className="text-gray-600">
            <span className="font-medium">Preço:</span>{" "}
            R$ {lote?.precoPorKg?.toFixed(2).replace(".", ",")} / kg
          </p>
          {lote?.taxaFixaEntrega && (
            <p className="text-gray-600">
              <span className="font-medium">Taxa de entrega:</span>{" "}
              R$ {lote?.taxaFixaEntrega?.toFixed(2)} (raio de {lote?.raioMaximoEntregaKm} km)
            </p>
          )}
        </div>

        <Input
          label="Quantidade (kg)"
          type="number"
          placeholder="0"
          value={form.quantidadeKg}
          onChange={(e) => set("quantidadeKg", e.target.value)}
          required
        />

        {/* Tipo de entrega */}
        <div>
          <label className="text-sm font-medium text-gray-700 block mb-2">Tipo de entrega</label>
          <div className="grid grid-cols-2 gap-3">
            {["RETIRADA", "ENTREGA"].map((tipo) => (
              <button
                key={tipo}
                type="button"
                disabled={tipo === "ENTREGA" && !lote?.taxaFixaEntrega}
                onClick={() => set("tipoEntrega", tipo)}
                className={`py-2.5 rounded-xl border-2 text-sm font-semibold transition-all disabled:opacity-40 disabled:cursor-not-allowed ${
                  form.tipoEntrega === tipo
                    ? "border-green-700 bg-green-50 text-green-800"
                    : "border-gray-200 text-gray-500 hover:border-gray-300"
                }`}
              >
                {tipo === "RETIRADA" ? "📍 Retirada" : "🚚 Entrega"}
              </button>
            ))}
          </div>
        </div>

        {/* Localização entrega */}
        {form.tipoEntrega === "ENTREGA" && (
          <div className="bg-blue-50 rounded-xl p-4 flex flex-col gap-3">
            <p className="text-sm text-blue-700 font-medium">Sua localização para entrega:</p>
            <div className="grid grid-cols-2 gap-3">
              <Input
                placeholder="Latitude"
                type="number"
                value={form.latitudeEntrega}
                onChange={(e) => set("latitudeEntrega", e.target.value)}
              />
              <Input
                placeholder="Longitude"
                type="number"
                value={form.longitudeEntrega}
                onChange={(e) => set("longitudeEntrega", e.target.value)}
              />
            </div>
            <p className="text-xs text-blue-600">
              O sistema verificará se você está dentro do raio de {lote?.raioMaximoEntregaKm} km.
            </p>
          </div>
        )}

        {/* Cálculo estimado */}
        {form.quantidadeKg && lote?.precoPorKg && (
          <div className="bg-gray-50 rounded-xl p-3 text-sm">
            <p className="text-gray-600">
              Estimativa:{" "}
              <span className="font-bold text-gray-900">
                R${" "}
                {(
                  parseFloat(form.quantidadeKg) * lote.precoPorKg +
                  (form.tipoEntrega === "ENTREGA" && lote.taxaFixaEntrega ? lote.taxaFixaEntrega : 0)
                )
                  .toFixed(2)
                  .replace(".", ",")}
              </span>
            </p>
          </div>
        )}

        <ErrorMessage message={error} />

        <div className="flex gap-3">
          <Button variant="secondary" onClick={onClose} fullWidth>Cancelar</Button>
          <Button onClick={handleSubmit} loading={loading} fullWidth>Confirmar Pedido</Button>
        </div>
      </div>
    </Modal>
  );
}

// ─── Card de lote para comprador ──────────────────────────────────────────────
function LotePublicoCard({ lote, onFazerPedido }) {
  const totalDisponivel = lote.volumeDisponivelKg || 0;
  const volumeAgrupado = lote.volumeAgrupado || 0;
  const disponivelCompra = Math.max(totalDisponivel - volumeAgrupado, 0);
  const podeComprar = lote.status === "ABERTO" && disponivelCompra > 0;

  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex flex-col gap-4">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="text-lg font-bold text-gray-900">{lote.produto}</h3>
          <p className="text-xs text-gray-400">
            Produtor: {lote.produtorNome || "—"}
          </p>
        </div>
        <Badge status={lote.status} />
      </div>

      <div className="grid grid-cols-2 gap-2 text-sm">
        <div>
          <p className="text-xs text-gray-500">Disponível para compra</p>
          <p className="font-semibold">{disponivelCompra.toLocaleString("pt-BR")} kg</p>
          <p className="text-xs text-gray-400">
            de {totalDisponivel.toLocaleString("pt-BR")} kg ofertados
          </p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Preço / kg</p>
          <p className="font-semibold text-green-800">
            R$ {lote.precoPorKg?.toFixed(2).replace(".", ",")}
          </p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Entrega</p>
          <p className="font-semibold">{lote.taxaFixaEntrega ? "Disponível" : "Somente retirada"}</p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Raio máx.</p>
          <p className="font-semibold">
            {lote.raioMaximoEntregaKm ? `${lote.raioMaximoEntregaKm} km` : "—"}
          </p>
        </div>
      </div>

      {/* Progresso */}
      <div>
        <ProgressBar value={lote.volumeAgrupado || 0} max={lote.volumeMinimoViavelKg} />
        <p className="text-xs text-gray-500 mt-1">
          {(lote.volumeAgrupado || 0).toLocaleString("pt-BR")} kg de{" "}
          {lote.volumeMinimoViavelKg?.toLocaleString("pt-BR")} kg mínimos agrupados
        </p>
      </div>

      <Button onClick={() => onFazerPedido(lote)} size="sm" fullWidth disabled={!podeComprar}>
        {podeComprar ? "Fazer Pedido" : "Indisponível"}
      </Button>
    </div>
  );
}

// ─── Página principal ─────────────────────────────────────────────────────────
export function LotesDisponiveisPage() {
  const { token } = useAuth();
  const [lotes, setLotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [loteSelecionado, setLoteSelecionado] = useState(null);
  const [toast, setToast] = useState(null);
  const [filtro, setFiltro] = useState("");

  const showToast = (msg, type = "success") => {
    setToast({ message: msg, type });
    setTimeout(() => setToast(null), 3500);
  };

  const fetchLotes = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await loteApi.listarAbertos(token);
      setLotes(data);
    } catch (err) {
      setError(err.message || "Erro ao carregar lotes.");
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    fetchLotes();
  }, [fetchLotes]);

  const lotesFiltrados = lotes.filter((l) =>
    l.produto?.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div className="p-6 flex flex-col gap-6 max-w-5xl">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Lotes Disponíveis</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Encontre produtos agrícolas e agrupe sua demanda com outros compradores.
        </p>
      </div>

      <Input
        placeholder="🔍 Buscar por produto..."
        value={filtro}
        onChange={(e) => setFiltro(e.target.value)}
      />

      <ErrorMessage message={error} />

      {loading ? (
        <Spinner />
      ) : lotesFiltrados.length === 0 ? (
        <EmptyState
          icon="🌾"
          title="Nenhum lote disponível"
          description="Não há lotes abertos no momento. Volte em breve!"
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {lotesFiltrados.map((lote) => (
            <LotePublicoCard
              key={lote.id}
              lote={lote}
              onFazerPedido={setLoteSelecionado}
            />
          ))}
        </div>
      )}

      {loteSelecionado && (
        <FazerPedidoModal
          lote={loteSelecionado}
          isOpen={!!loteSelecionado}
          onClose={() => setLoteSelecionado(null)}
          onSuccess={() => {
            showToast("Pedido realizado com sucesso! 🛒");
            fetchLotes();
          }}
        />
      )}

      {toast && (
        <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />
      )}
    </div>
  );
}
