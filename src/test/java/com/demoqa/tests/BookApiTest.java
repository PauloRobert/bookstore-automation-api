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
@Feature("Book Management")
@Owner("Stark")
@DisplayName("Book API Tests - Testes específicos da API de livros")
public class BookApiTest {

    private BookService bookService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        userService = new UserService();
    }

    @Test
    @Story("Listar livros")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Deve listar livros disponíveis com sucesso")
    void deveListarLivrosDisponiveisComSucesso() {
        Response response = bookService.listBooks();

        assertEquals(200, response.statusCode(), "Listagem deve retornar status 200");

        List<Object> books = response.jsonPath().getList("books");
        assertNotNull(books, "Lista de livros não deve ser nula");
        assertFalse(books.isEmpty(), "Deve existir pelo menos um livro");

        String firstBookIsbn = response.jsonPath().getString("books[0].isbn");
        String firstBookTitle = response.jsonPath().getString("books[0].title");

        assertNotNull(firstBookIsbn, "ISBN deve estar presente");
        assertNotNull(firstBookTitle, "Título deve estar presente");
        assertFalse(firstBookIsbn.trim().isEmpty(), "ISBN não deve estar vazio");
        assertFalse(firstBookTitle.trim().isEmpty(), "Título não deve estar vazio");
    }

    @Test
    @Story("Adicionar livro ao usuário")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Deve adicionar livro ao usuário autorizado")
    void deveAdicionarLivroAoUsuarioAutorizado() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);

        Response booksResponse = bookService.listBooks();
        List<String> availableIsbns = booksResponse.jsonPath().getList("books.isbn");
        assertTrue(availableIsbns.size() >= 1, "Deve haver pelo menos um livro disponível");

        List<String> selectedBook = Arrays.asList(availableIsbns.get(0));

        Response addResponse = bookService.addBooksToUser(userId, selectedBook, token);

        assertEquals(201, addResponse.statusCode(), "Adição deve retornar status 201");

        List<Object> addedBooks = addResponse.jsonPath().getList("books");
        assertNotNull(addedBooks, "Lista de livros adicionados não deve ser nula");
        assertEquals(1, addedBooks.size(), "Deve retornar exatamente um livro adicionado");
    }

    @Test
    @Story("Adicionar múltiplos livros")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Deve adicionar múltiplos livros ao usuário")
    void deveAdicionarMultiplosLivrosAoUsuario() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);

        Response booksResponse = bookService.listBooks();
        List<String> availableIsbns = booksResponse.jsonPath().getList("books.isbn");
        assertTrue(availableIsbns.size() >= 2, "Deve haver pelo menos dois livros para o teste");

        List<String> selectedBooks = Arrays.asList(
                availableIsbns.get(0),
                availableIsbns.get(1)
        );

        Response addResponse = bookService.addBooksToUser(userId, selectedBooks, token);

        assertEquals(201, addResponse.statusCode(), "Deve permitir adicionar múltiplos livros");

        List<Object> addedBooks = addResponse.jsonPath().getList("books");
        assertEquals(2, addedBooks.size(), "Deve adicionar exatamente 2 livros");

        List<String> addedIsbns = addResponse.jsonPath().getList("books.isbn");
        assertTrue(addedIsbns.containsAll(selectedBooks),
                "Livros adicionados devem corresponder aos selecionados");
    }

    @Test
    @Story("Adicionar livro com token inválido")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Não deve adicionar livros com token inválido")
    void naoDeveAdicionarLivrosComTokenInvalido() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String tokenInvalido = "token_invalido_123";

        Response booksResponse = bookService.listBooks();
        List<String> availableIsbns = booksResponse.jsonPath().getList("books.isbn");

        if (!availableIsbns.isEmpty()) {
            List<String> selectedBook = Arrays.asList(availableIsbns.get(0));

            Response addResponse = bookService.addBooksToUser(userId, selectedBook, tokenInvalido);

            assertNotEquals(201, addResponse.statusCode(),
                    "Não deve permitir adicionar livros com token inválido");
            assertTrue(addResponse.statusCode() >= 400,
                    "Deve retornar erro (4xx ou 5xx)");
        }
    }
}