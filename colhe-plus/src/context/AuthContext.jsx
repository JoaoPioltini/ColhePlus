/**
 * AuthContext - Gerencia o estado de autenticação global da SPA
 *
 * Disponibiliza:
 *  - usuario: objeto com dados do usuário logado (ou null)
 *  - token: JWT para chamadas autenticadas
 *  - login(email, senha): autentica e redireciona
 *  - logout(): limpa sessão
 *  - loading: booleano enquanto verifica sessão inicial
 */

import { createContext, useContext, useState, useEffect, useCallback } from "react";
import { authApi } from "../api/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Restaura sessão do localStorage ao carregar a SPA
  useEffect(() => {
    const storedToken = localStorage.getItem("colhe_token");
    const storedUsuario = localStorage.getItem("colhe_usuario");
    if (storedToken && storedUsuario) {
      setToken(storedToken);
      setUsuario(JSON.parse(storedUsuario));
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (email, senha) => {
    const data = await authApi.login(email, senha);
    // Backend retorna: { token: "...", usuario: { id, nome, papel, termoAceito, ... } }
    setToken(data.token);
    setUsuario(data.usuario);
    localStorage.setItem("colhe_token", data.token);
    localStorage.setItem("colhe_usuario", JSON.stringify(data.usuario));
    return data.usuario;
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    setUsuario(null);
    localStorage.removeItem("colhe_token");
    localStorage.removeItem("colhe_usuario");
  }, []);

  const atualizarUsuario = useCallback((novosDados) => {
    const atualizado = { ...usuario, ...novosDados };
    setUsuario(atualizado);
    localStorage.setItem("colhe_usuario", JSON.stringify(atualizado));
  }, [usuario]);

  return (
    <AuthContext.Provider value={{ usuario, token, loading, login, logout, atualizarUsuario }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de AuthProvider");
  return ctx;
}
