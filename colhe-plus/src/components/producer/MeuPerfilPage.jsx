/**
 * MeuPerfilPage - Painel do Produtor: edição de perfil
 *
 * INTEGRAÇÃO: Ainda não existe endpoint explícito de perfil nos diagramas.
 * Sugestão: PUT /usuarios/perfil → atualiza nome, senha, localização
 */

import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { Button, Input, Card, Toast, ErrorMessage } from "../common";

export function MeuPerfilPage() {
  const { usuario, token, atualizarUsuario } = useAuth();

  const [form, setForm] = useState({
    nome: usuario?.nome || "",
    email: usuario?.email || "",
    senhaHash: "",
    latitude: usuario?.latitude || "",
    longitude: usuario?.longitude || "",
  });
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);
  const [error, setError] = useState("");

  function set(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleSalvar() {
    setError("");
    setLoading(true);
    try {
      // INTEGRAÇÃO: substituir pela chamada real ao endpoint de perfil
      // const updated = await request("PUT", "/usuarios/perfil", form, token);
      // atualizarUsuario(updated);

      // Mock temporário:
      await new Promise((r) => setTimeout(r, 800));
      atualizarUsuario({ nome: form.nome });
      setToast({ message: "Perfil atualizado com sucesso!", type: "success" });
    } catch (err) {
      setError(err.message || "Erro ao salvar alterações.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="p-6 flex flex-col gap-6 max-w-4xl">
      <h1 className="text-2xl font-bold text-gray-900">Meu Perfil</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Formulário */}
        <div className="md:col-span-2">
          <Card className="p-6 flex flex-col gap-6">
            <div>
              <h2 className="text-base font-semibold text-green-800 mb-4">Informações Pessoais</h2>
              <div className="flex flex-col gap-3">
                <Input
                  label="Nome completo"
                  value={form.nome}
                  onChange={(e) => set("nome", e.target.value)}
                />
                <Input
                  label="E-mail"
                  type="email"
                  value={form.email}
                  onChange={(e) => set("email", e.target.value)}
                  disabled
                />
                <Input
                  label="Nova senha (deixe em branco para não alterar)"
                  type="password"
                  placeholder="••••••••"
                  value={form.senhaHash}
                  onChange={(e) => set("senhaHash", e.target.value)}
                />
              </div>
            </div>

            {usuario?.papel === "PRODUTOR" && (
              <div>
                <h2 className="text-base font-semibold text-green-800 mb-4">Localização</h2>
                <div className="grid grid-cols-2 gap-3">
                  <Input
                    label="Latitude"
                    type="number"
                    value={form.latitude}
                    onChange={(e) => set("latitude", e.target.value)}
                  />
                  <Input
                    label="Longitude"
                    type="number"
                    value={form.longitude}
                    onChange={(e) => set("longitude", e.target.value)}
                  />
                </div>
              </div>
            )}

            <ErrorMessage message={error} />

            <Button onClick={handleSalvar} loading={loading}>
              Salvar Alterações
            </Button>
          </Card>
        </div>

        {/* Sidebar com estatísticas */}
        <div className="flex flex-col gap-4">
          <Card className="p-4">
            <p className="text-xs text-gray-500 mb-1">Tipo de Conta</p>
            <p className="font-bold text-green-700">{usuario?.papel}</p>
          </Card>
          <Card className="p-4">
            <p className="text-xs text-gray-500 mb-1">Lotes Criados</p>
            <p className="text-2xl font-bold text-gray-900">
              {/* INTEGRAÇÃO: buscar do backend */}
              —
            </p>
          </Card>
          <Card className="p-4">
            <p className="text-xs text-gray-500 mb-1">Pedidos Atendidos</p>
            <p className="text-2xl font-bold text-gray-900">—</p>
          </Card>
          <div className="bg-green-50 border border-green-100 rounded-2xl p-4">
            <p className="text-sm text-green-800 font-medium">
              Perfil completo aumenta a confiança dos compradores 🌱
            </p>
          </div>
        </div>
      </div>

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
