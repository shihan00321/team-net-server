package com.teamnet.team_net.domain.team.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.teamnet.team_net.domain.team.entity.QTeam.team;

@RequiredArgsConstructor
@Repository
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Team findTeamByKeyword(String keyword, TeamSearchType type) {
        return queryFactory
                .selectFrom(team)
                .where(getSearchCondition(keyword, type))
                .fetchOne();
    }

    private BooleanExpression getSearchCondition(String searchKeyword, TeamSearchType searchType) {
        return switch (searchType) {
            case NAME -> team.name.containsIgnoreCase(searchKeyword);
            case AUTHOR -> team.createdBy.containsIgnoreCase(searchKeyword);
        };
    }
}
