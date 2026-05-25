/**
 * MeusLotesPage - Painel do Produtor: lista e gerencia lotes
 *
 * Chama:
 *  - GET  /lotes/meus     → listar lotes do produtor
 *  - DELETE /lotes/:id    → excluir lote
 *  - PATCH /lotes/:id/cancelar → cancelar lote
 */

import { useState, useEffect, useCallback } from "react";
import { loteApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Button, StatCard, EmptyState, Spinner, ErrorMessage, Toast } from "../common";
import { LoteCard } from "./LoteCard";
import { CriarLoteModal } from "./CriarLoteModal";

export function MeusLotesPage() {
  const { token } = useAuth();
  const [lotes, setLotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [toast, setToast] = useState(null);

  const showToast = (message, type = "success") => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3500);
  };

  const fetchLotes = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await loteApi.listarMeus(token);
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

  async function handleExcluir(id) {
    if (!confirm("Tem certeza que deseja excluir este lote?")) return;
    try {
      await loteApi.excluir(id, token);
      setLotes((l) => l.filter((lote) => lote.id !== id));
      showToast("Lote excluído com sucesso.");
    } catch (err) {
      showToast(err.message || "Erro ao excluir lote.", "error");
    }
  }

  async function handleCancelar(id) {
    if (!confirm("Cancelar este lote?")) return;
    try {
      await loteApi.cancelar(id, token);
      setLotes((l) => l.map((lote) => lote.id === id ? { ...lote, status: "CANCELADO" } : lote));
      showToast("Lote cancelado.");
    } catch (err) {
      showToast(err.message || "Erro ao cancelar lote.", "error");
    }
  }

  function handleLoteCriado(lote) {
    setLotes((l) => [lote, ...l]);
    showToast("Lote criado com sucesso! 🌾");
  }

  // Estatísticas
  const abertos = lotes.filter((l) => l.status === "ABERTO").length;
  const ativados = lotes.filter((l) => l.status === "ATIVADO").length;
  const totalKg = lotes.reduce((acc, l) => acc + (l.volumeDisponivelKg || 0), 0);

  return (
    <div className="p-6 flex flex-col gap-6 max-w-5xl">
      {/* Cabeçalho */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Meus Lotes</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            Gerencie seus lotes e acompanhe quando a demanda mínima for atingida.
          </p>
        </div>
        <Button onClick={() => setModalOpen(true)}>+ Cadastrar Novo Lote</Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4">
        <StatCard label="Lotes Abertos" value={abertos} color="text-emerald-600" />
        <StatCard label="Lotes Ativados" value={ativados} color="text-blue-600" />
        <StatCard
          label="Volume Total Ofertado"
          value={`${totalKg.toLocaleString("pt-BR")} kg`}
        />
      </div>

      <ErrorMessage message={error} />

      {/* Lista de lotes */}
      {loading ? (
        <Spinner />
      ) : lotes.length === 0 ? (
        <EmptyState
          icon="📦"
          title="Nenhum lote cadastrado"
          description="Clique em 'Cadastrar Novo Lote' para começar a receber pedidos."
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {lotes.map((lote) => (
            <LoteCard
              key={lote.id}
              lote={lote}
              mode="producer"
              onExcluir={handleExcluir}
              onCancelar={handleCancelar}
            />
          ))}

          {/* Card "Quer ofertar um novo lote?" */}
          <button
            onClick={() => setModalOpen(true)}
            className="border-2 border-dashed border-green-200 rounded-2xl p-6 flex flex-col items-center justify-center gap-3 text-center hover:bg-green-50 transition-colors cursor-pointer"
          >
            <span className="text-3xl">📦</span>
            <div>
              <p className="font-semibold text-green-800">Quer ofertar um novo lote?</p>
              <p className="text-sm text-gray-500 mt-1">
                Cadastre um novo produto, defina o volume mínimo viável e deixe o Colhe+ agrupar a demanda.
              </p>
            </div>
          </button>
        </div>
      )}

      <CriarLoteModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSuccess={handleLoteCriado}
      />

      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
}
