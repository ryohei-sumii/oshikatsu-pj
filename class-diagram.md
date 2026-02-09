classDiagram
    class User {
        +String name
        +String email
        +String password
    }
    class Admin {
        +String name
        +String email
    }
    class UserService {
        +register(String name, String email, String password) User
        +login(String email, String password) boolean
        +registerFavoriteMember(String memberName) void
        +confirmRegistrationRequest(RegistrationRequest request) void
        +executeRegistration(RegistrationRequest request) void
    }
    class RegistrationRequest {
        +String name
        +String email
        +String password
    }
    class RegistrationResponse {
        +boolean success
        +String message
    }
    class GroupRegistrationRequest {
        +String groupName
        +String description
    }
    class GroupRegistrationResponse {
        +boolean success
        +String message
    }
    class MemberInfo {
        +String name
        +String description
    }
    class ActorInfo {
        +String name
        +String description
    }
    class AdminService {
        +confirmRegistrationRequest(RegistrationRequest request) void
        +executeRegistration(RegistrationRequest request) void
        +executeGroupRegistration(GroupRegistrationRequest request) void
    }
    User --> UserService
    Admin --> AdminService
    UserService --> RegistrationRequest
    UserService --> RegistrationResponse
    UserService --> GroupRegistrationRequest
    UserService --> GroupRegistrationResponse
    UserService --> MemberInfo
    UserService --> ActorInfo
    AdminService --> RegistrationRequest
    AdminService --> GroupRegistrationRequest