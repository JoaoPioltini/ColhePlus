/**
 * LoginPage - Tela de login do Colhe+
 * Chama: POST /auth/login via authApi.login()
 */

import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { Button, Input, ErrorMessage } from "../common";

export function LoginPage({ onNavigate }) {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleLogin() {
    if (!email || !senha) {
      setError("Preencha e-mail e senha.");
      return;
    }
    setError("");
    setLoading(true);
    try {
      await login(email, senha);
      // AuthContext atualiza o usuário; App.jsx lida com o redirecionamento
    } catch (err) {
      setError(err.message || "Credenciais inválidas.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Card */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          <div className="text-center mb-8">
            <h1 className="text-2xl font-bold text-green-800 mb-1">Bem-vindo ao Colhe+</h1>
            <p className="text-sm text-gray-500">Agrupe demandas e reduza desperdícios agrícolas 🌱</p>
          </div>

          <div className="flex flex-col gap-4">
            <Input
              label="E-mail"
              type="email"
              placeholder="seu@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <Input
              label="Senha"
              type="password"
              placeholder="••••••••"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
            />

            <ErrorMessage message={error} />

            <Button onClick={handleLogin} loading={loading} fullWidth size="lg">
              Entrar
            </Button>

            <p className="text-center text-sm text-gray-500">
              Não tem conta?{" "}
              <button
                onClick={() => onNavigate("cadastro")}
                className="text-green-700 font-semibold hover:underline"
              >
                Cadastrar-se
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
