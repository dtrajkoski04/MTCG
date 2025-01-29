package sampleapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sampleapp.DTO.UserDTO;
import sampleapp.exception.DataConflictException;
import sampleapp.model.User;
import sampleapp.persistence.repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the mocked repository into the UserService
        userService = new UserService(userRepositoryMock);
    }

    // ------------------------------------------------------------
    // TEST: register(String username, String password)
    // ------------------------------------------------------------
    @Test
    void testRegisterSuccessful() throws SQLException, DataConflictException {
        // Arrange
        String username = "testUser";
        String password = "secret";

        // We expect no exception, so we don't configure userRepositoryMock to throw one.
        doNothing().when(userRepositoryMock).registerUser(username, password);

        // Act
        String result = userService.register(username, password);

        // Assert
        assertEquals("User registered successfully", result);
        verify(userRepositoryMock, times(1)).registerUser(username, password);
    }

    @Test
    void testRegisterDataConflict() throws SQLException, DataConflictException {
        // Arrange
        String username = "duplicateUser";
        String password = "secret";
        // Simulate conflict thrown by the repository
        doThrow(new DataConflictException("User already exists"))
                .when(userRepositoryMock).registerUser(username, password);

        // Act & Assert
        DataConflictException thrown = assertThrows(
                DataConflictException.class,
                () -> userService.register(username, password)
        );
        assertEquals("User already exists", thrown.getMessage());
        verify(userRepositoryMock, times(1)).registerUser(username, password);
    }

    // ------------------------------------------------------------
    // TEST: getUser(String username)
    // ------------------------------------------------------------
    @Test
    void testGetUserFound() throws SQLException {
        // Arrange
        String username = "existingUser";
        User user = new User();
        user.setName("John Doe");
        // For demonstration, set more fields as needed, e.g. user.setBio("...")

        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.of(user));

        // Act
        Optional<UserDTO> result = userService.getUser(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("existingUser", result.get().getUsername());
        assertEquals("John Doe", result.get().getName());
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
    }

    @Test
    void testGetUserNotFound() throws SQLException {
        // Arrange
        String username = "nonExistingUser";
        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> result = userService.getUser(username);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
    }

    // ------------------------------------------------------------
    // TEST: updateUser(String username, UserDTO newUser)
    // ------------------------------------------------------------
    @Test
    void testUpdateUserSuccess() throws SQLException {
        // Arrange
        String username = "testUser";
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setBio("Old Bio");
        oldUser.setImage("Old Image");

        UserDTO newUserDTO = new UserDTO(username, "New Name", "New Bio", "New Image");

        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.of(oldUser));
        doNothing().when(userRepositoryMock).updateUser(any(User.class));

        // Act
        boolean result = userService.updateUser(username, newUserDTO);

        // Assert
        assertTrue(result);
        // Verify repository calls
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verify(userRepositoryMock, times(1)).updateUser(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() throws SQLException {
        // Arrange
        String username = "unknownUser";
        UserDTO newUserDTO = new UserDTO(username, "New Name", "New Bio", "New Image");

        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.empty());

        // Act
        boolean result = userService.updateUser(username, newUserDTO);

        // Assert
        assertFalse(result);
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        // updateUser should not be called because user doesn't exist
        verify(userRepositoryMock, never()).updateUser(any(User.class));
    }

    @Test
    void testUpdateUserSqlException() throws SQLException {
        // Arrange
        String username = "testUser";
        User oldUser = new User();
        oldUser.setName("Old Name");
        oldUser.setBio("Old Bio");
        oldUser.setImage("Old Image");

        UserDTO newUserDTO = new UserDTO(username, "New Name", "New Bio", "New Image");

        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.of(oldUser));
        // Simulate a SQL exception when updating
        doThrow(new SQLException("Something went wrong"))
                .when(userRepositoryMock).updateUser(any(User.class));

        // Act & Assert
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userService.updateUser(username, newUserDTO)
        );
        assertTrue(thrown.getMessage().contains("Something went wrong"));

        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verify(userRepositoryMock, times(1)).updateUser(any(User.class));
    }

    // ------------------------------------------------------------
    // TEST: checkAuth(String username, String token) [static]
    // ------------------------------------------------------------
    @Test
    void testCheckAuthValidToken() {
        assertTrue(UserService.checkAuth("john", "Bearer john-mtcgToken"));
    }

    @Test
    void testCheckAuthInvalidToken() {
        // missing "Bearer " prefix
        assertFalse(UserService.checkAuth("john", "john-mtcgToken"));
        // malformed token
        assertFalse(UserService.checkAuth("john", "Bearer mismatched-mtcgToken"));
        // null token
        assertFalse(UserService.checkAuth("john", null));
    }
}
