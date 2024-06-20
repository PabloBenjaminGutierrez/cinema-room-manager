package cinema

fun main() {
  val places = mutableListOf<MutableList<Char>>()
  println("Enter the number of rows:")
  val rowsNumber = readln().toInt()
  println("Enter the number of seats in each row:")
  val seatsByRow = readln().toInt()
  initialState(rowsNumber, seatsByRow, places)
  menu(places)
}

fun menu(places: MutableList<MutableList<Char>>) {
  do {
    println("1. Show the seats")
    println("2. Buy a ticket")
    println("3. Statistics")
    println("0. Exit")
    val option = readln().toInt()
    when (option) {
      1 -> printCinemaTheater(places)
      2 -> buyTicket(places)
      3 -> statistics(places)
    }
  } while (option != 0)
}

fun initialState(rowsNumber: Int, seatsByRow: Int, places: MutableList<MutableList<Char>>) {
  val rowLabels = mutableListOf<Char>()
  (0..seatsByRow).forEach { rowIndex -> rowLabels.add(rowIndex.digitToChar()) }
  rowLabels[0] = ' '
  places.add(rowLabels)
  for (rowIndex in 1..rowsNumber) {
    val row = mutableListOf<Char>()
    places.add(row)
    places[rowIndex].add(rowLabels[rowIndex])
  }
  for (rowIndex in 1..rowsNumber) {
    for (columnIndex in 1..seatsByRow) {
      places[rowIndex].add(columnIndex, 'S')
    }
  }
}

fun printCinemaTheater(places: MutableList<MutableList<Char>>) {
  println("Cinema:")
  places.forEach { row -> println(row.joinToString(separator = " ")) }
}

fun buyTicket(places: MutableList<MutableList<Char>>) {
  var validSeat = false
  var rowsSelected: Int
  var seatSelectedOnRow: Int
  do {
    println("Enter a row number:")
    rowsSelected = readln().toInt()
    println("Enter a seat number in that row:")
    seatSelectedOnRow = readln().toInt()
    try {
      if (rowsSelected !in 1..places.lastIndex || seatSelectedOnRow !in 1..places[0].lastIndex) {
        println("Wrong input!")
      } else {
        val takenSeat = places[rowsSelected][seatSelectedOnRow] == 'B'
        if (takenSeat) {
          println("That ticket has already been purchased!")
          validSeat = false
        } else {
          validSeat = true
        }
      }
    } catch (_: NumberFormatException) {
      println("Wrong input!")
    }
  } while (!validSeat)
  ticketPrice(rowsSelected, places)
  setSeatAsBuy(rowsSelected, seatSelectedOnRow, places)
}

fun ticketPrice(rowTicketNumber: Int, places: MutableList<MutableList<Char>>) {
  val seats = (places.size - 1) * (places[0].size - 1)
  val price =
      when {
        seats < 60 -> 10
        rowTicketNumber <= ((places.size - 1) / 2) -> 10
        rowTicketNumber > ((places.size - 1) / 2) -> 8
        else -> 0
      }
  println("Ticket price: $$price")
}

fun setSeatAsBuy(
    rowsSelected: Int,
    seatSelectedOnRow: Int,
    places: MutableList<MutableList<Char>>
) {
  places[rowsSelected][seatSelectedOnRow] = 'B'
}

fun statistics(places: MutableList<MutableList<Char>>) {
  val totalSeats = (places.size - 1) * (places[0].size - 1)
  val purchasedTickets = purchasedTickets(places)
  println("Number of purchased tickets: ${purchasedTickets.size}")
  val percentage = purchasedTickets.size / totalSeats.toDouble() * 100.0
  val formatPercentage = "%.2f".format(percentage)
  println("Percentage: $formatPercentage%")
  val currentIncome = calculateCurrentIncome(purchasedTickets, places)
  println("Current income: $$currentIncome")
  val totalIncome =
      when {
        totalSeats < 60 -> 10 * totalSeats
        (places.size - 1) % 2 == 0 -> 10 * (places.size - 1) / 2 + 8 * (places.size - 1) / 2
        else -> 10 * ((places.size - 1) / 2) * (places[0].size - 1) + 8 * (places.size / 2) * (places[0].size - 1)
      }
  println("Total income: $$totalIncome")
}

fun purchasedTickets(places: MutableList<MutableList<Char>>): List<Pair<Int, Int>> {
  val matches = mutableListOf<Pair<Int, Int>>()
  for (rowIndex in 1..places.lastIndex) {
    for (seatIndex in 1..places[rowIndex].lastIndex) {
      if (places[rowIndex][seatIndex] == 'B') {
        matches.add(Pair(rowIndex, seatIndex))
      }
    }
  }
  return matches
}

fun calculateCurrentIncome(
    purchasedTickets: List<Pair<Int, Int>>,
    places: MutableList<MutableList<Char>>
): Int {
  val seats = (places.size - 1) * (places[0].size - 1)
  val price =
      purchasedTickets
          .map { (rowIndex, _) ->
            when {
              seats < 60 -> 10
              rowIndex <= ((places.size - 1) / 2) -> 10
              rowIndex > ((places.size - 1) / 2) -> 8
              else -> 0
            }
          }
          .sum()
  return price
}
