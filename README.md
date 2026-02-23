💳 Fluxbank APIAPI REST robusta para gerenciamento de contas bancárias. Focada em Clean Architecture, transações seguras e alta cobertura de testes.🚀 Quick Start1. RequisitosJava 25 + MavenPostgreSQL2. ConfiguraçãoBash# Clone o repositório
git clone https://github.com/seu-usuario/fluxbank-api.git

# Defina as variáveis de ambiente
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha

# Execute a aplicação
./mvnw spring-boot:run
🛠 Tech StackCore: Java 25, Spring Boot 4.0.3, Spring Data JPADB: PostgreSQL + Flyway (Migrations)Testes: JUnit 5, Mockito, MockMvc🎯 Funcionalidades & RegrasRecursoRegras de NegócioContasCPF/Email únicos, Saldo inicial $0$, Geração de UUID.DepósitoApenas valores positivos.SaqueImpede saldo negativo.TransferênciaOperação transacional (ACID); impede auto-transferência.🔌 API Endpoints (Resumo)AccountsPOST /accounts/create - Cria nova conta.POST /accounts/deposit - Incrementa saldo.POST /accounts/withdraw - Deduz saldo (valida limite).POST /accounts/transfer - Movimentação entre duas contas.Tratamento de Erros: Respostas padronizadas (400, 404, 500) via GlobalExceptionHandler.🧪 Qualidade de CódigoO projeto prioriza a pirâmide de testes para garantir a integridade das transações financeiras:Testes Unitários: Regras de negócio no Service.Testes de Integração: Fluxos de Controller com MockMvc.Isolamento: Mockito para dependências externas.Bash# Rodar todos os testes
./mvnw test
📈 Roadmap[ ] Documentação com Swagger/OpenAPI[ ] Dockerização (Dockerfile & Compose)[ ] Histórico de transações (Extrato)[ ] Autenticação JWT com Spring SecurityDesenvolvido por Lucas Cabral
