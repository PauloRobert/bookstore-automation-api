package com.demoqa.tests;

import com.demoqa.models.UserCredentials;
import com.demoqa.services.UserService;
import com.demoqa.utils.DataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User API Tests - Testes específicos da API de usuário")
public class UserApiTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso usando dados válidos")
    void deveCriarUsuarioComDadosValidos() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        String userId = userService.createUser(credentials);

        assertNotNull(userId, "ID do usuário deve ser retornado");
        assertFalse(userId.trim().isEmpty(), "ID do usuário não deve estar vazio");
    }

    @Test
    @DisplayName("Deve gerar token para usuário criado")
    void deveGerarTokenParaUsuarioCriado() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        // Primeiro criar o usuário
        String userId = userService.createUser(credentials);
        assertNotNull(userId, "Usuário deve ser criado antes de gerar token");

        // Depois gerar o token
        String token = userService.generateToken(credentials);

        assertNotNull(token, "Token deve ser gerado");
        assertFalse(token.trim().isEmpty(), "Token não deve estar vazio");
    }

    @Test
    @DisplayName("Deve autorizar usuário com credenciais válidas")
    void deveAutorizarUsuarioComCredenciaisValidas() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        // Setup: criar usuário e gerar token
        userService.createUser(credentials);
        userService.generateToken(credentials);

        // Test: verificar autorização
        boolean autorizado = userService.isAuthorized(credentials);

        assertTrue(autorizado, "Usuário com credenciais válidas deve estar autorizado");
    }

    @Test
    @DisplayName("Deve retornar dados do usuário com token válido")
    void deveRetornarDadosDoUsuarioComTokenValido() {
        UserCredentials credentials = new UserCredentials(DataFactory.username(), DataFactory.password());

        // Setup
        String userId = userService.createUser(credentials);
        String token = userService.generateToken(credentials);

        // Test
        Response response = userService.getUser(userId, token);

        // Validações
        assertEquals(200, response.statusCode(), "Deve retornar status 200");

        String userIdFromResponse = response.jsonPath().getString("userId");
        String usernameFromResponse = response.jsonPath().getString("username");

        assertEquals(userId, userIdFromResponse, "ID deve coincidir");
        assertEquals(credentials.getUserName(), usernameFromResponse, "Username deve coincidir");
    }

    @Test
    @DisplayName("Não deve autorizar usuário com credenciais inválidas")
    void naoDeveAutorizarUsuarioComCredenciaisInvalidas() {
        UserCredentials credenciaisInvalidas = new UserCredentials("usuario_inexistente", "senha_errada");

        boolean autorizado = userService.isAuthorized(credenciaisInvalidas);

        assertFalse(autorizado, "Usuário com credenciais inválidas não deve estar autorizado");
    }
}