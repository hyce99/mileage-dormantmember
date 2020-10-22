package mileage;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DormantMemberRepository extends PagingAndSortingRepository<DormantMember, Long>{

    Optional<DormantMember> findByMemberId(Long memberId);

}