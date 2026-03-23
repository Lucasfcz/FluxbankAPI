# 💳 FluxbankAPI

FluxbankAPI é uma API REST de simulação bancária que desenvolvi para consolidar meus conhecimentos em backend Java. O projeto cobre desde operações financeiras básicas até autenticação com JWT, Docker e testes unitários.
Link: https://fluxbankapi-production.up.railway.app/swagger-ui/index.html
---

## ✨ O que a API faz?

Você pode criar contas bancárias, fazer depósitos, saques e transferências entre contas, consultar o histórico de transações e gerenciar os dados da conta. Tudo protegido por autenticação JWT.

Além disso, o sistema garante integridade dos dados com controle de concorrência otimista (via `@Version` do Hibernate), impedindo que duas operações simultâneas corrompam o saldo de uma conta.

---

## 🛠️ Tecnologias utilizadas

O projeto foi construído com **Java 25** e **Spring Boot 4**, usando **Spring Security** para proteger os endpoints e **Auth0 JWT** para geração e validação dos tokens. A persistência é feita com **Spring Data JPA** e **Hibernate**, com **PostgreSQL** como banco de dados e **Flyway** gerenciando as migrations automaticamente.

Para a documentação, usei o **Springdoc OpenAPI** que gera o Swagger UI automaticamente a partir das anotações do código. O ambiente roda inteiro via **Docker Compose**.

Os testes foram escritos com **JUnit 5**, **Mockito** e **AssertJ**, cobrindo domínio, services e controllers.

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida. Os controllers recebem as requisições HTTP e delegam para os services, que concentram toda a lógica de negócio. As entidades de domínio (`Account`, `Transaction`) encapsulam suas próprias regras, por exemplo, a validação de saldo e o bloqueio de operações em contas inativas ficam dentro da própria entidade seguindo conceitos do Domain-Driven Design (DDD).

```
controller  →  recebe e valida a requisição HTTP
service     →  aplica as regras de negócio
repository  →  acessa o banco via JPA
model       →  entidades com lógica de domínio encapsulada
dto         →  objetos de entrada e saída da API
exception   →  exceções customizadas + handler global
```

---

## 🔒 Autenticação

A API usa autenticação **stateless** com JWT. O fluxo é simples:

1. `POST /auth/register` — cria um usuário
2. `POST /auth/login` — retorna um token JWT
3. Inclua o token em todas as requisições seguintes no header `Authorization: Bearer <token>`

Esses dois endpoints são os únicos que não exigem autenticação. Todo o resto da API é protegido.

---

## 📡 Endpoints disponíveis

**Autenticação**
- `POST /auth/register` — registra um novo usuário
- `POST /auth/login` — faz login e retorna o token

**Contas**
- `POST /accounts` — cria uma nova conta bancária
- `GET /accounts` — lista todas as contas
- `GET /accounts/{id}` — busca conta por ID
- `GET /accounts/email/{email}` — busca conta por e-mail
- `GET /accounts/cpf/{cpf}` — busca conta por CPF
- `PATCH /accounts/{id}` — atualiza dados da conta
- `DELETE /accounts/{id}` — desativa a conta (soft delete)
- `GET /accounts/{id}/transactions` — histórico paginado de transações

**Transações**
- `POST /transactions/deposit` — deposita na conta
- `POST /transactions/withdraw` — saca da conta
- `POST /transactions/transfer` — transfere entre duas contas

---

## ⚠️ Erros e validações

Todas as respostas de erro seguem um formato padronizado com timestamp e mensagem. As principais situações tratadas são conta não encontrada (404), saldo insuficiente (400), CPF ou e-mail duplicado (409), conta inativa (403) e conflito de concorrência (409).

---

## 🐳 Rodando com Docker

Com Docker instalado, basta criar um arquivo `.env` na raiz do projeto:

```env
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=um_secret_bem_longo_aqui
```

E subir tudo com um único comando:

```bash
docker compose up --build
```

A API estará disponível em `http://localhost:8080` e o Swagger UI em `http://localhost:8080/swagger-ui/index.html`.

---

## 💻 Rodando localmente (sem Docker)

Se preferir rodar direto na máquina, você precisa do Java 25, Maven e PostgreSQL instalados. Crie o banco `fluxbank`, configure as variáveis de ambiente `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`, e execute:

```bash
./mvnw spring-boot:run
```

O Flyway cria as tabelas automaticamente na primeira execução.

---

## 🧪 Testes

A suite de testes cobre as três camadas principais do projeto — domínio, services e controllers. Para rodar:

```bash
./mvnw test
```

Os testes de domínio validam as regras de negócio diretamente nas entidades (depósito, saque, desativação). Os testes de service usam Mockito para isolar as dependências e cobrem todos os cenários de sucesso e falha. Os testes de controller usam MockMvc para simular requisições HTTP e verificar status codes e respostas.

---

## 👨‍💻 Sobre o autor

Sou **Lucas Cabral**, estudante de Análise e Desenvolvimento de Sistemas no IFPE Paulista. Desenvolvi esse projeto para aprofundar meus conhecimentos em backend Java e construir um portfólio sólido enquanto busco minha primeira oportunidade como desenvolvedor.

[![GitHub](https://img.shields.io/badge/GitHub-Lucasfcz-black?logo=github)](https://github.com/Lucasfcz)
