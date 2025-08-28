package com.example.demo.services.Implementation;

import com.example.demo.dto.request.GroupUpsertRequest;
import com.example.demo.dto.response.GroupResponse;
import com.example.demo.dto.response.GroupOption;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.StudyGroupService;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.List;

@Service
@Transactional
public class StudyGroupServiceImpl implements StudyGroupService {

  private final StudyGroupRepo studyGroupRepo;
  private final SchoolRepo schoolRepo;
  private final TeacherRepo teacherRepo;
  private final SubjectRepo subjectRepo;
  private final LevelRepo levelRepo;
  private final SectionRepo sectionRepo;
  private final ClassroomRepo classroomRepo;

  public StudyGroupServiceImpl(StudyGroupRepo studyGroupRepo,
                               SchoolRepo schoolRepo,
                               TeacherRepo teacherRepo,
                               SubjectRepo subjectRepo,
                               LevelRepo levelRepo,
                               SectionRepo sectionRepo,
                               ClassroomRepo classroomRepo) {
    this.studyGroupRepo = studyGroupRepo;
    this.schoolRepo = schoolRepo;
    this.teacherRepo = teacherRepo;
    this.subjectRepo = subjectRepo;
    this.levelRepo = levelRepo;
    this.sectionRepo = sectionRepo;
    this.classroomRepo = classroomRepo;
  }

  @Override
  public GroupResponse create(GroupUpsertRequest req) {
    Objects.requireNonNull(req, "request is null");
    if (req.getName() == null || req.getName().isBlank())
      throw new IllegalArgumentException("Group name is required");

    StudyGroup g = new StudyGroup();
    applyUpsert(g, req);
    g = studyGroupRepo.save(g);
    return toDto(g);
  }

  @Override
  public GroupResponse update(Long id, GroupUpsertRequest req) {
    Objects.requireNonNull(id, "id is null");
    Objects.requireNonNull(req, "request is null");

    StudyGroup g = studyGroupRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    applyUpsert(g, req);
    g = studyGroupRepo.save(g);
    return toDto(g);
  }

  @Transactional(readOnly = true)
  @Override
  public GroupResponse get(Long id) {
    StudyGroup g = studyGroupRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    return toDto(g);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<GroupResponse> list(String academicYear, Boolean active, Pageable pageable) {
    Specification<StudyGroup> spec = Specification.where(null);

    if (academicYear != null && !academicYear.isBlank()) {
      String ay = academicYear.trim().toLowerCase();
      spec = spec.and((root, q, cb) ->
          cb.equal(cb.lower(root.get("academicYear")), ay));
    }

    if (active != null) {
      spec = spec.and((root, q, cb) -> cb.equal(root.get("active"), active));
    }

    // if no filters, this just pages all groups
    return studyGroupRepo.findAll(spec, pageable).map(this::toDto);
  }

  // -------- NEW: lookup for dropdown (super simple) --------
  @Transactional(readOnly = true)
  @Override
  public List<GroupOption> lookup(String academicYear, Boolean active, String q, int limit) {
    Specification<StudyGroup> spec = Specification.where(null);

    if (academicYear != null && !academicYear.isBlank()) {
      spec = spec.and((root, cq, cb) -> cb.equal(root.get("academicYear"), academicYear));
    }
    if (active != null) {
      spec = spec.and((root, cq, cb) -> cb.equal(root.get("active"), active));
    }
    if (q != null && !q.isBlank()) {
      String like = "%" + q.toLowerCase() + "%";
      spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("name")), like));
    }

    int size = (limit > 0 && limit < 500) ? limit : 50;
    Pageable top = PageRequest.of(0, size, Sort.by("name").ascending());

    return studyGroupRepo.findAll(spec, top)
        .map(g -> new GroupOption(g.getId(), g.getName()))
        .getContent();
  }

  // ---------------------- helpers ----------------------
  private void applyUpsert(StudyGroup g, GroupUpsertRequest req) {
    g.setName(req.getName()); // <— NEW: set group name

    // school (optional)
    if (req.getSchoolId() != null) {
      School school = schoolRepo.findById(req.getSchoolId())
          .orElseThrow(() -> new IllegalArgumentException("School not found"));
      g.setSchool(school);
    } else if (g.getSchool() == null) {
      g.setSchool(null);
    }

    // required refs
    Teacher teacher = teacherRepo.findById(req.getTeacherId())
        .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
    Subject subject = subjectRepo.findById(req.getSubjectId())
        .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
    Level level = levelRepo.findById(req.getLevelId())
        .orElseThrow(() -> new IllegalArgumentException("Level not found"));
    Section section = sectionRepo.findById(req.getSectionId())
        .orElseThrow(() -> new IllegalArgumentException("Section not found"));

    g.setTeacher(teacher);
    g.setSubject(subject);
    g.setLevel(level);
    g.setSection(section);

    // classroom (optional)
    if (req.getClassroomId() != null) {
      Classroom classroom = classroomRepo.findById(req.getClassroomId())
          .orElseThrow(() -> new IllegalArgumentException("Classroom not found"));
      g.setClassroom(classroom);
    } else {
      g.setClassroom(null);
    }

    // simple fields
    g.setAcademicYear(req.getAcademicYear());
    if (req.getPrivateGroup() != null) g.setPrivateGroup(req.getPrivateGroup());
    if (req.getRevisionGroup() != null) g.setRevisionGroup(req.getRevisionGroup());
    if (req.getActive() != null) g.setActive(req.getActive());
    if (req.getCapacity() != null) g.setCapacity(req.getCapacity());

    g.setBillingModel(req.getBillingModel());
    g.setSessionsPerMonth(req.getSessionsPerMonth());
    g.setMonthlyFee(req.getMonthlyFee());
    g.setSessionCost(req.getSessionCost());
    g.setHourlyCost(req.getHourlyCost());
    g.setSessionDurationMin(req.getSessionDurationMin());
    if (req.getTeacherShareType() != null) g.setTeacherShareType(req.getTeacherShareType());
    if (req.getTeacherShareValue() != null) g.setTeacherShareValue(req.getTeacherShareValue());

    if (req.getAllowCheckInWithoutBalance() != null) g.setAllowCheckInWithoutBalance(req.getAllowCheckInWithoutBalance());
    if (req.getRequireFirstLessonAttendance() != null) g.setRequireFirstLessonAttendance(req.getRequireFirstLessonAttendance());
    if (req.getRegisterFirstAbsence() != null) g.setRegisterFirstAbsence(req.getRegisterFirstAbsence());
    if (req.getLastLessonReminder() != null) g.setLastLessonReminder(req.getLastLessonReminder());
    if (req.getAbsenceStopThreshold() != null) g.setAbsenceStopThreshold(req.getAbsenceStopThreshold());
    if (req.getWarnDuplicateCard() != null) g.setWarnDuplicateCard(req.getWarnDuplicateCard());
    if (req.getAllowMultipleCheckinsPerDay() != null) g.setAllowMultipleCheckinsPerDay(req.getAllowMultipleCheckinsPerDay());

    g.setStartDate(req.getStartDate());
    g.setNotes(req.getNotes());
  }

  private GroupResponse toDto(StudyGroup g) {
    GroupResponse r = new GroupResponse();
    r.setId(g.getId());
    r.setName(g.getName()); // <— NEW include in response
    r.setSchoolId(g.getSchool() != null ? g.getSchool().getId() : null);
    r.setAcademicYear(g.getAcademicYear());
    r.setTeacherId(g.getTeacher() != null ? g.getTeacher().getId() : null);
    r.setSubjectId(g.getSubject() != null ? g.getSubject().getId() : null);
    r.setLevelId(g.getLevel() != null ? g.getLevel().getId() : null);
    r.setSectionId(g.getSection() != null ? g.getSection().getId() : null);
    r.setClassroomId(g.getClassroom() != null ? g.getClassroom().getId() : null);
    r.setClassroomName(g.getClassroom() != null ? g.getClassroom().getRoomName() : null);
    r.setPrivateGroup(g.isPrivateGroup());
    r.setRevisionGroup(g.isRevisionGroup());
    r.setActive(g.isActive());
    r.setCapacity(g.getCapacity());
    r.setBillingModel(g.getBillingModel());
    r.setSessionsPerMonth(g.getSessionsPerMonth());
    r.setMonthlyFee(g.getMonthlyFee());
    r.setSessionCost(g.getSessionCost());
    r.setHourlyCost(g.getHourlyCost());
    r.setSessionDurationMin(g.getSessionDurationMin());
    r.setTeacherShareType(g.getTeacherShareType());
    r.setTeacherShareValue(g.getTeacherShareValue());
    r.setAllowCheckInWithoutBalance(g.isAllowCheckInWithoutBalance());
    r.setRequireFirstLessonAttendance(g.isRequireFirstLessonAttendance());
    r.setRegisterFirstAbsence(g.isRegisterFirstAbsence());
    r.setLastLessonReminder(g.isLastLessonReminder());
    r.setAbsenceStopThreshold(g.getAbsenceStopThreshold());
    r.setWarnDuplicateCard(g.isWarnDuplicateCard());
    r.setAllowMultipleCheckinsPerDay(g.isAllowMultipleCheckinsPerDay());
    r.setStartDate(g.getStartDate());
    r.setNotes(g.getNotes());
    return r;
  }
}
