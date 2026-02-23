💳 FluxbankAPI

API REST para gerenciamento de contas bancárias, permitindo criação de contas, depósitos, saques e transferências entre contas.

O projeto foi desenvolvido com foco em boas práticas de arquitetura, regras de negócio bem definidas, tratamento de erros consistente e testes automatizados, simulando um cenário real de sistema bancário.

🚀 Tecnologias utilizadas

Java 25

Spring Boot 4.0.3

Spring Web MVC

Spring Data JPA (Hibernate)

PostgreSQL

Flyway

JUnit 5

Mockito

MockMvc

Maven Wrapper

🧱 Arquitetura

O projeto segue uma arquitetura em camadas bem definidas:

Controller → Service → Repository → Database
📌 Responsabilidades

Controller

Recebe e valida requisições HTTP

Converte dados de entrada/saída usando DTOs

Service

Contém as regras de negócio

Controla transações

Domain (Entity)

Entidades ricas com lógica encapsulada

Repository

Acesso ao banco de dados

DTOs

Contrato da API (request/response)

GlobalExceptionHandler

Tratamento centralizado de erros

📦 Funcionalidades
✔ Criar conta

CPF e e-mail únicos

Tipo de conta obrigatório

Saldo inicial padrão: 0

Geração automática de UUID

✔ Depósito

Valor deve ser maior que zero

Conta deve existir

✔ Saque

Valor deve ser maior que zero

Saldo não pode ficar negativo

✔ Transferência

Não permite transferência para a mesma conta

Valida contas de origem e destino

Operação transacional

🔌 Endpoints
🔹 Criar conta

POST /accounts/create

Request:

{
  "holderName": "Lucas Cabral",
  "cpf": "12345678900",
  "email": "lucas@email.com",
  "accountType": "CHECKING"
}

Response 201 Created:

{
  "accountId": "uuid",
  "balance": 0
}
🔹 Depositar

POST /accounts/deposit

Request:

{
  "accountId": "uuid",
  "amount": 100
}

Response 200 OK:

{
  "accountId": "uuid",
  "balance": 100
}
🔹 Sacar

POST /accounts/withdraw

Request:

{
  "accountId": "uuid",
  "amount": 50
}

Response 200 OK:

{
  "accountId": "uuid",
  "balance": 50
}
🔹 Transferir

POST /accounts/transfer

Request:

{
  "fromId": "uuid",
  "toId": "uuid",
  "amount": 25
}

Response 200 OK:

Transfer successful
⚠️ Tratamento de erros

A API utiliza um GlobalExceptionHandler para padronizar as respostas de erro.

Formato padrão:

{
  "timestamp": "2026-02-22T10:00:00",
  "message": "Mensagem de erro"
}
Casos tratados:

400 Bad Request

Valor inválido

Saldo insuficiente

Transferência para a mesma conta

Payload inválido

404 Not Found

Conta não encontrada

🗄 Banco de dados

PostgreSQL

Controle de versão do schema com Flyway

Hibernate configurado com:

spring.jpa.hibernate.ddl-auto=validate
Migração inicial:
src/main/resources/db/migration/V1__create_accounts_table.sql

Tabela criada:

tb_accounts (
  id,
  holder_name,
  cpf,
  email,
  balance,
  account_type,
  created_at
)
🧪 Testes

O projeto possui:

Testes unitários de Service (Mockito)

Testes de Controller com MockMvc

Testes de validação e exceções

Testes sem dependência do banco (rápidos e isolados)

Executar testes:
./mvnw test
▶ Como executar o projeto
1️⃣ Criar banco de dados
CREATE DATABASE fluxbank;
2️⃣ Configurar variáveis de ambiente

Windows (PowerShell):

$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
3️⃣ Subir a aplicação
./mvnw spring-boot:run

A API ficará disponível em:

http://localhost:8080
📈 Próximas melhorias planejadas

Swagger / OpenAPI

Dockerização da aplicação

Testcontainers para testes de integração

Histórico de transações

Autenticação com Spring Security

👨‍💻 Autor

Lucas Cabral
Desenvolvedor Backend Java
