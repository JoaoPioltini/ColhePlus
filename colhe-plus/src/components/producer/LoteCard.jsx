/**
 * LoteCard - Exibe informações de um lote com barra de progresso e ações
 *
 * Props:
 *  - lote: objeto Lote do backend
 *  - onCancelar: fn(id) — chamado ao cancelar lote
 *  - onExcluir: fn(id) — chamado ao excluir lote
 *  - mode: "producer" | "buyer" — controla quais ações são exibidas
 */

import { Badge, ProgressBar, Button } from "../common";

export function LoteCard({ lote, onCancelar, onExcluir, onFazerPedido, mode = "producer" }) {
  const pct = lote.volumeMinimoViavelKg
    ? Math.min(100, (lote.volumeAgrupado / lote.volumeMinimoViavelKg) * 100)
    : 0;

  const podeCancelar = lote.status === "ABERTO";
  const podeExcluir = lote.status === "ABERTO";
  const jaAtivado = lote.status === "ATIVADO";

  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex flex-col gap-4">
      {/* Cabeçalho */}
      <div className="flex items-start justify-between">
        <div>
          <h3 className="text-lg font-bold text-gray-900">{lote.produto}</h3>
          <p className="text-xs text-gray-400">Lote #{String(lote.id).padStart(3, "0")}</p>
        </div>
        <Badge status={lote.status} />
      </div>

      {/* Detalhes */}
      <div className="grid grid-cols-2 gap-3">
        <div>
          <p className="text-xs text-gray-500">Volume disponível</p>
          <p className="text-sm font-semibold text-gray-800">{lote.volumeDisponivelKg?.toLocaleString("pt-BR")} kg</p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Volume mínimo viável</p>
          <p className="text-sm font-semibold text-gray-800">{lote.volumeMinimoViavelKg?.toLocaleString("pt-BR")} kg</p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Preço por kg</p>
          <p className="text-sm font-semibold text-gray-800">
            R$ {lote.precoPorKg?.toFixed(2).replace(".", ",")}
          </p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Entrega</p>
          <p className="text-sm font-semibold text-gray-800">
            {lote.taxaFixaEntrega ? `Sim (+R$ ${lote.taxaFixaEntrega?.toFixed(2)})` : "Não"}
          </p>
        </div>
      </div>

      {/* Barra de progresso da demanda agrupada */}
      {lote.volumeMinimoViavelKg > 0 && (
        <div>
          <p className="text-xs text-gray-500 mb-1.5">Demanda agrupada atual</p>
          <ProgressBar value={lote.volumeAgrupado || 0} max={lote.volumeMinimoViavelKg} />
          <p className="text-xs text-gray-500 mt-1">
            {(lote.volumeAgrupado || 0).toLocaleString("pt-BR")} kg de{" "}
            {lote.volumeMinimoViavelKg.toLocaleString("pt-BR")} kg mínimos
          </p>
        </div>
      )}

      {/* Aviso lote ativado */}
      {jaAtivado && mode === "producer" && (
        <div className="bg-green-50 border border-green-200 rounded-xl px-4 py-2.5 text-xs text-green-700 font-medium">
          Este lote já foi ativado e não pode mais ser cancelado.
        </div>
      )}

      {/* Ações produtor */}
      {mode === "producer" && !jaAtivado && (
        <div className="flex gap-3">
          {podeExcluir && (
            <Button variant="secondary" size="sm" onClick={() => onExcluir?.(lote.id)} fullWidth>
              Excluir
            </Button>
          )}
          {podeCancelar && (
            <Button variant="warning" size="sm" onClick={() => onCancelar?.(lote.id)} fullWidth>
              Cancelar
            </Button>
          )}
        </div>
      )}

      {/* Ações comprador */}
      {mode === "buyer" && lote.status === "ABERTO" && (
        <Button onClick={() => onFazerPedido?.(lote)} fullWidth size="sm">
          Fazer Pedido
        </Button>
      )}
    </div>
  );
}
