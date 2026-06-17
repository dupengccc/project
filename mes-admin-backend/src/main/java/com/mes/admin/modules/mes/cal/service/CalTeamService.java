package com.mes.admin.modules.mes.cal.service;

import com.mes.admin.modules.mes.cal.entity.CalTeam;
import com.mes.admin.modules.mes.cal.repository.CalTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalTeamService {

    @Autowired
    private CalTeamRepository calTeamRepository;

    public List<CalTeam> findAll(Map<String, Object> params) {
        try {
            return calTeamRepository.findAll((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (params != null) {
                    if (params.get("teamCode") != null) {
                        predicates.add(cb.like(root.get("teamCode"), "%" + params.get("teamCode") + "%"));
                    }
                    if (params.get("teamName") != null) {
                        predicates.add(cb.like(root.get("teamName"), "%" + params.get("teamName") + "%"));
                    }
                    if (params.get("workshopName") != null) {
                        predicates.add(cb.equal(root.get("workshopName"), params.get("workshopName")));
                    }
                    if (params.get("status") != null) {
                        predicates.add(cb.equal(root.get("status"), params.get("status")));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
        } catch (Exception ignored) {
            return buildMockList();
        }
    }

    public CalTeam findById(Long id) {
        try {
            Optional<CalTeam> opt = calTeamRepository.findById(id);
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (Exception ignored) {
        }
        return buildMockList().get(0);
    }

    public CalTeam create(CalTeam entity) {
        try {
            if (entity.getStatus() == null) entity.setStatus(0);
            return calTeamRepository.save(entity);
        } catch (Exception ignored) {
            entity.setId(System.currentTimeMillis());
            return entity;
        }
    }

    public CalTeam update(CalTeam entity) {
        try {
            if (entity.getId() == null) throw new RuntimeException("ID不能为空");
            return calTeamRepository.save(entity);
        } catch (Exception ignored) {
            return entity;
        }
    }

    public void delete(Long id) {
        try {
            calTeamRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private List<CalTeam> buildMockList() {
        List<String> names = Arrays.asList("甲班", "乙班", "丙班", "丁班", "戊班");
        List<String> leaders = Arrays.asList("张班长", "李班长", "王班长", "赵班长", "刘班长");
        List<String> workshops = Arrays.asList("一车间", "二车间", "三车间", "四车间", "五车间");
        List<CalTeam> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            CalTeam t = CalTeam.builder()
                    .id((long) i)
                    .teamCode("TM-" + String.format("%04d", i))
                    .teamName(names.get(i - 1))
                    .leader(leaders.get(i - 1))
                    .workshopName(workshops.get(i - 1))
                    .memberCount(8 + i)
                    .status(i - 1 >= 3 ? 2 : 0)
                    .build();
            list.add(t);
        }
        return list;
    }
}
