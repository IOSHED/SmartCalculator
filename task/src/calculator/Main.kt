package calculator

fun main() {    // главная функция программы

     // Создаём основной цыкл программы. Мы его сможем закончить при помощи - break@Loop и прервать - continue@Loop
    Loop@while(true) {
        // print(">>> ")     // вывод для красивого ввода

         // Принемаем ввод от пользователя, разделяем его по поробелам создаём строковый лист из значений
        val inputList = readln().split(" ").toMutableList()
        when {

             // Если пользователь введёт команду "/help",то пользователь получит всю ему необходимую информацию
            "/help" in inputList -> {
                println("The program calculates the sum of numbers"); continue@Loop
            }

            // Если пользователь введёт команду "/exit", то пользователь тем самым выйдет из калькулятора
            "/exit" in inputList -> {
                println("Bye!"); break@Loop
            }

            // Если введена несзвестная команда, то вызываем исключение
            Regex("/[a-zA-Z0-9]*").matches(inputList[0]) -> {
                println("Unknown command"); continue@Loop
            }

            // Если введёно ничего, то просто игнорируем
            Regex("").matches(inputList[0]) -> {
                continue@Loop
            }

            // Если не распознанно специяальных команд, то мы отсортируем наш пользовательский ввод,
            // Получим результат и напечатаем его
            else -> {
                try {
                    val sortList = BulkheadForSort(inputList).bulkheadForSort()  // получаем отфильтрованный список
                    println("${Calculator(sortList).recipientValue()}") // выводим резултат нашего калькулятора
                } catch (err: Exception) {
                    println("Invalid expression")   // если произошла ошибка то выдаём исключение
                }
            }
        }
    }
}


/*
 * Создание класса калькулятора, который примет отсортированный лист чисели операций. Его единственная функция
 * создаст возращаемую переменную value, в которое будет помещенно первое значение, введённое пользователемю
 * Так же в ней перебирается полученный отсортированный массив и пребавляется или вычитается (в зависимости от ситуации)
 * значение в переменную value
 */

class Calculator(_sortList: List<String>) {
    private val sortList = _sortList    // создаём поле для манипуляции с отсортированным вводом

    fun recipientValue(): Int {
        var value = this.sortList[0].toInt() // записываем в результат первую цифру, введённую пользоватлем
        for (i in this.sortList.indices) {  // проходимся по массиву
            when (this.sortList[i]) {
                "+" -> value += sortList[i + 1].toInt()  // если встреченно значение "+", то прибавляем следуещее число
                "-" -> value -= sortList[i + 1].toInt()    // если встреченно значение "-", то вычитаем следуещее число
            }
        }
        return value
    }
}


/*
 * Создание класса, принемающий пользовательский ввод. В нем функции сортируют пользовательский ввод ( пропускает
 * только числа с  доступными операциями - "-", "+" операциями, игнорирует пробелы, а так же преобразует выражения,
 * по типу "---", "++++" в более прростые ) и помещают эти отсортированные значения в своё возращаемое значение.
 */

class BulkheadForSort(_inputList: List<String>) {
    private val listNumbers = mutableListOf<String>()   // создадим массив для ввода туда отфильтрованных частей
    private val inputList = _inputList  // получаем пользовательский ввод для с ней монипуляциями
    private var flag = 0    // переменная для определения коректности ввода пользователя
    private var indexOperation = "+"    // предварительное значение для встречной операции

    fun bulkheadForSort(): MutableList<String> {
        val listPossibleOperation = mutableListOf("+", "-") // запишем доступные действия
        loop@ for (j in this.inputList.indices) {   // проходим по пользовательскому вводу
            try {
                this.inputList[j].toInt()   // проверка на, то что это число
                this.listNumbers.add(this.inputList[j]) // если это число добавим его в наш лист (фильтровочный список)
                this.flag -= 1
            } catch (_: Exception) {
                this.flag += 1
                when {
                    // игнорируем все лишние пробелы
                    this.inputList[j] == "" -> continue@loop
                    // если это было не число, то проверим доступная ли это операция (если да, то мы её добавим в лист)
                    this.inputList[j] in listPossibleOperation -> this.listNumbers.add(this.inputList[j])
                    // если это операция с двойным плюсом или минусом, то мы добавим в лист "+"
                    this.inputList[j].length % 2 == 0 -> this.listNumbers.add("+")
                    this.inputList[j].length % 2 == 1 -> {
                        counterPlusAndMinus(j)  // определяем сокращённую запись для выражений: "----", "+++"
                        this.listNumbers.add(this.indexOperation)   // добавляем получинную сокращённую запись
                    }
                }
                continue@loop   // если ничего из этого не подходим, то пропусним этот элемент
            }
        }
        if (this.flag >= -1) {
            return  this.listNumbers  // возвращаем отфильтрованный список
        } else {
            throw Exception()   // вызываем исключение
        }
    }

    private fun counterPlusAndMinus(index: Int) {
        // принемаем индекс пользоватеского ввода, по которому будем искать правильную сокращённую запись
        when(this.inputList[index][0]) {
            '+' -> this.indexOperation = "+"   // если это выражение начинается с "+", то мы запомним выражение как "+"
            '-' -> this.indexOperation = "-"   // если это выражение начинается с "-", то мы запомним выражение как "-"
        }
    }
}
