package com.teamnet.team_net.domain.team.repository;

import com.teamnet.team_net.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
