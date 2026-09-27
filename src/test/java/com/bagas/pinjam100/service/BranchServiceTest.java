package com.bagas.pinjam100.service;

import com.bagas.pinjam100.dto.request.BranchRequest;
import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.repository.BranchRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BranchServiceTest")
class BranchServiceTest {

    private static final UUID BRANCH_ID = UUID.randomUUID();
    private static final String NAME = "Cabang Jakarta Selatan";
    private static final String PROVINCE = "DKI Jakarta";
    private static final String CITY = "Jakarta Selatan";
    private static final String POSTAL_CODE = "12190";

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchService branchService;

    @Nested
    @DisplayName("findAllByDeletedDateIsNull")
    class FindAllByDeletedDateIsNullTest {

        @Test
        @DisplayName("should return list of branch responses when active branches exist")
        void shouldReturnListOfBranchResponses() {
            Branch branch1 = createBranch(BRANCH_ID, NAME);
            Branch branch2 = createBranch(UUID.randomUUID(), "Cabang Bandung");

            when(branchRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(branch1, branch2));

            List<BranchResponse> result = branchService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(NAME, result.get(0).getName());
            assertEquals("Cabang Bandung", result.get(1).getName());

            verify(branchRepository).findAllByDeletedDateIsNull();
        }

        @Test
        @DisplayName("should return empty list when no active branches exist")
        void shouldReturnEmptyListWhenNoBranchesExist() {
            when(branchRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of());

            List<BranchResponse> result = branchService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(branchRepository).findAllByDeletedDateIsNull();
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdAndDeletedDateIsNullTest {

        @Test
        @DisplayName("should return branch response when branch found by id")
        void shouldReturnBranchResponseWhenFound() {
            Branch branch = createBranch(BRANCH_ID, NAME);

            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.of(branch));

            BranchResponse result = branchService.findByIdAndDeletedDateIsNull(BRANCH_ID);

            assertNotNull(result);
            assertEquals(BRANCH_ID, result.getId());
            assertEquals(NAME, result.getName());

            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when branch not found by id")
        void shouldThrowExceptionWhenBranchNotFound() {
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> branchService.findByIdAndDeletedDateIsNull(BRANCH_ID)
            );

            assertEquals("Cabang tidak ditemukan", exception.getMessage());
            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should save branch successfully")
        void shouldSaveBranchSuccessfully() {
            BranchRequest request = createBranchRequest(NAME);

            when(branchRepository.save(any(Branch.class)))
                    .thenAnswer(invocation -> {
                        Branch branch = invocation.getArgument(0);
                        branch.setId(BRANCH_ID);
                        return branch;
                    });

            BranchResponse result = branchService.save(request);

            assertNotNull(result);
            assertEquals(BRANCH_ID, result.getId());
            assertEquals(NAME, result.getName());

            verify(branchRepository).save(any(Branch.class));
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update branch successfully when branch exists")
        void shouldUpdateBranchSuccessfully() {
            Branch branch = createBranch(BRANCH_ID, NAME);
            BranchRequest request = createBranchRequest("Cabang Jakarta Pusat");

            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.of(branch));
            when(branchRepository.save(any(Branch.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            BranchResponse result = branchService.update(BRANCH_ID, request);

            assertNotNull(result);
            assertEquals("Cabang Jakarta Pusat", branch.getName());

            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
            verify(branchRepository).save(branch);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when branch to update is not found")
        void shouldThrowExceptionWhenBranchToUpdateNotFound() {
            BranchRequest request = createBranchRequest(NAME);

            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> branchService.update(BRANCH_ID, request)
            );

            assertEquals("Cabang tidak ditemukan", exception.getMessage());
            verify(branchRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should soft delete branch by setting deletedDate")
        void shouldSoftDeleteBranchSuccessfully() {
            Branch branch = createBranch(BRANCH_ID, NAME);

            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.of(branch));
            when(branchRepository.save(any(Branch.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            BranchResponse result = branchService.delete(BRANCH_ID);

            assertNotNull(result);
            assertNotNull(branch.getDeletedDate());

            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
            verify(branchRepository).save(branch);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when branch to delete is not found")
        void shouldThrowExceptionWhenBranchToDeleteNotFound() {
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> branchService.delete(BRANCH_ID)
            );

            assertEquals("Cabang tidak ditemukan", exception.getMessage());
            verify(branchRepository, never()).save(any());
        }
    }

    private Branch createBranch(UUID id, String name) {
        Branch branch = new Branch();
        branch.setId(id);
        branch.setName(name);
        branch.setProvince(PROVINCE);
        branch.setCity(CITY);
        branch.setPostalCode(POSTAL_CODE);
        return branch;
    }

    private BranchRequest createBranchRequest(String name) {
        BranchRequest request = new BranchRequest();
        request.setName(name);
        request.setProvince(PROVINCE);
        request.setCity(CITY);
        request.setPostalCode(POSTAL_CODE);
        return request;
    }
}
