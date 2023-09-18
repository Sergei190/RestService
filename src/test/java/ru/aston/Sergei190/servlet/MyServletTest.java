package ru.aston.Sergei190.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import static org.mockito.Mockito.*;

public class MyServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Before
    public void setup() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("username=test")));

        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn("test");
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetUsers() throws Exception {
        MyServlet servlet = new MyServlet();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        servlet.doGet(request, response);

        verify(response, times(1)).setContentType("application/json");
        verify(response.getWriter(), times(1)).println("[User{id=1, username='test', roles=null}]");
    }

    @Test
    public void shouldCreateUser() throws Exception {
        MyServlet servlet = new MyServlet();

        servlet.doPost(request, response);

        verify(connection, times(1)).prepareStatement("INSERT INTO users (username) VALUES (?)");
        verify(preparedStatement, times(1)).setString(1, "test");
        verify(preparedStatement, times(1)).executeUpdate();
        verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }
}
