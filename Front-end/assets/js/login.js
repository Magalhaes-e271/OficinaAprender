/* ============================================================
   login.js — Lógica exclusiva da tela de login
   Oficina Aprender+
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {

  /* Inicializa tema */
  initTheme();

  /* Toggle de senha */
  makePasswordToggle('togglePass', 'senha');

  /* Submissão do formulário */
  const form     = document.getElementById('loginForm');
  const btnLogin = document.getElementById('btnLogin');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideFeedback('feedback');

    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value;

    if (!email || !senha) {
      return showFeedback('feedback', 'Por favor, preencha todos os campos.', 'error');
    }

    btnLogin.disabled = true;
    btnLogin.classList.add('loading');

    try {
      const data = await apiFetch('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, senha }),
      });

      setUsuario(data);
      showFeedback('feedback', '✓ Login realizado! Redirecionando…', 'success');

      setTimeout(() => {
        const role = data.role;
        if (role === 'ROLE_PROFISSIONAL') {
          window.location.href = 'dashboard.html';
        } else if (role === 'ROLE_FAMILIA') {
          window.location.href = 'dashboard-familia.html';
        } else {
          window.location.href = 'dashboard.html';
        }
      }, 1200);

    } catch (err) {
      showFeedback('feedback', '❌ ' + (err.message || 'Erro ao conectar com o servidor.'), 'error');
    } finally {
      btnLogin.disabled = false;
      btnLogin.classList.remove('loading');
    }
  });

});
