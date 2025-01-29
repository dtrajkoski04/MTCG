package sampleapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sampleapp.exception.InsufficientFundsException;
import sampleapp.exception.ResourceNotFoundException;
import sampleapp.model.Card;
import sampleapp.model.Package;
import sampleapp.model.User;
import sampleapp.persistence.repository.PackageRepository;
import sampleapp.persistence.repository.UserRepository;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackageServiceTest {

    @Mock
    private PackageRepository packageRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    private PackageService packageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject mocks
        packageService = new PackageService(packageRepositoryMock, userRepositoryMock);
    }

    // ------------------------------------------------------------
    // Tests for save(Package pkg, List<String> cardIds)
    // ------------------------------------------------------------
    @Test
    void testSaveWithNullCardIds() throws SQLException {
        // Arrange
        Package pkg = new Package(1);
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> packageService.save(pkg, null),
                "Expected IllegalArgumentException when cardIds is null"
        );
        verifyNoInteractions(packageRepositoryMock);
    }

    @Test
    void testSaveWithInvalidCardIdsSize() throws SQLException {
        // Arrange
        Package pkg = new Package(1);
        // Only 3 card IDs
        List<String> cardIds = Arrays.asList("c1", "c2", "c3");

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> packageService.save(pkg, cardIds),
                "Expected IllegalArgumentException when cardIds.size != 5"
        );
        verifyNoInteractions(packageRepositoryMock);
    }

    @Test
    void testSaveWithValidCardIds() throws SQLException {
        // Arrange
        Package pkg = new Package(1);
        List<String> cardIds = Arrays.asList("c1", "c2", "c3", "c4", "c5");
        doNothing().when(packageRepositoryMock).save(pkg, cardIds);

        // Act
        packageService.save(pkg, cardIds);

        // Assert
        verify(packageRepositoryMock, times(1)).save(pkg, cardIds);
    }

    // ------------------------------------------------------------
    // Tests for acquirePackages(String username)
    // ------------------------------------------------------------
    @Test
    void testAcquirePackagesUserNotFound() throws SQLException {
        // Arrange
        String username = "nonexistentUser";
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> packageService.acquirePackages(username)
        );
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verifyNoInteractions(packageRepositoryMock);
    }

    @Test
    void testAcquirePackagesInsufficientFunds() throws SQLException, ResourceNotFoundException {
        // Arrange
        String username = "poorUser";
        User user = new User();
        user.setUsername(username);
        user.setCoins(4); // Less than 5 coins
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> packageService.acquirePackages(username)
        );

        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verifyNoInteractions(packageRepositoryMock);
    }

    @Test
    void testAcquirePackagesNoPackageAvailable() throws SQLException {
        // Arrange
        String username = "userWithCoins";
        User user = new User();
        user.setUsername(username);
        user.setCoins(10);
        when(userRepositoryMock.getUserByUsername(username)).thenReturn(Optional.of(user));

        // No packages returned by findAll()
        when(packageRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> packageService.acquirePackages(username)
        );

        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verify(packageRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(packageRepositoryMock);
    }

    @Test
    void testAcquirePackagesSuccess() throws SQLException, InsufficientFundsException, ResourceNotFoundException {
        // Arrange
        String username = "richUser";
        User user = new User();
        user.setUsername(username);
        user.setCoins(10);

        Package pkg = new Package(1);
        pkg.setId(1);

        Card card1 = new Card();
        card1.setId("c1");
        Card card2 = new Card();
        card2.setId("c2");

        List<Card> cardsInPackage = Arrays.asList(card1, card2);

        when(userRepositoryMock.getUserByUsername(username)).thenReturn(Optional.of(user));
        when(packageRepositoryMock.findAll()).thenReturn(Collections.singletonList(pkg));
        when(packageRepositoryMock.findCardsByPackageId(pkg.getId())).thenReturn(cardsInPackage);

        // Act
        packageService.acquirePackages(username);

        // Assert
        // 1) User coins updated to 10 - 5 = 5
        assertEquals(5, user.getCoins());
        verify(userRepositoryMock, times(1)).updateUser(user);

        // 2) Each card added to user
        verify(userRepositoryMock, times(1)).addCardToUser(username, "c1");
        verify(userRepositoryMock, times(1)).addCardToUser(username, "c2");

        // 3) Package deleted
        verify(packageRepositoryMock, times(1)).delete(pkg.getId());
    }
}
