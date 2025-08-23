package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.BookService;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SmokeFlowTest {

    private UserService userService;
    private BookService bookService;
    private UserCredentials userCredentials;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        bookService = new BookService();
        userCredentials = new UserCredentials(DataFactory.username(), DataFactory.password());
    }

    @Test
    void deveCriarAutorizarAlugarEListar() {
        // Criar usuário
        String userId = userService.createUser(userCredentials);
        System.out.println("Usuario criado: " + userId);
        assertNotNull(userId);

        // Gerar token
        String token = userService.generateToken(userCredentials);
        System.out.println("Token gerado: " + token.substring(0, Math.min(20, token.length())) + "...");
        assertNotNull(token);

        // Verificar se está autorizado
        assertTrue(userService.isAuthorized(userCredentials));

        // Listar livros disponíveis - COM DEBUG
        System.out.println("Tentando listar livros...");
        Response listResp = bookService.listBooks();
        System.out.println("Status da resposta: " + listResp.statusCode());

        if (listResp.statusCode() != 200) {
            System.out.println("Corpo da resposta de erro: " + listResp.asString());
            fail("API de livros retornou erro " + listResp.statusCode() + ": " + listResp.asString());
        }

        List<String> isbns = listResp.jsonPath().getList("books.isbn");
        assertTrue(isbns.size() >= 2, "Precisa ter pelo menos 2 livros");

        List<String> escolhidos = Arrays.asList(isbns.get(0), isbns.get(1));
        System.out.println("Livros escolhidos: " + escolhidos);

        // Adicionar livros ao usuário
        Response addResp = bookService.addBooksToUser(userId, escolhidos, token);
        System.out.println("Status adicionar livros: " + addResp.statusCode());

        if (addResp.statusCode() != 201) {
            System.out.println("Erro ao adicionar livros: " + addResp.asString());
        }
        assertEquals(201, addResp.statusCode());

        // Verificar se os livros foram adicionados
        Response userResp = userService.getUser(userId, token);
        assertEquals(200, userResp.statusCode());

        List<String> isbnsDoUsuario = userResp.jsonPath().getList("books.isbn");
        assertTrue(isbnsDoUsuario.containsAll(escolhidos));

        System.out.println("Teste concluído com sucesso");
    }
}