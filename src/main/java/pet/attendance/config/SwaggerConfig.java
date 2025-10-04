package pet.attendance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.List;
import java.util.Map;

public class SwaggerConfig {
        public static OpenAPI createOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Attendance System API")
                                                .description("REST API for Student Attendance Management System")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("dazukio")
                                                                .email("ndeadweight@mail.ru")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Development Server")))
                                .path("/api/status", createStatusPathItem())
                                .path("/api/groups", createGroupsPathItem())
                                .path("/api/professors", createProfessorsPathItem())
                                .path("/api/subjects", createSubjectsPathItem())
                                .path("/api/students", createStudentsPathItem())
                                .path("/api/attendance", createAttendancePathItem());
        }

        private static PathItem createStatusPathItem() {
                return new PathItem()
                                .get(new Operation()
                                                .summary("API status")
                                                .description("Check if API is running")
                                                .responses(createDefaultResponses()));
        }

        private static PathItem createGroupsPathItem() {
                Schema groupSchema = new Schema()
                                .type("object")
                                .addProperty("name", new Schema().type("string").example("21-IT"));

                return new PathItem()
                                .get(new Operation()
                                                .summary("Get all groups")
                                                .description("Retrieve list of all student groups")
                                                .responses(createDefaultResponses()))
                                .post(new Operation()
                                                .summary("Create new group")
                                                .description("Create a new student group")
                                                .requestBody(new RequestBody()
                                                                .content(new Content()
                                                                                .addMediaType("application/json",
                                                                                                new MediaType().schema(
                                                                                                                groupSchema))))
                                                .responses(createDefaultResponses()));
        }

        private static PathItem createProfessorsPathItem() {
                Schema professorSchema = new Schema()
                                .type("object")
                                .addProperty("firstName", new Schema().type("string").example("Alexey"))
                                .addProperty("lastName", new Schema().type("string").example("Sidorov"))
                                .addProperty("email", new Schema().type("string").example("sidorov@university.edu"));

                return new PathItem()
                                .get(new Operation()
                                                .summary("Get all professors")
                                                .description("Retrieve list of all professors")
                                                .responses(createDefaultResponses()))
                                .post(new Operation()
                                                .summary("Create new professor")
                                                .description("Create a new professor")
                                                .requestBody(new RequestBody()
                                                                .content(new Content()
                                                                                .addMediaType("application/json",
                                                                                                new MediaType().schema(
                                                                                                                professorSchema))))
                                                .responses(createDefaultResponses()));
        }

        private static PathItem createSubjectsPathItem() {
                Schema subjectSchema = new Schema()
                                .type("object")
                                .addProperty("name", new Schema().type("string").example("Mathematics"))
                                .addProperty("professorId", new Schema().type("integer").example(1));

                return new PathItem()
                                .get(new Operation()
                                                .summary("Get all subjects")
                                                .description("Retrieve list of all subjects")
                                                .responses(createDefaultResponses()))
                                .post(new Operation()
                                                .summary("Create new subject")
                                                .description("Create a new subject")
                                                .requestBody(new RequestBody()
                                                                .content(new Content()
                                                                                .addMediaType("application/json",
                                                                                                new MediaType().schema(
                                                                                                                subjectSchema))))
                                                .responses(createDefaultResponses()));
        }

        private static PathItem createStudentsPathItem() {
                Schema studentSchema = new Schema()
                                .type("object")
                                .addProperty("firstName", new Schema().type("string").example("Ivan"))
                                .addProperty("lastName", new Schema().type("string").example("Petrov"))
                                .addProperty("studentId", new Schema().type("string").example("21IT-001"))
                                .addProperty("groupId", new Schema().type("integer").example(1));

                return new PathItem()
                                .get(new Operation()
                                                .summary("Get all students")
                                                .description("Retrieve list of all students")
                                                .responses(createDefaultResponses()))
                                .post(new Operation()
                                                .summary("Create new student")
                                                .description("Create a new student")
                                                .requestBody(new RequestBody()
                                                                .content(new Content()
                                                                                .addMediaType("application/json",
                                                                                                new MediaType().schema(
                                                                                                                studentSchema))))
                                                .responses(createDefaultResponses()));
        }

        private static PathItem createAttendancePathItem() {
                Schema attendanceSchema = new Schema()
                                .type("object")
                                .addProperty("date", new Schema().type("string").example("2024-01-15"))
                                .addProperty("subjectId", new Schema().type("integer").example(1))
                                .addProperty("groupId", new Schema().type("integer").example(1))
                                .addProperty("records", new Schema()
                                                .type("array")
                                                .items(new Schema()
                                                                .type("object")
                                                                .addProperty("studentId",
                                                                                new Schema().type("integer").example(1))
                                                                .addProperty("status", new Schema().type("string")
                                                                                .example("present"))));

                return new PathItem()
                                .get(new Operation()
                                                .summary("Get attendance records")
                                                .description("Retrieve attendance records for specific date, subject and group")
                                                .responses(createDefaultResponses()))
                                .post(new Operation()
                                                .summary("Save attendance records")
                                                .description("Save or update attendance records for a class")
                                                .requestBody(new RequestBody()
                                                                .content(new Content()
                                                                                .addMediaType("application/json",
                                                                                                new MediaType().schema(
                                                                                                                attendanceSchema))))
                                                .responses(createDefaultResponses()));
        }

        private static ApiResponses createDefaultResponses() {
                return new ApiResponses()
                                .addApiResponse("200", new ApiResponse().description("Success"))
                                .addApiResponse("201", new ApiResponse().description("Created"))
                                .addApiResponse("400", new ApiResponse().description("Bad Request - Invalid input"))
                                .addApiResponse("404", new ApiResponse().description("Not Found"))
                                .addApiResponse("405", new ApiResponse().description("Method Not Allowed"))
                                .addApiResponse("500", new ApiResponse().description("Internal Server Error"));
        }
}