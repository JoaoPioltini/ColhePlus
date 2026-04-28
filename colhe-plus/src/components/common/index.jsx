/**
 * Componentes comuns reutilizáveis - Colhe+
 * Button, Input, Badge, Card, Modal, Spinner, Navbar, Sidebar
 */

import { useState } from "react";
import { useAuth } from "../../context/AuthContext";

// ─── Button ───────────────────────────────────────────────────────────────────
export function Button({
  children,
  variant = "primary",
  size = "md",
  loading = false,
  disabled = false,
  fullWidth = false,
  onClick,
  type = "button",
  className = "",
}) {
  const base =
    "inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed";

  const variants = {
    primary: "bg-green-700 hover:bg-green-800 text-white focus:ring-green-600 shadow-sm",
    secondary: "bg-white border-2 border-green-700 text-green-700 hover:bg-green-50 focus:ring-green-600",
    danger: "bg-red-500 hover:bg-red-600 text-white focus:ring-red-500 shadow-sm",
    ghost: "text-green-700 hover:bg-green-50 focus:ring-green-600",
    warning: "bg-amber-50 border-2 border-amber-400 text-amber-700 hover:bg-amber-100 focus:ring-amber-400",
  };

  const sizes = {
    sm: "px-3 py-1.5 text-sm gap-1.5",
    md: "px-5 py-2.5 text-sm gap-2",
    lg: "px-6 py-3 text-base gap-2",
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={`${base} ${variants[variant]} ${sizes[size]} ${fullWidth ? "w-full" : ""} ${className}`}
    >
      {loading && (
        <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
      )}
      {children}
    </button>
  );
}

// ─── Input ────────────────────────────────────────────────────────────────────
export function Input({
  label,
  error,
  type = "text",
  placeholder,
  value,
  onChange,
  required,
  className = "",
  ...props
}) {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      {label && (
        <label className="text-sm font-medium text-gray-700">
          {label} {required && <span className="text-red-500">*</span>}
        </label>
      )}
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required={required}
        className={`w-full px-4 py-2.5 rounded-xl border text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent ${
          error ? "border-red-400 bg-red-50" : "border-gray-200 bg-white hover:border-gray-300"
        }`}
        {...props}
      />
      {error && <span className="text-xs text-red-500">{error}</span>}
    </div>
  );
}

// ─── Badge ────────────────────────────────────────────────────────────────────
export function Badge({ status }) {
  const map = {
    ABERTO:    { label: "Aberto",    cls: "bg-emerald-100 text-emerald-700 border border-emerald-200" },
    ATIVADO:   { label: "Ativado",   cls: "bg-blue-100 text-blue-700 border border-blue-200" },
    CANCELADO: { label: "Cancelado", cls: "bg-gray-100 text-gray-600 border border-gray-200" },
    DESATIVADO:{ label: "Desativado",cls: "bg-red-100 text-red-600 border border-red-200" },
    PENDENTE:  { label: "Pendente",  cls: "bg-amber-100 text-amber-700 border border-amber-200" },
    ACEITO:    { label: "Aceito",    cls: "bg-blue-100 text-blue-700 border border-blue-200" },
    RECUSADO:  { label: "Recusado",  cls: "bg-red-100 text-red-600 border border-red-200" },
  };

  const config = map[status] || { label: status, cls: "bg-gray-100 text-gray-600" };

  return (
    <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold ${config.cls}`}>
      {config.label}
    </span>
  );
}

// ─── Card ─────────────────────────────────────────────────────────────────────
export function Card({ children, className = "" }) {
  return (
    <div className={`bg-white rounded-2xl border border-gray-100 shadow-sm ${className}`}>
      {children}
    </div>
  );
}

// ─── Modal ────────────────────────────────────────────────────────────────────
export function Modal({ isOpen, onClose, title, children }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onClose}
      />
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between p-6 border-b border-gray-100">
          <h2 className="text-lg font-bold text-green-800">{title}</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div className="p-6">{children}</div>
      </div>
    </div>
  );
}

// ─── Spinner ──────────────────────────────────────────────────────────────────
export function Spinner({ size = "md" }) {
  const sizes = { sm: "h-4 w-4", md: "h-8 w-8", lg: "h-12 w-12" };
  return (
    <div className="flex items-center justify-center p-8">
      <svg
        className={`animate-spin ${sizes[size]} text-green-700`}
        fill="none"
        viewBox="0 0 24 24"
      >
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
      </svg>
    </div>
  );
}

// ─── Toast / Notificação simples ──────────────────────────────────────────────
export function Toast({ message, type = "success", onClose }) {
  const colors = {
    success: "bg-green-700 text-white",
    error: "bg-red-600 text-white",
    info: "bg-blue-600 text-white",
  };

  return (
    <div
      className={`fixed bottom-6 right-6 z-50 px-5 py-3 rounded-xl shadow-lg flex items-center gap-3 ${colors[type]}`}
    >
      <span className="text-sm font-medium">{message}</span>
      <button onClick={onClose} className="opacity-70 hover:opacity-100">✕</button>
    </div>
  );
}

// ─── Navbar ───────────────────────────────────────────────────────────────────
export function Navbar({ onLogout }) {
  const { usuario } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-gray-100 shadow-sm flex items-center px-6 gap-4 sticky top-0 z-40">
      {/* Logo */}
      <div className="flex items-center gap-2">
        <span className="text-2xl">🌿</span>
        <span className="text-xl font-bold text-green-800 tracking-tight">Colhe+</span>
      </div>

      <div className="flex-1" />

      {usuario ? (
        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600 hidden sm:block">
            Olá, <span className="font-semibold text-gray-800">{usuario.nome?.split(" ")[0]}</span>
          </span>
          <button
            onClick={onLogout}
            className="px-4 py-1.5 text-sm border border-gray-200 rounded-xl text-gray-600 hover:bg-gray-50 transition-colors"
          >
            Sair
          </button>
        </div>
      ) : (
        <div className="flex items-center gap-3 text-sm text-gray-500">
          <span>Sobre</span>
          <span>Contato</span>
        </div>
      )}
    </header>
  );
}

// ─── Sidebar ──────────────────────────────────────────────────────────────────
export function Sidebar({ items, activePage, onNavigate, role }) {
  const roleLabel = {
    PRODUTOR: "Painel do Produtor",
    COMPRADOR: "Painel do Comprador",
    ADMIN: "Painel Admin",
  };

  return (
    <aside className="w-64 bg-white border-r border-gray-100 min-h-[calc(100vh-4rem)] flex flex-col">
      <div className="p-4 pt-6">
        <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-4">
          {roleLabel[role] || "Painel"}
        </p>
        <nav className="flex flex-col gap-1">
          {items.map((item) => (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className={`w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors ${
                activePage === item.id
                  ? "bg-green-50 text-green-800 font-semibold"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </div>
    </aside>
  );
}

// ─── ProgressBar ──────────────────────────────────────────────────────────────
export function ProgressBar({ value, max, className = "" }) {
  const pct = Math.min(100, Math.round((value / max) * 100));
  const color = pct >= 100 ? "bg-blue-500" : "bg-green-500";

  return (
    <div className={`w-full bg-gray-100 rounded-full h-2 ${className}`}>
      <div
        className={`h-2 rounded-full transition-all duration-500 ${color}`}
        style={{ width: `${pct}%` }}
      />
    </div>
  );
}

// ─── StatCard ─────────────────────────────────────────────────────────────────
export function StatCard({ label, value, color = "text-gray-900" }) {
  return (
    <Card className="p-5">
      <p className="text-sm text-gray-500 mb-1">{label}</p>
      <p className={`text-3xl font-bold ${color}`}>{value}</p>
    </Card>
  );
}

// ─── EmptyState ───────────────────────────────────────────────────────────────
export function EmptyState({ icon = "🌱", title, description }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <span className="text-5xl mb-4">{icon}</span>
      <h3 className="text-lg font-semibold text-gray-700 mb-1">{title}</h3>
      {description && <p className="text-sm text-gray-500 max-w-xs">{description}</p>}
    </div>
  );
}

// ─── ErrorMessage ─────────────────────────────────────────────────────────────
export function ErrorMessage({ message }) {
  if (!message) return null;
  return (
    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm">
      {message}
    </div>
  );
}
