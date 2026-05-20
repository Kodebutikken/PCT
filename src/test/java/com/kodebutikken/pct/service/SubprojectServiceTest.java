package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.SubprojectForm;
import com.kodebutikken.pct.exception.InsufficientPermissionsException;
import com.kodebutikken.pct.exception.ProjectNotFoundException;
import com.kodebutikken.pct.model.Subproject;
import com.kodebutikken.pct.repository.SubprojectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubprojectServiceTest {

    @Mock
    private SubprojectRepository subprojectRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private SubprojectService subprojectService;

    @Test
    void createSubproject_success() {
        SubprojectForm form = new SubprojectForm();
        form.setTitle("Subproject 1");
        form.setDescription("Beskrivelse");
        form.setDeadline(LocalDate.now().plusDays(5));

        subprojectService.createSubproject(1, form);

        ArgumentCaptor<Subproject> captor = ArgumentCaptor.forClass(Subproject.class);
        verify(subprojectRepository).createSubproject(captor.capture());

        Subproject saved = captor.getValue();

        assertEquals("Subproject 1", saved.getTitle());
        assertEquals("Beskrivelse", saved.getDescription());
        assertEquals(1, saved.getProjectId());
    }

    @Test
    void shouldAllowMultipleSubprojectsUnderSameProject() {
        SubprojectForm form1 = new SubprojectForm();
        form1.setTitle("A");
        form1.setDescription("A");
        form1.setDeadline(LocalDate.now().plusDays(1));

        SubprojectForm form2 = new SubprojectForm();
        form2.setTitle("B");
        form2.setDescription("B");
        form2.setDeadline(LocalDate.now().plusDays(2));

        subprojectService.createSubproject(1, form1);
        subprojectService.createSubproject(1, form2);

        verify(subprojectRepository, times(2)).createSubproject(any());
    }

    @Test
    void updateSubproject_shouldUpdateSubproject() {
        SubprojectForm form = new SubprojectForm();
        form.setTitle("Updated Title");
        form.setDescription("Updated Description");
        form.setDeadline(LocalDate.now().plusDays(10));
        int subprojectId = 1;
        int userId = 1;

        Subproject subproject = new Subproject();
        subproject.setId(subprojectId);
        subproject.setTitle("Original Title");
        subproject.setDescription("Original Description");
        subproject.setDeadline(LocalDate.now().plusDays(5));
        subproject.setProjectId(1);

        when(subprojectRepository.existsById(subprojectId)).thenReturn(true);
        when(subprojectRepository.getProjectIdBySubprojectId(subprojectId)).thenReturn(1);
        when(subprojectRepository.getSubprojectById(subprojectId)).thenReturn(subproject);

        subprojectService.updateSubproject(subprojectId, form, userId);

        verify(projectAccessService).requireEditProject(1, userId);
        verify(subprojectRepository).updateSubproject(any());

        assertEquals("Updated Title", subproject.getTitle());
        assertEquals("Updated Description", subproject.getDescription());
        assertEquals(LocalDate.now().plusDays(10), subproject.getDeadline());
    }

    @Test
    void updateSubproject_shouldThrowException_whenUserIsNotOwnerOrEditor() {
        SubprojectForm form = new SubprojectForm();
        form.setTitle("Updated Title");
        form.setDescription("Updated Description");
        form.setDeadline(LocalDate.now().plusDays(10));

        int subprojectId = 1;
        int userId = 2;

        when(subprojectRepository.existsById(subprojectId)).thenReturn(true);
        when(subprojectRepository.getProjectIdBySubprojectId(subprojectId)).thenReturn(1);
        doThrow(new InsufficientPermissionsException("Du har ikke tilladelse til at redigere dette delprojekt"))
                .when(projectAccessService).requireEditProject(1, userId);

        assertThrows(InsufficientPermissionsException.class, () -> subprojectService.updateSubproject(subprojectId, form, userId));

        verify(subprojectRepository, never()).getSubprojectById(anyInt());
        verify(subprojectRepository, never()).updateSubproject(any());
    }

    @Test
    void updateSubproject_shouldThrowException_whenSubprojectDoesNotExist() {
        SubprojectForm form = new SubprojectForm();
        form.setTitle("Updated Title");
        form.setDescription("Updated Description");
        form.setDeadline(LocalDate.now().plusDays(10));

        int subprojectId = 1;
        int userId = 1;

        when(subprojectRepository.existsById(subprojectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> subprojectService.updateSubproject(subprojectId, form, userId));

        verify(projectAccessService, never()).requireEditProject(anyInt(), anyInt());
        verify(subprojectRepository, never()).getSubprojectById(anyInt());
        verify(subprojectRepository, never()).updateSubproject(any());
    }

    @Test
    void deleteSubproject_shouldDeleteProject_whenUserIsOwnerOrEditor() {
        int subProjectId = 1;
        int userId = 1;

        when(subprojectRepository.existsById(subProjectId)).thenReturn(true);
        when(subprojectRepository.getProjectIdBySubprojectId(subProjectId)).thenReturn(1);

        subprojectService.deleteSubproject(subProjectId, userId);

        verify(projectAccessService).requireEditProject(1, userId);
        verify(subprojectRepository).deleteProject(subProjectId);
    }

    @Test
    void deleteSubproject_shouldThrowException_whenUserIsNotOwnerOrEditor() {
        int subProjectId = 1;
        int userId = 1;

        when(subprojectRepository.existsById(subProjectId)).thenReturn(true);
        when(subprojectRepository.getProjectIdBySubprojectId(subProjectId)).thenReturn(1);
        doThrow(new InsufficientPermissionsException("Du har ikke tilladelse til at slette dette delprojekt")).when(projectAccessService).requireEditProject(1, userId);

        assertThrows(InsufficientPermissionsException.class, () -> subprojectService.deleteSubproject(subProjectId, userId));

        verify(subprojectRepository, never()).deleteProject(anyInt());
    }
}
