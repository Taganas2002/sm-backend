package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
    name = "students",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"card_uid"}) // student card/QR must be unique
    }
)
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // -------- Relations (FKs) --------
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id")
  private School school;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_id")
  private Level level;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "section_id")
  private Section section;

  // -------- Core info --------
  @NotBlank
  @Column(name = "full_name", nullable = false, length = 160)
  private String fullName;

  @Column(name = "photo_url", length = 500)
  private String photoUrl;

  @Column(name = "dob")
  private LocalDate dob;

  // e.g. "M" / "F" — keep as a short string or change to an Enum later
  @Column(name = "gender", length = 1)
  private String gender;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "phone", length = 30)
  private String phone;

  @Email
  @Column(name = "email", length = 120)
  private String email;

  @Column(name = "guardian_name", length = 160)
  private String guardianName;

  @Column(name = "guardian_phone", length = 30)
  private String guardianPhone;

  @Column(name = "enrollment_date")
  private LocalDate enrollmentDate;

  // IMPORTANT: Java property name is cardUid (repository uses findByCardUid)
  @NotBlank
  @Column(name = "card_uid", nullable = false, length = 100)
  private String cardUid;

  @Column(name = "medical_notes", columnDefinition = "TEXT")
  private String medicalNotes;

  // -------- Constructors --------
  public Student() { }

  // (optional) convenience constructor
  public Student(String fullName, String cardUid) {
    this.fullName = fullName;
    this.cardUid = cardUid;
  }

  // -------- Getters / Setters --------
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public School getSchool() { return school; }
  public void setSchool(School school) { this.school = school; }

  public Level getLevel() { return level; }
  public void setLevel(Level level) { this.level = level; }

  public Section getSection() { return section; }
  public void setSection(Section section) { this.section = section; }

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }

  public String getPhotoUrl() { return photoUrl; }
  public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

  public LocalDate getDob() { return dob; }
  public void setDob(LocalDate dob) { this.dob = dob; }

  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getGuardianName() { return guardianName; }
  public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

  public String getGuardianPhone() { return guardianPhone; }
  public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

  public LocalDate getEnrollmentDate() { return enrollmentDate; }
  public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

  public String getCardUid() { return cardUid; }
  public void setCardUid(String cardUid) { this.cardUid = cardUid; }

  public String getMedicalNotes() { return medicalNotes; }
  public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }

  // -------- Equality (by id) --------
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Student)) return false;
    Student that = (Student) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
