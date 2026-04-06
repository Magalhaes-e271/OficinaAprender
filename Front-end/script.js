const BASE_URL = "http://localhost:8080";

// Objetos de input e mensagens de erro
const form = document.getElementById("form_cadastro");
const nomeInput = document.getElementById("nome");
const emailInput = document.getElementById("email");
const senhaInput = document.getElementById("senha");
const confirmarSenhaInput = document.getElementById("confirmarSenha");
const ocupacaoInput = document.getElementById("ocupacao");
const verSenhasCheckbox = document.getElementById("verSenhas");

const erroNome = document.getElementById("erroNome");
const erroEmail = document.getElementById("erroEmail");
const erroSenha = document.getElementById("erroSenha");
const erroConfirmarSenha = document.getElementById("erroConfirmarSenha");
const erroOcupacao = document.getElementById("erroOcupacao");
const mensagemGeral = document.getElementById("mensagemGeral");

// Regex do e-mail
const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// Funções de erro
function limparErro(campoErro) {
    campoErro.textContent = "";
    campoErro.style.display = "none";
}

function mostrarErro(campoErro, mensagem) {
    campoErro.textContent = mensagem;
    campoErro.style.display = "block";
    campoErro.style.color = "red";
}

function limparErros() {
    limparErro(erroNome);
    limparErro(erroEmail);
    limparErro(erroSenha);
    limparErro(erroConfirmarSenha);
    limparErro(erroOcupacao);
    limparErro(mensagemGeral);
}

// Define a role com base na ocupação
function verificarOcupacao(valor) {
    if (valor === "Familia") {
        return "ROLE_FAMILIA";
    }
    return "ROLE_PROFISSIONAL";
}

// Validação do formulário
function validarFormulario() {
    limparErros();
    let valido = true;

    if (nomeInput.value.trim() === "") {
        mostrarErro(erroNome, "Digite o nome.");
        valido = false;
    }

    if (emailInput.value.trim() === "") {
        mostrarErro(erroEmail, "Digite o email.");
        valido = false;
    } else if (!regexEmail.test(emailInput.value.trim())) {
        mostrarErro(erroEmail, "Digite um email válido.");
        valido = false;
    }

    if (senhaInput.value.trim() === "") {
        mostrarErro(erroSenha, "Digite a senha.");
        valido = false;
    }

    if (confirmarSenhaInput.value.trim() === "") {
        mostrarErro(erroConfirmarSenha, "Confirme a senha.");
        valido = false;
    } else if (senhaInput.value !== confirmarSenhaInput.value) {
        mostrarErro(erroConfirmarSenha, "As senhas não coincidem.");
        valido = false;
    }

    if (ocupacaoInput.value === "") {
        mostrarErro(erroOcupacao, "Selecione uma ocupação.");
        valido = false;
    }

    return valido;
}

// Garantir que as senhas estejam ocultas ao carregar a página
window.addEventListener("load", function () {
    verSenhasCheckbox.checked = false;
    senhaInput.type = "password";
    confirmarSenhaInput.type = "password";
});

// Mostrar ou ocultar senhas
verSenhasCheckbox.addEventListener("change", function () {
    const tipo = verSenhasCheckbox.checked ? "text" : "password";
    senhaInput.type = tipo;
    confirmarSenhaInput.type = tipo;
});

// Limpa erro da ocupação ao selecionar
ocupacaoInput.addEventListener("change", function () {
    if (ocupacaoInput.value !== "") {
        limparErro(erroOcupacao);
    }
});

// Validação em tempo real do e-mail
emailInput.addEventListener("input", function () {
    const valor = emailInput.value.trim();

    if (valor === "") {
        limparErro(erroEmail);
        return;
    }

    if (!regexEmail.test(valor)) {
        mostrarErro(erroEmail, "Digite um email válido.");
    } else {
        limparErro(erroEmail);
    }
});

// Validação em tempo real das senhas
function validarSenhasEmTempoReal() {
    if (
        senhaInput.value.trim() !== "" &&
        confirmarSenhaInput.value.trim() !== "" &&
        senhaInput.value !== confirmarSenhaInput.value
    ) {
        mostrarErro(erroConfirmarSenha, "As senhas não coincidem.");
    } else {
        limparErro(erroConfirmarSenha);
    }
}

senhaInput.addEventListener("input", validarSenhasEmTempoReal);
confirmarSenhaInput.addEventListener("input", validarSenhasEmTempoReal);

// Envio do formulário
form.addEventListener("submit", async function (event) {
    event.preventDefault();

    if (!validarFormulario()) {
        return;
    }

    const dados = {
        email: emailInput.value.trim(),
        senha: senhaInput.value.trim(),
        role: verificarOcupacao(ocupacaoInput.value)
    };

    try {
        const response = await fetch(`${BASE_URL}/usuarios/criar-conta`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        if (response.ok) {
            mensagemGeral.style.color = "green";
            mensagemGeral.style.display = "block";
            mensagemGeral.textContent = "Cadastro realizado com sucesso.";

            setTimeout(() => {
                window.location.href = "./index.html";
            }, 1000);

            return;
        }

        const texto = await response.text();
        mostrarErro(mensagemGeral, texto || "Erro ao cadastrar.");
    } catch (erro) {
        mostrarErro(mensagemGeral, "Não foi possível conectar ao servidor.");
        console.error(erro);
    }
});