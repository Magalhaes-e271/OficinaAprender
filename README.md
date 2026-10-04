# 🎓 Oficina Aprender

API REST desenvolvida em **Java com Spring Boot** para uma plataforma voltada à interação e ao aprendizado entre usuários.

O sistema possui funcionalidades de **cadastro de usuários, autenticação, gerenciamento de perfil, criação de postagens e relações entre usuários**, utilizando uma arquitetura organizada em camadas e persistência com **Spring Data JPA**.

---

## 🚀 Tecnologias

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.4-brightgreen?style=for-the-badge&logo=springboot)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle)
![H2](https://img.shields.io/badge/H2-Database-blue?style=for-the-badge)

- Java 21
- Spring Boot 4.0.4
- Spring Web
- Spring Data JPA
- H2 Database
- JWT
- Lombok
- Gradle
- JUnit

---

## 📚 Sobre o projeto

O **Oficina Aprender** foi desenvolvido com o objetivo de criar uma API para uma plataforma onde usuários possam possuir perfis, publicar conteúdos e estabelecer relações com outros usuários.

Atualmente a API possui recursos para:

- 👤 cadastro de usuários;
- 🔐 autenticação de usuários;
- 🪪 gerenciamento de perfil;
- 📝 criação de postagens;
- 🤝 relações entre usuários;
- 🎯 gerenciamento de metas;
- 📎 anexos em conteúdos;
- 💾 persistência dos dados em banco H2.

---

## 🏗️ Arquitetura

O projeto utiliza uma arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Controller

Responsável por receber as requisições HTTP e disponibilizar os endpoints da API.

### Service

Contém as regras de negócio da aplicação.

### Repository

Responsável pela comunicação com o banco de dados através do **Spring Data JPA**.

### Entity

Representa as entidades persistidas no banco de dados.

### DTO

Objetos utilizados para transferência de dados entre cliente e servidor.

---

## 📁 Estrutura do projeto

```text
OficinaAprender/
│
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
│   │   │       ├── Enuns/
│   │   │       ├── Repository/
│   │   │       ├── Service/
│   │   │       └── OficinaAprenderApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🧩 Principais entidades

### 👤 Usuário

Representa os usuários cadastrados na plataforma.

Principais informações:

- ID
- e-mail
- senha
- tipo de usuário

### 🪪 Perfil

Armazena informações adicionais sobre o usuário, como:

- nome;
- telefone;
- CPF;
- foto de perfil;
- data de nascimento;
- ocupação;
- instituição;
- descrição/conteúdo do perfil.

### 📝 Postagem

Representa os conteúdos publicados dentro da plataforma.

Uma postagem pode possuir:

- título;
- conteúdo;
- tipo de postagem;
- visibilidade;
- data de criação;
- data de atualização.

### 🤝 Relação entre usuários

Permite representar vínculos entre usuários da plataforma, como relações entre alunos, professores ou outros tipos definidos pelo sistema.

---

## 🌐 Principais endpoints

### 🔐 Autenticação

```http
POST /auth/login
```

Exemplo:

```json
{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

### 👤 Usuários

```http
POST /usuario/criar
PUT /usuario/editar
DELETE /usuario/deletar
GET /usuario/email
GET /usuario/id
```

### 🪪 Perfil

```http
POST /perfil
PUT /perfil/editar
```

### 📝 Postagens

```http
POST /postagem/criar
```

### 📎 Anexos

```http
POST /anexo/criar
```

### 🤝 Relações

```http
POST /usuario-aluno/criar
PUT /usuario-aluno/editar
```

---

## 💾 Banco de dados

Durante o desenvolvimento, o projeto utiliza o **H2 Database**.

A configuração está localizada em:

```text
src/main/resources/application.properties
```

Exemplo:

```properties
spring.datasource.url=jdbc:h2:file:./data/dbTCC
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

O console web do H2 pode ser acessado, quando habilitado, em:

```text
http://localhost:8080/h2-console
```

---

## ⚙️ Como executar

### Pré-requisitos

- Git
- Java 21

O projeto utiliza **Gradle Wrapper**, então não é necessário instalar o Gradle separadamente.

### 1. Clone o repositório

```bash
git clone https://github.com/Magalhaes-e271/OficinaAprender.git
cd OficinaAprender
```

### 2. Compile o projeto

Linux/macOS:

```bash
./gradlew clean build
```

Windows:

```bash
gradlew.bat clean build
```

### 3. Execute a aplicação

Linux/macOS:

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```

Por padrão, a API ficará disponível em:

```text
http://localhost:8080
```

---

## 🧪 Testes

Linux/macOS:

```bash
./gradlew test
```

Windows:

```bash
gradlew.bat test
```

---

## 🔒 Autenticação

O projeto utiliza a biblioteca:

```text
com.auth0:java-jwt:4.4.0
```

para trabalhar com **JSON Web Tokens (JWT)**.

---

## 🛠️ Melhorias futuras

- [ ] ampliar a autenticação e autorização com JWT;
- [ ] adicionar validações com Bean Validation;
- [ ] criar tratamento global de exceções;
- [ ] aumentar a cobertura de testes;
- [ ] adicionar documentação com Swagger/OpenAPI;
- [ ] utilizar PostgreSQL em produção;
- [ ] implementar upload de arquivos;
- [ ] adicionar paginação nas consultas;
- [ ] integrar a API com o front-end da plataforma;
- [ ] realizar deploy da aplicação.

---

## 🎯 Objetivo

O **Oficina Aprender** busca servir como base para uma plataforma educacional e social, permitindo que diferentes usuários compartilhem informações, publiquem conteúdos e criem conexões dentro de um ambiente voltado ao aprendizado.

O projeto também coloca em prática conceitos importantes de desenvolvimento de sistemas, como:

- APIs REST;
- arquitetura em camadas;
- orientação a objetos;
- persistência de dados;
- autenticação;
- relacionamento entre entidades;
- integração entre front-end e back-end.

---

## 👨‍💻 Autor

Desenvolvido por **Magalhaes-e271**.

GitHub: [@Magalhaes-e271](https://github.com/Magalhaes-e271)

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais.

---

<p align="center">
  Desenvolvido com ☕ Java + 🌱 Spring Boot
</p>
