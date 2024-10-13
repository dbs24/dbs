
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.system.measureTimeMillis



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
Задан массив aa размера nn. Необходимо посчитать количество уникальных элементов в данном массиве. Элемент называется уникальным, если встречается в массиве ровно один раз.
Формат ввода

В первой строке входных данных подается одно целое число nn (1≤n≤1051≤n≤105).

Во второй строке входных данных подается nn целых чисел, разделенных пробелами −− a1a1​, a2a2​, ……, anan​ (1≤ai≤1091≤ai​≤109).
Формат вывода

В единственной строке выведите ответ на задачу.
Ограничения
 */
fun main() {

    doTask { reader, writer ->
        val size = reader.readLine().toInt()
        require(size > 0) { "invalid input size ($size)" }

        val array = reader.readLine().split(" ")
        require(size == array.size) { "input size does not match actual array size ($size <> ${array.size})" }

        val uniqueValueCount =
            array
            .map { it.toUInt() }
            .groupBy { it }
            .filter { it.value.size == 1 }
            .map { it.key }
            .count()

        require(size >= uniqueValueCount) { "uniqueElements should less then $size" }
        writer.write(uniqueValueCount.toString())
    }
}
private fun doTask(task: (BufferedReader, BufferedWriter) -> Unit) {

    val debugMode = false

    BufferedReader(InputStreamReader(System.`in`)).apply reader@ {
        BufferedWriter(OutputStreamWriter(System.out)).apply writer@ {
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


