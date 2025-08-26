# Bookstore Automation API
## Automação de Testes da API da Bookstore

Este projeto foi desenvolvido para automatizar testes na API da Bookstore, garantindo a estabilidade e o comportamento esperado da plataforma. Ele valida funcionalidades como criação de usuários, autenticação, listagem de livros e operações de gerenciamento de livros na conta de um usuário.

---

## Tecnologias Utilizadas

* **Java 17**: Linguagem principal do projeto.
* **JUnit 5**: Framework para execução e organização dos testes.
* **RestAssured**: Facilita requisições HTTP e validação de respostas da API.
* **ExtentReports 5.1.2**: Geração de relatórios HTML interativos e detalhados.
* **Jackson**: Serialização e desserialização de objetos JSON.
* **JavaFaker**: Geração de dados de teste aleatórios, tornando os cenários mais dinâmicos.
* **Maven**: Gerenciador de dependências e execução de build/testes.

---

## Funcionalidades de Teste

O projeto cobre as seguintes funcionalidades da API:

* **Autenticação e Autorização**: Criação de usuários, geração de tokens e validação de permissões.
* **Gestão de Livros**: Listagem de livros disponíveis na plataforma.
* **Operações CRUD**: Adição de livros à conta de um usuário.
* **Relatórios**: Geração de relatórios detalhados e interativos em HTML usando ExtentReports.

---

### Como Clonar e Executar

### Pré-requisitos

Certifique-se de ter instalados:

* **Maven**
* **JDK 17**

### Clonando o Repositório

```bash
git clone https://github.com/PauloRobert/bookstore-automation-api
cd bookstore-automation-api
```
### Executando os Testes Localmente

Para rodar todos os testes e gerar o relatório ExtentReports:
```bash
mvn clean test
```

O relatório será gerado automaticamente em:

```bash
target/extent-report.html
```

Abra esse arquivo no navegador para visualizar os resultados detalhados.

### Integração com GitHub Actions

O projeto agora está integrado com GitHub Actions, automatizando a execução dos testes e geração de relatórios na nuvem.

##### Funcionalidades do Workflow

* Executa nos branches master e develop.

* Executa automaticamente de hora em hora via agendamento (cron).

* Gera o relatório ExtentReports HTML.

* Publica o relatório como artefato do workflow, disponível para download.

#### Como Funciona

Qualquer push ou pull request nos branches master ou develop dispara o workflow.

O Maven executa todos os testes da API.

O relatório HTML é gerado automaticamente.

O relatório é publicado como artefato do workflow para download direto pelo GitHub Actions.

Na seção Artifacts, clique no artefato gerado para baixar o relatório.

Com essa integração, você garante visibilidade contínua da qualidade da API e facilita o acompanhamento dos testes sem precisar rodar localmente.