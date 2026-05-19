package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.SubprojectForm;
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
}
