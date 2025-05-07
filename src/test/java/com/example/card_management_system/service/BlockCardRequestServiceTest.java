package com.example.card_management_system.service;

import com.example.card_management_system.model.BlockCardRequest;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.BlockCardRequestRepository;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BlockCardRequestServiceTest {

    @Mock
    private BlockCardRequestRepository blockCardRequestRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BlockCardRequestService blockCardRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makeCardBlockRequest_shouldCreateAndSaveRequest() {
        Long userId = 1L;
        Long cardId = 10L;

        User mockUser = new User();
        mockUser.setId(userId);

        Card mockCard = new Card();
        mockCard.setId(cardId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));

        blockCardRequestService.makeCardBlockRequest(userId, cardId);

        ArgumentCaptor<BlockCardRequest> captor = ArgumentCaptor.forClass(BlockCardRequest.class);
        verify(blockCardRequestRepository).save(captor.capture());

        BlockCardRequest savedRequest = captor.getValue();

        assertNotNull(savedRequest);
        assertEquals(mockUser, savedRequest.getUser());
        assertEquals(mockCard, savedRequest.getCard());
        assertEquals(BlockCardRequest.Status.PENDING, savedRequest.getStatus());
        assertNotNull(savedRequest.getRequest_date());
    }
}
