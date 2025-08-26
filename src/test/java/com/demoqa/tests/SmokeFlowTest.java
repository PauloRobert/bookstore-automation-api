package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.BookService;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fluxo completo: criar usuário, autorizar, alugar e listar livros")
public class SmokeFlowTest {

    private static ExtentReports extent;
    private ExtentTest test;

    private UserService userService;
    private BookService bookService;
    private UserCredentials userCredentials;

    @BeforeAll
    static void setupReport() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("target/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @AfterAll
    static void tearDownReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    @BeforeEach
    void setUp(TestInfo info) {
        userService = new UserService();
        bookService = new BookService();
        userCredentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        test = extent.createTest(info.getDisplayName());
        test.info("Inicializando serviços e credenciais do usuário");
    }

    @Test
    @DisplayName("Deve criar usuário, gerar token, autorizar, alugar e listar livros")
    void deveCriarAutorizarAlugarEListar() {
        // Criar usuário
        test.info("Criando usuário");
        String userId = userService.createUser(userCredentials);
        assertNotNull(userId, "Usuário deve ser criado com sucesso");
        test.pass("Usuário criado: " + userId);

        // Gerar token
        test.info("Gerando token para o usuário");
        String token = userService.generateToken(userCredentials);
        assertNotNull(token, "Token deve ser gerado");
        test.pass("Token gerado: " + token.substring(0, Math.min(20, token.length())) + "...");

        // Verificar autorização
        test.info("Verificando se o usuário está autorizado");
        assertTrue(userService.isAuthorized(userCredentials), "Usuário deveria estar autorizado");
        test.pass("Usuário autorizado");

        // Listar livros disponíveis
        test.info("Listando livros disponíveis");
        Response listResp = bookService.listBooks();
        assertEquals(200, listResp.statusCode(), "API de livros deveria retornar 200");

        List<String> isbns = listResp.jsonPath().getList("books.isbn");
        assertTrue(isbns.size() >= 2, "Precisa ter pelo menos 2 livros disponíveis");

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
        test.pass("Validação concluída: livros alugados presentes no usuário");
    }
}