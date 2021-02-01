import java.sql.*;
import java.util.Scanner;

public class WorldApp {

    private Connection connection;
    private Scanner scanner;

    public void run() {
        //wskazujemy nazwe sterownika
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Sterownik do bazy nie został znalziony");
            return; //kończy działanie jak sterownik nie został znaleziony
        }

        //łączymy się z bazą word
        String url = "jdbc:mysql://localhost:3306/world?serverTimezone=UTC";
        try {
            connection = DriverManager.getConnection(url, "root", "zioom1");
        } catch (SQLException e) {
            System.out.println("Błąd podczas nawiązywania połączenia: " + e.getMessage());
            e.printStackTrace();
        }

        while (true){
            //opcje dla menu
            System.out.println("Co chcesz zroboć?");
            System.out.println("1. Miasta z Polski");
            System.out.println("2. Miasta z danego kodu kraju");
            System.out.println("3. Informacje o języku");
            System.out.println("0. Koniec");
            scanner = new Scanner(System.in);
            String option = scanner.nextLine();
            scanner.nextLine();

            switch (option){
                case "1":
                    citiesFromPoland();
                    break;
                case "2:"
                    citiesForGivenCountryCode();
                    break;
                case "3":
                    languageInfo();
                case "0":
                    close(); //zamknij połączenie i zakoncz działanie
                    return; //zakończy metode
                default:
                    System.out.println("Nieznana opcja");
            }
        }
    }

    private void languageInfo() {
        System.out.println("Podaj język. Do wyboru masz:");

        displayAllLanguages();

        String language = scanner.nextLine();

        String sql = "SELECT Name, IsOfficial, Percentage FROM countrylanguage cl " +
                "JOIN country c ON c.code = cl.CountryCode " +
                "WHERE Language = ? " +
                "ORDER BY Percentage DESC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, language); //ustawiamy set string na pierwszy znak zapytania
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String countryName = resultSet.getString("Name");
                String isOfficial = resultSet.getString("IsOfficial");
                double percentage = resultSet.getDouble("Percentage");

                String offical = (isOfficial.equals("T")) ? "Oficjalny" : "Nieoficjalny";
                //jeśli offficial = oficjalny to zapisz oficjalnu jesli nie to nieoficjalny

                System.out.printf("%-30s %-10s %f%n", countryName, offical, percentage);
                //%-30s - minus 30 znaków
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayAllLanguages() {

        try {
            String sql = "SELECT Distinct Language FROM countrylanguage";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String language = resultSet.getString("Language");
                System.out.println(language);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Błąd podczas zamykania połączenia: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //pobieranie informacji o wszystkich polskich miastach
    private void citiesFromPoland() {
        fetchAndDisplayCities("POL");
    }

    private void fetchAndDisplayCities(String countryCode) {
        try {
            String sql = "SELECT * FROM city WHERE CountryCode = ? ORDER BY Name ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, countryCode); // zamiast ? zostanie wstawiony parametr z kodem kraju
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
    }

    private void citiesForGivenCountryCode() { //szuka wartosci dla kodu podanego przez użytkownika

        System.out.println("Podaj kod kraju");
        String countryCode = scanner.nextLine();

        fetchAndDisplayCities(countryCode);

    }
}
