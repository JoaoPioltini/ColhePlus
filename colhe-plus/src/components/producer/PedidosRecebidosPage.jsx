/**
 * PedidosRecebidosPage - Painel do Produtor: pedidos recebidos nos seus lotes
 *
 * Chama:
 *  - GET /pedidos → filtra pedidos dos lotes do produtor
 *    (o backend pode ter um endpoint específico como GET /pedidos/recebidos)
 *
 * NOTA PARA INTEGRAÇÃO: Verifique com o backend se o endpoint retorna
 * apenas os pedidos dos lotes do produtor logado, ou se é necessário filtrar.
 */

import { useState, useEffect, useCallback } from "react";
import { pedidoApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Badge, EmptyState, Spinner, ErrorMessage } from "../common";

function PedidoRow({ pedido }) {
  return (
    <div className="flex items-center justify-between py-4 border-b border-gray-50 last:border-0">
      <div className="flex flex-col gap-0.5">
        <p className="font-semibold text-gray-900">{pedido.loteProduto || `Lote #${pedido.loteId}`}</p>
        <p className="text-sm text-gray-500">
          Comprador: {pedido.compradorNome || "—"}
        </p>
        <p className="text-sm text-gray-500">
          Quantidade: {pedido.quantidadeKg?.toLocaleString("pt-BR")} kg
        </p>
        <p className="text-sm text-gray-500">
          Entrega: {pedido.tipoEntrega === "ENTREGA"
            ? `Sim (${pedido.distanciaKm?.toFixed(0)} km)`
            : "Não (Retirada)"}
        </p>
      </div>
      <div className="flex flex-col items-end gap-2">
        <Badge status={pedido.status} />
        {pedido.status === "PENDENTE" && (
          <p className="text-xs text-gray-400">Aguardando ativação do lote</p>
        )}
        {pedido.status === "ACEITO" && (
          <p className="text-xs text-gray-500">Pedido confirmado</p>
        )}
        {pedido.status === "RECUSADO" && (
          <p className="text-xs text-gray-500">Pedido rejeitado</p>
        )}
      </div>
    </div>
  );
}

export function PedidosRecebidosPage() {
  const { token } = useAuth();
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchPedidos = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      // INTEGRAÇÃO: Se o backend tiver endpoint específico para produtor,
      // troque por: request("GET", "/pedidos/recebidos", null, token)
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

  return (
    <div className="p-6 flex flex-col gap-6 max-w-3xl">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Pedidos Recebidos</h1>
        <p className="text-sm text-gray-500 mt-0.5">
          Acompanhe todos os pedidos feitos nos seus lotes.
        </p>
      </div>

      <ErrorMessage message={error} />

      {loading ? (
        <Spinner />
      ) : pedidos.length === 0 ? (
        <EmptyState
          icon="🌱"
          title="Nenhum novo pedido no momento"
          description="Quando compradores fizerem pedidos nos seus lotes, eles aparecerão aqui."
        />
      ) : (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm divide-y divide-gray-50 px-5">
          {pedidos.map((pedido) => (
            <PedidoRow key={pedido.id} pedido={pedido} />
          ))}

          {/* Empty footer */}
          <div className="py-4 text-center">
            <span className="text-sm text-green-600">Nenhum novo pedido no momento 🌱</span>
          </div>
        </div>
      )}
    </div>
  );
}
