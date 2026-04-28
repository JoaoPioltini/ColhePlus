/**
 * CadastroPage - Tela de cadastro do Colhe+
 * Chama: POST /auth/cadastro via authApi.cadastrar()
 *
 * Papel PRODUTOR exige latitude e longitude.
 * Papel COMPRADOR não precisa de localização.
 */

import { useState } from "react";
import { authApi } from "../../api/api";
import { Button, Input, ErrorMessage } from "../common";

export function CadastroPage({ onNavigate }) {
  const [form, setForm] = useState({
    nome: "",
    email: "",
    senhaHash: "",
    papel: "PRODUTOR", // PRODUTOR | COMPRADOR
    latitude: "",
    longitude: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function set(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleCadastro() {
    if (!form.nome || !form.email || !form.senhaHash) {
      setError("Preencha todos os campos obrigatórios.");
      return;
    }
    if (form.papel === "PRODUTOR" && (!form.latitude || !form.longitude)) {
      setError("Produtores precisam informar localização.");
      return;
    }
    setError("");
    setLoading(true);
    try {
      await authApi.cadastrar(form);
      // Após cadastro, redireciona para login
      onNavigate("login");
    } catch (err) {
      setError(err.message || "Erro ao criar conta.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          <div className="text-center mb-8">
            <h1 className="text-2xl font-bold text-green-800 mb-1">Criar conta</h1>
            <p className="text-sm text-gray-500">Comece a usar o Colhe+ hoje 🌱</p>
          </div>

          <div className="flex flex-col gap-4">
            <Input
              label="Nome completo"
              placeholder="Seu nome"
              value={form.nome}
              onChange={(e) => set("nome", e.target.value)}
              required
            />
            <Input
              label="E-mail"
              type="email"
              placeholder="seu@email.com"
              value={form.email}
              onChange={(e) => set("email", e.target.value)}
              required
            />
            <Input
              label="Senha"
              type="password"
              placeholder="••••••••"
              value={form.senhaHash}
              onChange={(e) => set("senhaHash", e.target.value)}
              required
            />

            {/* Tipo de usuário */}
            <div className="flex flex-col gap-2">
              <label className="text-sm font-medium text-gray-700">Tipo de usuário</label>
              <div className="grid grid-cols-2 gap-3">
                {["PRODUTOR", "COMPRADOR"].map((papel) => (
                  <button
                    key={papel}
                    type="button"
                    onClick={() => set("papel", papel)}
                    className={`py-2.5 rounded-xl border-2 text-sm font-semibold transition-all ${
                      form.papel === papel
                        ? "border-green-700 bg-green-50 text-green-800"
                        : "border-gray-200 text-gray-500 hover:border-gray-300"
                    }`}
                  >
                    {papel === "PRODUTOR" ? "🌾 Produtor" : "🛒 Comprador"}
                  </button>
                ))}
              </div>
            </div>

            {/* Localização - só para produtor */}
            {form.papel === "PRODUTOR" && (
              <div className="bg-green-50 rounded-xl p-4 flex flex-col gap-3">
                <label className="text-sm font-medium text-gray-700">
                  Localização <span className="text-red-500">*</span>
                </label>
                <div className="grid grid-cols-2 gap-3">
                  <Input
                    placeholder="Latitude"
                    type="number"
                    value={form.latitude}
                    onChange={(e) => set("latitude", e.target.value)}
                  />
                  <Input
                    placeholder="Longitude"
                    type="number"
                    value={form.longitude}
                    onChange={(e) => set("longitude", e.target.value)}
                  />
                </div>
              </div>
            )}

            <ErrorMessage message={error} />

            <Button onClick={handleCadastro} loading={loading} fullWidth size="lg">
              Cadastrar
            </Button>

            <p className="text-center text-sm text-gray-500">
              Já tem conta?{" "}
              <button
                onClick={() => onNavigate("login")}
                className="text-green-700 font-semibold hover:underline"
              >
                Entrar
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
