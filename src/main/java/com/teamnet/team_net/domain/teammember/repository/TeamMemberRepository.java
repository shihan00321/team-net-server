package com.teamnet.team_net.domain.teammember.repository;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("select distinct t from TeamMember tm join tm.team t where tm.member.id = :memberId and t.status = :status")
    List<Team> findMyTeam(@Param("memberId") Long memberId, @Param("status") TeamActiveStatus status);

    @Query("select tm from TeamMember tm join tm.member m where tm.member.id = :memberId and tm.role = :role")
    Optional<TeamMember> findMemberWithRole(@Param("memberId") Long memberId, @Param("role") TeamRole role);

    Optional<TeamMember> findByMemberIdAndTeamId(@Param("memberId") Long memberId, @Param("teamId") Long teamId);
}
