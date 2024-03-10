package com.gridnine.testing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        // Исключаем перелеты, которые не удовлетворяют заданным правилам и выводим их списки.
        List<Flight> flightsExcludingDepartureBeforeNow = excludeflightsdeparturebeforenow(flights);
        System.out.println("Исключены перелеты с вылетом до текущего момента времени:");
        for (Flight flight : flightsExcludingDepartureBeforeNow) {
            System.out.println(flight);
        }
        System.out.println("---------------------------------------------");

        List<Flight> flightsExcludingSegmentsInverted = excludeFlightsSegmentsInverted(flights);
        System.out.println("Исключены перелеты с сегментами, где дата прилета раньше даты вылета:");
        for (Flight flight : flightsExcludingSegmentsInverted) {
            System.out.println(flight);
        }
        System.out.println("---------------------------------------------");

        List<Flight> flightsExcludingGroundTimeExceeded = excludeFlightsGroundTimeExceeded(flights);
        System.out.println("Исключены перелеты, где время проведенное на земле превышает 2 часа:");
        for (Flight flight : flightsExcludingGroundTimeExceeded) {
            System.out.println(flight);
        }
        System.out.println("---------------------------------------------");

        List<Flight> allFlights = FlightBuilder.createFlights();
        System.out.println("Выводим каждый перелет в командную строку");
        for (Flight flight : allFlights) {
            System.out.println(flight);
        }
        System.out.println("---------------------------------------------");

        printOrdinaryFlights(flights);
        System.out.println("---------------------------------------------");
        printMultiSegmentFlights(flights);
        System.out.println("---------------------------------------------");
        printFlightSegmentsTime(flights);
        System.out.println("---------------------------------------------");
        printFlightsInPast(flights);
        System.out.println("---------------------------------------------");
        printFlightsOverTwoHours(flights);
        System.out.println("---------------------------------------------");



        // Для каждого сегмента перелета вычисляем общее время проведенное на земле
        List<Flight> timeFlights = FlightBuilder.createFlights();
        for (Flight flight : timeFlights) {
            System.out.println("Сегмент полёта: " + flight.getSegments());
            for (Segment segment : flight.getSegments()) {
                long groundTime = segment.getGroundTime();
                if (groundTime < 0) {
                    System.out.println("Сегмент наземного времени: 0 часов");
                } else {
                    System.out.println("Сегмент наземного времени: " + groundTime + " часов");
                }
            }

            System.out.println();
        }

    }



    private static List<Flight> excludeflightsdeparturebeforenow(List<Flight> flights) {
        LocalDateTime now = LocalDateTime.now();
        return flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getDepartureDate().isAfter(now) ))
                .collect(Collectors.toList());
    }

    // Метод для исключения перелетов с сегментами, где дата прилета раньше даты вылета
    private static List<Flight> excludeFlightsSegmentsInverted(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getSegments()
                        .stream()
                        .allMatch(segment -> segment.getArrivalDate().isAfter(segment.getDepartureDate())))
                .collect(Collectors.toList());
    }

    // Метод для исключения перелетов, где время, проведенное на земле превышает 2 часа
    private static List<Flight> excludeFlightsGroundTimeExceeded(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> getTotalGroundTime(flight) <= 2)
                .collect(Collectors.toList());
    }





    // Метод для вычисления общего времени проведенного на земле в перелете
    private static int getTotalGroundTime(Flight flight) {
        int totalGroundTime = 0;
        List<Segment> segments = flight.getSegments();

        for (int i = 0; i < segments.size() - 1; i++) {
            LocalDateTime arrivalDate = segments.get(i).getArrivalDate();
            LocalDateTime departureDate = segments.get(i + 1).getDepartureDate();
            totalGroundTime += departureDate.toLocalTime().toSecondOfDay() - arrivalDate.toLocalTime().toSecondOfDay();
        }

        return totalGroundTime / 3600; // переводим в часы
    }
    private static void printOrdinaryFlights(List<Flight> flights) {
        System.out.println("Ординарные рейсы:");
        for (Flight flight : flights) {
            if (flight.getSegments().size() == 1) {
                System.out.println(flight);
            }
        }
    }

    private static void printMultiSegmentFlights(List<Flight> flights) {
        System.out.println("Рейсы с двумя или большим количеством сегментов:");
        for (Flight flight : flights) {
            if (flight.getSegments().size() > 1) {
                System.out.println(flight);
            }
        }
    }

    private static void printFlightSegmentsTime(List<Flight> flights) {
        System.out.println("Время между сегментами рейсов:");
        for (Flight flight : flights) {
            List<Segment> segments = flight.getSegments();
            if (segments.size() > 1) {
                for (int i = 0; i < segments.size() - 1; i++) {
                    Segment currentSegment = segments.get(i);
                    Segment nextSegment = segments.get(i + 1);
                    Duration duration = Duration.between(currentSegment.getArrivalDate(), nextSegment.getDepartureDate());
                    System.out.println("Между сегментом " + currentSegment + " и " + nextSegment +
                            " проходит " + duration.toHours() + " ч.");
                }
            }
        }
    }

    private static void printFlightsInPast(List<Flight> flights) {
        System.out.println("Рейсы отправленные в прошлом:");
        LocalDateTime now = LocalDateTime.now();
        for (Flight flight : flights) {
            for (Segment segment : flight.getSegments()) {
                if (segment.getDepartureDate().isBefore(now)) {
                    System.out.println(flight);
                    break;
                }
            }
        }
    }

    private static void printFlightsOverTwoHours(List<Flight> flights) {
        System.out.println("Рейсы стоящие более двух часов:");
        for (Flight flight : flights) {
            for (Segment segment : flight.getSegments()) {
                if (segment.getGroundTime() > 2) {
                    System.out.println(flight);
                    break;
                }
            }
        }
    }
}