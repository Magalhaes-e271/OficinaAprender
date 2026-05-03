/* ============================================================
   utils.js — Utilitários compartilhados entre todas as páginas
   Oficina Aprender+
   ============================================================ */

const API_BASE = 'http://localhost:8080';

/* ── Modo escuro ─────────────────────────────────────────── */
function initTheme() {
  const html        = document.documentElement;
  const themeToggle = document.getElementById('themeToggle');
  const themeIcon   = document.getElementById('themeIcon');
  const themeLabel  = document.getElementById('themeLabel');

  const saved = localStorage.getItem('theme') || 'light';
  html.setAttribute('data-theme', saved);
  _updateToggleUI(saved, themeIcon, themeLabel);

  themeToggle.addEventListener('click', () => {
    const next = html.getAttribute('data-theme') === 'light' ? 'dark' : 'light';
    html.setAttribute('data-theme', next);
    localStorage.setItem('theme', next);
    _updateToggleUI(next, themeIcon, themeLabel);
  });
}

function _updateToggleUI(theme, icon, label) {
  icon.textContent  = theme === 'dark' ? '☀️' : '🌙';
  label.textContent = theme === 'dark' ? 'Modo Claro' : 'Modo Escuro';
}

/* ── Feedback de formulário ──────────────────────────────── */
function showFeedback(elementId, msg, type) {
  const el = document.getElementById(elementId);
  el.textContent = msg;
  el.className = `feedback ${type} show`;
}

function hideFeedback(elementId) {
  document.getElementById(elementId).className = 'feedback';
}

/* ── Toggle de senha ─────────────────────────────────────── */
function makePasswordToggle(btnId, inputId) {
  const btn = document.getElementById(btnId);
  const inp = document.getElementById(inputId);
  if (!btn || !inp) return;
  btn.addEventListener('click', () => {
    const visible = inp.type === 'text';
    inp.type = visible ? 'password' : 'text';
    btn.textContent = visible ? '👁' : '🙈';
  });
}

/* ── Sessão do usuário ───────────────────────────────────── */
function getUsuario() {
  const data = sessionStorage.getItem('usuario');
  return data ? JSON.parse(data) : null;
}

function setUsuario(data) {
  sessionStorage.setItem('usuario', JSON.stringify(data));
}

function logout() {
  sessionStorage.removeItem('usuario');
  window.location.href = '../pages/login.html';
}

/* ── Requisição à API ────────────────────────────────────── */
async function apiFetch(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || `Erro ${res.status}`);
  return data;
}
