/**
 * MeusPedidosPage - Painel do Comprador: histórico e status de pedidos
 *
 * Chama:
 *  - GET  /pedidos         → lista pedidos do comprador logado
 *  - PATCH /pedidos/:id/cancelar → cancela pedido
 */

import { useState, useEffect, useCallback } from "react";
import { pedidoApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Badge, Button, EmptyState, Spinner, ErrorMessage, Toast } from "../common";

function PedidoCard({ pedido, onCancelar }) {
  const podeCancelar = pedido.status === "PENDENTE";

  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex items-start justify-between gap-4">
      <div className="flex flex-col gap-1.5">
        <p className="font-bold text-gray-900">{pedido.loteProduto || `Lote #${pedido.loteId}`}</p>
        <p className="text-sm text-gray-500">
          Quantidade: <span className="font-medium text-gray-800">{pedido.quantidadeKg?.toLocaleString("pt-BR")} kg</span>
        </p>
        <p className="text-sm text-gray-500">
          Tipo: <span className="font-medium text-gray-800">{pedido.tipoEntrega === "ENTREGA" ? "🚚 Entrega" : "📍 Retirada"}</span>
        </p>
        {pedido.distanciaKm && (
          <p className="text-sm text-gray-500">
            Distância: <span className="font-medium text-gray-800">{pedido.distanciaKm?.toFixed(1)} km</span>
          </p>
        )}
        {pedido.status === "ACEITO" && pedido.tipoEntrega === "RETIRADA" && (
          <div className="mt-2 rounded-lg border border-green-100 bg-green-50 p-3 text-sm text-gray-700">
            <p className="font-semibold text-green-800">Dados para retirada</p>
            <p>Local: {pedido.latitudeRetirada}, {pedido.longitudeRetirada}</p>
            <p>Horário: {pedido.horarioRetirada || "A combinar com o produtor"}</p>
            {pedido.instrucoesRetirada && <p>Instruções: {pedido.instrucoesRetirada}</p>}
            <p className="mt-1 font-semibold">Código: {pedido.codigoRetirada}</p>
          </div>
        )}
        <p className="text-xs text-gray-400">
          Criado em: {pedido.dataCriacao ? new Date(pedido.dataCriacao).toLocaleDateString("pt-BR") : "—"}
        </p>
      </div>

      <div className="flex flex-col items-end gap-3 shrink-0">
        <Badge status={pedido.status} />
        {podeCancelar && (
          <Button variant="ghost" size="sm" onClick={() => onCancelar(pedido.id)}>
            Cancelar
          </Button>
        )}
      </div>
    </div>
  );
}

export function MeusPedidosPage() {
  const { token } = useAuth();
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [toast, setToast] = useState(null);

  const showToast = (msg, type = "success") => {
    setToast({ message: msg, type });
    setTimeout(() => setToast(null), 3500);
  };

  const fetchPedidos = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await pedidoApi.listar(token);
      setPedidos(data);
    } catch (err) {
      setError(err.message || "Erro ao carregar pedidos.");
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    fetchPedidos();
  }, [fetchPedidos]);

  async function handleCancelar(id) {
    if (!confirm("Cancelar este pedido?")) return;
    try {
      await pedidoApi.cancelar(id, token);
      setPedidos((p) => p.map((ped) => ped.id === id ? { ...ped, status: "CANCELADO" } : ped));
      showToast("Pedido cancelado.");
    } catch (err) {
      showToast(err.message || "Erro ao cancelar pedido.", "error");
    }
  }

  return (
    <div className="p-6 flex flex-col gap-6 max-w-3xl">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Meus Pedidos</h1>
        <p className="text-sm text-gray-500 mt-0.5">Acompanhe o status dos seus pedidos.</p>
      </div>

      <ErrorMessage message={error} />

      {loading ? (
        <Spinner />
      ) : pedidos.length === 0 ? (
        <EmptyState
          icon="🛒"
          title="Nenhum pedido realizado"
          description="Você ainda não fez nenhum pedido. Explore os lotes disponíveis!"
        />
      ) : (
        <div className="flex flex-col gap-3">
          {pedidos.map((p) => (
            <PedidoCard key={p.id} pedido={p} onCancelar={handleCancelar} />
          ))}
        </div>
      )}

      {toast && (
        <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />
      )}
    </div>
  );
}
