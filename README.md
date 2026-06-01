# 🌾 Colhe+

**Colhe+** é uma plataforma digital desenvolvida para otimizar a comercialização agrícola por meio do agrupamento de demandas de pequenos e médios compradores em lotes de colheita viáveis.

O sistema conecta produtores rurais e compradores, permitindo que vários pedidos sejam somados em um mesmo lote. Quando a demanda total atinge o volume mínimo definido pelo produtor, a colheita se torna viável, reduzindo desperdícios e melhorando a eficiência econômica da produção agrícola.

---

## 🎯 Objetivo

O objetivo do **Colhe+** é reduzir perdas no campo e aumentar a rentabilidade dos produtores, tornando possível a comercialização de pequenas demandas por meio de um sistema de agrupamento inteligente.

A plataforma foi desenvolvida como um projeto acadêmico com foco em boas práticas de Engenharia de Software, arquitetura em camadas, integração entre frontend e backend, banco de dados relacional, autenticação, controle de acesso e automação com CI/CD.

---

## 🚀 Principais Funcionalidades

### 🔐 Autenticação e Controle de Acesso

O sistema possui autenticação de usuários e controle de permissões por tipo de perfil.

Perfis previstos:

- Produtor
- Comprador
- Administrador

Cada perfil possui acesso a funcionalidades específicas da plataforma.

---

### 🌾 Gestão de Lotes

Produtores podem cadastrar e gerenciar lotes agrícolas, definindo informações como:

- produto;
- volume disponível;
- volume mínimo viável;
- preço por kg;
- taxa de entrega;
- raio máximo de entrega;
- status do lote.

---

### 🛒 Gestão de Pedidos

Compradores podem visualizar lotes disponíveis e realizar pedidos de compra.

Os pedidos são agrupados automaticamente ao lote correspondente, contribuindo para que o volume mínimo de colheita seja atingido.

---

### 🔁 Agrupamento Inteligente de Demanda

A principal regra de negócio do Colhe+ é a soma dos pedidos feitos por diferentes compradores.

Exemplo:

```text
Volume mínimo viável do lote: 800 kg

Pedido 1: 200 kg
Pedido 2: 300 kg
Pedido 3: 300 kg

Total agrupado: 800 kg
Status: lote viável para colheita
```

Esse mecanismo permite que pequenas demandas se tornem economicamente viáveis quando agrupadas.

---

### 🚚 Validação de Entrega por Localização

O sistema considera a localização do produtor e do comprador para verificar a viabilidade da entrega, respeitando o raio máximo definido no lote.

---

### 🛠 Administração

Administradores podem acompanhar usuários, lotes e atividades gerais da plataforma.

---

## 🧱 Estrutura do Projeto

```text
ColhePlus/
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── colhe-plus/
│   └── Frontend React + Vite
│
├── src/
│   └── Backend Java + Spring Boot
│
├── compose.yaml
├── pom.xml
├── checkstyle.xml
├── package-lock.json
├── LICENSE
└── README.md
```

---

## ⚙️ Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security Crypto
- Maven
- PostgreSQL
- H2 para testes
- Lombok

### Frontend

- React
- Vite
- Tailwind CSS
- JavaScript

### Banco de Dados

- PostgreSQL 16
- Docker Compose

### DevOps

- GitHub Actions
- Maven
- Checkstyle
- Testes automatizados

---

# ▶️ Como Rodar o Projeto

Esta seção mostra o passo a passo para executar o projeto localmente.

---

## ✅ Pré-requisitos

Antes de começar, verifique se você possui instalado:

- Git
- Java 21
- Maven
- Node.js
- npm
- Docker
- Docker Compose

---

## 1. Clonar o Repositório

```bash
git clone https://github.com/JoaoPioltini/ColhePlus.git
cd ColhePlus
```

---

## 2. Subir o Banco de Dados PostgreSQL

O projeto utiliza PostgreSQL via Docker Compose.

Na raiz do projeto, execute:

```bash
docker compose up -d postgres
```

O banco será iniciado com as seguintes configurações:

```text
Banco: colheplus
Usuário: colheplus
Senha: colheplus
Porta: 5432
```

URL padrão de conexão:

```text
jdbc:postgresql://localhost:5432/colheplus
```

---

## 3. Rodar o Backend

Com o banco PostgreSQL em execução, rode a API Spring Boot:

```bash
mvn spring-boot:run
```

A API será executada em:

```text
http://localhost:8080
```

## Local Frontend

Run the React SPA from the `colhe-plus` directory after starting the backend API:

```bash
cd colhe-plus
npm install
npm run dev
```

By default, Vite serves the frontend at `http://localhost:5173`. Set
`VITE_API_URL=http://localhost:8080` in `colhe-plus/.env.local` when the backend
is running on the default local port.

---

## 4. Rodar o Frontend

Em outro terminal, entre na pasta do frontend:

```bash
cd colhe-plus
```

Instale as dependências:

```bash
npm install
```

Execute o frontend:

```bash
npm run dev
```

O frontend será executado em:

```text
http://localhost:5173
```

---

## 5. Rodando Backend e Frontend ao Mesmo Tempo

Para usar o sistema completo localmente, mantenha três processos ativos:

### Terminal 1 — Banco de dados

```bash
docker compose up -d postgres
```

### Terminal 2 — Backend

```bash
mvn spring-boot:run
```

### Terminal 3 — Frontend

```bash
cd colhe-plus
npm install
npm run dev
```

Depois, acesse:

```text
http://localhost:5173
```

---

## 🧪 Executando Testes

Para executar os testes do backend:

```bash
mvn test
```

---

## 🧹 Verificando Qualidade do Código

O projeto utiliza Checkstyle para validação de padrões de código.

Execute:

```bash
mvn checkstyle:check
```

---

## 📦 Gerando o Build do Backend

Para compilar o backend e gerar o arquivo `.jar`:

```bash
mvn clean package
```

Para executar o `.jar` gerado:

```bash
java -jar target/*.jar
```

---

## 🏗️ Gerando o Build do Frontend

Entre na pasta do frontend:

```bash
cd colhe-plus
```

Execute:

```bash
npm run build
```

Para visualizar o build localmente:

```bash
npm run preview
```

---

## 🔁 CI/CD

O projeto possui pipeline de Integração Contínua com GitHub Actions.

O workflow é executado em pushes e pull requests nas branches:

- `main`
- `dev`

Etapas do pipeline:

1. Clonagem do repositório.
2. Configuração do Java 21.
3. Cache do Maven.
4. Verificação de qualidade com Checkstyle.
5. Build do projeto com Maven.
6. Execução dos testes automatizados.

---

## 📌 Status do Projeto

🚧 **Em desenvolvimento**

O Colhe+ é um projeto acadêmico em evolução, com foco em boas práticas de desenvolvimento, arquitetura organizada, integração entre camadas e resolução de um problema real do setor agrícola.

---

## 🧭 Próximos Passos

Possíveis melhorias futuras:

- finalizar autenticação completa;
- melhorar a proteção das rotas;
- adicionar mais testes automatizados;
- documentar endpoints da API;
- criar dashboard administrativo;
- melhorar validações dos pedidos;
- evoluir o deploy em nuvem;
- containerizar mais partes da aplicação.

---

## 👨‍💻 Equipe

Projeto desenvolvido para fins acadêmicos.

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT.
