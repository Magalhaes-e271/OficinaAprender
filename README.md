# Oficina Aprender API

API do acompanhamento colaborativo de pacientes, com contas profissionais e de pacientes, equipe, metas, postagens, anexos e agenda. Implementada com Java 21, Spring Boot 4.0.4, Gradle 9.4 e PostgreSQL 17.

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

Fotos e anexos novos ficam no PostgreSQL, na tabela `arquivo`. A publicação não depende do disco temporário do Render. A atualização não consegue reconstruir arquivos antigos que já desapareceram desse disco; a tela Meu perfil permite enviar a foto novamente.

## Executar localmente

Requisitos: JDK 21 e Docker com Docker Compose. Copie `.env.example` para `.env` e defina uma senha local. O arquivo `.env` não deve ser enviado ao GitHub.

```powershell
Copy-Item .env.example .env
docker compose up -d
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5436/oficina_aprender"
$env:SPRING_DATASOURCE_USERNAME="oficina_user"
$env:SPRING_DATASOURCE_PASSWORD="MESMA_SENHA_DO_ARQUIVO_ENV"
.\gradlew.bat bootRun
```

No Linux/macOS, exporte as mesmas variáveis e execute `bash gradlew bootRun`. O arquivo `.env` é lido pelo Docker Compose; o Spring Boot usa as variáveis do seu processo. A porta local é 8080 e a prontidão fica em `GET /health`.

## Configuração do Render

Serviço Web com Docker, branch `master`, raiz vazia e `./Dockerfile`. O serviço atual tem publicação automática quando a branch é atualizada. Use um banco PostgreSQL na mesma região.

| Variável | Finalidade |
| --- | --- |
| `SPRING_DATASOURCE_URL` | URL JDBC real do PostgreSQL hospedado |
| `SPRING_DATASOURCE_USERNAME` | Usuário desse banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha desse banco |
| `JWT_SECRET` | Segredo aleatório com pelo menos 32 bytes |
| `CORS_ALLOWED_ORIGINS` | Origens exatas do site, separadas por vírgula |
| `PORT` | Porta fornecida pelo Render |

Quando `JWT_SECRET` não é definido, a aplicação gera uma chave aleatória e a preserva em `app_segredo`, sem colocá-la no código. Em produção é possível usar a variável do Render. Rotacionar a chave ou alterar a senha invalida sessões anteriores. Nenhuma resposta da API inclui senha ou hash.

O Dockerfile executa Java com usuário sem privilégios. `GET /health` responde 200 somente após acesso ao banco e às tabelas necessárias. O plano gratuito do Render pode suspender a API por inatividade; o site aguarda sua inicialização.

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
| Ativação | `POST /paciente/{id}/ativacao`; `POST /auth/ativar` com token e senha |
| Equipe | `GET /usuario/buscar?nome`; `GET /usuario-aluno`; `POST /usuario-aluno/criar`; `PUT /usuario-aluno/{id}/aceitar`; `DELETE /usuario-aluno/{id}` |
| Metas | `GET /meta?idPaciente`; `POST /meta/criar?idPaciente`; `GET/PATCH/DELETE /meta/{id}` |
| Postagens | `GET /postagem?idMeta`; `POST /postagem/publicar?idMeta` multipart `dados` + `arquivos`; `POST /postagem/criar?idMeta`; `PATCH/DELETE /postagem/{id}` |
| Anexos | `GET /anexo/{id}/arquivo`; criação, consulta, renomeação e exclusão autenticadas |
| Agenda | `GET /agenda`; `POST /agenda/criar`; `DELETE /agenda/{id}` |

Erros: 400 dados inválidos, 401 sessão inválida, 403 acesso proibido, 404 ausente, 409 conflito e 413 limite de upload. A resposta de erro tem `status` e `message`. As publicações aceitam até 10 arquivos, até 10 MB cada e até 11 MB somados. HTML ativo é removido pelo servidor e pelo site.

## Banco e migrações

Flyway controla `db/migration`. A versão V1 reconhece a estrutura anterior; V2 adiciona pacientes, responsáveis, respostas, arquivos e agenda. As seis contas encontradas antes da publicação foram incluídas em backup privado, fora do repositório. As migrações preservam dados e não usam `ddl-auto=update`.

Faça backup antes de atualizar um banco com dados. A migração interrompe a atualização se houver vínculos duplicados; resolva a inconsistência sem apagar registros às cegas. A execução antiga não precisa acessar as novas colunas caso seja necessário reverter o código.

## Testes

```powershell
$env:TEST_DATABASE_URL="jdbc:postgresql://localhost:5436/oficina_aprender_test"
$env:TEST_DATABASE_USERNAME="oficina_user"
$env:TEST_DATABASE_PASSWORD="SENHA_DO_BANCO_DE_TESTE"
.\gradlew.bat test
```

Crie um banco exclusivo terminado em `_test`. Os testes recusam outro nome e limpam somente esse banco dedicado. O workflow de CI cria PostgreSQL 17 e verifica os cenários HTTP, permissões, transações, dependências, concorrência, migração legada, reinício e restauração de backup. Consulte também [docs/ENTREGA.md](docs/ENTREGA.md).
