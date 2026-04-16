# Next Steps - Frontend Development Guide

## ✅ Backend is Complete

Your authentication backend is production-ready. Now it's time to build the user-facing features with Thymeleaf.

---

## 📝 Phase 1: Login & Registration Pages (1-2 hours)

### Task 1: Create Login Template
**File**: `src/main/resources/templates/login.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login - Conference Management</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="login-container">
        <h1>Conference Management System</h1>
        <h2>User Login</h2>
        
        <!-- Error message display -->
        <div th:if="${error}" class="alert alert-danger">
            <span th:text="${error}">Error message</span>
        </div>
        
        <!-- Login form -->
        <form method="POST" th:action="@{/api/auth/login}">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required autofocus>
            </div>
            
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Login</button>
        </form>
        
        <!-- Links -->
        <div class="login-links">
            <p>Don't have an account? <a th:href="@{/register}">Register here</a></p>
            <p><a th:href="@{/forgot-password}">Forgot password?</a></p>
        </div>
    </div>
</body>
</html>
```

### Task 2: Create Registration Template
**File**: `src/main/resources/templates/register.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Register - Conference Management</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="register-container">
        <h1>Conference Management System</h1>
        <h2>User Registration</h2>
        
        <!-- Error message display -->
        <div th:if="${error}" class="alert alert-danger">
            <span th:text="${error}">Error message</span>
        </div>
        
        <!-- Registration form -->
        <form method="POST" th:action="@{/api/auth/register}">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" required>
            </div>
            
            <div class="form-group">
                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" required>
            </div>
            
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
                <small>Min 8 characters, 1 uppercase, 1 number</small>
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Register</button>
        </form>
        
        <!-- Links -->
        <div class="register-links">
            <p>Already have an account? <a th:href="@{/login}">Login here</a></p>
        </div>
    </div>
</body>
</html>
```

---

## 🔧 Phase 2: Create UserController (2-3 hours)

### Task 3: Implement UserController
**File**: `src/main/java/com/vasu/conference_management/controller/UserController.java`

```java
package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.RegisterRequest;
import com.vasu.conference_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

   @Autowired
   private UserService userService;

   @GetMapping("/profile")
   public String getProfile(Authentication auth, Model model) {
      // Redirect to login if not authenticated
      if (auth == null || !auth.isAuthenticated()) {
         return "redirect:/login";
      }

      // Get current user from context
      String username = auth.getName();
      // Fetch user from database if needed

      return "user/profile";
   }

   @PostMapping("/register")
   public String registerUser(@ModelAttribute RegisterRequest request, Model model) {
      try {
         userService.register(request);
         return "redirect:/login?registered=true";
      } catch (IllegalStateException e) {
         model.addAttribute("error", e.getMessage());
         return "register";
      }
   }

   @PostMapping("/profile/update")
   public String updateProfile(Authentication auth,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               Model model) {
      if (auth == null || !auth.isAuthenticated()) {
         return "redirect:/login";
      }

      try {
         // Update user profile
         return "redirect:/user/profile?updated=true";
      } catch (Exception e) {
         model.addAttribute("error", e.getMessage());
         return "user/profile";
      }
   }
}
```

### Task 4: Create RegisterRequest DTO
**File**: `src/main/java/com/vasu/conference_management/dto/RegisterRequest.java`

```java
package com.vasu.conference_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
}
```

---

## 🎨 Phase 3: Create Navigation & Layout (1-2 hours)

### Task 5: Create Base Layout Template
**File**: `src/main/resources/templates/layout/base.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${title} + ' - Conference Management'">Page Title</title>
    <link href="/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar">
        <div class="navbar-brand">
            <a th:href="@{/}">Conference Management</a>
        </div>
        
        <div class="navbar-menu">
            <!-- Not logged in -->
            <div th:unless="${#authentication.isAuthenticated()}">
                <a th:href="@{/login}">Login</a>
                <a th:href="@{/register}">Register</a>
            </div>
            
            <!-- Logged in -->
            <div th:if="${#authentication.isAuthenticated()}">
                <a th:href="@{/dashboard}">Dashboard</a>
                
                <!-- Author-only links -->
                <div sec:authorize="hasRole('AUTHOR')">
                    <a th:href="@{/paper/submit}">Submit Paper</a>
                </div>
                
                <!-- Reviewer-only links -->
                <div sec:authorize="hasRole('REVIEWER')">
                    <a th:href="@{/reviews}">My Reviews</a>
                </div>
                
                <!-- Admin-only links -->
                <div sec:authorize="hasRole('ADMIN')">
                    <a th:href="@{/admin}">Admin Panel</a>
                </div>
                
                <!-- User menu -->
                <div class="user-menu">
                    <span th:text="${#authentication.name}">Username</span>
                    <form method="POST" th:action="@{/logout}" style="display:inline">
                        <button type="submit" class="btn-logout">Logout</button>
                    </form>
                </div>
            </div>
        </div>
    </nav>
    
    <!-- Main Content -->
    <main class="container">
        <div th:insert="~{this :: content}"></div>
    </main>
    
    <!-- Footer -->
    <footer>
        <p>&copy; 2026 Conference Management System</p>
    </footer>
</body>
</html>
```

---

## 📋 Phase 4: Dashboard Page (2-3 hours)

### Task 6: Create Dashboard Template
**File**: `src/main/resources/templates/dashboard.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <title>Dashboard</title>
</head>
<body>
    <div class="dashboard">
        <h1>Welcome, <span th:text="${#authentication.name}">User</span>!</h1>
        
        <!-- Dashboard Stats (if user is admin) -->
        <div sec:authorize="hasRole('ADMIN')" class="stats">
            <h2>System Statistics</h2>
            <div class="stat-card">
                <h3>Total Users</h3>
                <p th:text="${stats.userStats.totalUsers}">0</p>
            </div>
            <div class="stat-card">
                <h3>Active Conferences</h3>
                <p th:text="${stats.totalConferences}">0</p>
            </div>
            <div class="stat-card">
                <h3>Papers Submitted</h3>
                <p th:text="${stats.totalPapers}">0</p>
            </div>
        </div>
        
        <!-- Author Section -->
        <div sec:authorize="hasRole('AUTHOR')" class="author-section">
            <h2>My Submissions</h2>
            <a th:href="@{/paper/submit}" class="btn btn-primary">Submit New Paper</a>
            <div class="submissions-list">
                <!-- List user's papers here -->
            </div>
        </div>
        
        <!-- Reviewer Section -->
        <div sec:authorize="hasRole('REVIEWER')" class="reviewer-section">
            <h2>My Reviews</h2>
            <div class="reviews-list">
                <!-- List papers to review here -->
            </div>
        </div>
    </div>
</body>
</html>
```

---

## 🧪 Phase 5: Testing Your Frontend (1-2 hours)

### Task 7: Create Frontend Tests
```bash
# Test login flow
1. Open http://localhost:8080/login
2. Enter valid credentials
3. Should redirect to /dashboard
4. Session cookie (JSESSIONID) should be visible

# Test registration
1. Open http://localhost:8080/register
2. Fill in form with new user
3. Submit
4. Should redirect to login with success message

# Test logout
1. Click logout button
2. Should redirect to home page
3. Session should be cleared
```

---

## 🎯 Checklist to Complete

### Core UI Pages
- [ ] `/login` - Login page template
- [ ] `/register` - Registration page template
- [ ] `/dashboard` - Dashboard page template
- [ ] `/user/profile` - User profile page

### Controllers
- [ ] Update UserController for registration
- [ ] Create page routing for login/register/dashboard
- [ ] Add redirect logic for unauthorized access

### Views & Styling
- [ ] Create base layout template
- [ ] Create CSS stylesheet (/static/css/style.css)
- [ ] Add Bootstrap or Tailwind for styling
- [ ] Create navigation bar with role-based menu

### Testing
- [ ] Test login flow end-to-end
- [ ] Test registration flow
- [ ] Test logout
- [ ] Test role-based visibility
- [ ] Test session persistence

### Security Features
- [ ] Add password strength indicator
- [ ] Implement email verification
- [ ] Add "Remember Me" functionality
- [ ] Add password reset flow
- [ ] Implement rate limiting on login

---

## 🚀 Quickstart Commands

```bash
# Navigate to project
cd /home/vasu/Downloads/conference-management

# Start development server
./mvnw spring-boot:run

# Build and test
./mvnw clean test

# Access application
# http://localhost:8080/login
# http://localhost:8080/register
# http://localhost:8080/dashboard (after login)
```

---

## 💡 Pro Tips

1. **Use Thymeleaf security extras**
   ```html
   <div sec:authorize="hasRole('ADMIN')">
       <!-- Only visible to admins -->
   </div>
   ```

2. **Access current user in templates**
   ```html
   <span th:text="${#authentication.name}">Username</span>
   ```

3. **Add CSRF token to forms** (if enabling CSRF)
   ```html
   <input type="hidden" th:name="${_csrf.parameterName}" 
          th:value="${_csrf.token}"/>
   ```

4. **Redirect to login on 401**
   ```html
   <script>
       $(document).ready(function() {
           $(document).ajaxError(function(event, jqxhr, settings, exc) {
               if (jqxhr.status === 401) {
                   window.location.href = '/login';
               }
           });
       });
   </script>
   ```

---

## 📚 Resources

- [Spring Security Thymeleaf Integration](https://www.thymeleaf.org/doc/articles/thymeleaf-spring-security-integration.html)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.0/)

---

## Status

**Backend**: ✅ COMPLETE
**Frontend**: 🚀 READY TO BUILD

Next: Start with Phase 1 (Login & Registration Pages)

Estimated total time: 7-10 hours for all phases
Recommended: Complete one phase per session

