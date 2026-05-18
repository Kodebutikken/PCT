package com.kodebutikken.pct.service;

import com.kodebutikken.pct.dto.ProjectForm;
import com.kodebutikken.pct.dto.ProjectMemberForm;
import com.kodebutikken.pct.exception.UnauthorizedException;
import com.kodebutikken.pct.model.ProjectAccessLevel;
import com.kodebutikken.pct.model.ProjectMember;
import com.kodebutikken.pct.model.Project;
import com.kodebutikken.pct.model.User;
import com.kodebutikken.pct.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectAccessService projectAccessService,
                          UserService userService) {
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
        this.userService = userService;
    }

    @Transactional
    public void createProject(ProjectForm projectForm, int userId) {
        if(projectForm == null) {
            throw new IllegalArgumentException("Project form cannot be null");
        }
        String validationError = isValidProjectForm(projectForm);
        if(validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        if (!projectAccessService.canCreateProject(userId)) {
            throw new UnauthorizedException("Du har ikke adgang til at oprette projekter");
        }

        Project project = new Project();
        project.setTitle(projectForm.getTitle().trim());
        project.setDescription(projectForm.getDescription() != null ? projectForm.getDescription().trim() : null);
        project.setDeadline(projectForm.getDeadline());
        project.setCreatedBy(userId);

        int projectId = projectRepository.save(project, userId);
        projectRepository.replaceProjectMembers(projectId, toAssignedMembers(projectForm.getMembers(), userId));
    }

    public List<Project> getProjectsAccessibleByUserId(int userId) {
        return projectRepository.getProjectsAccessibleByUserId(userId);
    }

    public Project getProjectById(int id) {
        return projectRepository.getProjectById(id);
    }

    public List<ProjectMember> getProjectMembers(int projectId) {
        return projectRepository.getProjectMembers(projectId);
    }

    @Transactional
    public void updateProject(int projectId, ProjectForm projectForm, int userId) {
        if (!projectAccessService.canManageProject(projectId, userId)) {
            throw new UnauthorizedException("Du har ikke adgang til at redigere dette projekt");
        }

        String validationError = isValidProjectForm(projectForm);
        if(validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        Project existingProject = projectRepository.getProjectById(projectId);
        existingProject.setTitle(projectForm.getTitle().trim());
        existingProject.setDescription(projectForm.getDescription() != null ? projectForm.getDescription().trim() : null);
        existingProject.setDeadline(projectForm.getDeadline());

        projectRepository.update(existingProject);
        projectRepository.replaceProjectMembers(projectId, toAssignedMembers(projectForm.getMembers(), existingProject.getCreatedBy()));
    }

    @Transactional
    public void deleteProject(int id, int userId) {
        if(!projectAccessService.canDeleteProject(id, userId)) {
            throw new UnauthorizedException("Du har ikke adgang til at slette dette projekt");
        }
        projectRepository.delete(id, userId);
    }


    public ProjectForm buildProjectForm(Integer projectId, int currentUserId) {
        ProjectForm form = new ProjectForm();

        if (projectId != null) {
            Project project = getProjectById(projectId);
            form.setTitle(project.getTitle());
            form.setDescription(project.getDescription());
            form.setDeadline(project.getDeadline());
        }

        List<ProjectMember> existingMembers = projectId != null ? getProjectMembers(projectId) : List.of();
        List<User> users = userService.getAllUsers();

        Map<Integer, ProjectMember> memberByUserId = new HashMap<>();
        for (ProjectMember member : existingMembers) {
            memberByUserId.put(member.getUserId(), member);
        }

        List<ProjectMemberForm> memberForms = new ArrayList<>();
        for (User user : users) {
            if (user.getId() == currentUserId) continue;

            ProjectMember existing = memberByUserId.get(user.getId());
            ProjectMemberForm memberForm = new ProjectMemberForm();
            memberForm.setUserId(user.getId());
            memberForm.setUserName(user.getName());
            memberForm.setUserEmail(user.getEmail());
            memberForm.setAssigned(existing != null);
            if (existing != null) {
                memberForm.setAccessLevel(existing.getAccessLevel());
            }
            memberForms.add(memberForm);
        }

        form.setMembers(memberForms);
        return form;
    }

    public void repopulateProjectMembers(ProjectForm projectForm, int currentUserId) {
        List<User> users = userService.getAllUsers();

        Map<Integer, User> userById = new HashMap<>();
        for (User user : users) {
            userById.put(user.getId(), user);
        }

        for (ProjectMemberForm memberForm : projectForm.getMembers()) {
            User user = userById.get(memberForm.getUserId());
            if (user == null || user.getId() == currentUserId) continue;
            memberForm.setUserName(user.getName());
            memberForm.setUserEmail(user.getEmail());
        }
    }

    private String isValidProjectForm(ProjectForm projectForm) {
        String title = projectForm.getTitle();
        if (title == null || title.trim().isEmpty()) {
            return "Projektet skal have en titel";
        }
        if(projectForm.getDeadline() == null) {
            return "Deadline er påkrævet";
        }
        if(projectForm.getDeadline().isBefore(java.time.LocalDate.now())) {
            return "Deadline skal være en fremtidig dato";
        }
        return null;
    }

    private List<ProjectMember> toAssignedMembers(List<ProjectMemberForm> members, int ownerUserId) {
        List<ProjectMember> assignedMembers = new ArrayList<>();
        if (members == null) {
            return assignedMembers;
        }

        for (ProjectMemberForm memberForm : members) {
            if (!memberForm.isAssigned() || memberForm.getUserId() == ownerUserId) {
                continue;
            }

            ProjectAccessLevel accessLevel = memberForm.getAccessLevel() != null
                    ? memberForm.getAccessLevel()
                    : ProjectAccessLevel.VIEWER;

            assignedMembers.add(new ProjectMember(
                    memberForm.getUserId(),
                    memberForm.getUserName(),
                    memberForm.getUserEmail(),
                    accessLevel
            ));
        }

        return assignedMembers;
    }
}
