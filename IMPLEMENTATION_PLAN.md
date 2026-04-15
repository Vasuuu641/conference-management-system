# Research Paper & Conference Management System - Implementation Plan

## Project Overview
A Spring Boot + Thymeleaf + PostgreSQL application for managing academic research conferences, paper submissions, reviews, and decisions.

---

## Phase 1: Database Schema & Entity Models

### 1.1 Entity Relationships Overview
```
One-to-One:
  - User ↔ UserProfile (bio, affiliation, photo)

One-to-Many:
  - Conference → Papers (one conference has many papers)
  - Author → Papers (one author submits many papers)

Many-to-One:
  - Papers → Conference (inverse relationship)

Many-to-Many:
  - Papers ↔ Reviewers (many reviewers review many papers)
  - Papers ↔ Tags (many tags per paper, many papers per tag)
```

### 1.2 Entities to Create
1. **User** - Core user entity with role-based access
2. **UserProfile** - One-to-One with User (bio, affiliation, photo)
3. **Conference** - Conference details and metadata
4. **Paper** - Paper submission entity
5. **Review** - Reviews submitted by reviewers
6. **Tag** - Research topics/tags for papers
7. **Role** - User roles (AUTHOR, REVIEWER, ADMIN/CHAIR)

### 1.3 Database Tables Required
- `users` (id, username, email, password, created_date)
- `user_profiles` (id, user_id, bio, affiliation, photo_url)
- `roles` (id, role_name)
- `user_roles` (user_id, role_id) - Many-to-Many join table
- `conferences` (id, title, description, start_date, end_date, deadline, status)
- `papers` (id, title, abstract, file_path, status, submission_date, conference_id, author_id)
- `reviews` (id, paper_id, reviewer_id, score, comments, review_date)
- `tags` (id, tag_name, description)
- `paper_tags` (paper_id, tag_id) - Many-to-Many join table
- `paper_reviewers` (paper_id, reviewer_id) - Many-to-Many join table

---

## Phase 2: Project Setup & Dependencies

### 2.1 Verify Current Configuration
- ✅ Spring Boot 4.0.5
- ✅ PostgreSQL connection (port 5433)
- ✅ Thymeleaf integration
- ✅ Spring Security
- ✅ Spring Data JPA
- ✅ Lombok

### 2.2 Additional Dependencies Needed
- Spring Security (already present)
- Validation annotations
- File upload handling (Apache Commons FileUpload)
- DateTime handling (Java 8+ Time API)

---

## Phase 3: Backend Implementation

### 3.1 Step 1: Create Entity Classes
**Order of creation (handles dependencies):**
1. `Role` - Basic enum/entity
2. `User` - Core entity
3. `UserProfile` - References User (One-to-One)
4. `Conference` - Independent entity
5. `Tag` - Independent entity
6. `Paper` - References Conference, User (Many-to-One)
7. `Review` - References Paper, User (Many-to-One)
8. Set up Many-to-Many relationships

**Location:** `src/main/java/com/vasu/conference_management/entity/`

### 3.2 Step 2: Create Repository Interfaces
**Repositories needed:**
1. `UserRepository` - Custom queries for user lookup
2. `UserProfileRepository` - Profile by user
3. `ConferenceRepository` - Search/filter conferences
4. `PaperRepository` - Papers by conference/author/status
5. `ReviewRepository` - Reviews by paper/reviewer
6. `TagRepository` - Find tags by name
7. `RoleRepository` - Find roles

**Location:** `src/main/java/com/vasu/conference_management/repository/`

### 3.3 Step 3: Create Service Layer
**Services needed:**
1. `UserService` - User management, registration
2. `AuthenticationService` - Login/authentication logic
3. `ConferenceService` - CRUD operations, search
4. `PaperService` - Paper submission, status tracking
5. `ReviewService` - Submit reviews, calculate statistics
6. `TagService` - Manage research topics
7. `DashboardService` - Statistics and analytics

**Location:** `src/main/java/com/vasu/conference_management/service/`

### 3.4 Step 4: Create REST Controllers
**Controllers needed:**
1. `AuthController` - Login, registration, logout
2. `UserController` - User profile management
3. `ConferenceController` - Conference CRUD and browsing
4. `PaperController` - Paper submission and status tracking
5. `ReviewController` - Submit and view reviews
6. `DashboardController` - Statistics and analytics
7. `SearchController` - Search and filter functionality

**Location:** `src/main/java/com/vasu/conference_management/controller/`

### 3.5 Step 5: Security Configuration
**Tasks:**
1. Configure `SecurityConfig` with Spring Security
2. Set up password encoding (BCrypt)
3. Create custom `UserDetailsService` implementation
4. Configure login/logout endpoints
5. Set up role-based access control (RBAC)
6. Configure CSRF protection

**Location:** `src/main/java/com/vasu/conference_management/config/`

### 3.6 Step 6: DTOs (Data Transfer Objects)
**DTOs needed:**
1. `UserDTO` - For registration/user info
2. `PaperDTO` - Paper submission form
3. `ReviewDTO` - Review submission form
4. `ConferenceDTO` - Conference details
5. `LoginRequestDTO` - Login credentials

**Location:** `src/main/java/com/vasu/conference_management/dto/`

### 3.7 Step 7: Utility Classes
**Create:**
1. `FileUploadUtil` - Handle file uploads
2. `ValidationUtil` - Input validation
3. `DateUtil` - Date formatting and operations

**Location:** `src/main/java/com/vasu/conference_management/util/`

---

## Phase 4: Frontend Implementation (Thymeleaf Templates)

### 4.1 Templates Structure
```
templates/
├── layout.html              (Base template)
├── index.html               (Home page)
├── auth/
│   ├── login.html
│   └── register.html
├── conference/
│   ├── list.html            (Browse conferences)
│   ├── details.html         (Conference overview)
│   ├── create.html          (Admin only)
│   └── edit.html            (Admin only)
├── paper/
│   ├── submit.html          (Author submission form)
│   ├── my-papers.html       (Author's submitted papers)
│   ├── list.html            (Browse papers with filters)
│   ├── details.html         (Paper overview)
│   └── edit.html            (Edit submission)
├── review/
│   ├── assigned.html        (Reviewer's papers)
│   ├── submit-review.html   (Review form)
│   └── view-reviews.html    (View all reviews for paper)
├── dashboard/
│   └── index.html           (Statistics and analytics)
└── user/
    └── profile.html         (User profile management)
```

### 4.2 Template Features
1. Responsive Bootstrap 5 design
2. Role-based view rendering
3. Form validation messages
4. Flash messages for feedback
5. Search and filter functionality
6. Pagination for large datasets

---

## Phase 5: Features Implementation Order

### Priority 1: Core Authentication & Setup
- [ ] User registration
- [ ] User login/logout
- [ ] Role assignment
- [ ] Basic user profile

### Priority 2: Conference Management (Admin)
- [ ] Create conference
- [ ] Edit/delete conference
- [ ] View all conferences
- [ ] List conferences (public view)

### Priority 3: Paper Submission (Author)
- [ ] Submit paper with file upload
- [ ] View my submitted papers
- [ ] Track paper status
- [ ] Edit paper submission (if under review)
- [ ] Delete draft papers

### Priority 4: Reviewer Assignment & Reviews
- [ ] Assign reviewers to papers (Admin)
- [ ] View assigned papers (Reviewer)
- [ ] Submit review with score and comments (Reviewer)
- [ ] View all reviews for a paper (Author)

### Priority 5: Search, Filter & Dashboard
- [ ] Search papers by title/author
- [ ] Filter papers by: conference, status, topic
- [ ] Dashboard statistics:
  - Submissions per conference
  - Acceptance rate
  - Papers under review
  - User statistics

### Priority 6: Status Workflow
- [ ] Implement status transitions:
  - Submitted → Under Review (when reviewers assigned)
  - Under Review → Accepted/Rejected (when decision made)
- [ ] Admin decision making UI
- [ ] Notification system (email for status changes)

### Priority 7: Advanced Features
- [ ] File management (secure download/upload)
- [ ] Paper versioning
- [ ] Deadline enforcement
- [ ] Report generation
- [ ] Export functionality (CSV, PDF)

---

## Phase 6: Testing Strategy

### 6.1 Unit Tests
- Entity validation
- Service layer logic
- Repository queries

### 6.2 Integration Tests
- Controller endpoints
- Authentication flow
- Database transactions

### 6.3 Test Locations
```
src/test/java/com/vasu/conference_management/
├── service/
├── controller/
└── repository/
```

---

## Phase 7: Deployment & DevOps

### 7.1 Environment Configuration
- Development (local)
- Production (configuration profiles)

### 7.2 Database Migrations
- Use Flyway/Liquibase for schema versioning
- Create initial seed data

### 7.3 Deployment Package
- Build WAR or JAR
- Configure production database
- Set up SSL/HTTPS

---

## Development Stack Summary

### Backend
- **Framework:** Spring Boot 4.0.5
- **ORM:** Spring Data JPA + Hibernate
- **Database:** PostgreSQL 16
- **Security:** Spring Security with BCrypt
- **Validation:** Jakarta Validation
- **Lombok:** For reducing boilerplate

### Frontend
- **Template Engine:** Thymeleaf 3.1.3
- **CSS Framework:** Bootstrap 5
- **JavaScript:** Vanilla JS + Bootstrap components

### Project Structure
```
src/main/java/com/vasu/conference_management/
├── entity/              (Database entities)
├── repository/          (Data access layer)
├── service/             (Business logic)
├── controller/          (HTTP endpoints)
├── config/              (Spring configuration)
├── dto/                 (Data transfer objects)
├── util/                (Utility classes)
├── security/            (Security components)
└── ConferenceManagementApplication.java
```

---

## Implementation Checkpoints

### Checkpoint 1: Entity Models (Phase 3.1)
- All entities created with proper annotations
- Relationships configured correctly
- Database tables auto-created via Hibernate

### Checkpoint 2: Repositories & Basic Services (Phase 3.2-3.3)
- All CRUD operations working
- Custom queries tested

### Checkpoint 3: Authentication (Phase 3.5)
- User registration working
- Login/logout functional
- Security endpoints protected

### Checkpoint 4: Core Features (Phase 3.4-3.6)
- All controllers responding
- DTOs properly validated

### Checkpoint 5: Frontend Templates (Phase 4)
- All views rendering correctly
- Forms submitting data properly

### Checkpoint 6: Full Workflow Testing (Phase 6)
- End-to-end scenarios tested
- All features integrated

---

## Key Design Decisions

1. **Soft Deletes:** Consider adding `deleted_at` timestamp instead of hard deletes
2. **Audit Trail:** Add `created_at`, `updated_at`, `created_by` fields to critical entities
3. **File Storage:** Store files on disk or cloud; store path in database
4. **Email Notifications:** Queue-based system for sending emails
5. **API Response:** Use consistent DTO-based API responses with proper HTTP status codes

---

## Estimated Effort
- Phase 1-2: 1-2 hours (Setup)
- Phase 3: 4-6 hours (Backend implementation)
- Phase 4: 3-5 hours (Frontend)
- Phase 5: 2-3 hours (Feature integration)
- Phase 6-7: 1-2 hours (Testing & deployment prep)

**Total: ~12-18 hours of development**

---

## Next Steps
1. ✅ Review this implementation plan
2. Start Phase 3.1: Create Entity Classes
3. Follow the step-by-step implementation guide
4. Test each phase before moving to the next

---

**Document Status:** Ready for Implementation  
**Last Updated:** 2026-04-15  
**Version:** 1.0

