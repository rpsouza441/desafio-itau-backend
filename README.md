# 🏦 Desafio Itaú - API de Estatísticas de Transações

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Sobre o Projeto

API REST desenvolvida em **Java 21** com **Spring Boot 3.5.6** que recebe transações financeiras e calcula estatísticas em tempo real. O sistema foi projetado para alta performance, utilizando algoritmo de buckets que garante complexidade **O(1)** para operações de inserção e cálculo de estatísticas.

### 🎯 Funcionalidades Principais

- ✅ **Registro de Transações**: Endpoint para registrar transações com validações robustas
- ✅ **Estatísticas em Tempo Real**: Cálculo de estatísticas (count, sum, avg, min, max) em janela temporal
- ✅ **Limpeza Automática**: Remoção automática de transações antigas
- ✅ **Alta Performance**: Algoritmo de buckets com complexidade O(1)
- ✅ **Observabilidade**: Logging estruturado e métricas com Actuator
- ✅ **Documentação**: API documentada com Swagger/OpenAPI

## 🚀 Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework principal
- **Spring Web** - API REST
- **Spring Validation** - Validações
- **Spring Actuator** - Monitoramento e métricas
- **SpringDoc OpenAPI** - Documentação da API
- **SLF4J** - Logging estruturado
- **JUnit 5** - Testes automatizados
- **Docker** - Containerização
- **Maven** - Gerenciamento de dependências

## 📊 Arquitetura

O projeto segue os princípios da **Arquitetura Hexagonal (Ports & Adapters)**:

```
src/main/java/br/dev/rodrigopinheiro/estatistica_transacao/
├── adapters/           # Camada de adaptadores
│   ├── web/           # Controllers REST
│   └── scheduler/     # Agendamentos
├── application/       # Camada de aplicação
│   ├── usecases/     # Casos de uso
│   └── ports/        # Interfaces/Contratos
├── domain/           # Camada de domínio
│   ├── entities/     # Entidades de negócio
│   └── exceptions/   # Exceções de domínio
├── infrastructure/   # Camada de infraestrutura
│   ├── repository/   # Implementações de repositório
│   └── config/       # Configurações
└── shared/          # Utilitários compartilhados
```

### 🔧 Algoritmo de Performance

O sistema utiliza uma estratégia de **buckets temporais** que divide o tempo em intervalos fixos:

- **Complexidade O(1)** para inserção de transações
- **Complexidade O(k)** para cálculo de estatísticas (onde k é o número de buckets na janela)
- **Otimização automática** entre estratégia de buckets e tradicional baseada no volume de dados

## 🛠️ Como Executar

### Pré-requisitos

- **Java 21** ou superior
- **Maven 3.6+** ou usar o wrapper incluído (`./mvnw`)
- **Docker** (opcional, para execução containerizada)

### 🏃‍♂️ Execução Local

1. **Clone o repositório**:
```bash
git clone <repository-url>
cd desafio-itau-backend
```

2. **Execute com Maven**:
```bash
cd estatistica-transacao
./mvnw spring-boot:run
```

3. **Ou compile e execute**:
```bash
./mvnw clean package
java -jar target/estatistica-transacao-0.0.1-SNAPSHOT.jar
```

### 🐳 Execução com Docker

1. **Usando Docker Compose** (recomendado):
```bash
docker-compose up --build
```

2. **Ou usando Docker diretamente**:
```bash
docker build -t estatistica-transacao .
docker run -p 8080:8080 estatistica-transacao
```

### ✅ Verificação

Após iniciar a aplicação, acesse:

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Métricas**: http://localhost:8080/actuator/metrics

## 📡 Endpoints da API

### 1. Registrar Transação
```http
POST /transacao
Content-Type: application/json

{
  "valor": 123.45,
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

**Validações**:
- `valor`: Obrigatório, não pode ser nulo, deve ser positivo
- `timestamp`: Obrigatório, não pode ser futuro, deve estar dentro da janela temporal

### 2. Obter Estatísticas
```http
GET /estatistica
```

**Resposta**:
```json
{
  "count": 10,
  "sum": 1234.56,
  "avg": 123.456,
  "min": 50.00,
  "max": 200.00
}
```

### 3. Limpar Transações
```http
DELETE /transacao
```

## ⚙️ Configurações

As configurações podem ser ajustadas no arquivo `application.yml`:

```yaml
app:
  repository:
    type: bucket  # "bucket" ou "inmemory"
  estatistica:
    janela-segundos: 60  # Janela para cálculo (padrão: 60s)
    max-janela-segundos: 3600  # Janela máxima (padrão: 1h)
  limpeza:
    max-age-segundos: 3600  # Tempo de retenção (padrão: 1h)
    intervalo-milissegundos: 60000  # Intervalo de limpeza (padrão: 1min)
```

### 🔧 Configurações via Variáveis de Ambiente

```bash
# Tipo de repositório
APP_REPOSITORY_TYPE=bucket

# Configurações de janela temporal
APP_ESTATISTICA_JANELA_SEGUNDOS=60
APP_ESTATISTICA_MAX_JANELA_SEGUNDOS=3600

# Configurações de limpeza
APP_LIMPEZA_MAX_AGE_SEGUNDOS=3600
APP_LIMPEZA_INTERVALO_MILISSEGUNDOS=60000

# Configurações de logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_BR_DEV_RODRIGOPINHEIRO=DEBUG
```

## 🧪 Testes

### Executar todos os testes:
```bash
./mvnw test
```

### Executar testes específicos:
```bash
# Testes de controller
./mvnw test -Dtest=TransacaoControllerTest

# Testes de performance
./mvnw test -Dtest=BucketPerformanceTest
```

### Cobertura de Testes

O projeto inclui:
- ✅ **Testes de Integração**: Controllers e endpoints
- ✅ **Testes de Unidade**: Casos de uso e domínio
- ✅ **Testes de Performance**: Algoritmo de buckets
- ✅ **Testes de Validação**: Regras de negócio

## 📊 Monitoramento e Observabilidade

### Endpoints de Monitoramento

- **Health Check**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Informações**: `/actuator/info`
- **Buckets Status**: `/actuator/buckets` (customizado)

### Logging Estruturado

O sistema implementa logging estruturado em todas as camadas:

```
# Exemplo de logs
2024-01-15 10:30:00.123 DEBUG [estatistica-transacao] TransacaoController - Iniciando registro de transação: valor=123.45, timestamp=2024-01-15T10:30:00Z
2024-01-15 10:30:00.125 INFO  [estatistica-transacao] RegistrarTransacaoUseCase - Transação salva com sucesso: valor=123.45, timestamp=2024-01-15T10:30:00Z
2024-01-15 10:30:00.127 DEBUG [estatistica-transacao] BucketTransacaoRepository - Transação salva no bucket: bucketId=1705316400, duration=1.2ms, totalBuckets=61
```

## 🔒 Tratamento de Erros

A API implementa tratamento de erros conforme especificação do desafio:

### Dados Inválidos
- **Status HTTP**: `422 Unprocessable Entity`
- **Content**: Vazio (sem corpo na resposta)
- **Cenários**: Valor nulo, negativo, timestamp futuro, etc.

### Transação Antiga (fora da janela temporal)
- **Status HTTP**: `204 No Content` 
- **Content**: Vazio (sem corpo na resposta)
- **Cenário**: Timestamp anterior à janela de 60 segundos

### Sucesso
- **POST /transacao**: `201 Created` com content vazio
- **GET /estatistica**: `200 OK` com JSON das estatísticas
- **DELETE /transacao**: `204 No Content` com content vazio

**Nota**: Conforme especificação do desafio, o tratamento de erros é feito apenas através do **content** (corpo da resposta vazio) e códigos de status HTTP apropriados, sem retorno de mensagens de erro detalhadas no corpo da resposta.

## 🚀 Performance

### Benchmarks

O algoritmo de buckets oferece performance superior:

- **Inserção**: ~0.1ms (O(1))
- **Cálculo de Estatísticas**: ~0.5ms para janela de 60s
- **Memória**: ~1MB para 1 milhão de transações em 1 hora

### Otimizações Implementadas

1. **Algoritmo de Buckets**: Complexidade O(1) para inserção
2. **Estratégia Adaptativa**: Escolha automática entre algoritmos
3. **Limpeza Automática**: Remoção de dados antigos
4. **Configurações Otimizadas**: JVM tuning para containers

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

**Rodrigo Pinheiro**
- GitHub: [@rpsouza441](https://github.com/rpsouza441/)
- LinkedIn: [Rodrigo Pinheiro](https://linkedin.com/in/rpsouza)

---

⭐ **Se este projeto foi útil para você, considere dar uma estrela!**