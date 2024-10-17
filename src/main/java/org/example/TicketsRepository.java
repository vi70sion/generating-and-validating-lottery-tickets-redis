package org.example;

import com.google.gson.Gson;
import org.example.model.Ticket;
import java.sql.*;
import static org.example.utility.Constants.*;

public class TicketsRepository {

    public TicketsRepository() {
    }

    public boolean addTicket(Ticket ticket, Gson gson) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets (uuid, numbers, verified) VALUES (?,?,?)")){
            statement.setString(1, ticket.getUuidCode());
            statement.setString(2, gson.toJson(ticket.getNumbers()));
            statement.setBoolean(3, false);
            return (statement.executeUpdate() > 0) ? true : false;
        } catch (SQLException e) {
            return false;    //other errors
        }
    }

    public Ticket getOneTicket(Gson gson) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM tickets WHERE verified IS false ORDER BY uuid ASC LIMIT 1");
             ResultSet resultSet = statement.executeQuery()) {
            boolean hasResults = resultSet.next();
            if(!hasResults) return null;
            return new Ticket(resultSet.getString("uuid"),
                              gson.fromJson(resultSet.getString("numbers"), int[].class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTicket(Ticket ticket) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE tickets SET verified = ? WHERE uuid = ?")) {
            statement.setBoolean(1, true);
            statement.setString(2, ticket.getUuidCode());
            return (statement.executeUpdate() > 0) ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
