package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Bookstore API")
@Feature("User Management")
@Owner("Stark")
@DisplayName("User API Tests - Testes específicos da API de usuário")
public class UserApiTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @Story("Criação de usuário")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Deve criar usuário com sucesso usando dados válidos")
    void deveCriarUsuarioComDadosValidos() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        String userId = userService.createUser(credentials);

        assertNotNull(userId, "ID do usuário deve ser retornado");
        assertFalse(userId.trim().isEmpty(), "ID do usuário não deve estar vazio");
    }

    @Test
    @Story("Geração de token")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Deve gerar token para usuário criado")
    void deveGerarTokenParaUsuarioCriado() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        String userId = userService.createUser(credentials);
        assertNotNull(userId, "Usuário deve ser criado antes de gerar token");

        String token = userService.generateToken(credentials);

        assertNotNull(token, "Token deve ser gerado");
        assertFalse(token.trim().isEmpty(), "Token não deve estar vazio");
    }

    @Test
    @Story("Autorização de usuário")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Deve autorizar usuário com credenciais válidas")
    void deveAutorizarUsuarioComCredenciaisValidas() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        userService.createUser(credentials);
        userService.generateToken(credentials);

        boolean autorizado = userService.isAuthorized(credentials);

        assertTrue(autorizado, "Usuário com credenciais válidas deve estar autorizado");
    }

    @Test
    @Story("Consulta de dados do usuário")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Deve retornar dados do usuário com token válido")
    void deveRetornarDadosDoUsuarioComTokenValido() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);

        Response response = userService.getUser(userId, token);

        assertEquals(200, response.statusCode(), "Deve retornar status 200");

        String userIdFromResponse = response.jsonPath().getString("userId");
        String usernameFromResponse = response.jsonPath().getString("username");

        assertEquals(userId, userIdFromResponse, "ID deve coincidir");
        assertEquals(credentials.getUserName(), usernameFromResponse, "Username deve coincidir");
    }

    @Test
    @Story("Autorização com falha")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Não deve autorizar usuário com credenciais inválidas")
    void naoDeveAutorizarUsuarioComCredenciaisInvalidas() {
        UserCredentials credenciaisInvalidas = new UserCredentials("usuario_inexistente", "senha_errada");

        boolean autorizado = userService.isAuthorized(credenciaisInvalidas);

        assertFalse(autorizado, "Usuário com credenciais inválidas não deve estar autorizado");
    }
}