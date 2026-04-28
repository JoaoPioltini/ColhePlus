/**
 * CAMADA DE API - Colhe+
 *
 * Todas as chamadas ao backend estão centralizadas aqui.
 * Para integrar com o backend real, basta trocar a BASE_URL
 * e garantir que os endpoints batem com os controllers Spring Boot.
 *
 * Endpoints mapeados:
 *  - AuthController:   POST /auth/cadastro, POST /auth/login, GET /termo, POST /auth/aceite
 *  - LoteController:  GET /lotes, POST /lotes, DELETE /lotes/:id, PATCH /lotes/:id/cancelar
 *  - PedidoController: GET /pedidos, POST /pedidos, PATCH /pedidos/:id/cancelar
 *  - AdminController: GET /admin/usuarios, DELETE /admin/usuarios/:id, PATCH /admin/lotes/:id/desativar
 */

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

// ─── Utilitário de fetch ──────────────────────────────────────────────────────

async function request(method, path, body = null, token = null) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const options = { method, headers };
  if (body) options.body = JSON.stringify(body);

  const res = await fetch(`${BASE_URL}${path}`, options);

  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: "Erro desconhecido" }));
    throw new Error(error.message || `Erro ${res.status}`);
  }

  if (res.status === 204) return null;
  return res.json();
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

export const authApi = {
  /** POST /auth/cadastro */
  cadastrar: (dados) => request("POST", "/auth/cadastro", dados),

  /** POST /auth/login → retorna { token, usuario } */
  login: (email, senha) => request("POST", "/auth/login", { email, senha }),

  /** GET /termo → retorna { id, versao, conteudo, dataPublicacao } */
  getTermo: (token) => request("GET", "/termo", null, token),

  /** POST /auth/aceite → marca termoAceito = true */
  aceitarTermo: (token) => request("POST", "/auth/aceite", null, token),
};

// ─── Lotes ────────────────────────────────────────────────────────────────────

export const loteApi = {
  /** GET /lotes → lista de lotes abertos (para compradores) */
  listarAbertos: (token) => request("GET", "/lotes", null, token),

  /** GET /lotes/meus → lotes do produtor logado */
  listarMeus: (token) => request("GET", "/lotes/meus", null, token),

  /** POST /lotes → cria lote (produtor) */
  criar: (dados, token) => request("POST", "/lotes", dados, token),

  /** DELETE /lotes/:id → exclui lote */
  excluir: (id, token) => request("DELETE", `/lotes/${id}`, null, token),

  /** PATCH /lotes/:id/cancelar → cancela lote */
  cancelar: (id, token) => request("PATCH", `/lotes/${id}/cancelar`, null, token),
};

// ─── Pedidos ──────────────────────────────────────────────────────────────────

export const pedidoApi = {
  /** GET /pedidos → pedidos do usuário logado */
  listar: (token) => request("GET", "/pedidos", null, token),

  /** POST /pedidos → realiza pedido (comprador) */
  criar: (dados, token) => request("POST", "/pedidos", dados, token),

  /** PATCH /pedidos/:id/cancelar */
  cancelar: (id, token) => request("PATCH", `/pedidos/${id}/cancelar`, null, token),
};

// ─── Admin ────────────────────────────────────────────────────────────────────

export const adminApi = {
  /** GET /admin/usuarios */
  listarUsuarios: (token) => request("GET", "/admin/usuarios", null, token),

  /** DELETE /admin/usuarios/:id */
  excluirUsuario: (id, token) => request("DELETE", `/admin/usuarios/${id}`, null, token),

  /** PATCH /admin/lotes/:id/desativar */
  desativarLote: (id, token) => request("PATCH", `/admin/lotes/${id}/desativar`, null, token),
};
