package org.example.projecttestassignment.controllers;


import org.example.projecttestassignment.dto.UserRequest;
import org.example.projecttestassignment.exception.UserNotFoundException;
import org.example.projecttestassignment.model.User;
import org.example.projecttestassignment.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;



import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser_Success() throws Exception {
        UserRequest userRequest = new UserRequest("email@example.com", "John", "Doe", LocalDate.now().minusYears(20), "123 Main St", "555-1234");
        User user = new User(1L, "email@example.com", "John", "Doe", LocalDate.now().minusYears(20), "123 Main St", "555-1234");

        when(userService.createUser(any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@example.com\"," +
                                "\"firstName\":\"John\"," +
                                "\"lastName\":\"Doe\"," +
                                "\"birthDate\":\"2000-01-01\"," +
                                "\"address\":\"123 Main St\"," +
                                "\"phoneNumber\":\"555-1234\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    public void getUser_whenUserFound_thenReturnsUser() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userService.findUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService).findUserById(userId);
    }
    @Test
    public void getUser_whenUserNotFound_thenThrowsException() throws Exception {
        Long userId = 1L;

        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException("User with id " + userId + " not found"));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("User with id " + userId + " not found"));

        verify(userService).findUserById(userId);
    }

    @Test
    void updateUser_Success() throws Exception {
        Long userId = 1L;
        UserRequest userRequest = new UserRequest("email@update.com",
                "John", "Doe",
                LocalDate.now().minusYears(25),
                "456 New St", "555-6789");
        User updatedUser = new User(userId, "email@update.com",
                "John", "Doe", LocalDate.now().minusYears(25),
                "456 New St", "555-6789");

        when(userService.updateUser(eq(userId), any(UserRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@update.com\"," +
                                "\"firstName\":\"John\"," +
                                "\"lastName\":\"Doe\"," +
                                "\"birthDate\":\"1995-01-01\"," +
                                "\"address\":\"456 New St\"," +
                                "\"phoneNumber\":\"555-6789\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("email@update.com"));

        verify(userService).updateUser(eq(userId), any(UserRequest.class));
    }
    @Test
    public void updateUser_whenUserNotFound_thenThrowsException() throws Exception {
        Long userId = 1L;
        UserRequest updateRequest = new UserRequest();

        when(userService.updateUser(eq(userId), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User with id " + userId + " not found"));

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"update@example.com\"," +
                                "\"firstName\":\"UpdatedFirstName\"," +
                                "\"lastName\":\"UpdatedLastName\"," +
                                "\"birthDate\":\"1992-01-01\"," +
                                "\"address\":\"UpdatedAddress\"," +
                                "\"phone\":\"UpdatedPhone\"" +
                                "}"))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(userId), any(UserRequest.class));
    }


    @Test
    void deleteUser_Success() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }
    @Test
    public void deleteUser_whenUserNotFound_thenThrowsException() throws Exception {
        Long userId = 1L;
        doThrow(new UserNotFoundException("User with id " + userId + " not found")).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(userId);
    }


    @Test
    public void partialUpdateUser_whenSuccessful_thenReturnsUpdatedUser() throws Exception {
        Long userId = 1L;
        UserRequest partialUpdateRequest = new UserRequest();
        User partiallyUpdatedUser = new User();

        when(userService.partialUpdateUser(eq(userId), any(UserRequest.class))).thenReturn(partiallyUpdatedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"update@example.com\"" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(partiallyUpdatedUser.getId()));

        verify(userService).partialUpdateUser(eq(userId), any(UserRequest.class));
    }

    @Test
    public void getAllUsers_whenCalled_thenReturnsUserList() throws Exception {
        int page = 0;
        int size = 10;
        LocalDate from = LocalDate.now().minusYears(1);
        LocalDate to = LocalDate.now();

        List<User> users = List.of(new User(), new User());

        when(userService.searchUsers(any(PageRequest.class), eq(from), eq(to))).thenReturn(users);

        mockMvc.perform(get("/users")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(users.size())));

        verify(userService).searchUsers(any(PageRequest.class), eq(from), eq(to));
    }



}
