# Entrega local

O conjunto entregue contém API, site, fontes Android, APK local, inicialização com Docker e instruções de desenvolvimento. O ponto de entrada é `../../LEIA-ME.md`.

## Recursos

Contas e sessões, perfil e foto, pacientes e equipe, convites, metas com dependências, registros e respostas, anexos, agenda, ativação inicial do paciente e relatório imprimível no site. O Android usa JWT real e apresenta os dados do mesmo servidor configurado.

As fotos e os anexos ficam no PostgreSQL. O volume `dados_postgres` mantém os dados nas paradas normais. `../../scripts/backup.py` gera uma cópia do banco local.

## Verificação

`../../documentacao/TESTES.md` distingue as verificações desta entrega das execuções anteriores que validaram a base. Os comandos permitem repetir os testes em um banco exclusivo terminado em `_test`.

## Limites

A ativação do paciente usa um link de uso único entregue pelo profissional. Não há envio de e-mails de recuperação de senha. O APK incluído é de desenvolvimento, com configuração de servidor local; uma distribuição pública exige HTTPS e assinatura própria.

O banco começa vazio. Não são distribuídos dados pessoais, segredos ou backups de ambientes existentes. Esta entrega não publica nem altera recursos no GitHub ou Render.
