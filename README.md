# Fluxbank API

API REST para simulacao de operacoes bancarias basicas, desenvolvida com Spring Boot, com foco em arquitetura em camadas, validacao, transacoes e testes automatizados.

## Funcionalidades

- Criacao de conta bancaria
- Deposito
- Saque
- Transferencia entre contas
- Validacoes de regra de negocio
- Tratamento global de excecoes
- Migracoes de banco com Flyway
- Testes unitarios de service, controller e dominio

## Stack Tecnologica

- Java 25
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA (Hibernate)
- Flyway
- PostgreSQL
- Maven Wrapper (`mvnw` / `mvnw.cmd`)
- JUnit 5
- Mockito
- MockMvc

## Arquitetura

- `controller`: recebe requests HTTP e retorna responses
- `service`: regras de negocio e transacoes
- `domain`: entidades e enums
- `repository`: acesso ao banco (JPA)
- `dto`: contratos de entrada e saida da API
- `exception`: excecoes customizadas e handler global

## Modelo de Conta (`tb_accounts`)

Campos principais:

- `id` (UUID)
- `holder_name`
- `cpf` (unico)
- `email` (unico)
- `balance`
- `account_type`
- `created_at`
- `version`

Tipos de conta:

- `CHECKING`
- `SAVINGS`
- `BUSINESS`
- `INVESTMENT`
- `DIGITAL_WALLET`

## Endpoints

Base path: `/accounts`

### POST `/accounts/create`

Request:

```json
{
  "holderName": "Lucas Cabral",
  "cpf": "12345678900",
  "email": "lucas@email.com",
  "accountType": "CHECKING"
}
```

Response `201`:

```json
{
  "accountId": "uuid",
  "balance": 0
}
```

### POST `/accounts/deposit`

Request:

```json
{
  "accountId": "uuid",
  "amount": 150.00
}
```

Response `200`:

```json
{
  "accountId": "uuid",
  "balance": 150.00
}
```

### POST `/accounts/withdraw`

Request:

```json
{
  "accountId": "uuid",
  "amount": 50.00
}
```

Response `200`:

```json
{
  "accountId": "uuid",
  "balance": 100.00
}
```

### POST `/accounts/transfer`

Request:

```json
{
  "fromId": "uuid",
  "toId": "uuid",
  "amount": 100.00
}
```

Response `200`:

```text
Transfer successful
```

## Tratamento de Erros

Formato padrao:

```json
{
  "timestamp": "2026-02-22T10:00:00",
  "message": "error message"
}
```

Casos tratados:

- `404 Not Found`: conta inexistente
- `400 Bad Request`: transferencia para mesma conta, saldo insuficiente, valor invalido, payload invalido
- `409 Conflict`: CPF/email ja cadastrados, conflito de atualizacao concorrente

## Banco de Dados e Flyway

Migracoes em:

- `src/main/resources/db/migration/V1__create_accounts_table.sql`
- `src/main/resources/db/migration/V2__add_version_column_to_accounts.sql`

## Configuracao

Arquivo: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fluxbank
spring.datasource.username=${DB_USERNAME:}
spring.datasource.password=${DB_PASSWORD:}
```

### Variaveis de ambiente (PowerShell)

```powershell
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

## Como Executar

Subir a aplicacao:

```powershell
.\mvnw.cmd spring-boot:run
```

API disponivel em `http://localhost:8080`.

Rodar testes:

```powershell
.\mvnw.cmd "-Dtest=AccountControllerTest,AccountServiceTest,AccountTest" test
```

## Objetivo do Projeto

Projeto desenvolvido para consolidar fundamentos de backend Java com Spring Boot, aplicando regras de negocio reais, validacoes e testes automatizados.

## Autor

Lucas Cabral  
Estudante de ADS | Backend Java  
IFPE - Paulista
