/**
 * AdminPage - Painel Admin: gerenciar usuários e lotes
 *
 * Chama (AdminController / AdminService):
 *  - GET  /admin/usuarios          → listar todos usuários
 *  - DELETE /admin/usuarios/:id    → excluir usuário
 *  - PATCH /admin/lotes/:id/desativar → desativar lote
 */

import { useState, useEffect, useCallback } from "react";
import { adminApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Badge, Button, EmptyState, Spinner, ErrorMessage, Toast, Card } from "../common";

function UsuarioRow({ usuario, onExcluir }) {
  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-50 last:border-0">
      <div>
        <p className="font-semibold text-gray-900 text-sm">{usuario.nome}</p>
        <p className="text-xs text-gray-500">{usuario.email}</p>
        <p className="text-xs text-gray-400 mt-0.5">
          <span
            className={`font-medium ${
              usuario.papel === "PRODUTOR"
                ? "text-green-700"
                : usuario.papel === "ADMIN"
                ? "text-purple-700"
                : "text-blue-700"
            }`}
          >
            {usuario.papel}
          </span>{" "}
          · {usuario.ativo ? "Ativo" : "Inativo"}
          {" · "}Termo:{" "}
          {usuario.termoAceito ? (
            <span className="text-green-600">Aceito</span>
          ) : (
            <span className="text-amber-600">Pendente</span>
          )}
        </p>
      </div>
      <Button
        variant="danger"
        size="sm"
        onClick={() => onExcluir(usuario.id)}
      >
        Excluir
      </Button>
    </div>
  );
}

function LoteRow({ lote, onDesativar }) {
  const podeDesativar = lote.status !== "DESATIVADO";

  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-50 last:border-0 gap-4">
      <div>
        <p className="font-semibold text-gray-900 text-sm">{lote.produto}</p>
        <p className="text-xs text-gray-500">
          Produtor: {lote.produtorNome || "Sem produtor"} · {lote.volumeDisponivelKg} kg
        </p>
      </div>
      <div className="flex items-center gap-3">
        <Badge status={lote.status} />
        <Button
          variant="warning"
          size="sm"
          onClick={() => onDesativar(lote.id)}
          disabled={!podeDesativar}
        >
          Desativar
        </Button>
      </div>
    </div>
  );
}

export function AdminPage() {
  const { token } = useAuth();
  const [usuarios, setUsuarios] = useState([]);
  const [lotes, setLotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [toast, setToast] = useState(null);

  const showToast = (msg, type = "success") => {
    setToast({ message: msg, type });
    setTimeout(() => setToast(null), 3500);
  };

  const fetchDados = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [usuariosData, lotesData] = await Promise.all([
        adminApi.listarUsuarios(token),
        adminApi.listarLotes(token),
      ]);
      setUsuarios(usuariosData);
      setLotes(lotesData);
    } catch (err) {
      setError(err.message || "Erro ao carregar dados administrativos.");
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    fetchDados();
  }, [fetchDados]);

  async function handleExcluirUsuario(id) {
    if (!confirm("Excluir este usuário permanentemente?")) return;
    try {
      await adminApi.excluirUsuario(id, token);
      setUsuarios((u) => u.filter((usr) => usr.id !== id));
      showToast("Usuário excluído.");
    } catch (err) {
      showToast(err.message || "Erro ao excluir usuário.", "error");
    }
  }

  async function handleDesativarLote(id) {
    if (!confirm("Desativar este lote?")) return;
    try {
      const lote = await adminApi.desativarLote(id, token);
      setLotes((lista) => lista.map((item) => item.id === lote.id ? lote : item));
      showToast("Lote desativado.");
    } catch (err) {
      showToast(err.message || "Erro ao desativar lote.", "error");
    }
  }

  const produtores = usuarios.filter((u) => u.papel === "PRODUTOR");
  const compradores = usuarios.filter((u) => u.papel === "COMPRADOR");

  return (
    <div className="p-6 flex flex-col gap-6 max-w-4xl">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Painel Administrativo</h1>
        <p className="text-sm text-gray-500 mt-0.5">Gerencie usuários e lotes da plataforma.</p>
      </div>

      {/* Estatísticas rápidas */}
      <div className="grid grid-cols-3 gap-4">
        <Card className="p-5 text-center">
          <p className="text-3xl font-bold text-gray-900">{usuarios.length}</p>
          <p className="text-sm text-gray-500 mt-1">Total de usuários</p>
        </Card>
        <Card className="p-5 text-center">
          <p className="text-3xl font-bold text-green-700">{produtores.length}</p>
          <p className="text-sm text-gray-500 mt-1">Produtores</p>
        </Card>
        <Card className="p-5 text-center">
          <p className="text-3xl font-bold text-blue-700">{compradores.length}</p>
          <p className="text-sm text-gray-500 mt-1">Compradores</p>
        </Card>
      </div>

      <ErrorMessage message={error} />

      {/* Tabela de usuários */}
      <Card className="p-6">
        <h2 className="text-base font-semibold text-gray-900 mb-4">Usuários Cadastrados</h2>
        {loading ? (
          <Spinner />
        ) : usuarios.length === 0 ? (
          <EmptyState icon="👥" title="Nenhum usuário cadastrado" />
        ) : (
          <div>
            {usuarios.map((u) => (
              <UsuarioRow key={u.id} usuario={u} onExcluir={handleExcluirUsuario} />
            ))}
          </div>
        )}
      </Card>

      <Card className="p-6">
        <h2 className="text-base font-semibold text-gray-900 mb-4">Lotes Cadastrados</h2>
        {loading ? (
          <Spinner />
        ) : lotes.length === 0 ? (
          <EmptyState icon="📦" title="Nenhum lote cadastrado" />
        ) : (
          <div>
            {lotes.map((lote) => (
              <LoteRow key={lote.id} lote={lote} onDesativar={handleDesativarLote} />
            ))}
          </div>
        )}
      </Card>

      {toast && (
        <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />
      )}
    </div>
  );
}
