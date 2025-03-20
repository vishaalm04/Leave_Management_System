package com.example.leaveManagementSystem.repository;

import com.example.leaveManagementSystem.entity.LeaveTransactionEntity;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Setter
public class LeaveTransactionSpecification {
    public static Specification<LeaveTransactionEntity> getFilteredTransactions(
            Long tenantId, Long userId, Long approverId, String search,List<String>statuses,String fromDate,String toDate,String sortBy,String sortOrder) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ensure filtering only within the given tenant
            predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));

            if (userId != null) {
                // User can search by Leave Type or Remarks
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

                if (search != null && !search.isEmpty()) {
                    Predicate leaveTypePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("leaveType").get("name")), "%" + search.toLowerCase() + "%");
                    Predicate remarksPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), "%" + search.toLowerCase() + "%");
                    predicates.add(criteriaBuilder.or(leaveTypePredicate, remarksPredicate));
                }
            } else if (approverId != null) {
                // Approver can search by User's Unique Code or Name
                predicates.add(criteriaBuilder.equal(root.get("approver").get("id"), approverId));

                if (search != null && !search.isEmpty()) {
                    Predicate userUniqueCodePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("uniqueCode")), "%" + search.toLowerCase() + "%");
                    Predicate userNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("name")), "%" + search.toLowerCase() + "%");
                    predicates.add(criteriaBuilder.or(userUniqueCodePredicate, userNamePredicate));
                }
            }
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("leaveStatus").in(statuses));
            }
            assert query != null;
            query.orderBy(criteriaBuilder.asc(criteriaBuilder.selectCase()
                    .when(criteriaBuilder.equal(root.get("leaveStatus"), "PENDING"), 1)
                    .when(criteriaBuilder.equal(root.get("leaveStatus"), "APPROVED"), 2)
                    .when(criteriaBuilder.equal(root.get("leaveStatus"), "REJECTED"), 3)
                    .when(criteriaBuilder.equal(root.get("leaveStatus"), "CANCELLED"), 4)
                    .otherwise(5)));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate from = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate, formatter) : null;
            LocalDate to = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate, formatter) : null;

            if (from != null && to != null) {
                predicates.add(criteriaBuilder.between(root.get("startDate"), from, to));
            } else if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), from));
            } else if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), to));
            }

            List<Order> orderList = new ArrayList<>();
            if ("leaveType".equalsIgnoreCase(sortBy)) {
                if ("desc".equalsIgnoreCase(sortOrder)) {
                    orderList.add(criteriaBuilder.desc(root.get("leaveType").get("name")));
                } else {
                    orderList.add(criteriaBuilder.asc(root.get("leaveType").get("name")));
                }
            } else if ("date".equalsIgnoreCase(sortBy)) {
                if ("desc".equalsIgnoreCase(sortOrder)) {
                    orderList.add(criteriaBuilder.desc(root.get("startDate")));
                } else {
                    orderList.add(criteriaBuilder.asc(root.get("startDate")));
                }
            } else {
                // Default Sorting: Latest record first
                orderList.add(criteriaBuilder.desc(root.get("createdAt")));
            }

            query.orderBy(orderList);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


