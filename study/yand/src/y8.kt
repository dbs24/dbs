import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.system.measureTimeMillis

/**
8. Произведение
Не решалась
Сложная

У Васи есть массив AA длины NN из неотрицательных целых чисел и число MM. Необходимо выбрать ровно KK элементов массива AA, чтобы их произведение было в точности равно MM.
Формат ввода

Первая строка входного файла содержит три числа NN, MM, KK (1≤K≤N≤5 000(1≤K≤N≤5000, 0≤M≤109)0≤M≤109) — размер массива AA, произведение, которое нужно построить, и количество выбираемых элементов соответственно.

Вторая строка входного файла содержит NN неотрицательных целых чисел AiAi​ (0≤Ai≤109)(0≤Ai​≤109) — элементы массива AA.

Гарантируется, что ответ всегда существует.
Формат вывода

Выведите KK различных натуральных чисел — индексы выбранных элементов массива AA. Если решений несколько, выведите любое. Индексы можно выводить в произвольном порядке.
 */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


fun main() {

    val DELIMITER = " "
    val debugMode = System.getProperty("user.dir").contains("untitled")
    val maxValue = 1000000000

    fun doTask(task: (BufferedReader, BufferedWriter) -> Unit) {

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

    fun primeFactors(n: UInt): List<UInt> {

        if (n <= 0u) return listOf(0u)

        if (n <= 2u) return listOf(n)

        val factors = mutableListOf<UInt>()
        var number: UInt = n

        // Проверяем на делимость на 2
        while (number % 2u == 0u) {
            factors.add(2u)
            number /= 2u
        }

        // Проверяем на делимость на нечетные числа от 3 до корня из числа
        var factor = 3u
        while (factor * factor <= number) {
            while (number % factor == 0u) {
                factors.add(factor)
                number /= factor
            }
            factor += 2u // Переход к следующему нечетному числу
        }

        // Если число больше 2, добавляем его как последний множитель
        if (number > 2u) {
            factors.add(number)
        }

        return factors.also {
            if (debugMode)
                println("prime factors: $it")
        }
    }

    class TestContainer() {

        val testNumber by lazy { Random.nextUInt(1u, maxValue.toUInt()) }
        val fakedNumber by lazy {
            Random.nextUInt(20u, 100u).also {
                if (debugMode)
                    println("fakedNumber numbers: $it")
            }
        }
        val testNumbers by lazy {
            primeFactors(testNumber).let { pf ->

                val ml = mutableListOf<UInt>().let { it.addAll(pf); it }

                repeat(fakedNumber.toInt()) {
                    ml.add(Random.nextUInt(1u, (pf.maxOf { it })))
                }

                ml
            }
        }


    }

    doTask { reader, writer ->

        val testContainer by lazy { TestContainer() }

//        println("${testContainer.testNumber}")
//        println(" (${testContainer.testNumbers.size}) ${testContainer.testNumbers}")
//        println("${testContainer.fakedNumber}")

        // массив цифр
        val input = if (debugMode)
        //    "7 60 4".split(DELIMITER).map { it.toInt() }.toList()
        //    "7 27 2".split(DELIMITER).map { it.toInt() }.toList()
        //    "75 293 25".split(DELIMITER).map { it.toInt() }.toList()
        //    "5 256 3".split(DELIMITER).map { it.toInt() }.toList()
        //    testContainer.let { "${it.testNumbers.size} ${it.testNumber} ${it.testNumbers.size - it.fakedNumber.toInt()}" }.split(DELIMITER).map { it.toInt() }.toList()
        //    "87 323472583 5".split(DELIMITER).map { it.toInt() }.toList()
        //    "8 323472583 5".split(DELIMITER).map { it.toInt() }.toList()
            "7 323472583 5".split(DELIMITER).map { it.toInt() }.toList()
        else
            reader.readLine().split(DELIMITER).map { it.toInt() }.toList()
        //val input = "7 27 2".split(DELIMITER).map { it.toInt() }.toList()
        //val input = "7 60 4".split(DELIMITER).map { it.toInt() }.toList()
        //val input = "11 24 4".split(DELIMITER).map { it.toInt() }.toList()
        // elements
        val N = input[0]
        // искомое произведение
        val M = input[1] //reader.readLine().toInt()
        // количество индексов в ответе
        val K = input[2] //reader.readLine().toInt()

        //val A = reader.readLine().split(DELIMITER).map { it.toInt() }.toList()
        //val A = "9 1 1 27 3 27 3".split(DELIMITER).map { it.toInt() }.toList()
        //val A =  "30 1 1 3 10 6 4".split(DELIMITER).map { it.toInt() }.toList()
        val A = if (debugMode)
        //"0 0 1 0 0 1 0 0 0 1 0 0 1 0 0 1 1 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 31 2".split(DELIMITER)
        //    "33071 7973 1399 119 67 29 17 7".split(DELIMITER)
            "1399 119 119 67 29 17 7".split(DELIMITER)
                //    "9 1 1 27 3 27 3".split(DELIMITER)
                //    "30 1 1 3 10 6 4".split(DELIMITER).map { it.toInt() }.toList()
                //            testContainer.testNumbers.map { it.toInt() }.toList()
                //    "7 17 29 67 1399 1309 630 1287 755 817 930 824 883 569 44 1111 380 655 119 845 691 1288 1349 447 362 1119 671 58 710 1166 1395 419 20 1073 13 568 457 764 1090 9 219 270 53 442 1178 1080 291 162 1012 892 1050 516 870 850 920 420 291 1162 835 649 253 823 833 6 560 619 613 677 179 1097 79 1342 970 83 276 1012 1268 1376 673 699 114 1341 607 403 309 717 18".split(DELIMITER)
                //    "1 4 16 16 4".split(DELIMITER)
//                "0 0 2 1 4 0 6 0 3 3 3 0 3 3 3 3 3 3 1 3 3 3 3 1 1 0 0 0 0 1 1 1 1 1 1 1 1 1 1 0 0 0 00 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 4 293".split(
//                    DELIMITER
//                )
                //    "30 1 1 3 10 6 4".split(DELIMITER1)
                //if (debugMode) "30 1 1 3 10 6 4".split(DELIMITER)
                //   "0 0 0 0 12".split(DELIMITER)
                .map { it.toInt() }.toList()
        else reader.readLine().split(DELIMITER).map { it.toInt() }.toList()

        if (debugMode) {
            println("target=$M")
            println("indexes amount=$K\n")
            println("A=$A")
        }

        require((K > 0) and (K <= N)) { "too many searched elements" }
        require(N <= 5000) { "too many numbers" }
        require((M >= 0) and (M <= maxValue)) { "invalid search number" }
        require(N == A.size) { "sizes do not matches ($N<>${A.size})" }
        require(A.filter { (!((it >= 0) and (it <= maxValue))) }.count() == 0) { "invalid number applied" }
        require(N <= 5000) { "too many numbers" }

        val result = mutableListOf<Int>()

        // искомый элемент 0
        if (M == 0) {
            // любой нулевой элемент
            val zeroIndex = A.indexOf(0)
            result.add(zeroIndex + 1)
            // остальные чем попало
            for (i in 0..K - 1) {
                if ((i != zeroIndex))
                    result.add(i + 1)
                if (result.size == K) break
            }

        } else {

            if (K == N) {
                result.addAll(A.mapIndexed { index, _ -> index + 1 })
            } else {

                val orderedList = A.filter { ((it > 0) and ((it == M) or (M % it == 0))) }//.sortedDescending()

                require(orderedList.isNotEmpty()) { "orderList should not empty" }

                if (debugMode)
                    println("M=$M, K=$K, sortedList: $orderedList")

                for (i in orderedList.indices) {

                    val items = arrayListOf<Int>()
                    var quotient: UInt = M.toUInt()

                    if (debugMode)
                        println("i: $i : start elemet: ${orderedList[i]}")

                    for (j in i..i + (orderedList.size-1)) {

                        val k = if (j < orderedList.size) j else (j - orderedList.size )

                        val mp = orderedList[k].toUInt()

                        if (quotient >= mp)
                            if ((quotient % mp == 0u)) {
                                //if (!fakedNum(j)) {
                                //if ((/*(primeFactors.contains(mp)) or */ (mp == M.toUInt()) or (mp == 1u))) {
                                quotient /= mp
                                items.add(mp.toInt())

                                if (debugMode)
                                    println("+++ add multiplayer ($mp)")

                                //}
                            }

                        if (debugMode)
                            println("i: $i, j: $j, k: $k, $mp, q: $quotient; M: $M")

                        if ((items.size == K) and (quotient == 1u))
                            break

                    }


                    if (debugMode)
                        println("//  items=$items, q: $quotient ==================================================")

//                    require(quotient != M.toUInt()) { "bad quotient" }

//                    if (quotient  == M.toUInt()) {
//                        //require(orderedList.size >1000 ) { "empty orderedList" }
//                        require(i>0 ) { "empty orderedList" }
//                    }


                    if ((items.size == K) and (quotient == 1u)) {

                        if (debugMode)
                            println(" transform Items: items = $items")

                        for (k in 0..A.size - 1) {
                            val itemIndex = items.indexOf(A[k])
                            if (itemIndex >= 0) {
                                result.add(k + 1)
                                items.removeAt(itemIndex)
                            }
//                            if (debugMode)
//                                println(" result = $result")

                            if (result.size == K) break
                        }
                        break
                    }
                }

                require(result.isNotEmpty()) { "result is empty" }

            }
        }

        //require(result.size == K) { "Invalid result size (${result.size} != $K)" }

        if (debugMode) {

            println("//=============================================")
            println("result: $result, $M = (${result.map { A[it - 1] }})")
            println("//=============================================")

            if (result.isEmpty())
                println("result not found")
        } else {
            if (result.isEmpty())
                repeat(K) { result.add(0) }
        }

        if (debugMode) {
            val checkSum = result.map { A[it - 1] }.reduce { e1, e2 -> e1 * e2 }
            require(M == checkSum) { "invalid solution ($M <> $checkSum)" }
        }

        writer.write(result.joinToString(separator = DELIMITER))
    }

}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////