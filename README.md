# FluxbankAPI

API REST para operacoes bancarias basicas com contas pre-cadastradas:
- deposito
- saque
- transferencia

O projeto foi construido com Spring Boot, JPA/Hibernate, Flyway e PostgreSQL.

## Stack

- Java 25
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA
- Flyway
- PostgreSQL
- Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Pre-requisitos

- JDK 25 instalado
- PostgreSQL em execucao
- Banco `fluxbank` criado localmente (ou ajuste a URL no `application.properties`)

## Configuracao

A aplicacao usa estas propriedades em `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:postgresql://localhost:5432/fluxbank`
- `spring.datasource.username=${DB_USERNAME:}`
- `spring.datasource.password=${DB_PASSWORD:}`

Defina usuario e senha via variaveis de ambiente antes de subir a API.

### PowerShell (Windows)

```powershell
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

## Como executar

### 1) Subir a aplicacao

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrao, a API sobe em `http://localhost:8080`.

### 2) Rodar testes

```powershell
.\mvnw.cmd test
```

## Migracao e schema

O Flyway executa automaticamente a migracao:

- `src/main/resources/db/migration/V1__create_accounts_table.sql`

Tabela criada:
- `tb_accounts` (`id`, `holder_name`, `cpf`, `email`, `balance`, `account_type`, `created_at`)

## Endpoints

Base path: `/accounts`

### POST `/accounts/deposit`

Deposito em conta existente.

Request:

```json
{
  "accountId": "11111111-1111-1111-1111-111111111111",
  "amount": 150.00
}
```

Response `200`:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "balance": 1150.00
}
```

### POST `/accounts/withdraw`

Saque em conta existente.

Request:

```json
{
  "accountId": "11111111-1111-1111-1111-111111111111",
  "amount": 50.00
}
```

Response `200`:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "balance": 1100.00
}
```

### POST `/accounts/transfer`

Transferencia entre duas contas existentes.

Request:

```json
{
  "fromId": "11111111-1111-1111-1111-111111111111",
  "toId": "22222222-2222-2222-2222-222222222222",
  "amount": 100.00
}
```

Response `200`:

```text
Transfer successful
```

## Exemplos com curl

```bash
curl -X POST http://localhost:8080/accounts/deposit \
  -H "Content-Type: application/json" \
  -d "{\"accountId\":\"11111111-1111-1111-1111-111111111111\",\"amount\":150.00}"
```

```bash
curl -X POST http://localhost:8080/accounts/withdraw \
  -H "Content-Type: application/json" \
  -d "{\"accountId\":\"11111111-1111-1111-1111-111111111111\",\"amount\":50.00}"
```

```bash
curl -X POST http://localhost:8080/accounts/transfer \
  -H "Content-Type: application/json" \
  -d "{\"fromId\":\"11111111-1111-1111-1111-111111111111\",\"toId\":\"22222222-2222-2222-2222-222222222222\",\"amount\":100.00}"
```

## Tratamento de erros

A API retorna erros no formato:

```json
{
  "timestamp": "2026-02-22T10:00:00",
  "message": "mensagem de erro"
}
```

Casos tratados:
- `404 Not Found`: conta de origem/destino inexistente
- `400 Bad Request`: valor invalido, saldo insuficiente, transferencia para a mesma conta

## Observacoes importantes

- No estado atual, nao ha endpoint para criar conta.
- Para testar depositos/saques/transferencias, insira contas diretamente no banco (`tb_accounts`) antes.
- Tipos de conta disponiveis: `CHECKING`, `SAVINGS`, `BUSINESS`, `INVESTMENT`, `DIGITAL_WALLET`.
