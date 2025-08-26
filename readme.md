# Bookstore Automation API
## Automação de Testes da API da Bookstore

Este projeto foi desenvolvido para automatizar testes na API da Bookstore, com foco em operações de gerenciamento de usuários e livros. Ele garante a estabilidade e o comportamento esperado da API, validando funcionalidades como a criação de usuários, autenticação, e o ciclo de vida dos livros (listagem e adição à conta de um usuário). 

## Tecnologias Utilizadas

A automação foi construída utilizando um conjunto de ferramentas robustas, que garantem a eficiência e a qualidade dos testes.

* Java 17: Linguagem principal do projeto.

* JUnit 5: Framework de testes para a execução e organização dos cenários de teste.

* RestAssured: Biblioteca para facilitar as requisições HTTP e a validação das respostas da API.

* ExtentReports: Gerador de relatórios de testes, que oferece uma visualização detalhada e interativa dos resultados em HTML.

* Jackson: Biblioteca para serialização e desserialização de objetos JSON.

* JavaFaker: Ferramenta para a geração de dados de teste aleatórios, tornando os cenários mais dinâmicos e realistas.

## Funcionalidades de Teste

O projeto cobre as seguintes funcionalidades da API:

* Autenticação e Autorização: Criação de novos usuários e autenticação para obter tokens de acesso, além da validação de permissões de usuário.
* Gestão de Livros: Listagem de livros disponíveis na plataforma.
* Operações CRUD: Adição de livros à conta de um usuário específico. 
* Relatórios: Geração de relatórios de teste detalhados e visuais com a ferramenta ExtentReports.

## Como Clonar e Executar

Siga os passos abaixo para clonar o repositório e executar os testes na sua máquina. Pré-requisitos

Certifique-se de que você tem as seguintes ferramentas instaladas:

* Maven: Gerenciador de dependências do projeto.

* JDK 17: O kit de desenvolvimento Java necessário para compilar e rodar o projeto.

Clonando o Repositório

Abra seu terminal e execute o seguinte comando:
```
git clone https://github.com/PauloRobert/bookstore-automation-api cd bookstore-automation-api
```
## Executando os Testes

Para rodar todos os testes e gerar o relatório ExtentReports, basta executar o comando Maven:
```
mvn clean test
```
Após a execução, o relatório HTML será gerado automaticamente na pasta target/surefire-reports/. Você pode abrir o arquivo ExtentReport.html para visualizar os resultados.