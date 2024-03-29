package io.ceze.regulus.generator.service;


import io.ceze.regulus.account.service.AccountService;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.commons.data.LocationRepository;
import io.ceze.regulus.control.service.CollectionService;
import io.ceze.regulus.generator.DuplicateRequestException;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.model.Label;
import io.ceze.regulus.generator.repository.DisposalRepository;
import io.ceze.regulus.generator.web.DisposalRequest;
import io.ceze.regulus.generator.web.Priority;
import io.ceze.regulus.security.User;
import jakarta.security.enterprise.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DisposalServiceImplTest  {

    @InjectMocks
    DisposalServiceImpl disposalService = new DisposalServiceImpl();

    @Mock
    private SecurityContext securityContext;
    @Mock
    private AccountService accountService;
    @Mock
    private CollectionService collectionService;
    @Mock
    private DisposalRepository disposalRepository;
    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void userWithNullLocationCannotInitiateRequest() {
        when(securityContext.getCallerPrincipal()).thenReturn(() -> "bob");
        User bob = new User("bob", "pass", null);
        when(accountService.loadByUsername("bob"))
            .thenReturn(bob);
        disposalService.initializeUserInfo();
        DisposalRequest request = new DisposalRequest(Label.MSW, 10, Priority.MEDIUM);

        assertNull(bob.getLocation());
        assertThrows(LocationNotFoundException.class, () -> {
            disposalService.newDisposalRequest(request);
        });
    }

    @Test
    void shouldCreateRequestForNewLocation() {

        Location location = new Location();
        location.setId(1L);
        User bob = new User("bob", "pass", null);
        bob.setLocation(location);
        Disposal disposal = new Disposal();
        disposal.setId(123L);
        disposal.setStatus(DisposalStatus.PENDING);
        when(disposalRepository.save(any(Disposal.class))).thenAnswer(invocation -> {
            Disposal savedDisposal = invocation.getArgument(0);
            savedDisposal.setId(disposal.getId());
            return savedDisposal;
        });

        when(securityContext.getCallerPrincipal()).thenReturn(() -> "bob");
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        disposalService.initializeUserInfo();
        when(disposalRepository.findByLocationId(location.getId())).thenReturn(Optional.empty());
        DisposalRequest request = new DisposalRequest(Label.MSW, 10, Priority.MEDIUM);
        DisposalResponse response = disposalService.newDisposalRequest(request);


        verify(disposalRepository, times(1)).save(any(Disposal.class));
        verify(collectionService, times(1)).handleDisposal(any(Disposal.class));

        assertNotNull(response);
    }

    @Test
    void userCannotInitiateMultipleRequests() {
        Location location = new Location();
        location.setId(1L);
        User bob = new User("bob", "pass", null);
        bob.setLocation(location);
        Disposal disposal = new Disposal();
        disposal.setId(123L);
        disposal.setStatus(DisposalStatus.PENDING);

        when(disposalRepository.save(any(Disposal.class))).thenAnswer(invocation -> {
            Disposal savedDisposal = invocation.getArgument(0);
            savedDisposal.setId(disposal.getId());
            return savedDisposal;
        });

        when(securityContext.getCallerPrincipal()).thenReturn(() -> "bob");
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        disposalService.initializeUserInfo();
        DisposalRequest request = new DisposalRequest(Label.MSW, 10, Priority.MEDIUM);

        disposalService.newDisposalRequest(request);
        when(disposalRepository.findByLocationId(any(Long.class))).thenReturn(Optional.of(disposal));

        assertThrows(DuplicateRequestException.class, () -> {
            disposalService.newDisposalRequest(request);
        });

    }


}
