package ru.aston.Sergei190.servlet;

import ru.aston.Sergei190.roles.Role;
import ru.aston.Sergei190.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "Ser19052001");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String username = resultSet.getString("username");

                User user = new User(userId, username);
                users.add(user);
            }
            resultSet.close();
            statement.close();

            for (User user : users) {
                List<Role> roles = new ArrayList<>();

                PreparedStatement roleStatement = connection.prepareStatement("SELECT * FROM roles WHERE user_id = ?");
                roleStatement.setInt(1, user.getId());
                ResultSet roleResultSet = roleStatement.executeQuery();

                while (roleResultSet.next()) {
                    int roleId = roleResultSet.getInt("id");
                    String roleName = roleResultSet.getString("name");

                    Role role = new Role(roleId, roleName);
                    roles.add(role);
                }
                roleResultSet.close();
                roleStatement.close();

                user.setRoles(roles);
            }

            connection.close();

            response.setContentType("application/json");
            response.getWriter().println(users.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String username = request.getParameter("username");

            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "Ser19052001");

            String insertQuery = "INSERT INTO users (username) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            connection.close();

            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
