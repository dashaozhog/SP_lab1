// 1. Основний клас предметної області
// Властивості оголосіть у головному конструкторі (val та var),
// додайте значення за замовчуванням та блок init

import kotlinx.coroutines.*
import java.time.LocalDateTime


lateinit var elements: List<Atom>

class Property(val name: String, var description: String? = "") {
    init{
        println("Property $name has been initialized.")
        if(description!!.isNotEmpty()){
            println("Information about $name has been added.")
        }
    }
}

// 2. Клас з вторинним конструктором та власними методами доступу
class Substance(val formula: String, val name: String, val compounds: Map<Atom, Int>, curState: STATES) {
    // Обчислювана властивість з get()
    init {
        println("Substance $formula initialized.")
        this.addToList()

    }
    val molarMass : Double
        get() = compounds.entries.sumOf { (atom, count) -> atom.atomicMass*count }
    // Властивість з set(value) та ідентифікатором field
    var curState: STATES = curState
        set(value) {
            println("$formula state has been set from to $value")
            field = value
        }
    // Вторинний конструктор:
    constructor(formula: String, compounds: Map<Atom, Int>, curState: STATES) : this(formula, name = formula,compounds, curState){
        println("Secondary constructor has been set from to $formula")
    }
    // Об`єкт-компаньйон: companion object { ... }
    companion object{
        var allSubstances = mutableListOf<Substance>()
        fun Substance.addToList(){
            allSubstances.add(this)
        }
        fun showList(){
            println(allSubstances)
        }
    }
}

// 3. Клас даних та його звичайний аналог
data class Atom(val name: String, val chemicalSign: String, val seqNumber: Int, val atomicMass: Double, val meltingTemp: Float, val boilingTemp: Float,
                val density: Float, val atomicRadius: Int, val color: String, var description: String){
    init{
        println("Atom $name has been initialized.")
    }
}

class AtomClass(val name: String, val chemicalSign: String, val seqNumber: Int, val atomicMass: Double, val meltingTemp: Float, val boilingTemp: Float,
                val density: Float, val atomicRadius: Int, val color: String, var description: String)
// 4. Клас перелічень та запечатаний клас
enum class STATES {
    SOLID, LIQUID, GAS, PLASMA
}

sealed class SynthesisResult {
    data class Success(val resultSubstance: Substance) : SynthesisResult()
    data class Error(val error: String, private val errorCode: Int) : SynthesisResult(){
        companion object {
            const val DEFAULT_ERROR_CODE = 404
            fun createUnknownError(reason: String): Error {
                return Error(reason, DEFAULT_ERROR_CODE)
            }
        }
    }
}
fun checkSynthesis(result: SynthesisResult){
    when(result){
        is SynthesisResult.Success -> println("Synthesis successful! You got ${result.resultSubstance.name}")
        is SynthesisResult.Error -> println("Synthesis failed! ${result.error}")
    }
}

fun main() = runBlocking {
    println("--- Створення екземплярів ---")
    // Тут створіть об'єкти ваших класів...
    var hydrogen = Atom("Hydrogen", "H", 1, 1.007941, -259.14f, -252.87f, 0.08988f, 120, "colorless", "description" )
    var oxygen = Atom("Oxygen", "O", 8, 15.999, -218.79f, -182.95f, 1.429f, 152, "colorless", "essential for respiration")
    var sulfur = Atom("Sulfur", "S", 16, 32.06, 115.21f, 444.6f, 2070f, 180, "yellow", "used in fertilizers and industry")
    var carbon = Atom("Carbon", "C", 6, 12.011, 3550f, 4827f, 2260f, 170, "black (graphite) / transparent (diamond)", "basis of organic chemistry")
    var calcium = Atom("Calcium", "Ca", 20, 40.078, 842f, 1484f, 1550f, 197, "silvery-white", "essential for bones and shells")
    var calcium2 = Atom("Calcium", "Ca", 20, 40.078, 842f, 1484f, 1550f, 197, "silvery-white", "essential for bones and shells")

    // такі ж самі гідрогени, але звичайний клас, а не дата
    var hydrogen1 = AtomClass("Hydrogen", "H", 1, 1.007941, -259.14f, -252.87f, 0.08988f, 120, "colorless", "description" )
    var hydrogen2 = AtomClass("Hydrogen", "H", 1, 1.007941, -259.14f, -252.87f, 0.08988f, 120, "colorless", "description" )


    var h2so4 = Substance("H2SO4", "Sulfuric acid", compounds = mapOf(hydrogen to 2,oxygen to 4,sulfur to 1), curState = STATES.LIQUID)
    var calciumCarbonate = Substance("CaCO3", "Calcium carbonate", compounds = mapOf(
        calcium to 1,
        carbon to 1,
        oxygen to 3
    ),
        curState = STATES.SOLID
    )

    var atomicNumber = Property("Atomic Number", """The atomic number is equal to the number of
protons in an atom's nucleus. The atomic number
determines which element an atom is. For
example, any atom that contains exactly 47
protons in its nucleus is an atom of silver.
The number of neutrons in atom can change,
when this happens it is said to be a different
isotope of the element.""")

    println("--- Робота з властивостями ---")
    // Тут змініть значення var-властивостей та виведіть їх...
    hydrogen.description = "Hydrogen is a chemical element; it has the symbol H and atomic number 1. It is the lightest and most abundant chemical element in the universe, constituting about 75% of all normal matter. Under standard conditions, hydrogen is a gas of diatomic molecules with the formula H2, called dihydrogen, or sometimes hydrogen gas, molecular hydrogen, or simply hydrogen. "
    println(hydrogen.description)

    h2so4.curState = STATES.SOLID
    println(h2so4.curState)

    println("--- Порівняння класу даних ---")
    // Тут порівняйте два екземпляри через == та застосуйте copy()...
    println(calcium==calcium2) // порівняння два однаквові екземпляра дата класс - true
    var calcium3 = calcium.copy()  // створення копії
    println(calcium3)

    println(hydrogen1==hydrogen2) // порівняння однакових екземплярів ЗВИЧАЙНОГО класу - false
    println(hydrogen==hydrogen1) // порівняння однакових екземплярів ДАТА класу і ЗВИЧАЙНОГО класу - false

    println("--- Обробка результату ---")
    // Тут напишіть вичерпний when для запечатаного класу...
    var result1 = SynthesisResult.Success(h2so4)
    var result2 = SynthesisResult.Error("Elements are not compatible", 1488)
    checkSynthesis(result1)
    checkSynthesis(result2)

    // LABA 1

    // Додайте до змінної колекції новий елемент за допомогою методу
    //add та продемонструйте застосування операторів + і - для отримання нової
    //колекції на основі наявної. Побудуйте асоціативний масив Map, що пов'язує
    //певну властивість ваших об'єктів (наприклад, ідентифікатор або назву) із самим
    //об'єктом, використавши для створення пар інфіксний оператор to.

     elements = listOf(hydrogen, oxygen, calcium, carbon, sulfur)
    val mutElements = mutableListOf(hydrogen, oxygen, calcium, carbon, sulfur)

    mutElements.add(calcium2)

    mutElements -= calcium2

    var coll2 = mutElements+hydrogen

    val atoms = mapOf("Hydrogen" to hydrogen,
                        "Oxygen" to oxygen,
                     "Calcium" to calcium,
                    "Carbon" to carbon)
    coll2 = coll2.filter{it.atomicMass>16}
    var mapcoll = coll2.map{it.atomicMass}

    var groups = elements.groupBy { it.name.first() }
    for( g in groups){
        println("${g.key}: ${g.value}")
    }

     var substances = listOf(h2so4.compounds, calciumCarbonate.compounds).flatMap { it -> it.keys } // опрацьовано ксі елементи

    var seqElements = elements.asSequence().map{it.atomicMass}.filter{it>15}.first() // опрацьовано лише 2 - повернено перший результат, який задовільняє умову фільтру

// Тут викличте loadData(), а потім processData()...
    println(LocalDateTime.now())
    val data = load()
    process(data)
    println(LocalDateTime.now())

    println("--- launch ---")
// Тут запустіть дві незалежні корутини за допомогою launch...
    launch{
        load()
    }
    launch {
        process(elements)
    }
    println()
}
suspend fun load(): List<Atom>{
    println("Loading your data...[${LocalDateTime.now()}]")
    for(i in 1..10){
        print("-")
        delay(1000L)
    }
    println("\nLoading completed at ${LocalDateTime.now()}")
    return elements;
}

suspend fun process(data: List<Atom>) : List<Atom>{
    println("Processing your data...[${LocalDateTime.now()}]")
    delay(3000L)
    var processedData = data.asSequence().filter{it.color=="colorless"}.toList()
    println("Processed at ${LocalDateTime.now()}")
    return processedData;
}


