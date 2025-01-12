package com.teamnet.team_net.domain.teammember.repository;

import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
