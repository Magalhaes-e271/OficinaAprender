# OficinaAprender

API REST desenvolvida em Java com Spring Boot para gestão de usuários, perfis, postagens e relações entre usuários. O projeto foi estruturado em camadas com foco em organização e separação de responsabilidades, utilizando JPA para persistência em banco relacional.

## Visão geral

O sistema oferece recursos para:

- cadastro e gerenciamento de usuários;
- login simples com e-mail e senha;
- atualização de perfil do usuário;
- criação de postagens;
- relacionamento entre usuários;
- persistência em banco H2 em modo arquivo.

## Tecnologias utilizadas

- Java 21
- Spring Boot 4.0.4
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Gradle
- JWT (`com.auth0:java-jwt:4.4.0`)

## Estrutura do projeto

```text
OficinaAprender/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/sp/etec/sebrae/OficinaAprender/
│   │   │       ├── Controller/
│   │   │       │   ├── Login/
│   │   │       │   ├── Postagem/
│   │   │       │   ├── Relacao/
│   │   │       │   └── Usuario/
│   │   │       ├── DTO/
│   │   │       ├── Entity/
│   │   │       │   ├── Postagem/
│   │   │       │   ├── Relacao/
│   │   │       │   └── Usuario/
│   │   │       ├── Enuns/
│   │   │       ├── Repository/
│   │   │       │   ├── Postagem/
│   │   │       │   ├── Relacao/
│   │   │       │   └── Usuario/
│   │   │       ├── Service/
│   │   │       │   ├── Postagem/
│   │   │       │   ├── Relacao/
│   │   │       │   ├── Settings/
│   │   │       │   └── Usuario/
│   │   │       ├── OficinaAprenderApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── br/sp/etec/sebrae/OficinaAprender/
│               └── OficinaAprenderApplicationTests.java
```

## Pacotes principais

### Controller

Responsável por expor os endpoints da API.

Principais controladores:

- `AuthController` — autenticação e login
- `UsuarioController` — cadastro, busca, edição e exclusão de usuários
- `PerfilUsuarioController` — visualização e edição do perfil do usuário
- `PostagemController` — criação de postagens
- `UsuarioRelacaoController` — gestão de relações entre usuários

### Service

Contém a lógica de negócio da aplicação.

Exemplos:

- `UsuarioService`
- `PerfilUsuarioService`
- `PostagemService`
- `RelacaoUsuarioService`
- `TokenService`

### Repository

Camada de acesso ao banco de dados com Spring Data JPA.

Exemplos:

- `UsuarioRepository`
- `PerfilUsuarioRepository`
- `PostagemRepository`
- `RelacaoUsuarioRepository`

### Entity

Representam as entidades do banco.

Entidades principais:

- `Usuario`
- `PerfilUsuario`
- `Postagem`
- `Meta`
- `Anexo`
- `RelacaoUsuario`

### DTO

Objetos usados para transporte de dados entre camadas.

Exemplos:

- `UsuarioRequestDTO`
- `UsuarioResponseDTO`

### Enums

Definem tipos e categorias usados pelo sistema.

Exemplos:

- `TipoUsuario`
- `TipoPostagem`
- `TipoRelacao`
- `TipoAcesso`
- `Visualizacao`
- `Permissao`
- `Solicitacao`

## Modelo de domínio

### Usuário

A entidade `Usuario` possui:

- `id`
- `email`
- `senha`
- `tipoUsuario`

Cada usuário pode estar associado a um `PerfilUsuario`.

### Perfil do usuário

O perfil contém dados complementares do usuário, como:

- `nome`
- `telefone`
- `cpf`
- `urlFotoPerfil`
- `dataNascimento`
- `ocupacao`
- `instituicao`
- `conteudoHtml`
- `dataCriacao`
- `dataAtualizacao`

### Postagem

`Postagem` representa conteúdos publicados pela plataforma e contém:

- `titulo`
- `conteudoHtml`
- `tipoPostagem`
- `visualizacao`
- `dataCriacao`
- `dataAtualizacao`

### Relação entre usuários

A entidade `RelacaoUsuario` representa vínculos ou associações entre usuários.

## Endpoints da API

### Autenticação

#### POST `/auth/login`

Realiza autenticação do usuário com e-mail e senha.

Exemplo de payload:

```json
{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

Resposta:

- sucesso: retorna os dados do usuário autenticado;
- erro: retorna `401 Unauthorized` quando as credenciais são inválidas.

### Usuário

#### POST `/usuario/criar`
Cria um novo usuário.

#### PUT `/usuario/editar`
Edita um usuário existente.

#### DELETE `/usuario/deletar`
Exclui um usuário.

#### GET `/usuario/email`
Busca usuário por e-mail.

#### GET `/usuario/id`
Busca usuário por ID.

#### GET `/usuario/descricao`
Busca descrição ou perfil do usuário por ID.

#### PUT `/usuario/descricao`
Atualiza a descrição do usuário.

### Perfil

#### POST `/perfil`
Busca perfil por ID.

#### PUT `/perfil/editar`
Edita o perfil do usuário.

### Postagem

#### POST `/postagem/criar`
Cria uma nova postagem.

Parâmetros:

- corpo da postagem
- `idMeta` via query param

### Relação entre usuários

#### POST `/usuario-aluno/criar`
Cria uma relação entre usuários.

#### PUT `/usuario-aluno/editar`
Edita uma relação existente.

## Configuração do banco de dados

O projeto usa H2 em arquivo, com dados persistidos localmente no diretório do projeto:

```properties
spring.datasource.url=jdbc:h2:file:./data/dbTCC
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

### Console H2

O console do banco está habilitado em:

```text
http://localhost:8080/h2-console
```

## Como executar o projeto

### Pré-requisitos

- Java 21
- Gradle ou uso do wrapper
- Git

### Comandos

```bash
./gradlew clean build
./gradlew bootRun
```

No Windows:

```bash
gradlew.bat clean build
gradlew.bat bootRun
```

## Observações importantes

- O projeto usa Java 21, por isso é necessário ter esse JDK instalado.
- A organização em camadas facilita manutenção e expansão da aplicação.
- Há um bom começo para uma API REST, mas ainda é possível melhorar:
  - validação de entradas;
  - padronização de respostas de erro;
  - uso de DTOs em mais operações;
  - autenticação com JWT mais robusta;
  - cobertura de testes mais ampla.

## Conclusão

O repositório `OficinaAprender` é uma API REST em Java/Spring Boot voltada para a gestão de usuários, perfis, postagens e relações de conexão entre pessoas. A estrutura está bem organizada em camadas e serve como base para evoluir uma plataforma mais completa.

## Repositório

- Repositório: `Magalhaes-e271/OficinaAprender`
- Linguagem principal: Java
- Branch padrão: `master`
