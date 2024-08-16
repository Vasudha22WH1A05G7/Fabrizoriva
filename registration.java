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

@WebServlet("/registration")
public class registration extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public registration() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Print input for debugging
        System.out.println("Received Name: " + name);
        System.out.println("Received Email: " + email);
        System.out.println("Received Password: " + password);

        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            boolean isRegistered = checkIfEmailExists(email);
            
            if (isRegistered) {
                response.getWriter().write("<div class='error-message'>Email already registered. Please <a href='login.jsp'>login</a>.</div>");
            } else {
                boolean success = registerUser(name, email, password);

                if (success) {
                    response.getWriter().write("<div class='success-message'>Successfully Registered! <a href='login.jsp'>Login here</a>.</div>");
                } else {
                    response.getWriter().write("<div class='error-message'>Failed to register user.</div>");
                }
            }
        } else {
            response.getWriter().write("<div class='error-message'>Invalid email or password.</div>");
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

            System.out.println("Executing query: " + pstmt.toString());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private boolean registerUser(String name, String email, String password) {
        boolean success = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/logindb", "root", "1234");
            String query = "INSERT INTO userlogin (gmail, password, name) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password); // Ideally, hash the password
            pstmt.setString(3, name);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                success = true;
            }
            pstmt.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
}
