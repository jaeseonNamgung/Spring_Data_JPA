package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() throws Exception{
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        // then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void basicCRUD() throws Exception{
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all).hasSize(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);


    }

    @Test
    void findByUsernameAndAgeGreaterThan() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result).hasSize(1);

    }
    @Test
    void testNamedQuery() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        // when
        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        // then
        assertThat(findMember).isEqualTo(m1);
    }
    @Test
    void testdQuery() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        // when
        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        // then
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    void findUsernameList() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        // when
        List<String> result = memberRepository.findUsernameList();
        String str = result.get(0);
        // then
        assertThat(str).isEqualTo(m1.getUsername());
    }
    @Test
    void findMemberDto() throws Exception{
        // given
        Team t = new Team("teamA");
        teamRepository.save(t);
        Member m1 = new Member("AAA", 10, t);
        memberRepository.save(m1);


        // when
        List<MemberDto> result = memberRepository.findMemberDto();
        // then
        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
    }
    @Test
    void findByNames() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }
    @Test
    void paging() throws Exception{
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));

        int age = 10;
        PageRequest pageRequest =
                PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // Entity -> Dto 변환
        Page<MemberDto> memberDto =
                page.map(member -> new MemberDto(member.getId(), member.getUsername(), "teamA"));
        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
       assertThat(content.size()).isEqualTo(3);
       assertThat(page.getTotalElements()).isEqualTo(7);
       assertThat(page.getNumber()).isEqualTo(0);
       assertThat(page.getTotalPages()).isEqualTo(3);
       assertThat(page.isFirst()).isTrue();
       assertThat(page.hasNext()).isTrue();
    }
    @Test
    void slice() throws Exception{
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));

        int age = 10;
        PageRequest pageRequest =
                PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void bulkUpdate() throws Exception{
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);
        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void entityGraph() throws Exception{
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        em.flush();
        em.clear();
        // when
        List<Member> all = memberRepository.findEntityGraphByUsername("member1");
        // then
        for (Member member : all) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    void queryHint() throws Exception{
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();
        // when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        em.flush();
        // then
    }
    @Test
    void lock() throws Exception{
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();
        // when
        List<Member> member = memberRepository.findLockByUsername("member1");

    }

    @Test
    void callCustom() throws Exception{
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }
    
    @Test
    void projections() throws Exception{
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        // when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }


        // then
    }
    
    @Test
    void nativeQuery() throws Exception{
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        // when
        Page<MemberProjection> page = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = page.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getusername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
        // then
    }

}