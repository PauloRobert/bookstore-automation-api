package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.BookService;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.aventstack.extentreports.ExtentTest;
import com.demoqa.TestSuite;

@DisplayName("Book API Tests - Testes específicos da API de livros")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookApiTest {

    private BookService bookService;
    private UserService userService;
    private ExtentTest test;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        userService = new UserService();
    }

    @AfterAll
    static void tearDown() {
        TestSuite.flushReport();
    }

    @Test
    @Order(1)
    void deveListarLivrosDisponiveisComSucesso() {
        test = TestSuite.extent.createTest(
                "Listar livros disponíveis",
                "Verifica se a listagem de livros retorna corretamente"
        );

        Response response = bookService.listBooks();
        assertEquals(200, response.statusCode(), "Listagem deve retornar status 200");
        test.pass("Status 200 retornado com sucesso");

        List<Object> books = response.jsonPath().getList("books");
        assertNotNull(books, "Lista de livros não deve ser nula");
        assertFalse(books.isEmpty(), "Deve existir pelo menos um livro");
        test.pass("Lista de livros válida");

        String firstBookIsbn = response.jsonPath().getString("books[0].isbn");
        String firstBookTitle = response.jsonPath().getString("books[0].title");

        assertNotNull(firstBookIsbn, "ISBN deve estar presente");
        assertNotNull(firstBookTitle, "Título deve estar presente");
        assertFalse(firstBookIsbn.trim().isEmpty(), "ISBN não deve estar vazio");
        assertFalse(firstBookTitle.trim().isEmpty(), "Título não deve estar vazio");
        test.pass("Primeiro livro possui ISBN e título válidos");
    }

    @Test
    @Order(2)
    void deveAdicionarLivroAoUsuarioAutorizado() {
        test = TestSuite.extent.createTest(
                "Adicionar livro ao usuário autorizado",
                "Valida a adição de um livro a um usuário existente"
        );

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);
        test.info("Usuário criado e token gerado");

        Response booksResponse = bookService.listBooks();
        List<String> availableIsbns = booksResponse.jsonPath().getList("books.isbn");
        assertTrue(availableIsbns.size() >= 1, "Deve haver pelo menos um livro disponível");

        List<String> selectedBook = Arrays.asList(availableIsbns.get(0));
        Response addResponse = bookService.addBooksToUser(userId, selectedBook, token);

        assertEquals(201, addResponse.statusCode(), "Adição deve retornar status 201");
        test.pass("Livro adicionado com sucesso");

        List<Object> addedBooks = addResponse.jsonPath().getList("books");
        assertNotNull(addedBooks, "Lista de livros adicionados não deve ser nula");
        assertEquals(1, addedBooks.size(), "Deve retornar exatamente um livro adicionado");
        test.pass("Validação da quantidade de livros adicionados concluída");
    }

    @Test
    @Order(3)
    void deveAdicionarMultiplosLivrosAoUsuario() {
        test = TestSuite.extent.createTest(
                "Adicionar múltiplos livros ao usuário",
                "Valida a adição de múltiplos livros ao usuário"
        );

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);

        Response booksResponse = bookService.listBooks();
        List<String> availableIsbns = booksResponse.jsonPath().getList("books.isbn");
        assertTrue(availableIsbns.size() >= 2, "Deve haver pelo menos dois livros para o teste");

        List<String> selectedBooks = Arrays.asList(availableIsbns.get(0), availableIsbns.get(1));
        Response addResponse = bookService.addBooksToUser(userId, selectedBooks, token);

        assertEquals(201, addResponse.statusCode(), "Deve permitir adicionar múltiplos livros");
        test.pass("Múltiplos livros adicionados com sucesso");

        List<Object> addedBooks = addResponse.jsonPath().getList("books");
        assertEquals(2, addedBooks.size(), "Deve adicionar exatamente 2 livros");

        List<String> addedIsbns = addResponse.jsonPath().getList("books.isbn");
        assertTrue(addedIsbns.containsAll(selectedBooks), "Livros adicionados devem corresponder aos selecionados");
        test.pass("Validação de livros adicionados correta");
    }

    @Test
    @Order(4)
    void naoDeveAdicionarLivrosComTokenInvalido() {
        test = TestSuite.extent.createTest(
                "Não deve adicionar livros com token inválido",
                "Valida que adição com token inválido falha"
        );

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String tokenInvalido = "token_invalido_123";

        Response booksResponse = bookService.listBooks();
        List<String> availableIsbns = booksResponse.jsonPath().getList("books.isbn");

        if (!availableIsbns.isEmpty()) {
            List<String> selectedBook = Arrays.asList(availableIsbns.get(0));
            Response addResponse = bookService.addBooksToUser(userId, selectedBook, tokenInvalido);

            assertNotEquals(201, addResponse.statusCode(), "Não deve permitir adicionar livros com token inválido");
            assertTrue(addResponse.statusCode() >= 400, "Deve retornar erro (4xx ou 5xx)");
            test.pass("Falha corretamente identificada ao adicionar livro com token inválido");
        }
    }
}