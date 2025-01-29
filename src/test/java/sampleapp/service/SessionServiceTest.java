package sampleapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sampleapp.exception.AuthenticationException;
import sampleapp.persistence.repository.UserRepository;

import java.sql.SQLException;

class SessionServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Inject mock UserRepository into SessionService
        sessionService = new SessionService(userRepositoryMock);
    }

    @Test
    void testLoginSuccess() throws SQLException, AuthenticationException {
        // Arrange
        String username = "john";
        String password = "secret";
        // Simulate the userRepository returning a token or session string
        String expectedToken = "Bearer john-mtcgToken";
        when(userRepositoryMock.loginUser(username, password)).thenReturn(expectedToken);

        // Act
        String actualToken = sessionService.login(username, password);

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(userRepositoryMock, times(1)).loginUser(username, password);
    }

    @Test
    void testLoginAuthenticationException() throws SQLException, AuthenticationException {
        // Arrange
        String username = "john";
        String password = "wrongPassword";
        // Simulate an authentication failure
        doThrow(new AuthenticationException("Invalid credentials"))
                .when(userRepositoryMock).loginUser(username, password);

        // Act & Assert
        AuthenticationException thrown = assertThrows(
                AuthenticationException.class,
                () -> sessionService.login(username, password)
        );
        assertEquals("Invalid credentials", thrown.getMessage());
        verify(userRepositoryMock, times(1)).loginUser(username, password);
    }

    @Test
    void testLoginSQLException() throws SQLException, AuthenticationException {
        // Arrange
        String username = "john";
        String password = "secret";
        // Simulate a database failure
        doThrow(new SQLException("Database error"))
                .when(userRepositoryMock).loginUser(username, password);

        // Act & Assert
        SQLException thrown = assertThrows(
                SQLException.class,
                () -> sessionService.login(username, password)
        );
        assertTrue(thrown.getMessage().contains("Database error"));
        verify(userRepositoryMock, times(1)).loginUser(username, password);
    }
}
