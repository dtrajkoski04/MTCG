package sampleapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sampleapp.exception.ResourceNotFoundException;
import sampleapp.model.Card;
import sampleapp.model.User;
import sampleapp.persistence.repository.CardRepository;
import sampleapp.persistence.repository.UserRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardService = new CardService(cardRepositoryMock, userRepositoryMock);
    }

    // ------------------------------------------------------------
    // getAllCardsByUsername Tests
    // ------------------------------------------------------------
    @Test
    void testGetAllCardsByUsername_UserNotFound() throws SQLException {
        // Arrange
        String username = "nonExistingUser";
        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> cardService.getAllCardsByUsername(username),
                "Expected ResourceNotFoundException if user does not exist"
        );

        // Verify user lookup
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        // No calls to cardRepository because we never got a valid user
        verifyNoInteractions(cardRepositoryMock);
    }

    @Test
    void testGetAllCardsByUsername_NoCardsFound() throws SQLException, ResourceNotFoundException {
        // Arrange
        String username = "emptyUser";
        User user = new User();
        user.setUsername(username);

        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.of(user));

        // Mock an empty list of cards
        when(cardRepositoryMock.findAllByUsername(username))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> cardService.getAllCardsByUsername(username),
                "Expected ResourceNotFoundException if user has no cards"
        );

        // Verify calls
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verify(cardRepositoryMock, times(1)).findAllByUsername(username);
    }

    @Test
    void testGetAllCardsByUsername_Success() throws SQLException, ResourceNotFoundException {
        // Arrange
        String username = "existingUser";
        User user = new User();
        user.setUsername(username);

        Card card1 = new Card();
        card1.setId("c1");
        Card card2 = new Card();
        card2.setId("c2");

        when(userRepositoryMock.getUserByUsername(username))
                .thenReturn(Optional.of(user));
        when(cardRepositoryMock.findAllByUsername(username))
                .thenReturn(List.of(card1, card2));

        // Act
        List<Card> cards = cardService.getAllCardsByUsername(username);

        // Assert
        assertNotNull(cards);
        assertEquals(2, cards.size());
        assertTrue(cards.contains(card1));
        assertTrue(cards.contains(card2));

        // Verify calls
        verify(userRepositoryMock, times(1)).getUserByUsername(username);
        verify(cardRepositoryMock, times(1)).findAllByUsername(username);
    }

    // ------------------------------------------------------------
    // addCard Tests
    // ------------------------------------------------------------
    @Test
    void testAddCard() throws SQLException {
        // Arrange
        Card card = new Card();
        card.setId("card123");

        doNothing().when(cardRepositoryMock).save(card);

        // Act
        cardService.addCard(card);

        // Assert
        verify(cardRepositoryMock, times(1)).save(card);
    }
}
