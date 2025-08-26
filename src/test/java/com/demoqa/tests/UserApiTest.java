package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.aventstack.extentreports.ExtentTest;
import com.demoqa.TestSuite;

@DisplayName("User API Tests - Testes específicos da API de usuário")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiTest {

    private UserService userService;
    private ExtentTest test;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @AfterAll
    static void tearDown() {
        TestSuite.flushReport();
    }

    @Test
    @Order(1)
    void deveCriarUsuarioComDadosValidos() {
        test = TestSuite.extent.createTest("Criar usuário com dados válidos", "Valida se o usuário é criado com sucesso");

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);

        assertNotNull(userId, "ID do usuário deve ser retornado");
        assertFalse(userId.trim().isEmpty(), "ID do usuário não deve estar vazio");
        test.pass("Usuário criado com sucesso. ID: " + userId);
    }

    @Test
    @Order(2)
    void deveGerarTokenParaUsuarioCriado() {
        test = TestSuite.extent.createTest("Gerar token para usuário criado", "Valida se um token válido é gerado");

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        assertNotNull(userId, "Usuário deve ser criado antes de gerar token");

        String token = userService.generateToken(credentials);
        assertNotNull(token, "Token deve ser gerado");
        assertFalse(token.trim().isEmpty(), "Token não deve estar vazio");

        test.pass("Token gerado com sucesso: " + token);
    }

    @Test
    @Order(3)
    void deveAutorizarUsuarioComCredenciaisValidas() {
        test = TestSuite.extent.createTest("Autorizar usuário com credenciais válidas", "Valida que usuário válido está autorizado");

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        userService.createUser(credentials);
        userService.generateToken(credentials);

        boolean autorizado = userService.isAuthorized(credentials);
        assertTrue(autorizado, "Usuário com credenciais válidas deve estar autorizado");
        test.pass("Usuário autorizado com sucesso");
    }

    @Test
    @Order(4)
    void deveRetornarDadosDoUsuarioComTokenValido() {
        test = TestSuite.extent.createTest("Retornar dados do usuário com token válido", "Valida a consulta dos dados do usuário");

        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());
        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);

        Response response = userService.getUser(userId, token);
        assertEquals(200, response.statusCode(), "Deve retornar status 200");

        String userIdFromResponse = response.jsonPath().getString("userId");
        String usernameFromResponse = response.jsonPath().getString("username");

        assertEquals(userId, userIdFromResponse, "ID deve coincidir");
        assertEquals(credentials.getUserName(), usernameFromResponse, "Username deve coincidir");

        test.pass("Dados do usuário retornados corretamente: " + usernameFromResponse);
    }

    @Test
    @Order(5)
    void naoDeveAutorizarUsuarioComCredenciaisInvalidas() {
        test = TestSuite.extent.createTest("Não autorizar usuário com credenciais inválidas", "Valida que credenciais inválidas não autorizam");

        UserCredentials credenciaisInvalidas = new UserCredentials("usuario_inexistente", "senha_errada");
        boolean autorizado = userService.isAuthorized(credenciaisInvalidas);

        assertFalse(autorizado, "Usuário com credenciais inválidas não deve estar autorizado");
        test.pass("Falha de autorização confirmada para usuário inválido");
    }
}