# Proteção da autenticação — entrega local de 20/10/2026

- Limite de tentativas por conta e endereço de conexão, compartilhado pelas rotas `/login` e `/auth/login`, com contadores no PostgreSQL e resposta 429 com Retry-After.
- Segundo fator TOTP para profissionais, configuração confirmada pelo usuário, segredo cifrado com chave externa e códigos de recuperação de uso único.
- Proteção de mudanças do autenticador e troca de senha por senha atual e segundo fator; revogação das sessões anteriores após mudanças de segurança.
- Tela de configuração no perfil e etapa adicional no login do site.
- Atualização de Spring Boot, bibliotecas diretas, Jackson e Tomcat; manifesto de dependências e consulta reproduzível ao OSV.
- Migração V4; script local para acrescentar a chave ao .env sem exibi-la; documentação de instalação e testes.

# Versão 1.0.0 — entrega local de 20 de outubro de 2026

- Login BCrypt unificado em `/login` e no adaptador `/auth/login`, com JWT e revogação de sessão após mudança de senha.
- Autorização por conta e vínculo aceito; ausência de senha ou hash nas respostas.
- Pacientes, equipes e convites, metas com dependências, postagens e respostas, anexos e agenda.
- Fotos e anexos persistentes no PostgreSQL, com transações e download autorizado.
- Ativação de pacientes, alteração de senha, erros padronizados e limpeza de HTML ativo.
- Migrações Flyway e testes HTTP de permissões, transações, dependências e concorrência.
- Site e Android integrados à mesma API; configuração local de servidor no APK de desenvolvimento.
- Docker Compose do conjunto, geração de segredos locais, script de backup e documentação para execução a partir do ZIP.

A entrega contém arquivos locais. Nenhuma publicação ou mudança em GitHub ou Render faz parte desta versão do pacote.
