/**
 * CriarLoteModal - Modal para cadastrar novo lote
 *
 * Fluxo (Sequência - Criação de Lote):
 *  POST /lotes → LoteController → LoteService → LoteRepository → DB
 *
 * Props:
 *  - isOpen: boolean
 *  - onClose: fn
 *  - onSuccess: fn(lote) — chamado quando o lote é criado com sucesso
 */

import { useState } from "react";
import { loteApi } from "../../api/api";
import { useAuth } from "../../context/AuthContext";
import { Modal, Button, Input, ErrorMessage } from "../common";

const initialForm = {
  produto: "",
  volumeDisponivelKg: "",
  volumeMinimoViavelKg: "",
  precoPorKg: "",
  horarioRetirada: "",
  instrucoesRetirada: "",
  ofereceEntrega: false,
  taxaFixaEntrega: "",
  raioMaximoEntregaKm: "",
};

const produtosSugeridos = [
  "Abacate",
  "Alface",
  "Batata",
  "Cebola",
  "Cenoura",
  "Feijão",
  "Laranja",
  "Mandioca",
  "Milho",
  "Tomate",
];

function produtoValido(produto) {
  return /\p{L}/u.test(produto);
}

export function CriarLoteModal({ isOpen, onClose, onSuccess }) {
  const { token } = useAuth();
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function set(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  function reset() {
    setForm(initialForm);
    setError("");
  }

  function handleClose() {
    reset();
    onClose();
  }

  async function handleSubmit() {
    if (!form.produto || !form.volumeDisponivelKg || !form.volumeMinimoViavelKg || !form.precoPorKg) {
      setError("Preencha todos os campos obrigatórios.");
      return;
    }
    if (!produtoValido(form.produto)) {
      setError("Produto deve conter letras.");
      return;
    }
    if (form.ofereceEntrega && (!form.taxaFixaEntrega || !form.raioMaximoEntregaKm)) {
      setError("Informe taxa e raio máximo de entrega.");
      return;
    }

    setError("");
    setLoading(true);

    // Monta o payload conforme o backend espera
    const payload = {
      produto: form.produto,
      volumeDisponivelKg: parseFloat(form.volumeDisponivelKg),
      volumeMinimoViavelKg: parseFloat(form.volumeMinimoViavelKg),
      precoPorKg: parseFloat(form.precoPorKg),
      horarioRetirada: form.horarioRetirada || "A combinar com o produtor",
      instrucoesRetirada: form.instrucoesRetirada,
      modalidadeEntrega: form.ofereceEntrega ? "RETIRADA_E_ENTREGA" : "RETIRADA",
      taxaFixaEntrega: form.ofereceEntrega ? parseFloat(form.taxaFixaEntrega) : null,
      raioMaximoEntregaKm: form.ofereceEntrega ? parseFloat(form.raioMaximoEntregaKm) : null,
    };

    try {
      const lote = await loteApi.criar(payload, token);
      onSuccess(lote);
      reset();
      onClose();
    } catch (err) {
      setError(err.message || "Erro ao criar lote.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Cadastrar Novo Lote">
      <div className="flex flex-col gap-4">
        <Input
          label="Produto"
          placeholder="ex: Milho"
          value={form.produto}
          onChange={(e) => set("produto", e.target.value)}
          list="produtos-sugeridos"
          required
        />
        <datalist id="produtos-sugeridos">
          {produtosSugeridos.map((produto) => (
            <option key={produto} value={produto} />
          ))}
        </datalist>

        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Volume disponível (kg)"
            type="number"
            placeholder="0"
            value={form.volumeDisponivelKg}
            onChange={(e) => set("volumeDisponivelKg", e.target.value)}
            required
          />
          <Input
            label="Volume mínimo viável (kg)"
            type="number"
            placeholder="0"
            value={form.volumeMinimoViavelKg}
            onChange={(e) => set("volumeMinimoViavelKg", e.target.value)}
            required
          />
        </div>

        <Input
          label="Preço por kg (R$)"
          type="number"
          placeholder="0,00"
          value={form.precoPorKg}
          onChange={(e) => set("precoPorKg", e.target.value)}
          required
        />

        <div className="bg-amber-50 rounded-xl p-4 flex flex-col gap-3">
          <Input
            label="Horário para retirada"
            placeholder="ex: seg a sex, 08h às 17h"
            value={form.horarioRetirada}
            onChange={(e) => set("horarioRetirada", e.target.value)}
          />
          <Input
            label="Instruções de retirada"
            placeholder="ex: retirar no portão principal"
            value={form.instrucoesRetirada}
            onChange={(e) => set("instrucoesRetirada", e.target.value)}
          />
        </div>

        {/* Seção de entrega */}
        <div className="bg-green-50 rounded-xl p-4 flex flex-col gap-3">
          <label className="flex items-center gap-3 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={form.ofereceEntrega}
              onChange={(e) => set("ofereceEntrega", e.target.checked)}
              className="w-4 h-4 rounded accent-green-700"
            />
            <span className="text-sm font-medium text-gray-700">Oferecer entrega?</span>
          </label>

          {form.ofereceEntrega && (
            <div className="grid grid-cols-2 gap-3 pt-1">
              <Input
                label="Taxa fixa de entrega (R$)"
                type="number"
                placeholder="0,00"
                value={form.taxaFixaEntrega}
                onChange={(e) => set("taxaFixaEntrega", e.target.value)}
              />
              <Input
                label="Raio máximo (km)"
                type="number"
                placeholder="0"
                value={form.raioMaximoEntregaKm}
                onChange={(e) => set("raioMaximoEntregaKm", e.target.value)}
              />
            </div>
          )}
        </div>

        <ErrorMessage message={error} />

        <div className="flex gap-3 pt-2">
          <Button variant="secondary" onClick={handleClose} fullWidth>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} loading={loading} fullWidth>
            Cadastrar Lote
          </Button>
        </div>
      </div>
    </Modal>
  );
}
