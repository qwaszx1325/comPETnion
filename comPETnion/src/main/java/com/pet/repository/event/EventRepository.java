package com.pet.repository.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.event.Event;
import com.pet.model.event.Participant;
import com.pet.model.member.Members;
import java.util.Date;




public interface EventRepository extends JpaRepository<Event, Integer> {
	
	//關鍵字模糊查詢
	@Query("from Event e where e.eventCancelStatus = 0 " +
		       "AND e.approvalStatus = 2 " +
		       "AND (e.eventSubject like %:keyword% " +
		       "OR e.eventContent like %:keyword% " +
		       "OR e.eventPlaceAddress like %:keyword% " +
		       "OR e.eventPlaceName like %:keyword% " +
		       "OR FORMAT(e.eventDate, 'yyyy-MM-dd') like %:keyword% " +
		       "OR FORMAT(e.closingDate, 'yyyy-MM-dd') like %:keyword%)")
		List<Event> findEventByKeyword(@Param("keyword") String keyword);
	
	
//	@Query("from Event where eventCancelStatus = 0 AND (eventStatus = 1 OR eventStatus = 2) AND (eventSubject like %:keyword% or eventContent like %:keyword%)")
//	List<Event> findEventByKeyword(@Param(value = "keyword") String keyword);
	
	//已取消活動
	@Query("from Event where eventCancelStatus = 1")
	List<Event> findCancelEvents();

	//正常未取消活動
	@Query("from Event where eventCancelStatus = 0")
	List<Event> findNotCancelEvents();
	
	//前台活動首頁顯示沒取消且通過審核的活動
//	@Query("from Event where eventCancelStatus = 0 AND approvalStatus = 2  order by eventDate")
	@Query("SELECT e FROM Event e " +
		       "WHERE e.eventCancelStatus = 0 AND e.approvalStatus = 2 " +
		       "ORDER BY CASE WHEN e.eventDate >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END, e.eventDate ASC")
	List<Event> findEventFront();
			
	List<Event> findByHostMemberId(Integer hostMemberId);
	
	List<Event> findByHostMember(Members hostMember);
	
	//特定會員舉辦的活動
	List<Event> findByHostMemberIdAndEventCancelStatus(Integer hostMemberId,Integer eventCancelStatus);
	
	List<Event> findByClosingDate(Date closingDate);
	
	List<Event> findByClosingDateBetween(Date startDate, Date endDate);
	
	List<Event> findByClosingDateAndEventCancelStatus(Date closingDate, Integer eventCancelStatus);
	
	@Query("from Event where closingDate = :closingDate and eventCancelStatus = :eventCancelStatus and approvalStatus = :approvalStatus")
	List<Event> findByClosingDateAndEventCancelStatusAndApprovalStatus(
	    @Param("closingDate") Date closingDate,
	    @Param("eventCancelStatus") Integer eventCancelStatus,
	    @Param("approvalStatus") Integer approvalStatus
	);

	
	List<Event> findByEventStatusAndEventCancelStatus(Integer eventStatus, Integer eventCancelStatus);
	

}
