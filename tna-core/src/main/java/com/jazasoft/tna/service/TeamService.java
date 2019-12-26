package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Department;
import com.jazasoft.tna.entity.Team;
import com.jazasoft.tna.repository.DepartmentRepository;
import com.jazasoft.tna.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class TeamService {
    private final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private TeamRepository teamRepository;
    private DepartmentRepository departmentRepository;

    public TeamService(TeamRepository teamRepository, DepartmentRepository departmentRepository) {
        this.teamRepository = teamRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Team> findAll() {
        List<Team> teamList = teamRepository.findAll();
        teamList.forEach(team -> team.setDepartmentId(team.getDepartment() != null ? team.getDepartment().getId() : null));
        return teamList;
    }

    public Page<Team> findAll(Pageable pageable) {
        Page<Team> teamList = teamRepository.findAll(pageable);
        teamList.forEach(team -> team.setDepartmentId(team.getDepartment() != null ? team.getDepartment().getId() : null));
        return teamList;
    }

    public Page<Team> findAll(Specification<Team> spec, Pageable pageable) {
        Page<Team> teamList = teamRepository.findAll(spec,pageable);
        teamList.forEach(team -> team.setDepartmentId(team.getDepartment() != null ? team.getDepartment().getId() : null));
        return teamList;
    }

    public Optional<Team> findOne(Long id) {
      Optional<Team> mTeam = teamRepository.findById(id);
        mTeam.ifPresent(team -> team.setDepartmentId(team.getDepartment().getId()));
        return mTeam;
    }

    @Transactional(value = "tenantTransactionManager")
    public Team save(Team team) {
        Department department = departmentRepository.getOne(team.getDepartmentId());
        team.setDepartment(department);
        return teamRepository.save(team);
    }

    @Transactional(value = "tenantTransactionManager")
    public Team update(Team team) {
        Team mTeam = teamRepository.findById(team.getId()).orElseThrow();
        mTeam.setName(team.getName());
        mTeam.setDesc(team.getDesc());
        if(!mTeam.getDepartment().getId().equals(team.getDepartmentId())){
            Department department=departmentRepository.getOne(team.getDepartmentId());
            mTeam.setDepartment(department);
        }
        return mTeam;
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id) {
        logger.trace("delete : id ={}", id);
        teamRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        logger.trace("exists : id ={}", id);
        return teamRepository.existsById(id);
    }
}
