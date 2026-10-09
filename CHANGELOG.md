# Histórico de versões — Oficina Aprender

Este arquivo registra a evolução do backend/API do Oficina Aprender. As alterações do site e do aplicativo mobile devem ser registradas nos respectivos projetos.

## Atualização do backend — 09/10/2026

Registro das alterações presentes no arquivo `OficinaAprender(1).zip`, em relação à base de código analisada anteriormente. Esta atualização ainda possui a pendência de login descrita abaixo.

### Autenticação e segurança

- Adição do Spring Security ao backend.
- Criação de `SecurityConfig` para centralizar as regras de autenticação e a configuração de CORS.
- Configuração de autenticação stateless, sem sessão HTTP no servidor.
- Adição de `TokenFilter` para autenticar requisições com JWT.
- Proteção dos endpoints, mantendo `/auth/login` e `/usuario/criar` públicos na configuração atual.
- Validação do cabeçalho `Authorization: Bearer <token>`, com tratamento de token ausente, inválido ou expirado.
- Criptografia das senhas com BCrypt no cadastro e na atualização de senha.
- Aumento da validade do JWT de 30 segundos para 2 horas.
- Reorganização de `TokenService`, do pacote `Service/Settings` para `Service/Token`.

### Login

- Criação de `LoginController` e `LoginService`.
- Criação de `LoginRequestDTO` e `LoginResponseDTO`.
- Implementação de comparação de senha com `PasswordEncoder.matches` no novo serviço de login.
- Retorno do token JWT junto aos dados do usuário no novo fluxo.

### Cadastro e gerenciamento de usuários

- Alteração de `POST /usuario/criar` para receber `multipart/form-data`.
- Recebimento dos dados de cadastro na parte `dados`, em JSON, e da imagem na parte `foto`.
- Criação de `UsuarioCriarDTO` para representar os dados do cadastro.
- Exigência de foto para concluir o cadastro.
- Validação de e-mail, nome, tipo de usuário e senha.
- Normalização do e-mail, removendo espaços nas extremidades e convertendo para letras minúsculas.
- Verificação de e-mail já cadastrado.
- Validação da senha com mínimo de 8 caracteres e máximo de 72 bytes.
- Gravação do usuário e do perfil na mesma transação.
- Geração de token JWT após o cadastro, com retorno de token e dados do usuário.
- Criação de `GET /usuario/me` para recuperar os dados do usuário autenticado pelo token.
- Adição do campo `token` a `UsuarioResponseDTO`.
- Adição de validação JWT às operações de usuários.

### Fotos de perfil

- Armazenamento das fotos em `uploads/perfis`, com possibilidade de configurar o diretório pela propriedade `app.uploads.fotos-directory`.
- Geração de nomes de arquivo com UUID.
- Validação de formatos PNG, JPEG e GIF.
- Limite de tamanho de 10 MB por imagem.
- Limite de resolução de 25 milhões de pixels.
- Validação da leitura do conteúdo da imagem.
- Remoção da foto salva quando o cadastro falha ou a transação é revertida.
- Criação de `GET /usuario/foto?id=...`, com autenticação JWT, para consultar a foto.
- Definição do tipo de conteúdo da imagem e dos cabeçalhos `Cache-Control: no-store` e `X-Content-Type-Options: nosniff` na resposta.

### Perfil do usuário

- Adição do campo interno `fotoArquivo` para identificar a imagem armazenada.
- Adição dos campos `horarioDeEntrada`, `horarioDeSaida` e `condicao`.
- Ajustes no controle das datas de criação e alteração do perfil.
- Reorganização dos endpoints de consulta, criação e edição de perfil.
- Adição de validação JWT às operações de perfil.

### Metas, postagens, relações e anexos

- Criação de `MetaController` e do endpoint `POST /meta/criar`.
- Adição de validação JWT nas operações de metas e postagens.
- Adição de validação JWT nas operações de relação entre usuários e alunos.
- Atualização dos controllers e services de anexos para exigir autenticação nas operações de criação, consulta, edição e exclusão.

### Banco de dados e ambiente

- Substituição da dependência do H2 pela configuração de PostgreSQL no backend.
- Adição do driver PostgreSQL ao Gradle.
- Atualização de `application.properties` para conexão com PostgreSQL.
- Criação de `docker-compose.yml` com PostgreSQL 17.
- Configuração de volume persistente e verificação de disponibilidade do banco no Docker Compose.
- Adição de `.env.example` com as variáveis utilizadas pelo Docker Compose.

### Mudanças no contrato da API

| Endpoint | Alteração |
| --- | --- |
| `POST /usuario/criar` | Cadastro passa a exigir multipart com as partes `dados` e `foto`; retorna token e usuário. |
| `GET /usuario/me` | Novo endpoint para consultar o usuário autenticado. |
| `GET /usuario/foto?id=...` | Novo endpoint autenticado para consultar a foto de perfil. |
| `GET /perfil/{id}` | Consulta de perfil substitui o antigo `POST /perfil?id=...`. |
| `POST /perfil/criar` | Novo endpoint para criar perfil. |
| `PUT /perfil/{id}` | Edição de perfil pelo identificador na URL. |
| `POST /meta/criar` | Novo endpoint para criação de metas. |
| Operações protegidas | Exigem o cabeçalho `Authorization: Bearer <token>`. |

O site e o aplicativo mobile precisam adaptar o cadastro ao envio multipart e enviar o token nas requisições protegidas.

### Organização do código

- Criação de DTOs específicos para login e cadastro.
- Atualização de controllers e services para receber e validar o cabeçalho de autorização.
- Reorganização do serviço de tokens.
- Ajustes de formatação e quebras de linha em arquivos do projeto, sem mudança funcional nos trechos que tiveram somente essas alterações.

### Pendência conhecida nesta atualização

Existem dois endpoints de login: `POST /auth/login` e `POST /login`.

- O `AuthController`, em `/auth/login`, ainda compara a senha diretamente com `equals`, o que não é compatível com as senhas BCrypt geradas pelo cadastro atual.
- O novo `LoginService`, utilizado por `/login`, compara a senha com BCrypt, mas `/login` ainda não está entre as rotas públicas de `SecurityConfig`.

É necessário alinhar os endpoints, a comparação de senha e a configuração de segurança antes de considerar o novo fluxo de login concluído. Este histórico não registra essa correção como realizada.
