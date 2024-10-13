import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.system.measureTimeMillis

/**
6. Средний элемент
Решена
Лёгкая

Рассмотрим три числа aa, bb и cc. Упорядочим их по возрастанию.

Какое число будет стоять между двумя другими?

Решение этой задачи на С++ могло бы выглядеть так:

#include <iostream>
#include <algorithm>

using namespace std;

int main()
{
int a[3];
for (int i = 0; i < 3; ++i) cin >> a[i];
sort(a, a + 3);
cout << a[1] << endl;
return 0;
}

Формат ввода

В единственной строке записаны три целых числа aa, bb, cc (−1000≤a,b,c≤1000−1000≤a,b,c≤1000), числа разделены одиночными пробелами.
Формат вывода

Выведите число, которое будет стоять между двумя другими после упорядочивания.
Ограничения
 */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun main() {

    doTask { reader, writer ->
        val size = 3 //reader.readLine().toInt()
        //require(size == 3) { "invalid input size ($size)" }

        val array = reader.readLine().split(" ")
        require(size == array.size) { "input size does not match actual array size ($size <> ${array.size})" }

        val element =
            array
                .map { it.toInt() }
                .sorted()[1]

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