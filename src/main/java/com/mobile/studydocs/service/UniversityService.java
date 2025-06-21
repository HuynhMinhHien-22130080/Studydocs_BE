package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.UniversityDao;
import com.mobile.studydocs.model.entity.University;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UniversityService {
    private final UniversityDao universityDao;

    public UniversityService(UniversityDao universityDao) {
        this.universityDao = universityDao;
    }

    // ===== hao lam phần này (upload university) =====
    public void uploadUniversity(University university) throws Exception {
        universityDao.save(university);
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (get all universities) =====
    public List<University> getAllUniversities() throws Exception {
        return universityDao.getAll();
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (add subject to university) =====
    public boolean addSubject(String universityId, String subjectName) throws Exception {
        return universityDao.addSubject(universityId, subjectName);
    }
    // ===== end hao lam phần này =====

    // ===== hao lam phần này (get university by id) =====
    public University getUniversityById(String universityId) throws Exception {
        return universityDao.getUniversityById(universityId);
    }
    // ===== end hao lam phần này =====
}
