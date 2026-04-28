# Colhe+ — Frontend React SPA

> Plataforma de agrupamento de demanda agrícola. Conecta produtores e compradores.

## 🚀 Como rodar

```bash
# Instalar dependências
npm install

# Iniciar servidor de dev (http://localhost:5173)
npm run dev

# Build de produção
npm run build
```

---

## 🗂️ Estrutura de arquivos

```
src/
├── api/
│   └── api.js              ← ⭐ TODAS as chamadas ao backend aqui
├── context/
│   └── AuthContext.jsx     ← Estado global de autenticação (JWT, usuário)
├── components/
│   ├── common/
│   │   └── index.jsx       ← Button, Input, Modal, Badge, Navbar, Sidebar...
│   ├── auth/
│   │   ├── LoginPage.jsx
│   │   ├── CadastroPage.jsx
│   │   └── TermoPage.jsx
│   ├── producer/
│   │   ├── MeusLotesPage.jsx
│   │   ├── PedidosRecebidosPage.jsx
│   │   ├── MeuPerfilPage.jsx
│   │   ├── LoteCard.jsx
│   │   └── CriarLoteModal.jsx
│   ├── buyer/
│   │   ├── LotesDisponiveisPage.jsx
│   │   └── MeusPedidosPage.jsx
│   └── admin/
│       └── AdminPage.jsx
└── App.jsx                 ← Roteador SPA + proteção por papel
```

---

## 🔌 Guia de integração com o Backend (Spring Boot)

### 1. Configurar a URL base

No arquivo `src/api/api.js`, a URL base é lida da variável de ambiente:

```js
const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
```

Crie um arquivo `.env.local` na raiz do projeto:

```
VITE_API_URL=http://localhost:8080
```

### 2. Proxy de desenvolvimento (CORS)

O `vite.config.js` já configura um proxy para evitar erros de CORS durante o desenvolvimento:

```js
proxy: {
  "/auth":    "http://localhost:8080",
  "/lotes":   "http://localhost:8080",
  "/pedidos": "http://localhost:8080",
  "/termo":   "http://localhost:8080",
  "/admin":   "http://localhost:8080",
}
```

### 3. Contrato de dados esperados

#### POST /auth/login → resposta esperada:
```json
{
  "token": "eyJ...",
  "usuario": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "papel": "PRODUTOR",
    "termoAceito": false,
    "latitude": -23.55,
    "longitude": -46.63
  }
}
```

#### GET /lotes (lotes abertos para compradores):
```json
[
  {
    "id": 1,
    "produto": "Milho",
    "volumeDisponivelKg": 1200,
    "volumeMinimoViavelKg": 800,
    "precoPorKg": 2.40,
    "taxaFixaEntrega": 15.00,
    "raioMaximoEntregaKm": 50,
    "status": "ABERTO",
    "volumeAgrupado": 500,
    "produtorNome": "João Silva"
  }
]
```

#### GET /lotes/meus (lotes do produtor logado):
Mesmo formato acima, mas filtrado pelo produtor autenticado.

#### POST /lotes → body:
```json
{
  "produto": "Milho",
  "volumeDisponivelKg": 1200,
  "volumeMinimoViavelKg": 800,
  "precoPorKg": 2.40,
  "taxaFixaEntrega": 15.00,
  "raioMaximoEntregaKm": 50
}
```

#### POST /pedidos → body:
```json
{
  "loteId": 1,
  "quantidadeKg": 200,
  "tipoEntrega": "ENTREGA",
  "latitudeEntrega": -23.55,
  "longitudeEntrega": -46.63
}
```

#### GET /admin/usuarios:
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "papel": "PRODUTOR",
    "ativo": true,
    "termoAceito": true
  }
]
```

### 4. Autenticação

O token JWT é enviado automaticamente no header de todas as requisições autenticadas:

```
Authorization: Bearer <token>
```

O backend deve validar este token em todas as rotas protegidas.

### 5. Campos que o frontend ainda não exibe (aguardando backend)

- `dataCriacao` / `dataAtualizacao` nos pedidos → já há espaço nos cards
- `lotesPorProdutor` no painel de admin → pode ser expandido no AdminPage
- Estatísticas de perfil (lotes criados, pedidos atendidos) → MeuPerfilPage tem comentário `// INTEGRAÇÃO`

---

## 🎨 Design

- **Framework**: Tailwind CSS v3
- **Fonte**: DM Sans (Google Fonts)
- **Cor primária**: Verde `#3a7d2e`
- **Componentes reutilizáveis**: todos em `src/components/common/index.jsx`

---

## 🔐 Papéis e rotas

| Papel     | Acesso                                      |
|-----------|---------------------------------------------|
| PRODUTOR  | Meus Lotes, Pedidos Recebidos, Meu Perfil  |
| COMPRADOR | Lotes Disponíveis, Meus Pedidos            |
| ADMIN     | Painel Admin (usuários e lotes)            |

A proteção é feita no `App.jsx` com base no campo `usuario.papel` retornado pelo login.
