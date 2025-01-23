package com.teamnet.team_net.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.enums.SearchType;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.teamnet.team_net.domain.post.entity.QPost.post;
import static com.teamnet.team_net.domain.team.entity.QTeam.team;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> searchPosts(Long teamId, String searchKeyword, SearchType searchType, Pageable pageable) {
        System.out.println("searchKeyword : " + searchKeyword);
        System.out.println("searchType : " + searchType);

        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.team, team)
                .where(
                        post.team.id.eq(teamId),
                        getSearchCondition(searchKeyword, searchType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.team, team)
                .where(
                        post.team.id.eq(teamId),
                        getSearchCondition(searchKeyword, searchType)
                );

        return PageableExecutionUtils.getPage(posts, pageable, count::fetchOne);
    }

    private BooleanExpression getSearchCondition(String searchKeyword, SearchType searchType) {
        if (!StringUtils.hasText(searchKeyword)) {
            return null;
        }

        return switch (searchType) {
            case TITLE -> post.title.containsIgnoreCase(searchKeyword);
            case CONTENT -> post.content.containsIgnoreCase(searchKeyword);
            case TITLE_CONTENT -> post.title.containsIgnoreCase(searchKeyword)
                    .or(post.content.containsIgnoreCase(searchKeyword));
            case AUTHOR -> post.createdBy.containsIgnoreCase(searchKeyword);
        };
    }
}
