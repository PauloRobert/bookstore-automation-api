package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.BookService;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Bookstore API")
@Feature("Smoke Flow")
@Owner("Stark")
@Severity(SeverityLevel.BLOCKER)
@DisplayName("Fluxo completo: criar usuário, autorizar, alugar e listar livros")
public class SmokeFlowTest {

    private UserService userService;
    private BookService bookService;
    private UserCredentials userCredentials;

    @BeforeEach
    @Step("Inicializando serviços e credenciais do usuário")
    void setUp() {
        userService = new UserService();
        bookService = new BookService();
        userCredentials = new UserCredentials(DataFactory.username(), DataFactory.password());
    }

    @Test
    @Story("Fluxo principal do usuário")
    @Description("Teste que valida o fluxo crítico: criar usuário → gerar token → autorizar → listar livros → alugar → validar aluguel")
    void deveCriarAutorizarAlugarEListar() {
        // Criar usuário
        Allure.step("Criando usuário");
        String userId = userService.createUser(userCredentials);
        assertNotNull(userId, "Usuário deve ser criado com sucesso");

        // Gerar token
        Allure.step("Gerando token para o usuário");
        String token = userService.generateToken(userCredentials);
        assertNotNull(token, "Token deve ser gerado");

        // Verificar autorização
        Allure.step("Verificando se o usuário está autorizado");
        assertTrue(userService.isAuthorized(userCredentials), "Usuário deveria estar autorizado");

        // Listar livros disponíveis
        Allure.step("Listando livros disponíveis");
        Response listResp = bookService.listBooks();
        assertEquals(200, listResp.statusCode(), "API de livros deveria retornar 200");

        List<String> isbns = listResp.jsonPath().getList("books.isbn");
        assertTrue(isbns.size() >= 2, "Precisa ter pelo menos 2 livros disponíveis");

        List<String> escolhidos = Arrays.asList(isbns.get(0), isbns.get(1));
        Allure.step("Selecionando livros para aluguel: " + escolhidos);

        // Adicionar livros ao usuário
        Allure.step("Adicionando livros ao usuário");
        Response addResp = bookService.addBooksToUser(userId, escolhidos, token);
        assertEquals(201, addResp.statusCode(), "Livros deveriam ser adicionados com sucesso");

        // Verificar se os livros foram adicionados
        Allure.step("Validando livros alugados pelo usuário");
        Response userResp = userService.getUser(userId, token);
        assertEquals(200, userResp.statusCode(), "Consulta do usuário deve retornar 200");

        List<String> isbnsDoUsuario = userResp.jsonPath().getList("books.isbn");
        assertTrue(isbnsDoUsuario.containsAll(escolhidos), "Livros alugados devem constar no perfil do usuário");
    }
}