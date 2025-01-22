package com.teamnet.team_net.domain.teammember.repository;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("select distinct t from TeamMember tm join tm.team t where tm.member.id = :memberId and t.status = :status")
    Page<Team> findTeamsByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") TeamActiveStatus status, Pageable pageable);

    Optional<TeamMember> findByMemberIdAndTeamId(@Param("memberId") Long memberId, @Param("teamId") Long teamId);
    Boolean existsByMemberIdAndTeamId(@Param("memberId") Long memberId, @Param("teamId") Long teamId);

    Optional<TeamMember> findByMemberIdAndTeamIdAndRole(@Param("memberId") Long memberId, @Param("teamId") Long teamId, @Param("role") TeamRole role);
}
