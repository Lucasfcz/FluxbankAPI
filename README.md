# FluxbankAPI

API REST bancária desenvolvida para aprender o Spring boot em aplicações reais. Simula operações financeiras com autenticação JWT, testes unitários e Swagger.

🔗 **[Swagger UI (deploy)](https://fluxbankapi-production.up.railway.app/swagger-ui/index.html)**

---

## Tecnologias

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=flat&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)

- **Backend:** Spring Boot 4, Spring Web, Spring Security, Spring Data JPA
- **Banco de dados:** PostgreSQL + Flyway
- **Autenticação:** JWT (Auth0)
- **Documentação:** Springdoc OpenAPI (Swagger)
- **Infraestrutura:** Docker, Docker Compose, Railway
- **Testes:** JUnit 5, Mockito, AssertJ, MockMvc

---

## Funcionalidades

- Criação e gerenciamento de contas bancárias
- Depósito, saque e transferência entre contas
- Histórico paginado de transações
- Autenticação stateless com JWT
- Uso do `@Version` para impedir que operações simultâneas corrompam o saldo
- Contas são desativadas, não deletadas, preservando o histórico

---

## Arquitetura

```
controller  →  recebe e valida a requisição HTTP
service     →  regras de negócio
repository  →  acessa o banco via JPA
model       →  entidades com lógica de domínio encapsulada
dto         →  objetos de entrada e saída
exception   →  exceções customizadas + handler global
```

---

## Autenticação

A API usa JWT stateless. Apenas `/auth/register` e `/auth/login` são públicos.

```
POST /auth/register   → cria um usuário
POST /auth/login      → retorna o token JWT
```

Inclua o token nas demais requisições:
```
Authorization: Bearer <token>
```

---

## Endpoints

<details>
<summary><strong>Contas</strong></summary>

```
POST   /accounts
GET    /accounts
GET    /accounts/{id}
GET    /accounts/email/{email}
GET    /accounts/cpf/{cpf}
PATCH  /accounts/{id}
DELETE /accounts/{id}
GET    /accounts/{id}/transactions
```
</details>

<details>
<summary><strong>Transações</strong></summary>

```
POST /transactions/deposit
POST /transactions/withdraw
POST /transactions/transfer
```
</details>

---

## Erros

Todas as respostas de erro seguem o mesmo formato:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "message": "descrição do erro"
}
```

| Situação | Status |
|---|---|
| Conta não encontrada | 404 |
| Saldo insuficiente | 400 |
| CPF ou e-mail duplicado | 409 |
| Conta inativa | 403 |
| Conflito de concorrência | 409 |

---

## Rodando com Docker

Crie um `.env` na raiz:

```env
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=um_secret_longo_aqui
```

```bash
docker compose up --build
```

Acesse em `http://localhost:8080/swagger-ui/index.html`

---

## Rodando localmente

Pré-requisitos: Java 21, Maven e PostgreSQL.

```bash
# Configure as variáveis DB_USERNAME, DB_PASSWORD e JWT_SECRET
./mvnw spring-boot:run
```

O Flyway cria as tabelas automaticamente na primeira execução.

---

## Testes

```bash
./mvnw test
```

Cobertura em três camadas:

- **Domínio** — regras de negócio nas entidades
- **Service** — todos os cenários de sucesso e falha com Mockito
- **Controller** — requisições HTTP simuladas com MockMvc

---

## Autor

Feito por **[Lucas Cabral](https://github.com/Lucasfcz)** — estudante de ADS no IFPE Paulista, em busca da primeira oportunidade como desenvolvedor backend.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=flat&logo=linkedin&logoColor=white)](https://linkedin.com/in/lucas-cabral-2432633a6)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/Lucasfcz)
