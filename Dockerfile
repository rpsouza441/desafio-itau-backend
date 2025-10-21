# Multi-stage Dockerfile para Desafio Itaú Backend
# Estágio 1: Build da aplicação
FROM openjdk:21-jdk-slim AS builder

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY estatistica-transacao/pom.xml ./
COPY estatistica-transacao/mvnw ./
COPY estatistica-transacao/mvnw.cmd ./
COPY estatistica-transacao/.mvn ./.mvn

# Baixar dependências (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY estatistica-transacao/src ./src

# Build da aplicação
RUN ./mvnw clean package -DskipTests -B

# Estágio 2: Runtime da aplicação
FROM openjdk:21-jre-slim AS runtime

# Criar usuário não-root para segurança
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da aplicação do estágio de build
COPY --from=builder /app/target/*.jar app.jar

# Alterar proprietário dos arquivos
RUN chown -R appuser:appuser /app

# Mudar para usuário não-root
USER appuser

# Expor porta da aplicação
EXPOSE 8080

# Configurar JVM para container
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]