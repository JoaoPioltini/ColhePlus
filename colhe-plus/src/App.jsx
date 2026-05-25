/**
 * App.jsx - Orquestrador da SPA Colhe+
 *
 * Gerencia:
 *  - Roteamento manual (sem react-router, facilita integração futura)
 *  - Proteção de rotas por papel (PRODUTOR / COMPRADOR / ADMIN)
 *  - Redirecionamento para aceite de termo (primeiro acesso)
 *
 * Para adicionar react-router depois:
 *  Troque os estados `page` por <Route path="..." element={<Componente/>} />
 *  e os onNavigate por navigate() do useNavigate()
 */

import { useState } from "react";
import { AuthProvider, useAuth } from "./context/AuthContext";

// Auth
import { LoginPage } from "./components/auth/LoginPage";
import { CadastroPage } from "./components/auth/CadastroPage";
import { TermoPage } from "./components/auth/TermoPage";

// Producer
import { MeusLotesPage } from "./components/producer/MeusLotesPage";
import { PedidosRecebidosPage } from "./components/producer/PedidosRecebidosPage";
import { MeuPerfilPage } from "./components/producer/MeuPerfilPage";

// Buyer
import { LotesDisponiveisPage } from "./components/buyer/LotesDisponiveisPage";
import { MeusPedidosPage } from "./components/buyer/MeusPedidosPage";

// Admin
import { AdminPage } from "./components/admin/AdminPage";

// Common
import { Navbar, Sidebar, Spinner } from "./components/common";

// ─── Sidebar configs por papel ────────────────────────────────────────────────
const PRODUCER_ITEMS = [
  { id: "meus-lotes", label: "Meus Lotes" },
  { id: "pedidos-recebidos", label: "Pedidos Recebidos" },
  { id: "meu-perfil", label: "Meu Perfil" },
];

const BUYER_ITEMS = [
  { id: "lotes-disponiveis", label: "Lotes Disponíveis" },
  { id: "meus-pedidos", label: "Meus Pedidos" },
];

const ADMIN_ITEMS = [
  { id: "admin", label: "Usuários e Lotes" },
];

// ─── Painel por papel ─────────────────────────────────────────────────────────
function Dashboard() {
  const { usuario, logout } = useAuth();
  const papel = usuario?.papel;

  const sidebarItems =
    papel === "PRODUTOR" ? PRODUCER_ITEMS :
    papel === "COMPRADOR" ? BUYER_ITEMS :
    ADMIN_ITEMS;

  const defaultPage =
    papel === "PRODUTOR" ? "meus-lotes" :
    papel === "COMPRADOR" ? "lotes-disponiveis" :
    "admin";

  const [activePage, setActivePage] = useState(defaultPage);

  function renderPage() {
    switch (activePage) {
      // Produtor
      case "meus-lotes":       return <MeusLotesPage />;
      case "pedidos-recebidos":return <PedidosRecebidosPage />;
      case "meu-perfil":       return <MeuPerfilPage />;

      // Comprador
      case "lotes-disponiveis":return <LotesDisponiveisPage />;
      case "meus-pedidos":     return <MeusPedidosPage />;

      // Admin
      case "admin":            return <AdminPage />;

      default:                 return <div className="p-6 text-gray-500">Página não encontrada.</div>;
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar onLogout={logout} />
      <div className="flex">
        <Sidebar
          items={sidebarItems}
          activePage={activePage}
          onNavigate={setActivePage}
          role={papel}
        />
        <main className="flex-1 overflow-auto">
          {renderPage()}
        </main>
      </div>
    </div>
  );
}

// ─── Roteador principal ───────────────────────────────────────────────────────
function Router() {
  const { usuario, loading } = useAuth();
  const [publicPage, setPublicPage] = useState("login"); // "login" | "cadastro"

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <span className="text-4xl">🌿</span>
          <p className="mt-2 text-green-800 font-semibold">Colhe+</p>
        </div>
      </div>
    );
  }

  // Não autenticado
  if (!usuario) {
    if (publicPage === "cadastro") {
      return <CadastroPage onNavigate={setPublicPage} />;
    }
    return <LoginPage onNavigate={setPublicPage} />;
  }

  // Autenticado mas não aceitou o termo
  if (!usuario.termoAceito) {
    return (
      <>
        <Navbar />
        <TermoPage />
      </>
    );
  }

  // Autenticado e com termo aceito → painel
  return <Dashboard />;
}

// ─── Root ─────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <AuthProvider>
      <Router />
    </AuthProvider>
  );
}
