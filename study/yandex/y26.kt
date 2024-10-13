
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.system.measureTimeMillis


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
26. Набор символов
Не решалась
Лёгкая

Задана строка ss, состоящая только из символов английского алфавита нижнего регистра ({a,…,z}{a,…,z}), и множество символов английского алфавита нижнего регистра C={c1,…,ck}C={c1​,…,ck​}.

Подстрокой (i,j)(i,j) (1≤i≤j≤∣s∣1≤i≤j≤∣s∣) назовем записанные подряд символы строки ss с ii-й по jj-ю позиции: sisi+1…sjsi​si+1​…sj​.

Назовем набором символов подстроки (i,j)(i,j) множество символов: T(i,j)={st∣i≤t≤j}T(i,j)={st​∣i≤t≤j}.

Вам необходимо найти подстроку (i,j)(i,j) минимальной длины, для которой выполняется: T(i,j)=CT(i,j)=C.
Формат ввода

В первой строке входных данных записана одна строка ss (1≤∣s∣≤1001≤∣s∣≤100). Гарантируется, что все символы строки ss являются символами английского алфавита нижнего регистра.

Во второй строке входных данных записана одна строка cc (1≤∣c∣≤261≤∣c∣≤26), в которой содержатся те и только те символы, которые содержатся в множестве CC.

Гарантируется, что все символы в строке CC различны.
Формат вывода

В единственной строке выходных данных Вам необходимо вывести минимальную длину искомой подстроки. Если не существует подстроки, удовлетворяющей необходимому свойству, выведите одно число 00.
Ограничения
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


