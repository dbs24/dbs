import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.system.measureTimeMillis

/**
 *
https://coderun.yandex.ru/selections/backend/problems/calendar-formatting
 *
 *
43. Форматирование календаря
(!!!!) Не решалась
Лёгкая

Для отрисовки календаря в студенческом проекте было решено выделить функциональность форматирования в отдельный модуль.

Параметрами модуля (формально для функции, которую можно будет импортировать из модуля) будут количество дней в месяце и название дня недели, на который приходится первое число месяца, записанное на английском языке.

Выведите все дни месяца по неделям, дополнив первую неделю пустыми значениями, если это требуется.
Формат ввода

В единственной строке входных данных записаны две величины:

nDaysnDays (28≤nDays≤3128≤nDays≤31) - количество дней в месяце;
weekday∈[Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday]weekday∈[Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday] — день недели, на который приходится первое число месяца.

Формат вывода

Выведите kk строк (4≤k≤64≤k≤6), в ii-й строке выведите даты, которые попадают на ii-ю неделю месяца.

При выводе следуйте следующим правилам:

все строки, кроме последней, должны иметь ровно 7 элементов (в последней строке также может оказаться 7 элементов);
при выводе дней с номерами от 1 до 9 следует добавить символ точки (.) перед цифрой;
при выводе дней первой недели перед первым числом используйте две точки (..).

Ограничения
 */

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun main() {

    doTask { reader, writer ->
        val size = reader.readLine().toInt()
        require(size > 0) { "invalid input size ($size)" }

        val array = reader.readLine().split(" ")
        require(size == array.size) { "input size does not match actual array size ($size <> ${array.size})" }

        val sortedMap =
            array
                .map { it.toUInt() }
                .groupBy { it }
                .toSortedMap()

        val maxOccurrs = sortedMap.maxOf { it.value.size }

        val element = sortedMap
            .filter { it.value.size == maxOccurrs }
            .map { it.key }
            .maxOf { it }

//        println("sortedMap: $sortedMap")
//        println("maxOccurrs: $maxOccurrs")

        writer.write(element.toString())
    }
}

private fun doTask(task: (BufferedReader, BufferedWriter) -> Unit) {

    val debugMode = false

    BufferedReader(InputStreamReader(System.`in`)).apply reader@{
        BufferedWriter(OutputStreamWriter(System.out)).apply writer@{
            if (debugMode)
                measureTimeMillis {
                    task(this@reader, this@writer)
                }.also {
                    println("execTime = $it ms")
                }
            else
                task(this@reader, this@writer)
            close()
        }
        close()
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////