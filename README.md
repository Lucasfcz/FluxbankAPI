💳 Fluxbank APIAPI REST robusta para gerenciamento de contas bancárias. Focada em Clean Architecture, transações seguras e alta cobertura de testes.🚀 Quick Start1. RequisitosJava 25 + MavenPostgreSQL2. ConfiguraçãoBash# Clone o repositório
git clone https://github.com/seu-usuario/fluxbank-api.git
💰 Fluxbank API

# Defina as variáveis de ambiente
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha
API REST para simulação de operações bancárias básicas, desenvolvida com Spring Boot, seguindo boas práticas de arquitetura, validação, transações e testes automatizados.

📌 Funcionalidades

✔️ Criação de contas bancárias
✔️ Depósito
✔️ Saque
✔️ Transferência entre contas
✔️ Validações de regra de negócio
✔️ Tratamento global de exceções
✔️ Migração de banco com Flyway
✔️ Testes unitários (Service e Controller)

🛠️ Stack Tecnológica
Tecnologia	Uso
Java 25	Linguagem principal
Spring Boot 4.0.3	Framework backend
Spring Web MVC	API REST
Spring Data JPA	Persistência
Hibernate	ORM
Flyway	Versionamento de banco
PostgreSQL	Banco de dados
Maven Wrapper	Build
JUnit 5	Testes
Mockito	Mocks
MockMvc	Testes de controller
🧱 Arquitetura
controller
 └── recebe requests HTTP
service
 └── regras de negócio e transações
domain
 └── entidades e enums
repository
 └── acesso ao banco (JPA)
dto
 └── entrada e saída da API
exception
 └── exceções customizadas + handler global
🗃️ Modelo de Conta
Account
 ├─ id (UUID)
 ├─ holderName
 ├─ cpf (único)
 ├─ email (único)
 ├─ balance
 ├─ accountType
 └─ createdAt
Tipos de Conta

CHECKING

SAVINGS

BUSINESS

INVESTMENT

DIGITAL_WALLET

🔁 Endpoints
🔹 Criar Conta

POST /accounts/create

Request

{
  "holderName": "Lucas Cabral",
  "cpf": "12345678900",
  "email": "lucas@email.com",
  "accountType": "CHECKING"
}

Response – 201

{
  "accountId": "uuid",
  "balance": 0
}
🔹 Depósito

POST /accounts/deposit

{
  "accountId": "uuid",
  "amount": 150.00
}
🔹 Saque

POST /accounts/withdraw

{
  "accountId": "uuid",
  "amount": 50.00
}
🔹 Transferência

POST /accounts/transfer

{
  "fromId": "uuid",
  "toId": "uuid",
  "amount": 100.00
}

Response

Transfer successful
⚠️ Tratamento de Erros

Formato padrão:

{
  "timestamp": "2026-02-22T10:00:00",
  "message": "mensagem de erro"
}
Casos tratados

❌ Conta inexistente → 404 Not Found

❌ Transferência para mesma conta → 400 Bad Request

❌ Saldo insuficiente

❌ Payload inválido (Bean Validation)

🧪 Testes

Testes unitários no Service

Testes de Controller com MockMvc

Banco mockado (Mockito) — não depende do PostgreSQL real

Rodar testes:

# Execute a aplicação
./mvnw spring-boot:run
🛠 Tech StackCore: Java 25, Spring Boot 4.0.3, Spring Data JPADB: PostgreSQL + Flyway (Migrations)Testes: JUnit 5, Mockito, MockMvc🎯 Funcionalidades & RegrasRecursoRegras de NegócioContasCPF/Email únicos, Saldo inicial $0$, Geração de UUID.DepósitoApenas valores positivos.SaqueImpede saldo negativo.TransferênciaOperação transacional (ACID); impede auto-transferência.🔌 API Endpoints (Resumo)AccountsPOST /accounts/create - Cria nova conta.POST /accounts/deposit - Incrementa saldo.POST /accounts/withdraw - Deduz saldo (valida limite).POST /accounts/transfer - Movimentação entre duas contas.Tratamento de Erros: Respostas padronizadas (400, 404, 500) via GlobalExceptionHandler.🧪 Qualidade de CódigoO projeto prioriza a pirâmide de testes para garantir a integridade das transações financeiras:Testes Unitários: Regras de negócio no Service.Testes de Integração: Fluxos de Controller com MockMvc.Isolamento: Mockito para dependências externas.Bash# Rodar todos os testes
./mvnw test
📈 Roadmap[ ] Documentação com Swagger/OpenAPI[ ] Dockerização (Dockerfile & Compose)[ ] Histórico de transações (Extrato)[ ] Autenticação JWT com Spring SecurityDesenvolvido por Lucas Cabral
🛢️ Banco de Dados & Flyway

Banco: PostgreSQL

Migração automática via Flyway

📂 Local:

src/main/resources/db/migration
└── V1__create_accounts_table.sql

Tabela criada:

tb_accounts
⚙️ Configuração
application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fluxbank
spring.datasource.username=${DB_USERNAME:}
spring.datasource.password=${DB_PASSWORD:}
Variáveis de ambiente (Windows PowerShell)
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
▶️ Executando a aplicação
./mvnw spring-boot:run

📍 API disponível em:

http://localhost:8080
🎯 Objetivo do Projeto

Este projeto foi desenvolvido com foco em:

Consolidação de Spring Boot

Aplicação de regras de negócio reais

Escrita de testes automatizados

Preparação para estágio backend Java

👨‍💻 Autor

Lucas Cabral
Estudante de ADS | Backend Java
📍 IFPE – Paulista
