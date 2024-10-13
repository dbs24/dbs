
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.system.measureTimeMillis


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
Камни и украшения
Не решалась
Лёгкая

Даны две строки строчных латинских символов: строка J и строка S. Символы, входящие в строку J, — «драгоценности», входящие в строку S — «камни». Нужно определить, какое количество символов из S одновременно являются «драгоценностями». Проще говоря, нужно проверить, какое количество символов из S входит в J.
Формат ввода

На двух первых строках входного файла содержатся две строки, состоящие из строчных латинских символов: строка J и строка S. Длина каждой не превосходит 100 символов. Также одна или обе строки могут быть пустыми.
Формат вывода

Выходной файл должен содержать единственное число — количество камней, являющихся драгоценностями.
 */
fun main() {

    doTask { reader, writer ->

        val regex =  Regex("[^a-z]")
        val EMPTY_STRING = ""

        val searchString = reader.readLine().lowercase().replace(regex, EMPTY_STRING).toList().groupBy { it }.map { it.key }
        //val searchString = "qcc".replace(regex, EMPTY_STRING).toList().groupBy { it }.map { it.key }.also { println("sourceString = $it") }
        require(searchString.size <= 100) { "invalid searchString" }

        val sourceString = reader.readLine().lowercase().replace(regex, EMPTY_STRING)
        //val sourceString = "cq".replace(regex, EMPTY_STRING).also { println("sourceString = $it") }
        require(sourceString.length <= 100) { "invalid sourceString" }

        var result = 0

        if (sourceString.isNotEmpty() and searchString.isNotEmpty())
            searchString.forEach { char ->
                result += sourceString.count { it == char }
            }

        writer.write(result.toString())
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


