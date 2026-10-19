# Entrega da Oficina Aprender

## Escopo verificado

Fluxos do site Front-TCC e da API OficinaAprender: conta e sessão, perfil e foto, pacientes e equipe, convites, metas e dependências, publicações e respostas, anexos, agenda e relatório imprimível. A ativação de paciente usa link de uso único; a entrega do link é feita pelo profissional. O sistema não envia e-mails de recuperação de senha.

## Evidências automatizadas

- Backend: seis testes de integração com várias verificações HTTP por teste, PostgreSQL 17 e Java 21.
- Acesso sem token, token adulterado, conta de outro profissional, convite pendente, aceite de outro usuário e revogação de vínculo.
- Criação idempotente de paciente; rejeição de dependência entre pacientes e de ciclo; conclusão e reabertura dependentes.
- Visibilidade de postagem para paciente e profissional; associação de autor e meta; rollback de publicação com anexo inválido; download e exclusão.
- Agenda: intervalos inválidos, limites contíguos, outro usuário e seis requisições concorrentes, com uma única reserva confirmada.
- Migração de estrutura legada com dados preservados; reinício da API com foto e sessão preservadas; backup PostgreSQL restaurado com arquivo presente.
- Frontend: 23 testes com DOM real para cadastro multipart, erros, sessão, perfil, foto, senha, equipe, ativação, relatório e referências locais.

A execução do backend que aprovou esses cenários está em [GitHub Actions](https://github.com/Magalhaes-e271/OficinaAprender/actions/runs/39272115923). As execuções posteriores acompanham os commits publicados.

## Operação

O banco anterior tinha seis usuários e seis perfis, sem pacientes vinculados, metas, postagens ou anexos. Um backup privado desses registros foi criado antes da migração. Segredos e dados de backup não estão no Git. O cliente deve autenticar novamente após a troca da chave JWT.

Arquivos novos são armazenados no banco. Arquivos antigos que já tenham desaparecido do disco temporário não podem ser reconstruídos a partir de seu nome; o usuário pode reenviar a foto na página de perfil.

O aplicativo Android foi identificado em MatheusOLiveira03/TCC. A integração substitui o login local simulado por JWT real, ativa contas com token de uso único e consulta os mesmos pacientes, metas, registros e vínculos. A compilação e o APK são verificados pelo workflow desse repositório. Os endereços antigos de login permanecem como adaptadores para outros clientes.

A entrega completa do frontend está em `frontend/` na branch `codex/entrega-frontend` deste repositório. A integração não recebeu acesso de escrita ao Front-TCC (403), por isso a publicação de entrega usa essa branch autorizada. O site possui CI próprio com os 23 testes da interface. A publicação de entrega está em https://oficina-aprender-tcc.onrender.com.

## Voltar à versão anterior

A publicação do Render permite selecionar uma versão anterior da API e do site. As migrações são aditivas e preservam as colunas originais. Faça backup do estado atual antes de restaurar dados. Use um banco vazio para ensaiar a restauração; confirme a cópia antes de substituir qualquer ambiente em uso. Não apague o banco como procedimento de deploy.
