import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //wskazujemy nazwe sterownika
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Sterownik do bazy nie został znalziony");
            return; //kończy działanie jak sterownik nie został znaleziony
        }

        //opcje dla menu
        System.out.println("Co chcesz zroboć?");
        System.out.println("1. Miasta z Polski");
        System.out.println("2. Miasta z danego kodu kraju");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();



        //łączymy się z bazą word
        String url = "jdbc:mysql://localhost:3306/world?serverTimezone=UTC";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, "root", "zioom1");
        } catch (SQLException e) {
            System.out.println("Błąd podczas nawiązywania połączenia: " + e.getMessage());
            e.printStackTrace();
        }

        //pobieranie informacji o wszystkich polskich miastach
        try {
            String sql = "SELECT * FROM city WHERE CountryCode = 'POL' ORDER BY Name ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                //pobieranie informacji z kolumn z bazy
                String cityName = resultSet.getString("Name");
                int population = resultSet.getInt("Population");
                System.out.println(cityName + " ludnosć: " + population);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas zamykania połączenia: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
