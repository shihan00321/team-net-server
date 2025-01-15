package com.teamnet.team_net.domain.team.repository;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("update Team t set t.status = :teamActiveStatus where t.id = :teamId")
    void updateTeamStatus(Long teamId, @Param("status") TeamActiveStatus status);
}
