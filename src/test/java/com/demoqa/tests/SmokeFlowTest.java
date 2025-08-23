package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.BookService;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Smoke Flow Test - Fluxo Principal da API")
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
    @DisplayName("Deve executar fluxo completo: criar usuário, autorizar e gerenciar livros")
    void deveCriarAutorizarAlugarEListar() {
        // Etapa 1: Criar usuário
        String userId = criarUsuario();

        // Etapa 2: Gerar token de autenticação
        String token = gerarToken();

        // Etapa 3: Validar autorização
        validarAutorizacao();

        // Etapa 4: Obter livros disponíveis e selecionar alguns
        List<String> livrosEscolhidos = obterLivrosDisponiveis();

        // Etapa 5: Adicionar livros ao usuário
        adicionarLivrosAoUsuario(userId, livrosEscolhidos, token);

        // Etapa 6: Validar que os livros foram adicionados corretamente
        validarLivrosDoUsuario(userId, livrosEscolhidos, token);
    }

    private String criarUsuario() {
        String userId = userService.createUser(userCredentials);
        System.out.println("Usuário criado: " + userId);

        assertNotNull(userId, "userId não deve ser nulo");
        assertFalse(userId.trim().isEmpty(), "userId não deve estar vazio");

        return userId;
    }

    private String gerarToken() {
        String token = userService.generateToken(userCredentials);

        assertNotNull(token, "token não deve ser nulo");
        assertFalse(token.trim().isEmpty(), "token não deve estar vazio");

        return token;
    }

    private void validarAutorizacao() {
        boolean autorizado = userService.isAuthorized(userCredentials);
        assertTrue(autorizado, "Usuário deve estar autorizado");
    }

    private List<String> obterLivrosDisponiveis() {
        Response listResp = bookService.listBooks();
        assertEquals(200, listResp.statusCode(), "Listagem de livros deve retornar 200");

        List<String> isbns = listResp.jsonPath().getList("books.isbn");
        assertNotNull(isbns, "Lista de ISBNs não deve ser nula");
        assertTrue(isbns.size() >= 2, "Deve haver pelo menos 2 livros");

        List<String> escolhidos = Arrays.asList(isbns.get(0), isbns.get(1));
        System.out.println("Livros escolhidos: " + escolhidos);

        return escolhidos;
    }

    private void adicionarLivrosAoUsuario(String userId, List<String> livros, String token) {
        Response addResp = bookService.addBooksToUser(userId, livros, token);
        assertEquals(201, addResp.statusCode(), "Adicionar livros deve retornar 201");
        System.out.println("Livros adicionados com sucesso ao usuário");
    }

    private void validarLivrosDoUsuario(String userId, List<String> livrosEsperados, String token) {
        Response userResp = userService.getUser(userId, token);
        assertEquals(200, userResp.statusCode(), "GET user deve retornar 200");

        List<String> isbnsDoUsuario = userResp.jsonPath().getList("books.isbn");
        assertNotNull(isbnsDoUsuario, "Lista de livros do usuário não deve ser nula");
        assertTrue(isbnsDoUsuario.containsAll(livrosEsperados),
                "Usuário deve conter os 2 livros escolhidos");

        System.out.println("Validação concluída: usuário possui " + isbnsDoUsuario.size() + " livros");
    }
}