package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.BookService;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SmokeFlowTest {

    @Test
    void deveCriarAutorizarAlugarEListar() {
        // Arrange
        String userName = DataFactory.username();
        String password = DataFactory.password();
        UserCredentials creds = new UserCredentials(userName, password);

        UserService userService = new UserService();
        BookService bookService = new BookService();

        // 1) Criar usuário
        String userId = userService.createUser(creds);
        assertNotNull(userId, "userId não deve ser nulo");

        // 2) Gerar token
        String token = userService.generateToken(creds);
        assertNotNull(token, "token não deve ser nulo");

        // 3) Verificar autorizado
        assertTrue(userService.isAuthorized(creds), "Usuário deve estar autorizado");

        // 4) Listar livros e escolher 2
        Response listResp = bookService.listBooks();
        assertEquals(200, listResp.statusCode(), "Listagem de livros deve retornar 200");
        List<String> isbns = listResp.jsonPath().getList("books.isbn");
        assertTrue(isbns.size() >= 2, "Deve haver pelo menos 2 livros");

        List<String> escolhidos = Arrays.asList(isbns.get(0), isbns.get(1));

        // 5) Alugar (adicionar) livros ao usuário
        Response addResp = bookService.addBooksToUser(userId, escolhidos, token);
        assertEquals(201, addResp.statusCode(), "Adicionar livros deve retornar 201");

        // 6) Listar detalhes do usuário e conferir livros
        Response userResp = userService.getUser(userId, token);
        assertEquals(200, userResp.statusCode(), "GET user deve retornar 200");
        List<String> isbnsDoUsuario = userResp.jsonPath().getList("books.isbn");
        assertTrue(isbnsDoUsuario.containsAll(escolhidos), "Usuário deve conter os 2 livros escolhidos");
    }
}