/**
 * TermoPage - Aceite do Termo de Responsabilidade (primeiro acesso)
 *
 * Fluxo (Sequência - Aceite do Termo):
 *  1. GET /termo → exibe o conteúdo
 *  2. POST /auth/aceite → marca termoAceito = true
 *  3. Redireciona para o painel
 */

import { useState, useEffect } from "react";
import { authApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Button, Spinner, ErrorMessage } from "../common";

export function TermoPage() {
  const { token, atualizarUsuario } = useAuth();
  const [termo, setTermo] = useState(null);
  const [aceito, setAceito] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    authApi
      .getTermo(token)
      .then(setTermo)
      .catch(() => setError("Não foi possível carregar o Termo."))
      .finally(() => setLoading(false));
  }, [token]);

  async function handleAceite() {
    setSaving(true);
    setError("");
    try {
      await authApi.aceitarTermo(token);
      // Atualiza o usuário no contexto para termoAceito = true
      atualizarUsuario({ termoAceito: true });
    } catch (err) {
      setError(err.message || "Erro ao registrar aceite.");
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <Spinner />;

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-2xl">
        {/* Header fora do card */}
        <div className="mb-4 flex justify-end">
          <span className="text-sm text-gray-400 italic">Etapa obrigatória</span>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          <h1 className="text-2xl font-bold text-green-800 text-center mb-6">
            Termo de Responsabilidade
          </h1>

          {/* Conteúdo do termo */}
          <div className="border border-gray-100 rounded-xl bg-gray-50 p-5 max-h-72 overflow-y-auto text-sm text-gray-700 leading-relaxed mb-6">
            {termo ? (
              <>
                <p className="mb-3 font-medium">
                  Ao utilizar a plataforma Colhe+, o usuário declara estar ciente de que:
                </p>
                <ul className="space-y-2 mb-4">
                  <li>• A plataforma atua apenas como intermediadora entre produtores e compradores.</li>
                  <li>• Os dados inseridos são de responsabilidade exclusiva do usuário.</li>
                  <li>• O agrupamento de demanda depende da adesão de múltiplos compradores.</li>
                  <li>• O sistema não garante a concretização de negociações.</li>
                  <li>• O usuário deve respeitar práticas comerciais éticas e legais.</li>
                </ul>
                {termo.conteudo && <p className="text-gray-600">{termo.conteudo}</p>}
                {termo.versao && (
                  <p className="mt-4 text-xs text-gray-400">Versão: {termo.versao}</p>
                )}
              </>
            ) : (
              <p className="text-gray-500">Termo não disponível.</p>
            )}
          </div>

          {/* Checkbox */}
          <label className="flex items-center gap-3 cursor-pointer mb-6 select-none">
            <input
              type="checkbox"
              checked={aceito}
              onChange={(e) => setAceito(e.target.checked)}
              className="w-4 h-4 rounded accent-green-700"
            />
            <span className="text-sm text-gray-700">Li e aceito o Termo de Responsabilidade</span>
          </label>

          <ErrorMessage message={error} />

          <Button
            onClick={handleAceite}
            loading={saving}
            disabled={!aceito}
            fullWidth
            size="lg"
          >
            Continuar
          </Button>
        </div>
      </div>
    </div>
  );
}
