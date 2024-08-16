package fullstack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RegisterServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name != null && !name.isEmpty() && email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            log("Received registration request: email=" + email);

            boolean emailExists = checkIfEmailExists(email);

            if (emailExists) {
                log("Email already exists: " + email);
                response.getWriter().write("<script>alert('Email already registered. Please log in.'); window.location.href = 'login.jsp';</script>");
            } else {
                boolean registered = registerUser(name, email, password);

                if (registered) {
                    log("User registered successfully: " + email);
                    response.sendRedirect("mainweb.html");
                } else {
                    log("User registration failed: " + email);
                    response.getWriter().write("<script>alert('Registration failed. Please try again.'); window.location.href = 'registration.jsp';</script>");
                }
            }
        } else {
            log("Invalid input received: email=" + email);
            response.getWriter().write("<script>alert('Invalid input. Please try again.'); window.location.href = 'registration.jsp';</script>");
        }
    }

    private boolean checkIfEmailExists(String email) {
        boolean exists = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/logindb", "root", "1234");
            String query = "SELECT * FROM userlogin WHERE gmail = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            logError("Error checking if email exists", e);
        }
        return exists;
    }

    private boolean registerUser(String name, String email, String password) {
        boolean success = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/logindb", "root", "1234");
            String query = "INSERT INTO userlogin (name, gmail, password) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Consider hashing passwords in a real application
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            logError("Error registering user", e);
        }
        return success;
    }

    private void logError(String message, Exception e) {
        // Log the error with additional information
        log(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
