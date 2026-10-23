# Oficina Aprender API

API do acompanhamento colaborativo de pacientes, com contas profissionais e de pacientes, equipe, metas, postagens, anexos e agenda. Implementada com Java 21, Spring Boot 4.0.8, Gradle 9.4 e PostgreSQL 17.

## Funcionalidades

- Cadastro com dados e foto na mesma transação; login BCrypt e JWT; sessão e alteração de senha.
- Perfil pessoal e profissional com edição, horários e troca de foto.
- Cadastro de pacientes, vínculo inicial e convites à equipe; repetição segura do mesmo cadastro após perda de resposta.
- Busca reduzida de profissionais, solicitações, aceite, recusa e revogação.
- Metas com dependências do mesmo paciente, rejeição de ciclos, conclusão e reabertura coerentes.
- Postagens vinculadas à meta e ao autor, respostas e visibilidade para equipe ou todos os envolvidos.
- Anexos com download autorizado, limites no servidor e gravação atômica com a postagem.
- Agenda em UTC com validação de intervalos e bloqueios no PostgreSQL contra reservas concorrentes.
- Ativação de paciente por link de uso único emitido pelo profissional responsável pelo cadastro.

Fotos e anexos ficam no PostgreSQL, na tabela `arquivo`, e permanecem disponíveis após reinícios. O banco local começa vazio; use a tela Meu perfil para enviar ou atualizar a foto.

## Proteção de autenticação

Limites de tentativas compartilhados pelo PostgreSQL e autenticação em duas etapas para contas profissionais. A ativação é feita pelo próprio profissional após confirmar um código do aplicativo autenticador. Consulte [PROTECAO_LOGIN.md](docs/PROTECAO_LOGIN.md) para instalar, gerar a chave local, usar códigos de recuperação e verificar dependências.

## Executar localmente

A execução recomendada está em `../LEIA-ME.md`: use o iniciador na raiz do pacote. O `compose.yaml` da raiz inicia PostgreSQL, API e site integrados; ele cria um banco novo e não acessa serviços externos.

Para executar só a API com JDK 21 e um PostgreSQL seu, configure as variáveis abaixo e execute `gradlew.bat bootRun` no Windows ou `bash gradlew bootRun` em Linux/macOS. Veja os comandos em `../documentacao/DESENVOLVIMENTO.md`.

| Variável | Finalidade |
| --- | --- |
| `SPRING_DATASOURCE_URL` | URL JDBC do banco escolhido |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Segredo aleatório com pelo menos 32 bytes |
| `CORS_ALLOWED_ORIGINS` | Origens exatas do site, separadas por vírgula |
| `PORT` | Porta da API; padrão local 8080 |
| `MFA_ENCRYPTION_KEY` | Chave Base64 de 32 bytes, separada do JWT, necessária para configurar o autenticador |

O iniciador gera segredos locais no `.env`. Na execução manual sem `JWT_SECRET`, a aplicação gera uma chave aleatória e a preserva na tabela `app_segredo`. Alterar a senha de uma conta revoga as sessões dessa conta; trocar a chave JWT revoga todas as sessões.

O Dockerfile executa Java com usuário sem privilégios. `GET /health` responde 200 após verificar o banco e as tabelas necessárias. Nenhum iniciador deste pacote publica código ou modifica GitHub ou Render.

## Permissões

| Recurso | Regra |
| --- | --- |
| Conta profissional e perfil completo | Próprio usuário |
| Resumo de profissional e avatar | Usuário autenticado; busca disponível aos profissionais |
| Paciente e registros | Paciente autenticado ou profissional com vínculo aceito |
| Convite pendente | Não concede acesso aos registros |
| Criação ou conclusão de metas | Profissional com vínculo aceito |
| Postagem restrita | Profissional com vínculo aceito |
| Edição ou exclusão de postagem/anexo | Autor com acesso ao paciente |
| Aceite de convite | Destinatário |
| Agenda | Profissional responsável; paciente também é protegido contra sobreposição |

A identidade vem do contexto de segurança. Autor, dono, datas e aceite não são definidos pelo corpo enviado pelo cliente. Encerrar uma conta revoga suas sessões, anonimiza seu perfil e preserva referências de autoria dos registros.

## Rotas e contratos

`POST /login` retorna `{token, usuario}`. O adaptador `POST /auth/login` usa o mesmo serviço BCrypt e preserva o formato antigo com token e dados no nível principal.

| Módulo | Operações |
| --- | --- |
| Usuário | `POST /usuario/criar` multipart `dados` + `foto`; `GET /usuario/me`; `PUT /usuario/descricao?id`; `POST /usuario/foto?id` multipart `arquivo`; `PUT /usuario/senha` |
| Paciente | `GET /paciente`; `GET /paciente/{id}`; `POST /paciente/criar`; `GET/POST /paciente/{id}/equipe` |
| Ativação | `POST /paciente/{id}/ativacao`; `POST /auth/ativar` com token e senha; `POST /auth/ativacao/consultar` para conferir o primeiro acesso no Android |
| Equipe | `GET /usuario/buscar?nome`; `GET /usuario-aluno`; `POST /usuario-aluno/criar`; `PUT /usuario-aluno/{id}/aceitar`; `DELETE /usuario-aluno/{id}` |
| Metas | `GET /meta?idPaciente`; `POST /meta/criar?idPaciente`; `GET/PATCH/DELETE /meta/{id}` |
| Postagens | `GET /postagem?idMeta`; `POST /postagem/publicar?idMeta` multipart `dados` + `arquivos`; `POST /postagem/criar?idMeta`; `PATCH/DELETE /postagem/{id}` |
| Anexos | `GET /anexo/{id}/arquivo`; criação, consulta, renomeação e exclusão autenticadas |
| Agenda | `GET /agenda`; `POST /agenda/criar`; `DELETE /agenda/{id}` |

Erros: 400 dados inválidos, 401 sessão inválida, 403 acesso proibido, 404 ausente, 409 conflito e 413 limite de upload. A resposta de erro tem `status` e `message`. As publicações aceitam até 10 arquivos, até 10 MB cada e até 11 MB somados. HTML ativo é removido pelo servidor e pelo site.

Quando o segundo fator está ativo, as duas rotas de login exigem também `codigo`. Sem ele, retornam HTTP 428 com `codigo: "MFA_REQUIRED"`; nenhum token de acesso é emitido. Limites excedidos retornam HTTP 429 e `Retry-After`.

## Banco e migrações

Flyway controla `db/migration`. A versão V1 reconhece a estrutura anterior; V2 adiciona pacientes, responsáveis, respostas, arquivos e agenda. V3 acrescenta versão e último editor das metas; V4 acrescenta limites de autenticação, configuração MFA cifrada e hashes de recuperação. As migrações preservam dados e não usam `ddl-auto=update`. Este pacote não inclui usuários, senhas ou backups de nenhum ambiente externo.

Faça backup antes de atualizar um banco com dados. A migração interrompe a atualização se houver vínculos duplicados; resolva a inconsistência sem apagar registros às cegas. A execução antiga não precisa acessar as novas colunas caso seja necessário reverter o código.

## Testes

```powershell
$env:TEST_DATABASE_URL="jdbc:postgresql://localhost:5436/oficina_aprender_test"
$env:TEST_DATABASE_USERNAME="oficina_user"
$env:TEST_DATABASE_PASSWORD="SENHA_DO_BANCO_DE_TESTE"
.\gradlew.bat test
```

Crie um banco exclusivo terminado em `_test`. Os testes recusam outro nome e limpam somente esse banco dedicado. A raiz do pacote inclui `compose.testes.yaml` para repetir a suíte em um banco temporário PostgreSQL 17. Os resultados e os limites da verificação desta entrega estão em `../documentacao/TESTES.md`.
