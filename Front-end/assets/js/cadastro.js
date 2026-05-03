/* ============================================================
   cadastro.js — Lógica exclusiva da tela de cadastro
   Oficina Aprender+
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {

  /* Inicializa tema */
  initTheme();

  /* Toggles de senha */
  makePasswordToggle('toggleSenha', 'senha');
  makePasswordToggle('toggleConfirmar', 'confirmarSenha');

  /* ── Seleção de perfil ──────────────────────────────── */
  const roleCards = document.querySelectorAll('.role-card');
  const roleInput = document.getElementById('roleInput');

  roleCards.forEach(card => {
    const activate = () => {
      roleCards.forEach(c => c.classList.remove('active'));
      card.classList.add('active');
      roleInput.value = card.dataset.role;
    };
    card.addEventListener('click', activate);
    card.addEventListener('keydown', e => {
      if (e.key === 'Enter' || e.key === ' ') activate();
    });
  });

  /* ── Força da senha ─────────────────────────────────── */
  const senhaInput   = document.getElementById('senha');
  const strengthFill = document.getElementById('strengthFill');
  const strengthText = document.getElementById('strengthText');

  senhaInput.addEventListener('input', () => {
    const v = senhaInput.value;
    let score = 0;
    if (v.length >= 6)  score++;
    if (v.length >= 10) score++;
    if (/[A-Z]/.test(v) && /[a-z]/.test(v)) score++;
    if (/\d/.test(v))   score++;
    if (/[^a-zA-Z0-9]/.test(v)) score++;

    const pct   = (score / 5) * 100;
    const color = score <= 1 ? '#e33d29' : score <= 3 ? '#f5a623' : '#1db87e';
    const label = score <= 1 ? 'Senha fraca' : score <= 3 ? 'Senha moderada' : 'Senha forte';

    strengthFill.style.width      = pct + '%';
    strengthFill.style.background = color;
    strengthText.style.color      = color;
    strengthText.textContent      = v.length ? label : '';
  });

  /* ── Submissão do formulário ────────────────────────── */
  const form   = document.getElementById('cadastroForm');
  const btnCad = document.getElementById('btnCadastro');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideFeedback('feedback');

    const nome           = document.getElementById('nome').value.trim();
    const email          = document.getElementById('email').value.trim();
    const senha          = senhaInput.value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;
    const role           = roleInput.value;
    const termos         = document.getElementById('termos').checked;

    /* Validações */
    if (!nome)
      return showFeedback('feedback', 'Informe seu nome completo.', 'error');
    if (!email || !email.includes('@'))
      return showFeedback('feedback', 'Informe um e-mail válido.', 'error');
    if (senha.length < 6)
      return showFeedback('feedback', 'A senha deve ter no mínimo 6 caracteres.', 'error');
    if (senha !== confirmarSenha)
      return showFeedback('feedback', 'As senhas não coincidem.', 'error');
    if (!termos)
      return showFeedback('feedback', 'Você precisa aceitar os termos para continuar.', 'error');

    btnCad.disabled = true;
    btnCad.classList.add('loading');

    try {
      await apiFetch('/usuarios/criar-conta', {
        method: 'POST',
        body: JSON.stringify({ email, senha, role }),
      });

      showFeedback('feedback', '✓ Conta criada com sucesso! Redirecionando para o login…', 'success');
      setTimeout(() => { window.location.href = 'login.html'; }, 1800);

    } catch (err) {
      showFeedback('feedback', '❌ ' + (err.message || 'Erro ao conectar com o servidor.'), 'error');
    } finally {
      btnCad.disabled = false;
      btnCad.classList.remove('loading');
    }
  });

});
