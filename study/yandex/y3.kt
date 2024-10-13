import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

/**
 * 3. Закрытый ключ
 * Решена
 * Лёгкая
 *
 * Во всех крупных IT-компаниях немалое внимание уделяется вопросам информационной безопасности, и Яндекс не является исключением.
 *
 * Дима и Егор разрабатывают новый сервис YD (Yandex Dorogi) и в данный момент занимаются аудитом его безопасности. Для шифрования пользовательских данных в YD используется алгоритм шифрования с открытым ключом YS (Yandex Shifrovatel).
 *
 * Схема работы алгоритма YS такова: для каждого сервиса генерируется закрытый ключ (p,qp,q), где pp и qq — натуральные числа. По закрытому ключу (p,qp,q) генерируется открытый ключ (НОД(p,qp,q), НОК(p,qp,q)), который доступен всем пользователям. Если злоумышленник сможет по открытому ключу получить закрытый ключ, то он получит доступ ко всем данным YD и нанесёт сервису непоправимый вред. Конечно же, Егор и Дима не хотят этого допустить, поэтому они хотят сделать так, чтобы злоумышленнику пришлось перебрать очень много вариантов открытого ключа, прежде чем он сможет его угадать.
 *
 * Дима уже сгенерировал закрытый ключ для YD и получил на его основе открытый ключ (x,yx,y). Егору сразу же стало интересно, сколько вариантов закрытого ключа придётся перебрать злоумышленнику для взлома YD в худшем случае, иными словами, сколько существует закрытых ключей (p,qp,q) таких, что открытым ключом для них является (x,yx,y). К сожалению, у Егора есть много других задач, очень важных для запуска YD, поэтому он просит вас вычислить это количество за него.
 * Формат ввода
 *
 * В первой строке содержатся два целых числа xx и yy (1≤x≤y≤10121≤x≤y≤1012) — описание открытого ключа.
 * Формат вывода
 *
 * Выведите одно целое число — количество закрытых ключей, для которых данный ключ является открытым.
 * Примечание
 *
 * В первом примере существует два закрытых ключа, для которых (5,105,10) является открытым ключом: (5,105,10) и (10,510,5).
 *
 * Во втором примере Дима ошибся, потому что ни один закрытый ключ не порождает открытый ключ (10,1110,11).
 *
 * В третьем примере подходящими закрытыми ключами являются (527,9486527,9486), (1054,47431054,4743), (4743,10544743,1054), (9486,5279486,527).
 *
 * НОД (наибольшим общим делителем) двух натуральных чисел pp и qq называется наибольшее число kk такое, что pp делится на kk и qq делится на kk. Например, НОД(6,156,15) равен 33, а НОД(16,816,8) равен 88.
 *
 * НОК (наименьшим общим кратным) двух натуральных чисел pp и qq называется наименьшее число kk такое, что kk делится на pp и kk делится на qq. Например, НОК(2,32,3) равен 66, а НОК(10,2010,20) равен 20.
 *
 */

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun main() {

    doTask { reader, writer ->
        val size = 2 //reader.readLine().toInt()
        //require(size == 3) { "invalid input size ($size)" }

        val array = reader.readLine().split(" ")
        require(size == array.size) { "input size does not match actual array size ($size <> ${array.size})" }

        val NOD: ULong = array[0].toULong()
        val NOK: ULong = array[1].toULong()

        require(NOK > 0U) { "key should be > 0" }
        require(NOD > 0U) { "key should be > 0" }
        require(NOD <= NOK) { "key1 should be < key2" }

        var entries = 0

        val n: ULong = NOK / NOD;

        if (NOK % NOD > 0uL)
            entries = 0
        else
            if (NOK == NOD)
                entries = 1
            else
                for (i in 1..(sqrt(n.toDouble()).toInt())) {

                    if ((n % (i.toULong())) == 0uL)
                        if (gcd(i.toULong(), n / (i.toULong())) == 1uL)
                            entries += 2
                }

        writer.write(entries.toString())
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

private fun gcd(a: ULong, b: ULong): ULong {
    var num1 = a
    var num2 = b

    while (num2 != 0uL) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }

    return num1
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////