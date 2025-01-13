package com.teamnet.team_net.domain.teammember.repository;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("select distinct t from TeamMember tm join tm.team t where tm.member.id = :memberId and t.status = :status")
    List<Team> findMyTeam(@Param("memberId") Long memberId, @Param("status") TeamActiveStatus status);
}
