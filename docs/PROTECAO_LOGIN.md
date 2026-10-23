# Proteção de autenticação — Oficina Aprender

Esta versão implementa o item 4: limite de tentativas, segundo fator para profissionais e revisão das dependências. No projeto completo de 21/10/2026 ela já está integrada às atualizações de cadastro e metas. O iniciador Docker da raiz gera a chave e a passa à API automaticamente; não precisa aplicar arquivos extras.

## Instalação pelo IntelliJ

1. Pare a API e abra os fontes de `servidor/` e `site/` deste projeto completo. Preserve o `.env` que você já utiliza.
2. No terminal dentro de `servidor/`, gere a chave no mesmo `.env` que sua configuração do IntelliJ já carrega:

   ```powershell
   python scripts/configurar_mfa.py --env .env
   ```

   Se esse arquivo estiver em outra pasta, informe o caminho correto depois de `--env`. O script acrescenta `MFA_ENCRYPTION_KEY`, preserva uma chave existente e não imprime seu valor. Ele não muda URL do banco, senha do banco, porta, CORS ou JWT.

3. Mantenha `JWT_SECRET` configurado por variável de ambiente, conforme a documentação da API. A chave do autenticador é separada dessa chave.
4. Compile os recursos e as classes:

   ```powershell
   .\gradlew.bat clean classes
   ```

5. Inicie a API pelo IntelliJ com o `.env` carregado. O Flyway aplicará `V4__protecao_autenticacao.sql` ao banco que estiver configurado.
6. Recarregue o site com Ctrl+F5. Entre e abra **Meu perfil → Configurar verificação em duas etapas**.

A API continua aceitando o login habitual das contas que ainda não ativaram o segundo fator. A ativação é feita por cada profissional, após comprovar a posse do aplicativo autenticador. Sem `MFA_ENCRYPTION_KEY`, a tela informa que a configuração ainda não está disponível; nenhuma configuração MFA é gravada sem cifrar o segredo.

O pacote completo inclui `application.properties` e `site/assets/js/config.js` com os padrões locais, além de exemplos de `.env`. Nenhum `.env` real ou credencial foi incluído. Preserve suas próprias configurações de banco, URL da API e CORS ao substituir uma pasta antiga. A migração V4 cria somente tabelas de autenticação; as migrações V1, V2 e V3 também estão incluídas. Os arquivos compartilhados com as metas já contêm as duas funcionalidades.

## Usar o autenticador

1. No perfil, abra a configuração e confirme sua senha atual.
2. Adicione uma conta em um aplicativo autenticador compatível com TOTP. Escolha chave manual e código baseado em tempo; copie a chave exibida. No mesmo celular, o link da tela também pode abrir um autenticador compatível.
3. Digite um código de seis dígitos para confirmar. A configuração pendente expira em dez minutos.
4. Guarde os dez códigos de recuperação, que são exibidos somente após a ativação. Eles podem ser baixados pelo botão da página.
5. No próximo login, informe a senha e depois o código do autenticador. Um código de recuperação disponível também serve como segundo fator.

Cada código do autenticador é aceito uma vez dentro da janela de tempo, mesmo com requisições simultâneas. Se acabou de usá-lo, aguarde o próximo código. Mantenha o horário do celular ajustado automaticamente. Cada código de recuperação também funciona uma única vez e sempre exige a senha da conta.

Na mesma tela, a senha atual e um segundo fator são necessários para desativar a proteção ou gerar outros códigos de recuperação. A geração de novos códigos invalida todos os anteriores. Ativar, desativar ou renovar os códigos revoga as sessões anteriores e renova a sessão atual. A troca de senha também exige o segundo fator quando ativo e encerra as sessões, preservando o autenticador configurado.

## Proteção do segredo e recuperação

- O segredo TOTP fica cifrado com AES-256-GCM; a chave `MFA_ENCRYPTION_KEY` deve ser preservada no ambiente e guardada separadamente do banco.
- O segredo cifrado é vinculado ao identificador do usuário; sua cópia para outro usuário não permite validá-lo.
- Os códigos de recuperação possuem 80 bits aleatórios e apenas seus hashes são armazenados no servidor.
- A chave manual, os códigos e a senha não são gravados no armazenamento de sessão do navegador. Os códigos de recuperação são removidos da tela quando o usuário confirma que os guardou.
- As respostas de configuração usam `Cache-Control: no-store`. A chave e os códigos não são enviados a geradores de QR ou a serviços externos.
- Guarde uma cópia segura da chave do ambiente antes de reinstalar ou migrar o servidor. Não gere uma chave diferente sobre uma instalação em uso.
- Se perder o autenticador, use um código de recuperação com a senha para entrar e reconfigurar a proteção. Sem autenticador e sem códigos de recuperação, o caso exige um procedimento de recuperação de identidade pelo responsável pela aplicação; não há rota pública que desative o segundo fator.

## Limites de tentativas

| Controle | Padrão |
| --- | --- |
| Login por e-mail normalizado | 10 tentativas por janela de 15 minutos |
| Autenticação por endereço da conexão: login, MFA e senha | 120 requisições por janela de 15 minutos |
| Operações do autenticador e troca de senha | 10 tentativas por conta autenticada na mesma janela |

Os logins `/login` e `/auth/login` compartilham os contadores. E-mails em maiúsculas ou com espaços não criam contadores independentes. O contador de login da conta é limpo somente após a autenticação completa. As tentativas das operações sensíveis e o limite por endereço contam também requisições bem-sucedidas.

O excesso retorna HTTP 429 com `Retry-After`. Os contadores são atômicos, ficam no PostgreSQL e continuam valendo entre instâncias ou após reiniciar a API. As chaves armazenadas são hashes; não são salvos e-mails ou endereços em texto nesses contadores. Entradas com mais de 24 horas são removidas periodicamente.

O endereço considerado é o da conexão recebida pela aplicação. O código não confia diretamente no cabeçalho `X-Forwarded-For`. Atrás de um proxy, vários usuários podem compartilhar esse endereço: ajuste o limite à capacidade e ao tráfego reais; configurar proxies confiáveis exige validação do ambiente de hospedagem.

Os limites podem ser configurados por `AUTH_ACCOUNT_ATTEMPTS`, `AUTH_IP_ATTEMPTS` e `AUTH_WINDOW_SECONDS`. Valores devem ser positivos; a janela pode ter até 86400 segundos. Alterar esses valores não exige alterar código.

## Contratos para outros clientes

| Operação | Contrato |
| --- | --- |
| `POST /login` e `POST /auth/login` | Recebem `email`, `senha` e, quando necessário, `codigo`. O formato de sucesso anterior é preservado em cada rota. |
| Segundo fator ausente após senha válida | HTTP 428, `codigo: "MFA_REQUIRED"`, sem token de acesso. |
| Segundo fator incorreto, vencido ou reutilizado | HTTP 400 com mensagem; sem token de acesso. |
| `GET /auth/mfa` | Estado da própria conta profissional, disponibilidade da configuração e número de códigos restantes. |
| `POST /auth/mfa/preparar` | Recebe `senhaAtual`; retorna chave manual, URI de provisionamento e prazo. |
| `POST /auth/mfa/cancelar` | Invalida a preparação pendente da própria conta; não desativa uma proteção já confirmada. |
| `POST /auth/mfa/ativar` | Recebe `codigo`; retorna `sessao` e `codigosRecuperacao`. |
| `POST /auth/mfa/desativar` | Recebe `senhaAtual` e `codigo`; retorna uma sessão renovada. |
| `POST /auth/mfa/recuperacao` | Recebe `senhaAtual` e `codigo`; retorna sessão renovada e novos códigos. |
| `PUT /usuario/senha` | Acrescenta o campo opcional `codigo`, exigido se houver segundo fator ativo. |

As rotas de configuração exigem token Bearer e conta profissional. O usuário é obtido da sessão autenticada; não há parâmetro para configurar outro usuário.

**Compatibilidade com o aplicativo:** esta entrega atualiza o site e a API. O APK anterior não contém a nova etapa de código. Para entrar por ele em uma conta com segundo fator ativo, o cliente Android precisa ser adaptado ao contrato acima; enquanto isso, use essa conta pelo site atualizado. A rota antiga não dispensa a verificação para manter compatibilidade.

## Dependências e repetição da verificação

| Dependência | Versão nesta revisão |
| --- | --- |
| Spring Boot | 4.0.8 |
| java-jwt | 4.6.2 |
| jsoup | 1.23.2 |
| PostgreSQL JDBC | 42.7.14 |
| Jackson 2 | 2.21.8 |
| Jackson 3 | 3.1.8 |
| Tomcat embarcado | 11.0.27 |

As versões de Jackson e Tomcat são substituições explícitas das versões do BOM, motivadas pelos avisos conhecidos encontrados durante a consulta de segurança. Foram mantidas as mesmas linhas de versão e repetidos os testes de integração após a atualização.

Para consultar novamente as dependências efetivamente resolvidas da API:

```powershell
.\gradlew.bat listarDependenciasSeguranca
python scripts/verificar_dependencias.py
```

O script envia ao OSV somente os nomes e versões das bibliotecas Maven. Ele grava o relatório em `build/seguranca/vulnerabilidades.json`; termina com código 0 se não houver avisos conhecidos, 1 se houver e 2 se a consulta não for concluída. Uma consulta que falha não é considerada aprovação. Faça a revisão novamente antes de uma publicação e quando atualizar as dependências.

Para as dependências de desenvolvimento do site, execute `npm audit` na pasta `site/`. Não houve necessidade de alterar o lockfile do site: a consulta desta revisão não apresentou vulnerabilidades conhecidas.

Ausência de avisos no catálogo consultado não garante ausência de falhas. O resultado é uma verificação das versões naquele momento, e não uma atualização automática permanente.

## Evidências da entrega

- 64 testes do site, incluindo login em duas etapas, configuração, recuperação, erros e preservação da sessão.
- 19 testes Java: 15 de integração HTTP com PostgreSQL real e quatro de TOTP/cifra, incluindo os vetores oficiais da RFC 6238, adulteração do segredo e rejeição de reutilização.
- Dois testes do gerador de chave, verificando preservação de configuração e ausência de segredos na saída.
- Navegador com API e PostgreSQL reais: ativação, login com recuperação, troca de senha, regeneração, desativação e persistência do bloqueio após reiniciar a API.
- Conferência em 390 e 1440 pixels, sem transbordamento horizontal.
- Compilação do servidor e geração de `bootJar` aprovadas.
- Consulta final ao OSV das 92 dependências de execução sem avisos conhecidos e `npm audit` sem vulnerabilidades reportadas.

Essas verificações usaram ambiente local descartável; não acessaram o banco do Render. O override opcional do Docker está documentado no LEIA-ME do ZIP e não foi executado neste ambiente.

## Referências técnicas

- [TOTP — RFC 6238](https://www.rfc-editor.org/rfc/rfc6238.html)
- [OWASP — autenticação multifator](https://cheatsheetseries.owasp.org/cheatsheets/Multifactor_Authentication_Cheat_Sheet.html)
- [OSV — API de consulta](https://google.github.io/osv.dev/api/)
- [Maven Central](https://repo.maven.apache.org/maven2/)
- [Tomcat — avisos de segurança](https://tomcat.apache.org/security-11.html)
- [Jackson 2.21.8](https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.21.8) e [Jackson 3.1.8](https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.1.8)
