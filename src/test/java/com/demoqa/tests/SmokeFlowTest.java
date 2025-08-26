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

@DisplayName("Fluxo completo: criar usuário, autorizar, alugar e listar livros")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmokeFlowTest {

    private UserService userService;
    private BookService bookService;
    private UserCredentials userCredentials;
    private ExtentTest test;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        bookService = new BookService();
        userCredentials = new UserCredentials(DataFactory.username(), DataFactory.password());
    }

    @AfterAll
    static void tearDown() {
        TestSuite.flushReport();
    }

    @Test
    @Order(1)
    void deveCriarAutorizarAlugarEListar() {
        test = TestSuite.extent.createTest(
                "Smoke Flow Test",
                "Fluxo completo: criar usuário, autorizar, alugar e listar livros"
        );

        // Criar usuário
        test.info("Criando usuário");
        String userId = userService.createUser(userCredentials);
        assertNotNull(userId, "Usuário deve ser criado com sucesso");
        test.pass("Usuário criado: " + userId);

        // Gerar token
        test.info("Gerando token para o usuário");
        String token = userService.generateToken(userCredentials);
        assertNotNull(token, "Token deve ser gerado");
        test.pass("Token gerado com sucesso");

        // Verificar autorização
        test.info("Verificando se o usuário está autorizado");
        assertTrue(userService.isAuthorized(userCredentials), "Usuário deveria estar autorizado");
        test.pass("Usuário autorizado com sucesso");

        // Listar livros disponíveis
        test.info("Listando livros disponíveis");
        Response listResp = bookService.listBooks();
        assertEquals(200, listResp.statusCode(), "API de livros deveria retornar 200");
        List<String> isbns = listResp.jsonPath().getList("books.isbn");
        assertTrue(isbns.size() >= 2, "Precisa ter pelo menos 2 livros disponíveis");
        test.pass("Livros listados com sucesso: " + isbns);

        // Selecionar livros
        List<String> escolhidos = Arrays.asList(isbns.get(0), isbns.get(1));
        test.info("Selecionando livros para aluguel: " + escolhidos);

        // Adicionar livros ao usuário
        test.info("Adicionando livros ao usuário");
        Response addResp = bookService.addBooksToUser(userId, escolhidos, token);
        assertEquals(201, addResp.statusCode(), "Livros deveriam ser adicionados com sucesso");
        test.pass("Livros adicionados com sucesso");

        // Verificar se os livros foram adicionados
        test.info("Validando livros alugados pelo usuário");
        Response userResp = userService.getUser(userId, token);
        assertEquals(200, userResp.statusCode(), "Consulta do usuário deve retornar 200");
        List<String> isbnsDoUsuario = userResp.jsonPath().getList("books.isbn");
        assertTrue(isbnsDoUsuario.containsAll(escolhidos), "Livros alugados devem constar no perfil do usuário");
        test.pass("Validação de livros concluída com sucesso");
    }
}